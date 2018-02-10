package org.aksw.beast.viz.xchart;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DimensionArranger<T>
	implements Function<Set<? extends T>, List<T>>
{
	protected Set<T> predefinedKeys;
	protected BiFunction<Set<? extends T>, Set<? extends T>, List<T>> mergeStrategy;

	public DimensionArranger() {
		this(new LinkedHashSet<>(), new MergeStrategy<>());
	}

	public DimensionArranger(BiFunction<Set<? extends T>, Set<? extends T>, List<T>> mergeStrategy) {
		this(new LinkedHashSet<>(), mergeStrategy);
	}

	public DimensionArranger(Set<T> predefinedKeys, BiFunction<Set<? extends T>, Set<? extends T>, List<T>> mergeStrategy) {
		super();
		this.predefinedKeys = predefinedKeys;
		this.mergeStrategy = mergeStrategy;
	}

	public Set<T> getPredefinedKeys() {
		return predefinedKeys;
	}

	public void setPredefinedKeys(Set<T> predefinedKeys) {
		this.predefinedKeys = predefinedKeys;
	}

	public BiFunction<Set<? extends T>, Set<? extends T>, List<T>> getMergeStrategy() {
		return mergeStrategy;
	}

	public void setMergeStrategy(MergeStrategy<T> mergeStrategy) {
		this.mergeStrategy = mergeStrategy;
	}

	@Override
	public List<T> apply(Set<? extends T> items) {
		List<T> result = mergeStrategy.apply(predefinedKeys, items);
		return result;
	}
}
