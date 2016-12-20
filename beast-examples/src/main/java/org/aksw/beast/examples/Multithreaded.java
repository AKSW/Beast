package org.aksw.beast.examples;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
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

		// Number of workflows to generate
		int n = 10;

		// Set up workloads and workflows

		Model m = RDFDataMgr.loadModel("queries.ttl");
		List<Resource> workloads = m.listSubjectsWithProperty(LSQ.text).toList();

		// Fake query execution
		Random rand = new Random();
		BiConsumer<Resource, Query> queryAnalyzer = (observationRes, query) -> {
			logger.debug("Faking query execution: " + observationRes + " with " + query);
			PerformanceAnalyzer.analyze(observationRes, () -> Thread.sleep(rand.nextInt(100)));
		};

		RdfStream<Resource, ResourceEnh> workflowTemplate =
				MainQueryPerformance.createQueryPerformanceEvaluationWorkflow(queryAnalyzer, 10, 100);

		// Create a stream where each element is an instanciation of the workflowTemplate with our workload
		// Further, attach an identifier for the thread and craft the final IRI
		Stream<Stream<Resource>> workflowGen =
				IntStream.range(0, n).mapToObj(i ->
					workflowTemplate.apply(() -> workloads.stream()).get()
					.peek(r -> r.addLiteral(IV.thread, i + 1))
					.map(r -> r.rename("http://ex.org/thread{0}-run{1}-query{2}", IV.thread, IV.run, IV.job)));

		Stream<Resource> joined = WorkflowExecutor.join(workflowGen);
		joined.
			forEach(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS));

	}

}
