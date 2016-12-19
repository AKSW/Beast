package org.aksw.beast.examples;

import static org.aksw.jena_sparql_api.rdf_stream.core.RdfStream.map;
import static org.aksw.jena_sparql_api.rdf_stream.core.RdfStream.peek;
import static org.aksw.jena_sparql_api.rdf_stream.core.RdfStream.repeat;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.aksw.iguana.vocab.IguanaVocab;
import org.aksw.jena_sparql_api.core.FluentQueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.rdf_stream.core.RdfStream;
import org.aksw.jena_sparql_api.rdf_stream.enhanced.ResourceEnh;
import org.aksw.jena_sparql_api.rdf_stream.processors.PerformanceAnalyzer;
import org.aksw.jena_sparql_api.vocabs.IV;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainQueryPerformance {

	private static final Logger logger = LoggerFactory.getLogger(MainKFoldCrossValidation.class);

	public static void main(String[] args) {

		QueryExecutionFactory qef = FluentQueryExecutionFactory
				.http("http://dbpedia.org/sparql", "http://dbpedia.org")
				.create();

		Model m = RDFDataMgr.loadModel("queries.ttl");

		List<Resource> workloads = m.listSubjectsWithProperty(LSQ.text).toList();

		// Build the workflow
		createQueryPerformanceEvaluationWorkflow(
				(observationRes, query) -> PerformanceAnalyzer.analyze(observationRes,
						() -> ResultSetFormatter.consume(qef.createQueryExecution(query).execSelect())),
				"http://example.org/observation/{0}-{1}", 1, 1)
		.apply(() -> workloads.stream()).get()
		.forEach(observationRes -> RDFDataMgr.write(System.out, observationRes.getModel(), RDFFormat.TURTLE_BLOCKS));

		logger.info("Done.");
	}


	public static Function<Supplier<Stream<Resource>>, Supplier<Stream<Resource>>>
	createQueryPerformanceEvaluationWorkflow(
				BiConsumer<Resource, Query> queryExecutor,
				String observationIriPattern,
				int warumUpRuns,
				int evalRuns)
	{

		return RdfStream.start()

		// Parse the work load resource's query and attach it as a trait
		.andThen(peek(workloadRes -> workloadRes.as(ResourceEnh.class)
				.addTrait(QueryFactory.create(workloadRes.getProperty(LSQ.text).getString()))))

		// Create a blank observation resource (we will give it a proper IRI later)
		// and link it back to the workload resource
		.andThen(map(workloadRes ->
				// Create the blank observation resource
				workloadRes.getModel().createResource().as(ResourceEnh.class)
				// Copy the query object attached to the workload resource over to this observation resource
				.copyTraitsFrom(workloadRes)
				// Add some properties to the observation
				.addProperty(RDF.type, QB.Observation)
				.addProperty(IguanaVocab.workload, workloadRes)
				.as(ResourceEnh.class)))

		// Measure performance of executing the query
		.andThen(peek(observationRes -> queryExecutor.accept(observationRes, observationRes.getTrait(Query.class).get())))

		// Repeat 5 times, use IV.run as a loop variable that starts with 1
		.andThen(repeat(5, IV.run, 1))

		// Give the observation resource a proper name
		.andThen(map(r -> r.rename(observationIriPattern, r.getProperty(IguanaVocab.workload).getResource().getLocalName(), IV.run)))

		;
	}

}
