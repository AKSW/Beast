package org.aksw.beast.examples;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.aksw.beast.benchmark.performance.BenchmarkTime;
import org.aksw.beast.benchmark.performance.PerformanceBenchmark;
import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.QB;
import org.aksw.iguana.vocab.IguanaVocab;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
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


//		QueryExecutionFactory qef = FluentQueryExecutionFactory
//				.http("http://dbpedia.org/sparql", "http://dbpedia.org")
//				.create();

//        BiConsumer<Resource, Query> queryAnalyzer = (observationRes, query) -> {
//            logger.debug("Processing " + observationRes + " with " + query);
//            PerformanceAnalyzer.analyze(observationRes,
//                () -> ResultSetFormatter.consume(
//                        qef.createQueryExecution(query).execSelect()));
//        };

        Model m = RDFDataMgr.loadModel("queries.ttl");

        List<Resource> workloads = m.listSubjectsWithProperty(LSQ.text).toList();

        Random rand = new Random();
        BiConsumer<Resource, Query> queryAnalyzer = (observationRes, query) -> {
            logger.debug("Faking query execution: " + observationRes + " with " + query);
            BenchmarkTime.benchmark(observationRes, () -> Thread.sleep(rand.nextInt(1000)));
        };

        Function<Resource, Query> queryParser = (workloadRes) -> QueryFactory.create(workloadRes.getProperty(LSQ.text).getString());

        // Build the workflow template
        PerformanceBenchmark.createQueryPerformanceEvaluationWorkflow(Query.class,
                queryParser, queryAnalyzer, 2, 5)
        .map(observationRes -> observationRes.rename("http://example.org/observation/{0}-{1}", IV.run, IV.job))
        // instanciate it for our  data
        .apply(() -> workloads.stream()).get()
        // write out every observation resource
        .forEach(observationRes -> RDFDataMgr.write(System.out, observationRes.getModel(), RDFFormat.TURTLE_BLOCKS));

        logger.info("Done.");
    }


}
