package org.aksw.beast.examples;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.AbstractIterator;

public class WorkflowExecutor {

	/**
	 * Returns a new stream based on executing all streams in the given stream in parallel, adds
	 * their items to a blocking queue and returns a new stream over that queue that lasts
	 * as long as there are active threads.
	 *
	 * Be aware, that chaining e.g.
	 * join(streams).limit(10) will keep all threads running in the background processing all items
	 *
	 * To remedy this issue, the returned stream has a .close() action attached which will
	 * interrupt all running threads, but still more than limit items will be processed.
	 *
	 * @param streams
	 * @return
	 */
	public static <T> Stream<T> join(Stream<Stream<T>> streams) {
		BlockingQueue<T> queue = new LinkedBlockingQueue<>();

		ExecutorService es = Executors.newCachedThreadPool();
		CompletionService<T> cs = new ExecutorCompletionService<T>(es);

		// Each concurrently running workflow puts its result into the queue

		// Submit all tasks
//		Set<Future<?>> streamFutures = Collections.synchronizedSet(streams
//			.map(stream ->
//				cs.submit(() -> {
//					Iterator<T> it = stream.iterator();
//					while(it.hasNext() && !Thread.interrupted()) {
//						T item = it.next();
//						queue.add(item);
//					}
//				}, null))
//			.collect(Collectors.toSet()));

		Set<Future<?>> streamFutures = Collections.synchronizedSet(streams
				.map(stream -> cs.submit(() -> stream.forEach(queue::add), null))
				.collect(Collectors.toSet()));


		Thread myThread = Thread.currentThread();

		ExecutorService completionChecker = Executors.newSingleThreadExecutor();
		Future<?> future = completionChecker.submit(() -> {
			while(!Thread.interrupted() && !streamFutures.isEmpty()) {
				try {
					Future<?> f = cs.take();
					streamFutures.remove(f);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			// Interrupt possible waiting on the queue
			myThread.interrupt();
		});

		// We are done with thread creations so we can shutdown the executorServices
		es.shutdown();
		completionChecker.shutdown();


		Runnable cancelAction = () -> {
			streamFutures.stream().forEach(f -> f.cancel(true));
			future.cancel(true);
			queue.clear();
			myThread.interrupt();
		};

		Iterator<T> it = new AbstractIterator<T>() {
			@Override
			protected T computeNext() {
				T result;
				if(streamFutures.isEmpty() || Thread.interrupted()) {
					result = endOfData();
				} else {
					try {
						result = queue.take();
					} catch (InterruptedException e) {
						result = endOfData();
						Thread.currentThread().interrupt();
					}
				}

				return result;
			}
		};

		Iterable<T> tmp = () -> it;

		Stream<T> result = StreamSupport.stream(tmp.spliterator(), false);

		// Note: If we consume the stream, we are in a clean state as all threads have terminated
		result.onClose(cancelAction);

		return result;
	}
}

