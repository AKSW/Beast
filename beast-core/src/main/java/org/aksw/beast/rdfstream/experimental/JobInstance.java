package org.aksw.beast.rdfstream.experimental;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 *
 * job = RdfJob.start("http://createSalesReport")
 * jobInstance = job.apply("salesForJanuary", staticConfig);
 * job = jobInstance.apply(resourceStream);
 * job.count(); // invoke some terminal operation in order to execute the pipeline
 */
public interface JobInstance<I, O, S>
	extends BiFunction<String, Supplier<Stream<I>>, Stream<O>>
{

//	@Override
//	public Stream<T> apply(Supplier<Stream<T>> t) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
