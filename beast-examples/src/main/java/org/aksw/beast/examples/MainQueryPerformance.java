package org.aksw.beast.examples;

import static org.aksw.jena_sparql_api.rdf_stream.core.RdfStream.map;
import static org.aksw.jena_sparql_api.rdf_stream.core.RdfStream.peek;
import static org.aksw.jena_sparql_api.rdf_stream.core.RdfStream.repeat;

import java.util.List;

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
import org.apache.jena.vocabulary.RDF;

public class MainQueryPerformance {
	public static void main(String[] args) {

		QueryExecutionFactory qef = FluentQueryExecutionFactory
				.http("http://dbpedia.org/sparql", "http://dbpedia.org")
				.create();

		Model m = RDFDataMgr.loadModel("queries.ttl");

		List<Resource> tasks = m.listSubjectsWithProperty(LSQ.text).toList();

		String uriPattern = "http://example.org/observation/{0}-{1}";

		RdfStream.start()
		// Parse the work load resource's query and attach it as a trait
		.andThen(peek(w -> w.as(ResourceEnh.class)
				.addTrait(QueryFactory.create(w.getProperty(LSQ.text).getString()))))

		// Create the task resource
		.andThen(map(w -> w.getModel().createResource().as(ResourceEnh.class)
				.copyTraitsFrom(w)
				.addProperty(RDF.type, QB.Observation)
				.addProperty(IguanaVocab.workload, w)))

		.andThen(peek(w -> PerformanceAnalyzer.analyze(w, () -> ResultSetFormatter.consume(
				qef.createQueryExecution(w.as(ResourceEnh.class).getTrait(Query.class).get()).execSelect()))))

//		.andThen(repeat(2, IguanaVocab.run))
		//.andThen(withIndex(IV.item))
		.andThen(repeat(5, IV.run, 1))
		.andThen(map(r -> r.as(ResourceEnh.class).rename(uriPattern, r.getProperty(IguanaVocab.workload).getResource().getLocalName(), IV.run)))
		.apply(() -> tasks.stream()).get()
		.forEach(r -> r.getModel().write(System.out, "TURTLE"));

	}
}
