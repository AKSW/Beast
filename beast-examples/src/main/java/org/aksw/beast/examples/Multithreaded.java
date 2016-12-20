package org.aksw.beast.examples;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.aksw.jena_sparql_api.rdf_stream.core.RdfStream;
import org.aksw.jena_sparql_api.rdf_stream.enhanced.ResourceEnh;
import org.aksw.jena_sparql_api.rdf_stream.processors.PerformanceAnalyzer;
import org.aksw.jena_sparql_api.vocabs.IV;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Multithreaded {

	private static final Logger logger = LoggerFactory.getLogger(Multithreaded.class);

	public static void main(String[] args) throws Exception {

		// Parallel execution with n threads
		int n = 4;


		// Set up workloads and workflows

		Model m = RDFDataMgr.loadModel("queries.ttl");
		List<Resource> workloads = m.listSubjectsWithProperty(LSQ.text).toList();

		// Fake query execution
		Random rand = new Random();
		BiConsumer<Resource, Query> queryAnalyzer = (observationRes, query) -> {
			logger.debug("Faking query execution: " + observationRes + " with " + query);
			PerformanceAnalyzer.analyze(observationRes, () -> Thread.sleep(rand.nextInt(1000)));
		};

		RdfStream<Resource, ResourceEnh> workflowTemplate =
				MainQueryPerformance.createQueryPerformanceEvaluationWorkflow(queryAnalyzer, 1, 10);

		// Workflow bound to a given workload
		Supplier<Stream<ResourceEnh>> boundWorkflow = workflowTemplate.apply(() -> workloads.stream());




		// Set up for multithreading

		BlockingQueue<Resource> queue = new LinkedBlockingQueue<>();

		Runnable writerTask = () -> {
			try {
				while(!(Thread.currentThread().isInterrupted())) {
					Resource r = queue.take();
					RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};
		Future<?> writerThreadFuture = Executors.newSingleThreadExecutor().submit(writerTask);

		ExecutorService es = Executors.newCachedThreadPool();
		for(int i = 0; i < n; ++i) {
			final int j = i;
			Stream<ResourceEnh> workflow = boundWorkflow.get();

			// Each concurrently running workflow puts its result into the queue
			es.submit(() -> workflow
					.map(r -> r.rename("http://ex.org/thread{0}-run{1}-query{2}", j, IV.run, IV.job))
					.forEach(queue::add));
		}

		es.shutdown();
		es.awaitTermination(60, TimeUnit.SECONDS);

		writerThreadFuture.cancel(true);
	}
}
