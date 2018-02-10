package org.aksw.beast.chart.accessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.aksw.beast.compare.StringPrettyComparator;
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
	implements BiFunction<Set<? extends T>, Set<? extends T>, List<T>>
{
	public static final int APPEND = 0;
	public static final int PREPEND = 1;
	public static final int MIX = 2;
	public static final int ERROR = 3;


	protected Comparator<? super T> statEleCmp;

	protected Comparator<? super T> dynEleCmp;

	/**
	 * In case the merge strategy is mixin, we need to know
	 * which comparator to use.
	 *
	 * Note: MIXIN with an absent comparator (null) falls back to APPEND
	 */
	protected Comparator<? super T> mergeEleCmp;

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

	public MergeStrategy() {
		super();
		this.statEleCmp = StringPrettyComparator::doCompare;
		this.mergeStrategy = APPEND;
	}

	@Override
	public List<T> apply(Set<? extends T> staticElements, Set<? extends T> dynamicElements) {

    	Set<? extends T> uniqDynamic = Sets.difference(dynamicElements, staticElements);
//    	Set<? extends T> uniqStatic = Sets.difference(staticElements, dynamicElements);

    	Set<? extends T> first;
    	Set<? extends T> second;

    	Comparator<? super T> firstCmp;
    	Comparator<? super T> secondCmp;


    	switch(mergeStrategy) {
    	case ERROR: {
        	if(!uniqDynamic.isEmpty()) {
        		throw new RuntimeException("Elements not covered: " + uniqDynamic);
        	}
        	// If there is no error, we fall through to MIX
    	}
    	case MIX:
    		first = Sets.union(staticElements, dynamicElements);
    		second = Collections.emptySet();
    		firstCmp = mergeEleCmp;
    		secondCmp = null;
    		break;
    	case APPEND:
    		first = staticElements;
    		second = uniqDynamic;
    		firstCmp = statEleCmp;
    		secondCmp = dynEleCmp;
    		break;
    	case PREPEND:
    		first = uniqDynamic;
    		second = staticElements;
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