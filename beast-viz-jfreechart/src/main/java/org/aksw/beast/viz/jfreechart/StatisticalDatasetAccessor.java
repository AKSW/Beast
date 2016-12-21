package org.aksw.beast.viz.jfreechart;

import java.util.Comparator;

public interface StatisticalDatasetAccessor<T, C, S> {
    C getCategory(T observation);
    S getSeries(T observation);

    String getCategoryLabel(T observation);
    String getSeriesLabel(T observation);

    Number getValue(T observation);
    Number getStdDev(T observation);

    Comparator<C> getCategoryComparator();
    Comparator<S> getSeriesComparator();
}
