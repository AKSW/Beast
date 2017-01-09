package org.aksw.beast.benchmark.performance;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.QB;
import org.aksw.iguana.vocab.IguanaVocab;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class PerformanceBenchmark {

    public static <T> RdfStream<Resource, ResourceEnh>
    createQueryPerformanceEvaluationWorkflow(
                Class<T> workloadClass,
                Function<Resource, ? extends T> workloadParser,
                BiConsumer<Resource, ? super T> workloadExecutor,
                int warmUpRuns,
                int evalRuns)
    {
        return RdfStream.startWithCopy()
                //QueryFactory.create(workloadRes.getProperty(LSQ.text).getString()))
            // Parse the work load resource's query and attach it as a tag
        	// TODO: We could check whether the workload already has such tag attached
        	
            .peek(workloadRes -> workloadRes.as(ResourceEnh.class)
                    .addTag(workloadParser.apply(workloadRes)))
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

            // Measure performance of executing the task
            .peek(observationRes -> workloadExecutor.accept(observationRes, observationRes.getTag(workloadClass).get()))
            //.map(x -> (Resource)x))
            .seq(
                // Warm up run - the resources are processed, but filtered out
                RdfStream.<ResourceEnh>start()
                	.repeat(warmUpRuns, IV.run, 1)
                    .peek(r -> r.addLiteral(IV.warmup, true))
                    .filter(r -> false),
                // Actual evaluation - repeat steps above and tag output resources as originating
                // from warm-up runs
                RdfStream.<ResourceEnh>start()
                	.repeat(evalRuns, IV.run, 1)
                	.peek(r -> r.addLiteral(IV.warmup, false))
            )

            // Give the observation resource a proper name
            .peek(r -> r.addLiteral(IV.job, r.getProperty(IguanaVocab.workload).getResource().getLocalName()))
            ;
    }
}
