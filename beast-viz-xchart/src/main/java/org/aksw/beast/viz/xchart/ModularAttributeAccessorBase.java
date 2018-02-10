package org.aksw.beast.viz.xchart;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.aksw.beast.compare.StringPrettyComparator;

public abstract class ModularAttributeAccessorBase<R, T>
	implements AttributeAccessor<R, T>
{
	protected Function<? super R, T> valueAccessor;
	protected Function<? super T, ?> labelAccessor;
	
	protected Function<Set<? extends T>, List<T>> valueArranger;

	public Function<? super R, T> getValueAccessor() {
		return valueAccessor;
	}

	public Function<? super T, ?> getLabelAcessor() {
		return labelAccessor;
	}

	public Function<Set<? extends T>, List<T>> getValueArranger() {
		return valueArranger;
	}

	@Override
	public T getValue(R item) {
		T result = valueAccessor.apply(item);
		return result;
	}
	
	@Override
	public Object getLabel(T value) {
		Function<? super T, ?> accessor = labelAccessor == null ? Objects::toString : labelAccessor;

		Object result = accessor.apply(value);
		return result;
	}

	@Override
	public List<T> arrange(Set<? extends T> values) {
		Function<Set<? extends T>, List<T>> arranger;
		
		if(valueArranger == null) {
		    MergeStrategy<T> ms = new MergeStrategy<>();
		    ms.setMergeEleCmp(StringPrettyComparator::doCompare);
		    ms.setMergeStrategy(MergeStrategy.MIX);
		    DimensionArranger<T> arr = new DimensionArranger<>(ms);
		    arranger = arr;
		} else {
			arranger = valueArranger;
		}

		List<T> result = arranger.apply(values);
		return result;
	      //xArranger = xArranger != null ? xArranger : arr;
	}
}