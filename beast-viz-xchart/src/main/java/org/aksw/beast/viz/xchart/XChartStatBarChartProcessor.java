package org.aksw.beast.viz.xchart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.aksw.beast.vocabs.CV;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.knowm.xchart.CategoryChart;


public class XChartStatBarChartProcessor {

    // Partitions
    public static <T, K> Map<K, Collection<T>> partition(Collection<T> items, Function<? super T, ? extends K> itemToKey) {
        Map<K, Collection<T>> result = new LinkedHashMap<>();

        // Didn't know how to accomplish the linkedHashMap aspect of the map with collectors,
        // hence the somewhat old school approach
        items.forEach(item -> {
            K key = itemToKey.apply(item);
            result.computeIfAbsent(key, (k) -> new ArrayList<>()).add(item);
        });

        return result;
    }

    /**
     * Return a mapping of which properties of which (reachable) resource are
     * invalid or missing
     *
     * @return
     */
//	public static Map<Resource, Map<Property, Object>> validate(Resource r) {
//
//	}



    /**
     * Whether to sort the static data (if present) by the given xData comparator (if present)
     * when adding series to the chart.
     *
     */
    public static void setOrderXData(boolean tf) {

    }

    public static <S, X> void addSeries(
    		CategoryChart chart,
    		Collection<Resource> seriesData,
    		Function<Set<S>, List<S>> seriesArranger,
    		Function<Set<X>, List<X>> xArranger) {

        // Partition resources by their series
        Map<String, Collection<Resource>> seriesToData =
                partition(seriesData, (r) -> r.getProperty(CV.seriesLabel).getString());

        seriesToData.forEach((seriesName, items) -> {
//
            int n = seriesData.size();
            List<Object> xData = new ArrayList<>(n);
            List<Number> yData = new ArrayList<>(n);
            List<Number> errorBars = new ArrayList<>(n);

            boolean hasErrorBars = false;

            for(Resource r : items) {
                Object x = r.getProperty(CV.categoryLabel).getObject().asLiteral().getValue();
                Number y = (Number)r.getProperty(CV.value).getObject().asLiteral().getValue();
                Number errorBar = 0;

                Statement errP = r.getProperty(CV.stDev);
                if(errP != null) {
                    hasErrorBars = true;
                    errorBar = r.getProperty(CV.stDev).getDouble();
                }

                xData.add(x);
                yData.add(y);
                errorBars.add(errorBar);

                xData.add(x);
                yData.add(y);
                errorBars.add(errorBar);
            }

//            System.out.println("series" + seriesName + " xData: " + xData);
//            System.out.println("series" + seriesName + " yData: " + yData);

            hasErrorBars = false;

            if(hasErrorBars) {
                chart.addSeries(seriesName, xData, yData, errorBars);
            } else {
                chart.addSeries(seriesName, xData, yData);
            }

        });
    }
}
