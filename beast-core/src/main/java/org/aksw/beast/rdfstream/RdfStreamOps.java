package org.aksw.beast.rdfstream;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.aksw.beast.enhanced.ResourceData;
import org.aksw.beast.enhanced.ResourceEnh;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * ss = stream supplier - a Supplier&gt;Stream&gt;T&lt;&lt; with T extends Resource
 *
 * @author raven
 *
 */
public class RdfStreamOps {

    public static <X> Supplier<Stream<ResourceData<X>>> zipWithResource(Supplier<Stream<X>> dataStream, Supplier<Function<X, ResourceData<X>>> itemToNode) {
        return () -> dataStream.get().map(itemToNode.get());
    }

//    public static <X> Supplier<Stream<ResourceData<X>>> zipWithResource(Supplier<Stream<X>> dataStream) {
//        return () -> dataStream.get().map(data -> {
//            ModelFactory.createDefaultModel().createResource();
//        });
//    }


    public static <T extends Resource> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        start()
    {
        return (ss) -> (() -> ss.get());
    }

//	public static <T extends Resource> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
//		start(Class<T> clazz)
//	{
//		return (ss) -> (() -> ss.get());
//	}


    /**
     * starts with copyResourceClosureIntoModelEnh
     *
     * For each item in the stream, map its closure to a new resource
     * @param stream
     * @param p
     * @return
     */
    public static <T extends Resource> Function<Supplier<Stream<T>>, Supplier<Stream<ResourceEnh>>>
        startWithCopy()
    {
        //return (ss) -> (() -> ss.get().map(RdfStreamOps::copyResourceClosureIntoModelEnh));
        return RdfStreamOps.<T>start().andThen(ObjStreamOps.map(ResourceEnh::copyClosure));
    }


    public static <T extends Resource> Supplier<Stream<T>>
        withIndex(Supplier<Stream<T>> stream, Property p)
    {
        int i = 0;

        return () -> stream.get().peek(r -> r.addLiteral(p, new Integer(i)));
    }


    /**
     * repeat repeats the provided supplied n times and to each item
     * adds a property indicating the repetation
     *
     * note that this is different from withIndex:
     * within a repetation, the repetation count stays the same for each item, whereas the index is increased.
     *
     * @param n
     * @param property
     * @return
     */
    public static <T extends Resource> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        repeat(int n, Property property, int offset)
    {
        return (ss) -> (() -> LongStream.range(0, n).boxed()
                .flatMap(i -> ss.get().peek(r -> r.addLiteral(property, offset + i))));
    }


    public static <T extends Resource> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        repeat(int n)
    {
        return (ss) -> (() -> LongStream.range(0, n).boxed()
            .flatMap(i -> ss.get()));
    }

    public static <T extends Resource, O> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        repeatForLiterals(Property p, Stream<O> os)
    {
        return (ss) -> (() -> os.flatMap(o -> ss.get().peek(r -> r.addLiteral(p, o))));
    }




//	public static <T extends Resource, O> Function<Supplier<Stream<T>>, Supplier<Stream<O>>>
//		seq(Iterable<Function<Supplier<Stream<T>>, Supplier<Stream<O>>>> subFlows)
//	{
//		return (ss) -> (() -> StreamUtils.stream(subFlows).flatMap(subFlow -> subFlow.apply(ss).get()));
//	}

    /**
     * withIndex creates a new stream, with each resource having an incremented value for the given property.
     *
     * @param p
     * @return
     */
    public static <T extends Resource> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        withIndex(Property p)
    {
        return (ss) -> (() -> {
            int i[] = {0};
            return ss.get().peek(r -> r.addLiteral(p, new Integer(++i[0])));
        });
    }

}
