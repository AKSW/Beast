package org.aksw.beast.viz.xchart;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.aksw.beast.compare.StringPrettyComparator;
import org.aksw.beast.vocabs.CV;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.RDFNode;
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

    public static String getLabel(RDFNode node, Function<? super RDFNode, String> fn) {
        String result = node == null
                ? "(null)"
                : fn != null ? fn.apply(node) : node.toString();

        return result;
    }

    public static Object getValue(RDFNode node, Function<? super RDFNode, Object> fn) {
        Object result = node == null
                ? "(null)"
                : fn != null ? fn.apply(node) : (
                    node.isURIResource()
                        ? node.asResource().getLocalName()
                        : node.asLiteral().getValue()
                );


        return result;
    }

    public static void addSeries(
            CategoryChart chart,
            Collection<Resource> seriesData,
            Function<? super RDFNode, String> seriesToLabel,
            Function<? super RDFNode, Object> xToLabel,
            Function<Set<RDFNode>, List<RDFNode>> seriesArranger,
            Function<Set<RDFNode>, List<RDFNode>> xArranger,
            boolean autoRange) {

	    MergeStrategy<RDFNode> ms = new MergeStrategy<>();
	    ms.setMergeEleCmp(StringPrettyComparator::doCompare);
	    ms.setMergeStrategy(MergeStrategy.MIX);
	    DimensionArranger<RDFNode> arr = new DimensionArranger<>(ms);

	      xArranger = xArranger != null ? xArranger : arr;
	      seriesArranger = seriesArranger != null ? seriesArranger : arr;

        // Collect the extensions of the series and category dimensions
        // and index data points
        Set<RDFNode> seriesExt = new LinkedHashSet<>();
        Set<RDFNode> xExt = new LinkedHashSet<>();
        Map<RDFNode, Map<RDFNode, Entry<Number, Number>>> seriesToCatToCell = new HashMap<>();

        boolean hasErrorBars = false;
        Double min = chart.getStyler().getYAxisMin();
        Double max = chart.getStyler().getYAxisMax();

        for(Resource r : seriesData) {
            RDFNode s = r.getProperty(CV.series).getObject();
            RDFNode x = r.getProperty(CV.category).getObject();

            Number value = (Number)r.getProperty(CV.value).getObject().asLiteral().getValue();
            Statement errP = r.getProperty(CV.stDev);

            if(value instanceof Number) {
                Number v = (Number)value;
                min = min == null ? v.doubleValue() : Math.min(min, v.doubleValue());
                max = max == null ? v.doubleValue() : Math.max(max, v.doubleValue());
            }

            Number errorBar = 0.0;
            if(errP != null) {
                errorBar = errP.getDouble();
            }

            seriesToCatToCell
                .computeIfAbsent(s, (_s) -> new HashMap<>())
                .put(x, new SimpleEntry<>(value, errorBar));

            seriesExt.add(s);
            xExt.add(x);
        }

//        System.out.println(seriesToCatToCell);

        // Arrange the dimensions
        List<RDFNode> series = seriesArranger == null
                ? new ArrayList<>(seriesExt)
                : seriesArranger.apply(seriesExt);

        List<RDFNode> xs = xArranger == null
                ? new ArrayList<>(xExt)
                : xArranger.apply(xExt);

        Entry<Number, Number> defaultE = new SimpleEntry<>(0.0, 0.0);

        int n = xs.size();
        List<Object> xLabels = xs.stream()
                .map(x -> "" + getValue(x, xToLabel))
                .map(x -> StringUtils.isEmpty(x) ? "(no category label)" : x)
                .collect(Collectors.toList());

        for(RDFNode s : series) {
            Map<RDFNode, Entry<Number, Number>> catToCell = seriesToCatToCell.getOrDefault(s, Collections.emptyMap());

            String seriesName = getLabel(s, seriesToLabel);

            
            if(StringUtils.isEmpty(seriesName)) {
            	seriesName = "no series name";
            }
            
            List<Number> yData = new ArrayList<>(n);
            List<Number> errorBars = hasErrorBars ? new ArrayList<>(n) : null;

            for(RDFNode x : xs) {
                Entry<Number, Number> e = catToCell.getOrDefault(x, defaultE);

                yData.add(e.getKey());
                if(errorBars != null) {
                    errorBars.add(e.getValue());
                }
            }

//            System.out.println(yData);

            if(hasErrorBars) {
                chart.addSeries(seriesName, xLabels, yData, errorBars);
            } else {
                chart.addSeries(seriesName, xLabels, yData);
            }

        }

        if(autoRange) {
            if(min != null) {
                min = Math.pow(10, Math.floor(Math.log10(min)));
            }

            // Charts seemingly look better without adjusting the maximum to the next label
            if(max != null) {
                //max = Math.pow(10, Math.floor(Math.log10(max)));
            }

            chart.getStyler().setYAxisMin(min);
            chart.getStyler().setYAxisMax(max);
        }
    }


}
