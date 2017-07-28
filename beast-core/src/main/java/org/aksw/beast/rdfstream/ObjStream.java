package org.aksw.beast.rdfstream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.jena.rdf.model.Resource;

// I extends Resource
public class ObjStream<I, O>
    implements Function<Supplier<Stream<I>>, Supplier<Stream<O>>>
{
    protected Function<Supplier<Stream<I>>, Supplier<Stream<O>>> current;

    public ObjStream(Function<Supplier<Stream<I>>, Supplier<Stream<O>>> current) {
        super();
        this.current = current;
    }

    public <Y> ObjStream<I, Y>
        map(Function<O, Y> fn)
    {
        return new ObjStream<I, Y>(this.current.andThen(ObjStreamOps.map(fn)));
    }

    public <Y extends Resource> RdfStream<I, Y>
        mapToRdf(Function<O, Y> fn)
    {
        return new RdfStream<I, Y>(this.current.andThen(ObjStreamOps.map(fn)));
    }


    public <Y> ObjStream<I, Y>
        flatMap(Function<O, Stream<Y>> fn)
    {
        return new ObjStream<I, Y>(this.current.andThen(ObjStreamOps.flatMap(fn)));
    }


    public ObjStream<I, O>
        filter(Predicate<O> predicate)
    {
        return new ObjStream<I, O>(this.current.andThen(ObjStreamOps.filter(predicate)));
    }

    public ObjStream<I, O>
        peek(Consumer<O> action)
    {
        return new ObjStream<I, O>(this.current.andThen(ObjStreamOps.peek(action)));
    }

    //@SafeVarargs
    @SuppressWarnings("unchecked")
    public <Y> ObjStream<I, Y>
        seq(Function<Supplier<Stream<O>>, Supplier<Stream<Y>>> ... subFlows)
    {
        return new ObjStream<I, Y>(this.current.andThen(ObjStreamOps.seq(subFlows)));
    }




    //@Override
    public Function<Supplier<Stream<I>>, Supplier<Stream<O>>> get() {
        return current;
    }



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
