package org.aksw.beast.examples;

import java.io.OutputStream;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.google.common.collect.AbstractIterator;

public class WorkflowExecutor {

	public static <T> Stream<T> join(Stream<Stream<T>> streams) {
		BlockingQueue<T> queue = new LinkedBlockingQueue<>();

		ExecutorService es = Executors.newCachedThreadPool();
		CompletionService<T> cs = new ExecutorCompletionService<T>(es);

		// Each concurrently running workflow puts its result into the queue

		// Submit all tasks
		Set<Future<?>> streamFutures = Collections.synchronizedSet(streams
			.map(stream -> cs.submit(() -> stream.forEach(queue::add), null))
			.collect(Collectors.toSet()));

		Thread myThread = Thread.currentThread();

		Future<?> future = Executors.newSingleThreadExecutor().submit(() -> {
			while(!Thread.interrupted() && !streamFutures.isEmpty()) {
				try {
					Future<?> f = cs.take();
					streamFutures.remove(f);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			System.out.println("ALL TASKS DONE");
			// Interrupt waiting
			myThread.interrupt();
		});

		es.shutdown();

		Iterator<T> it = new AbstractIterator<T>() {
			@Override
			protected T computeNext() {
				System.out.println("MOO: " + Thread.getAllStackTraces().keySet());
				T result;
				if(streamFutures.isEmpty() || Thread.interrupted()) {
					result = endOfData();
				} else {
					try {
						System.out.println("WAITING FOR QUEUE - " + Thread.currentThread());
						result = queue.take();
						System.out.println("GOT ITEM FROM QUEUE - " + Thread.currentThread());
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

		// Cancel all tasks
		result.onClose(() -> streamFutures.stream().forEach(f -> f.cancel(true)));
		result.onClose(() -> future.cancel(true));
		result.onClose(() -> System.out.println("STREAM CLOSED"));
		return result;
	}
}

