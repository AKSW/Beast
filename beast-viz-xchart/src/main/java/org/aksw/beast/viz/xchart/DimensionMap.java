package org.aksw.beast.viz.xchart;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ForwardingMap;

/**
 * A map whose entries are arranged by a given a function.
 * The arrange function may return additional keys to those passed in as arguments.
 *
 *
 * @author raven
 *
 * @param <K>
 * @param <V>
 */
public class DimensionMap<K, V>
	extends ForwardingMap<K, V>
{
	protected Map<K, V> delegate;
	protected Function<Set<? super K>, List<K>> arranger;


	public DimensionMap(Map<K, V> delegate, Function<Set<? super K>, List<K>> arranger) {
		this.delegate = delegate;
		this.arranger = arranger;
	}

	@Override
	protected Map<K, V> delegate() {
		return delegate;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<K> keySet = delegate.keySet();
		List<K> order = arranger.apply(keySet);

		Set<Entry<K, V>> result = order.stream()
			.map(k -> new SimpleEntry<>(k, delegate.get(k)))
			.collect(Collectors.toCollection(LinkedHashSet::new));

		return result;
	}
}
