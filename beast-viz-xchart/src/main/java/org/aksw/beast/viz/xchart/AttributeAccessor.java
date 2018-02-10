package org.aksw.beast.viz.xchart;

import java.util.List;
import java.util.Set;

public interface AttributeAccessor<R, T> {
	T getValue(R item);
	Object getLabel(T value);
	List<T> arrange(Set<? extends T> values);
}