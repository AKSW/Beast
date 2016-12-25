package org.aksw.beast.examples;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Resource;

/**
 * Template for a single worker that:
 * - parses workload resources
 * - creates observations for each workload
 * - benchmarks performance, describes observations with result, reports exceptions and timeouts
 * - tags time'd out workloads such that they are not retried
 *
 * Issue: how to tag the workload such that we can persist it?
 *   Essentialy we need to write an 'overlay' of the workloads, and then we write out the overlay.
 *   The question is: is there a jena diff model? maybe...
 *
 * There is Delta (Graph).
 *
 * @author raven
 *
 */
public class QueryBenchmarkDsl {
    //protected QueryExecutionFactory qef;

    public static Stream<Resource> run(Supplier<Stream<Resource>> workloads) {
        //new Delta(base)
        return null;
    }

}
