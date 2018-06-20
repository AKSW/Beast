package org.aksw.beast.rdfstream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.aksw.beast.enhanced.ResourceData;
import org.aksw.beast.enhanced.ResourceEnh;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class RdfStream<I, O extends Resource>
    implements Function<Supplier<Stream<I>>, Supplier<Stream<O>>>
//    extends ObjStream<I, O>
{
    protected Function<Supplier<Stream<I>>, Supplier<Stream<O>>> current;

    public RdfStream(Function<Supplier<Stream<I>>, Supplier<Stream<O>>> current) {
        super();
        this.current = current;
    }


    public static <T extends Resource> RdfStream<T, T>
        start()
    {
        return new RdfStream<T, T>(RdfStreamOps.<T>start());
    }

    /**
     * Convenience function which starts are stream builder that of each resource
     * creates a copy of its closure in an enhanced model
     * @return
     */
    public static <T extends Resource> RdfStream<T, ResourceEnh>
        startWithCopy()
    {
        return new RdfStream<T, ResourceEnh>(RdfStreamOps.<T>startWithCopy());
    }


    public static <X> RdfStream<X, ResourceData<X>> zipWithResource(Supplier<Function<X, ResourceData<X>>> itemToNode) {
        return new RdfStream<>((dataStream) -> RdfStreamOps.zipWithResource(dataStream, itemToNode));
    }

//    public static <X> RdfStream<X, ResourceData<X>> zipWithResource() {
//        return new RdfStream<>((dataStream) -> RdfStreamOps.zipWithResource());
//    }

    public RdfStream<I, O>
        withIndex(Property property)
    {
        return new RdfStream<I, O>(this.current.andThen(RdfStreamOps.withIndex(property)));
    }

    public RdfStream<I, O>
        repeat(int n)
    {
        return new RdfStream<I, O>(this.current.andThen(RdfStreamOps.repeat(n)));
    }

    public <X> RdfStream<I, O>
        repeatForLiterals(Property p, Stream<X> os)
    {
        return new RdfStream<I, O>(this.current.andThen(RdfStreamOps.repeatForLiterals(p, os)));
    }

    public RdfStream<I, O>
        repeat(int n, Property property, int offset)
    {
        return new RdfStream<I, O>(this.current.andThen(RdfStreamOps.repeat(n, property, offset)));
    }




    //@Override
    public <Y extends Resource> RdfStream<I, Y>
        map(Function<O, Y> fn)
    {
        return new RdfStream<I, Y>(this.current.andThen(ObjStreamOps.map(fn)));
    }

    //@Override
    public <Y> ObjStream<I, Y>
        mapToObj(Function<O, Y> fn)
    {
        return new ObjStream<I, Y>(this.current.andThen(ObjStreamOps.map(fn)));
    }


    //@Override
    public <Y extends Resource> RdfStream<I, Y>
        flatMap(Function<O, Stream<Y>> fn)
    {
        return new RdfStream<I, Y>(this.current.andThen(ObjStreamOps.flatMap(fn)));
    }

    public RdfStream<I, O>
        filter(Predicate<O> predicate)
    {
        return new RdfStream<I, O>(this.current.andThen(ObjStreamOps.filter(predicate)));
    }

    public RdfStream<I, O>
        peek(Consumer<O> action)
    {
        return new RdfStream<I, O>(this.current.andThen(ObjStreamOps.peek(action)));
    }

    @SuppressWarnings("unchecked")
    public <Y extends Resource> RdfStream<I, Y>
        seq(Function<Supplier<Stream<O>>, Supplier<Stream<Y>>> ... subFlows)
    {
        return new RdfStream<I, Y>(this.current.andThen(ObjStreamOps.seq(subFlows)));
    }





    //@Override
    public Function<Supplier<Stream<I>>, Supplier<Stream<O>>> get() {
        return current;
    }



    /**
     * Shorthand for .get().apply(...)
     */
    @Override
    public Supplier<Stream<O>> apply(Supplier<Stream<I>> t) {
        return current.apply(t);
    }

    public Supplier<Stream<O>> apply(Iterable<I> c) {
        return current.apply(() -> StreamSupport.stream(c.spliterator(), false));
    }

    public Stream<O> transform(Supplier<Stream<I>> t) {
        return apply(t).get();
    }

    public Stream<O> transform(Iterable<I> c) {
        return apply(c).get();
    }
}
