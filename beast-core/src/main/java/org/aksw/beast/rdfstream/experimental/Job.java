package org.aksw.beast.rdfstream.experimental;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.aksw.beast.vocabs.IV;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * This file only contains a collection of ideas.
 *
 * So the question is: what is necessary to make parts of an RDF data processing pipeline
 * for benchmarking restartable?
 * This also includes re-running warm-up runs before resuming with the actual benchmarking tasks.
 *
 *
 * .makeRestartable(
 *   initWorkflow(), // execute on each restart; output is discarded
 *   resumableWorkflow() // execute and emit data
 * )
 *
 *
 *
 *
 *
 * A StreamJob is essentially a job that maps a stream of items of type I to a stream of items of type O.
 * I.e. it is a job that applies a function on a sequence of items.
 *
 * The goal of this exercise is to investigate, how difficult it would be to mimic spring-batch / spark
 * like features in regard to caching and persitance.
 *
 * The advantage of the RDF stream approach is, that the pipeline can annotate the resources flowing
 * throw it (e.g. .repeat() adding the loop count to the item)
 *
 * Differences between spring batch job and data processing pipeline
 * - batch steps are not required to yield a result; a result may be placed into step or job execution context
 *
 *
 *
 * There are three options for caching/persisting intermediate results in streams:
 * - Make a checkpoint once a stream was processed.
 *   - This can happen by first consuming the stream, writing data to disk, and only when that is
 *     complete, the items are passed on again (possibly by reading from disk)
 * - Cache for each item under a given context its output.
 *     Thereby, when resuming, we could run prior computations from cache
 * - Resume processing directly from a point in the past (e.g. line 1000 in a file)
 *     This requires some kind of seeking, such as byte position in a file, or index in a list
 *
 * The last point would in spring-batch be accomplished with e.g. a FlatFileItemReader.
 * But I would like to avoid reinventing the wheel and rather provide a mechanism to reuse existing
 * components.
 * Essentially the solution to the problem is, that the steps need to be able to initialize themselves from
 * given context information (job/step/jobExecution/stepExecution).
 *
 * Once the input stream to a pipeline is consumed, the steps need to be closed in order.
 *
 *
 *
 *
 *
 * Use case: Resume operation: When restarting an interrupted benchmark, perform the warmup runs again.
 *
 *
 * job = RdfJob.start("http://createSalesReport")
 * jobInstance = job.apply("salesForJanuary", staticConfig);
 * job = jobInstance.apply(resourceStream);
 * job.count(); // invoke some terminal operation in order to execute the pipeline
 *
 * The main difference would be, that in each map operation it would be possible to access a context object
 * But we could still make shorthands so that works
 *
 * job.map((i, c) -> { })
 * job.map(i -> { })
 *
 *
 * Steps provide access to the stepContext.
 * The jobContext should then be made available via this context.
 *
 * The job provides a factory for step context, where this attribute can be set.
 *
 * @author raven
 *
 * @param <I> The input item type of the current step in the job
 * @param <O> The output item type of the current step in the job
 * @param <J> The job context type
 * @param <S> The step context type
 */
public class Job<I, O, J, S>
    implements BiFunction<String, J, JobInstance<I, O, S>>
{
    protected String jobName;

    protected int nextStepId = 0;

    protected String lastStep = null;

    protected BiFunction<String, StepContext<J, S>, JobInstance<I, O, S>> current;


    protected BiFunction<J, Void, S> stepContextFactory;;

    //protected Function<Supplier<Stream<I>>, Supplier<Stream<O>>

    public static <I, O, J, S> Job<I, O, J, S> start(String jobId) {

//		return new Job<>();
        return null;
    }

    public Job(int nextStepId, String lastStep) {
        super();
        this.nextStepId = nextStepId;
        this.lastStep = lastStep;
        this.current = current;
    }


    public static Resource getOrCreateStepExecCtx(Resource jobExecCtx, String stepName) {
        return null;
    }

    public static void initProperty(Resource stepExecCtx, Property property, Object val) {
        //return null;
    }

    public static long getOrSetLong(Resource stepExecCtx, Property property, Object val) {
        //return null;
        return 0;
    }

    /**
     * Create a repetition step
     *
     *
     *
     *
     * Will getOrCreate a stepExecutionCntext from the jobExecutionContext
     * and add the currentIteration and maxIteration to it.
     *
     * @param stepName
     */
    public void repeat(String stepName, int n) {

        Function<Resource, Object> result = (jobExecCtx) -> {
            Resource stepExecCtx = getOrCreateStepExecCtx(jobExecCtx, stepName);

            long offset = getOrSetLong(stepExecCtx, IV.phase, 0); // currentIteration
            long maxIt = getOrSetLong(stepExecCtx, IV.run, n); // maxIteration


            Function<Supplier<Stream<ItemContext<I, Resource>>>, Supplier<Stream<ItemContext<I, Resource>>>> r =
             (ss) -> (() -> LongStream.range(offset, maxIt).boxed()
                    .flatMap(i -> {
                        // Update step execution
                        stepExecCtx.removeAll(IV.phase).addLiteral(IV.phase, i);

                        // Map incoming items to the new step context
                        return ss.get().map(e -> new ItemContext<I, Resource>(e.getItem(), stepExecCtx));
                        //.peek(r -> r.addLiteral(property, offset + i))));
                    }));



            return null;
        };

        //return result;
    }

    /**
     * Store the result for a given item and its context.
     *
     *
     * out = fn(in)
     *
     */
    //public void cache()


    /**
     * Add a mapping step to the job
     *
     * @param stepName
     * @param fn
     * @return
     */
    public <U> Job<I, O, J, S> map(String stepName, BiFunction<I, J, O> fn) {
        lastStep = stepName;


        //BiFunction<String, C, JobInstance<T>> next = (ji, c) -> ((stepName, c) -> s);

        //return new Job<>(current.andThen(next));
        return null;
    }

    /**
     * Skip parent step execution if it can be read from a checkpoint file
     *
     * A step execution is completed when all items in the stream have been processed.
     *
     *
     * @return
     */
    public <U> Job<I, O, J, S> checkpoint() {
        // Return a job instance function which based on
        // (i) jobName, (ii) most recent stepName, (iii) jobInstanceName
        // performs a lookup of whether for an item there is a cache entry

        BiFunction<String, StepContext<J, S>, JobInstance<I, O, S>> stepFactory =
                (stepName, stepContext) ->
                    (instanceName, inSupplier) -> {
                        // if checkpoint entry for (job, step, jobContext, stepContext)
                        // else, execute the provided stream
                        inSupplier.get();
                        System.out.println(String.join(", ", jobName, stepName, instanceName));

                        return current.apply(stepName, stepContext).apply(instanceName, inSupplier);
                    };

                    return null;
    }

    @Override
    public JobInstance<I, O, S> apply(String t, J u) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Performs item based caching, i.e.
     * for a given input item.
     *
     * Note: equivalent items must not occur within the same context
     *
     * @return
     */



//	public <U> Job<I, O, J, S> map(String stepName, Function<T, U> fn) {
//		return map(stepName, (t, u) -> fn.apply(t));
//	}
//
//
//	public <U> Job<I, O, J, S> map(Function<T, U> fn) {
//		return map("step" + (++nextStepId), fn);
//	}
//
//	public <U> Job<I, O, J, S> map(BiFunction<T, U> fn) {
//		return map("step" + (++nextStepId), fn);
//	}

//	@Override
//	public JobInstance<I, O, S> apply(String t, C u) {
//		// TODO Auto-generated method stub
//		return null;
//	}


}
