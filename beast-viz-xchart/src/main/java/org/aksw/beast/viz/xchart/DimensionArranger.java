package org.aksw.beast.viz.xchart;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DimensionArranger<T>
	implements Function<Set<T>, List<T>>
{
	protected Set<T> predefinedKeys;
	protected MergeStrategy<T> mergeStrategy;

	public DimensionArranger(Set<T> predefinedKeys, MergeStrategy<T> mergeStrategy) {
		super();
		this.predefinedKeys = predefinedKeys;
		this.mergeStrategy = mergeStrategy;
	}

	@Override
	public List<T> apply(Set<T> items) {
		List<T> result = mergeStrategy.apply(predefinedKeys, items);
		return result;
	}
}
