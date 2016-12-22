package org.aksw.beast.benchmark.performance;

import java.util.function.BiConsumer;

import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.QB;
import org.aksw.iguana.vocab.IguanaVocab;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class PerformanceBenchmark {

    public static RdfStream<Resource, ResourceEnh>
    createQueryPerformanceEvaluationWorkflow(
                BiConsumer<Resource, Query> queryExecutor,
                int warmUpRuns,
                int evalRuns)
    {
        return RdfStream.startWithCopy()

            // Parse the work load resource's query and attach it as a tag
            .peek(workloadRes -> workloadRes.as(ResourceEnh.class)
                    .addTag(QueryFactory.create(workloadRes.getProperty(LSQ.text).getString())))
            // Create a blank observation resource (we will give it a proper IRI later)
            // and link it back to the workload resource
            .map(workloadRes ->
                    // Create the blank observation resource
                    workloadRes.getModel().createResource().as(ResourceEnh.class)
                    // Copy the query object attached to the workload resource over to this observation resource
                    .copyTagsFrom(workloadRes)
                    // Add some properties to the observation
                    .addProperty(RDF.type, QB.Observation)
                    .addProperty(IguanaVocab.workload, workloadRes)
                    .as(ResourceEnh.class))

            // Measure performance of executing the query
            .peek(observationRes -> queryExecutor.accept(observationRes, observationRes.getTag(Query.class).get()))
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
