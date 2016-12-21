package org.aksw.beast.viz.jfreechart;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.aksw.beast.compare.StringPrettyComparator;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

public class StatisticalDatasetAccessorModular<T, C, S>
    implements StatisticalDatasetAccessor<T, C, S>
{
    protected Comparator<C> categoryComparator = (a, b) -> StringPrettyComparator.doCompare(Objects.toString(a), Objects.toString(b));
    protected Comparator<S> seriesComparator = (a, b) -> StringPrettyComparator.doCompare(Objects.toString(a), Objects.toString(b));

    protected Function<T, C> categoryAccessor;
    protected Function<T, S> seriesAccessor;

    // Note: The label of the category may be an attribute of the observation
    // hence the access via T
    protected Function<T, String> categoryLabelAccessor = Objects::toString;
    protected Function<T, String> seriesLabelAccessor = Objects::toString;

    protected Function<T, ? extends Number> valueAccessor;
    protected Function<T, ? extends Number> stdDevAccessor;

    protected Supplier<DefaultStatisticalCategoryDataset> datasetSupplier = DefaultStatisticalCategoryDataset::new;

    public Comparator<C> getCategoryComparator() {
        return categoryComparator;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setCategoryComparator(Comparator<C> categoryComparator) {
        this.categoryComparator = categoryComparator;
        return this;
    }

    public Comparator<S> getSeriesComparator() {
        return seriesComparator;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setSeriesComparator(Comparator<S> seriesComparator) {
        this.seriesComparator = seriesComparator;
        return this;
    }

    public Function<T, C> getCategoryAccessor() {
        return categoryAccessor;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setCategoryAccessor(Function<T, C> categoryAccessor) {
        this.categoryAccessor = categoryAccessor;
        return this;
    }

    public Function<T, S> getSeriesAccessor() {
        return seriesAccessor;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setSeriesAccessor(Function<T, S> seriesAccessor) {
        this.seriesAccessor = seriesAccessor;
        return this;
    }

    public Function<T, String> getCategoryLabelAccessor() {
        return categoryLabelAccessor;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setCategoryLabelAccessor(
            Function<T, String> categoryLabelAccessor) {
        this.categoryLabelAccessor = categoryLabelAccessor;
        return this;
    }

    public Function<T, String> getSeriesLabelAccessor() {
        return seriesLabelAccessor;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setSeriesLabelAccessor(Function<T, String> seriesLabelAccessor) {
        this.seriesLabelAccessor = seriesLabelAccessor;
        return this;
    }

    public Function<T, ? extends Number> getValueAccessor() {
        return valueAccessor;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setValueAccessor(Function<T, ? extends Number> valueAccessor) {
        this.valueAccessor = valueAccessor;
        return this;
    }

    public Function<T, ? extends Number> getStdDevAccessor() {
        return stdDevAccessor;
    }

    public StatisticalDatasetAccessorModular<T, C, S> setStdDevAccessor(Function<T, ? extends Number> stdDevAccessor) {
        this.stdDevAccessor = stdDevAccessor;
        return this;
    }



    @Override
    public C getCategory(T observation) {
        C result = categoryAccessor.apply(observation);
        return result;
    }

    @Override
    public S getSeries(T observation) {
        S result = seriesAccessor.apply(observation);
        return result;
    }

    @Override
    public String getCategoryLabel(T observation) {
        String result = categoryLabelAccessor.apply(observation);
        return result;
    }

    @Override
    public String getSeriesLabel(T observation) {
        String result = seriesLabelAccessor.apply(observation);
        return result;
    }

    @Override
    public Number getValue(T observation) {
        Number result = valueAccessor.apply(observation);
        return result;
    }

    @Override
    public Number getStdDev(T observation) {
        Number result = stdDevAccessor.apply(observation);
        return result;
    }


}
