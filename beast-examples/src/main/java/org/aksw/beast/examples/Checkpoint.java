package org.aksw.beast.examples;

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Resource;

/**
 * Some considerations about how caching/persisting/checkpointing could work.
 *
 * Caching:
 * The purpose of persist is to short-cut function evaluation
 * out = fn(in)
 *
 * The problem is, that (in) is actually context dependent, as the same IRI resource may have different
 * (context) attributes. In the case of blank nodes, we may rely on skolemization for obtaining ids; maybe
 * we can use this approach to re-assign IRIs to IRI resources based on context?
 * Context attributes could be e.g. loop information.
 *
 *
 * for example, if a benchmarking operation was already performed, we do not need to perform it again
 *
 * in this regard, persist would be a wrapper for fn:
 * fn' = cache(fn, stepId)
 *
 * So we could actually extend all operations in the workflow with an ID attribute,
 * and we could allocate step IDs automatically in the stream contruction
 *
 * So actually we are seeing spring batch's model here:
 * The workflow itself has the jobId, and each step in it a stepId.
 *
 *
 *
 *
 *
 * Checkpoints:
 * A checkpoint enables continuation of a workflow at a specific point after interruption.
 *
 * workflow // job
 *   .map() // step
 *   .checkpoint()
 *
 * a checkpoint has two options:
 * (a) instanciate the supplied stream or (b) instanciate another stream by reading from a checkpoint file
 *
 * for this to work, we need metadata in order to derive an id about the supplied stream, which includes
 * - which step in which job with which jobInstance (input) does the supplied stream correspond to
 *
 * a job can be parameterized
 * jobInstance = job.apply(staticConfig);
 * job = jobInstance.apply(resourceStream);
 * job.count(); // invoke some terminal operation in order to execute the pipeline
 *
 *
 *
 * A checkpoint could first collect all (models of) resources of the supplied stream
 * and persist them.
 * However, for this to work we would need to create an identifier in order to decide whether
 *
 *
 * .map(...)
 * .persist(...) // short hand; maybe perist is too specific
 * .andThen(persist)
 *
 *
 *
 * @author raven
 *
 * @param <I>
 * @param <O>
 */
public class Checkpoint<I extends Resource, O extends Resource>
	implements Function<Stream<I>, Supplier<Stream<O>>>
{
	//protected boolean
	protected File file;

	@Override
	public Supplier<Stream<O>> apply(Stream<I> t) {
		if(file.exists()) {

		} else {

		}
		return null;
	}

}
