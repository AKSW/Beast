package org.aksw.beast.examples;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import org.aksw.beast.benchmark.performance.BenchmarkTime;
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

        // Build the workflow template
        createQueryPerformanceEvaluationWorkflow(
                queryAnalyzer, 2, 5)
        .map(observationRes -> observationRes.rename("http://example.org/observation/{0}-{1}", IV.run, IV.job))
        // instanciate it for our  data
        .apply(() -> workloads.stream()).get()
        // write out every observation resource
        .forEach(observationRes -> RDFDataMgr.write(System.out, observationRes.getModel(), RDFFormat.TURTLE_BLOCKS));

        logger.info("Done.");
    }


    public static RdfStream<Resource, ResourceEnh>
    createQueryPerformanceEvaluationWorkflow(
                BiConsumer<Resource, Query> queryExecutor,
                int warmUpRuns,
                int evalRuns)
    {
        return RdfStream.startWithCopy()

            // Parse the work load resource's query and attach it as a trait
            .peek(workloadRes -> workloadRes.as(ResourceEnh.class)
                    .addTrait(QueryFactory.create(workloadRes.getProperty(LSQ.text).getString())))

            // Create a blank observation resource (we will give it a proper IRI later)
            // and link it back to the workload resource
            .map(workloadRes ->
                    // Create the blank observation resource
                    workloadRes.getModel().createResource().as(ResourceEnh.class)
                    // Copy the query object attached to the workload resource over to this observation resource
                    .copyTraitsFrom(workloadRes)
                    // Add some properties to the observation
                    .addProperty(RDF.type, QB.Observation)
                    .addProperty(IguanaVocab.workload, workloadRes)
                    .as(ResourceEnh.class))

            // Measure performance of executing the query
            .peek(observationRes -> queryExecutor.accept(observationRes, observationRes.getTrait(Query.class).get()))
            //.map(x -> (Resource)x))
            .seq(
                // Warm up run - the resources are processed, but filtered out
                RdfStream.<ResourceEnh>start().repeat(warmUpRuns, IV.run, 1)
                    .peek(r -> r.addLiteral(IV.warmup, true))
                    .filter(r -> false),
                // Actual evaluation
                RdfStream.<ResourceEnh>start().repeat(evalRuns, IV.run, 1).peek(r -> r.addLiteral(IV.warmup, false))
            )

            // Give the observation resource a proper name
            .peek(r -> r.addLiteral(IV.job, r.getProperty(IguanaVocab.workload).getResource().getLocalName()))
            ;
    }

}
