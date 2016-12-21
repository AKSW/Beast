package org.aksw.beast.viz.jfreechart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;


/**
 *
 * @author raven
 *
 * @param <T>
 * @param <C> category type
 * @param <S> series type
 */
public class StatisticalCategoryDatasetBuilder<T, C extends Comparable<C>, S extends Comparable<S>>
    implements Function<Stream<T>, StatisticalCategoryDataset>
{
    protected StatisticalDatasetAccessor<T, C, S> accessor;
    protected Supplier<DefaultStatisticalCategoryDataset> datasetSupplier;

    public StatisticalCategoryDatasetBuilder(
            StatisticalDatasetAccessor<T, C, S> accessor,
            Supplier<DefaultStatisticalCategoryDataset> datasetSupplier) {
        super();

        Objects.requireNonNull(accessor);
        Objects.requireNonNull(datasetSupplier);

        this.accessor = accessor;
        this.datasetSupplier = datasetSupplier;
    }

    public static <T, C extends Comparable<C>, S extends Comparable<S>> StatisticalCategoryDatasetBuilder<T, C, S>
        create(StatisticalDatasetAccessor<T, C, S> accessor)
    {
        return new StatisticalCategoryDatasetBuilder<>(accessor, DefaultStatisticalCategoryDataset::new);
    }


//    public Supplier<DefaultStatisticalCategoryDataset> getDatasetSupplier() {
//        return datasetSupplier;
//    }
//
//    public StatisticalCategoryDatasetBuilder<T, C, S> setDatasetSupplier(
//            Supplier<DefaultStatisticalCategoryDataset> datasetSupplier) {
//        this.datasetSupplier = datasetSupplier;
//        return this;
//    }

    @Override
    public StatisticalCategoryDataset apply(Stream<T> observations) {

        Set<S> seenSeriesIds = new TreeSet<>(accessor.getSeriesComparator());
        Set<C> seenCategoryIds = new TreeSet<>(accessor.getCategoryComparator());

        Map<List<Object>, List<Number>> keyToValue = new HashMap<>();

        observations.forEach(observation -> {

            S seriesId = accessor.getSeries(observation);
            C categoryId = accessor.getCategory(observation);

            Number value = accessor.getValue(observation);
            Number stdDev = accessor.getStdDev(observation);

            seenSeriesIds.add(seriesId);
            seenCategoryIds.add(categoryId);

            keyToValue.put(Arrays.asList(seriesId, categoryId), Arrays.asList(value, stdDev));
        });

        DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
        for(Comparable<?> seriesId : seenSeriesIds) {
            for(Comparable<?> categoryId : seenCategoryIds) {
                List<Number> vals = keyToValue.getOrDefault(Arrays.asList(seriesId, categoryId), Arrays.<Number>asList(0.0, 0.0));

                result.add(vals.get(0), vals.get(1), seriesId, categoryId);
            }
        }

        return result;
    }
}
