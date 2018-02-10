package org.aksw.beast.viz.xchart;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface ModularAttributeAccessor<R, T> {
	Function<? super R, T> getValueAccessor();
	Function<? super T, ?> getLabelAcessor();
	Function<Set<? extends R>, List<R>> getValueArranger();
}