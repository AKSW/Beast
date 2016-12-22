package org.aksw.beast.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.AbstractIterator;

public class ParallelStreams {

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
	 * The first exception encountered will be thrown.
	 *
	 * @param streams
	 * @return
	 */
	public static <T> Stream<T> join(Stream<Stream<T>> streams) {
		ExecutorService es = Executors.newCachedThreadPool();
		Stream<T> result = join(streams, es);
		return result;
	}

	public static <T> Stream<T> join(Stream<Stream<T>> streams, ExecutorService es) {
		BlockingQueue<T> queue = new LinkedBlockingQueue<>();

		CompletionService<T> cs = new ExecutorCompletionService<T>(es);

		// Each concurrently running workflow puts its result into the queue
		Set<Future<?>> streamFutures = streams
				.map(stream -> cs.submit(() -> stream.forEach(queue::add), null))
				.collect(Collectors.toSet());

		// Keep a reference to the calling thread, so we may interrupt its wait on the queue
		// in case of consumption of all streams or exception
		Thread caller = Thread.currentThread();
		Boolean[] isCallerWaitingForQueue = {false};

		// Create a thread to monitor consumption or exceptions of the streams
		ExecutorService completionChecker = Executors.newSingleThreadExecutor();
		Future<?> future = completionChecker.submit(() -> {
			Throwable e = null;

			try {
				while(!Thread.interrupted() && !streamFutures.isEmpty()) {
					Future<?> f = cs.take();

					synchronized(streamFutures) {
						streamFutures.remove(f);
					}

					// Check whether an exception occurred
					f.get();
				}
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			} catch(ExecutionException ex) {
				e = ex.getCause();
			}

			es.shutdownNow();

			synchronized (isCallerWaitingForQueue) {
				if(isCallerWaitingForQueue[0]) {
					caller.interrupt();
				}
			}

			if(e != null) {
				throw new RuntimeException(e);
			}
		});

		// We are done with thread creations so we can shutdown the executorServices
		es.shutdown();
		completionChecker.shutdown();


		Runnable cancelAction = () -> {
			es.shutdownNow();
			future.cancel(true);
			queue.clear();

			try {
				future.get();
			} catch(ExecutionException e) {
				throw new RuntimeException(e.getCause());
			} catch (InterruptedException e) {
				// Should not happen
				throw new RuntimeException(e);
			}
		};

		Iterator<T> it = new AbstractIterator<T>() {
			@Override
			protected T computeNext() {
				T result;
				if(streamFutures.isEmpty() || Thread.interrupted()) {
					result = endOfData();
					cancelAction.run();
				} else {
					try {
						synchronized (isCallerWaitingForQueue) {
							isCallerWaitingForQueue[0] = true;
						}
						result = queue.take();
						synchronized (isCallerWaitingForQueue) {
							isCallerWaitingForQueue[0] = false;
						}
					} catch (InterruptedException e) {
						result = endOfData();
						cancelAction.run();
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

