package org.aksw.beast.rdfstream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ObjStreamOps {
    public static <I, O> Function<Supplier<Stream<I>>, Supplier<Stream<O>>>
        map(Function<I, O> fn)
    {
        return (ss) -> (() -> ss.get().map(fn));
    }

    public static <I, O> Function<Supplier<Stream<I>>, Supplier<Stream<O>>>
        flatMap(Function<I, Stream<O>> fn)
    {
        return (ss) -> (() -> ss.get().flatMap(fn));
    }


    public static <T> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        peek(Consumer<T> consumer)
    {
        return (ss) -> (() -> ss.get().peek(consumer));
    }

    public static <T> Function<Supplier<Stream<T>>, Supplier<Stream<T>>>
        filter(Predicate<T> predicate)
    {
        return (ss) -> (() -> ss.get().filter(predicate));
    }


    /**
     * Sequentially perform sub-flows
     * by passing a given streamSupplier to each subFlow in sequence.
     *
     * Useful e.g. for warm-up runs
     *
     * .seq(
     *   start().setAttr(r -> r.setProperty(WARMUP, true).run(...).repeat(...), // first flow
     *   execMeasurement(...).repeat(...), // second flow
     * ).filter(r.getProperty(WARMUP) == null)
     * ...
     *
     *
     *
     *
     *
     * @param subFlows
     * @return
     */
    //@SafeVarargs
    //public static <T extends Resource, O> Function<Supplier<Stream<T>>, Supplier<Stream<O>>>
    //	seq(Function<Supplier<Stream<T>>, Supplier<Stream<O>>> ... subFlows)
    //{
    //	return (ss) -> (() -> Stream.of(subFlows).flatMap(subFlow -> subFlow.apply(ss).get()));
    //}

    @SafeVarargs
    public static <I, O>
    Function<Supplier<Stream<I>>, Supplier<Stream<O>>>
        seq(Function<Supplier<Stream<I>>, Supplier<Stream<O>>> ... subFlows)
    {
        return (ss) -> (() -> Stream.of(subFlows).flatMap(subFlow -> subFlow.apply(ss).get()));
    }
}
