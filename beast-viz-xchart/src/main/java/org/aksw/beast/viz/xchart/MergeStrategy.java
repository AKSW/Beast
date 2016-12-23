package org.aksw.beast.viz.xchart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.jena.ext.com.google.common.collect.Sets;


/**
 * Strategy for merging two sets of items into an ordered list.
 * Note that the input sets may be LinkedHashSets. Their order can
 * thus be preserved by not setting the respective comparator on this class.
 *
 * @author raven
 *
 * @param <T>
 */
public class MergeStrategy<T>
	implements BiFunction<Set<T>, Set<T>, List<T>>
{

	protected static final int APPEND = 0;
	protected static final int PREPEND = 1;
	protected static final int MIX = 2;
	protected static final int ERROR = 3;


	protected Comparator<? super T> statEleCmp;

	protected Comparator<? super T> dynEleCmp;

	/**
	 * In case the merge strategy is mixin, we need to know
	 * which comparator to use.
	 *
	 * Note: MIXIN with an absent comparator (null) falls back to APPEND
	 */
	protected Comparator<? super T> mergeEleCmp;


    /**
     * Mapping from dimension elements to their labels.
     * By default, Objects.toString() will be used
     */
    protected Function<T, String> elementToLabel = Objects::toString;

    protected int mergeStrategy = APPEND;


    public Comparator<? super T> getStatEleCmp() {
		return statEleCmp;
	}

	public MergeStrategy<T> setStatEleCmp(Comparator<? super T> statEleCmp) {
		this.statEleCmp = statEleCmp;

		return this;
	}

	public Comparator<? super T> getDynEleCmp() {
		return dynEleCmp;
	}

	public MergeStrategy<T> setDynEleCmp(Comparator<? super T> dynEleCmp) {
		this.dynEleCmp = dynEleCmp;

		return this;
	}

	public Comparator<? super T> getMergeEleCmp() {
		return mergeEleCmp;
	}

	public MergeStrategy<T> setMergeEleCmp(Comparator<? super T> mergeEleCmp) {
		this.mergeEleCmp = mergeEleCmp;

		return this;
	}

	public Function<T, String> getElementToLabel() {
		return elementToLabel;
	}

	public MergeStrategy<T> setElementToLabel(Function<T, String> elementToLabel) {
		this.elementToLabel = elementToLabel;

		return this;
	}

	public int getMergeStrategy() {
		return mergeStrategy;
	}

	public MergeStrategy<T> setMergeStrategy(int mergeStrategy) {
		this.mergeStrategy = mergeStrategy;

		return this;
	}

	public void setGeneralCmp(Comparator<? super T> cmp) {
		setStatEleCmp(cmp);
		setDynEleCmp(cmp);
		setMergeEleCmp(cmp);
	}


	@Override
	public List<T> apply(Set<T> staticElements, Set<T> dynamicElements) {

    	Set<T> uniqDynamic = Sets.difference(dynamicElements, staticElements);
    	Set<T> uniqStatic = Sets.difference(staticElements, dynamicElements);

    	Set<T> first;
    	Set<T> second;

    	Comparator<? super T> firstCmp;
    	Comparator<? super T> secondCmp;


    	switch(mergeStrategy) {
    	case ERROR: {
        	if(!uniqDynamic.isEmpty()) {
        		throw new RuntimeException("Elements not covered: " + uniqDynamic);
        	}
    	}
    	case APPEND:
    		first = uniqStatic;
    		second = uniqDynamic;
    		firstCmp = statEleCmp;
    		secondCmp = dynEleCmp;
    		break;
    	case MIX:
    		first = Sets.union(staticElements, dynamicElements);
    		second = Collections.emptySet();
    		firstCmp = mergeEleCmp;
    		secondCmp = null;
    		break;
    	case PREPEND:
    		first = uniqDynamic;
    		second = uniqStatic;
    		firstCmp = dynEleCmp;
    		secondCmp = statEleCmp;
    		break;
    	default:
    		throw new RuntimeException("No handler for merge strategy " + mergeStrategy);
    	}


    	List<T> firstList = new ArrayList<>(first);
    	if(firstCmp != null) {
    		Collections.sort(firstList, firstCmp);
    	}

    	List<T> secondList = new ArrayList<>(second);
    	if(secondCmp != null) {
    		Collections.sort(secondList, secondCmp);
    	}


    	List<T> result = new ArrayList<>(first.size() + second.size());
    	result.addAll(firstList);
    	result.addAll(secondList);

    	return result;
    }

}