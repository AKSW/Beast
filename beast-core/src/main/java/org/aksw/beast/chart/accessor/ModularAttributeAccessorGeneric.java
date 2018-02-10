package org.aksw.beast.chart.accessor;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

class ModularAttributeAccessorGeneric<R, T>
	extends ModularAttributeAccessorBase<R, T>
{
	public void setValueAccessor(Function<? super R, T> valueAccessor) {
		this.valueAccessor = valueAccessor;
	}

	public void setLabelAcessor(Function<? super T, ?> labelAcessor) {
		this.labelAccessor = labelAcessor;
	}

	public void setValueArranger(Function<Set<? extends T>, List<T>> valueArranger) {
		this.valueArranger = valueArranger;
	}	
}