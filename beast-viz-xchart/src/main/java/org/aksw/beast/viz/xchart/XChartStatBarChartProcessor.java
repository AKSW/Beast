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

import org.aksw.beast.vocabs.CV;
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
    		Function<Set<? super RDFNode>, List<? extends RDFNode>> seriesArranger,
    		Function<Set<RDFNode>, List<RDFNode>> xArranger) {

    	// Collect the extensions of the series and category dimensions
    	// and index data points
    	Set<RDFNode> seriesExt = new LinkedHashSet<>();
    	Set<RDFNode> categoriesExt = new LinkedHashSet<>();
    	Map<RDFNode, Map<RDFNode, Entry<Object, Number>>> seriesToCatToCell = new HashMap<>();

    	boolean hasErrorBars = false;
    	seriesData.forEach(r -> {
    		RDFNode s = r.getProperty(CV.series).getObject();
    		RDFNode x = r.getProperty(CV.category).getObject();

    		Object value = r.getProperty(CV.value).getObject().asLiteral().getValue();
            Statement errP = r.getProperty(CV.stDev);

            Number errorBar = 0.0;
            if(errP != null) {
                errorBar = errP.getDouble();
            }

    		seriesToCatToCell
    			.computeIfAbsent(s, (_s) -> new HashMap<>())
    			.put(x, new SimpleEntry<>(value, errorBar));

    		seriesExt.add(s);
    		categoriesExt.add(x);
    	});

    	// Arrange the dimensions
    	List<? extends RDFNode> series = seriesArranger == null
    			? new ArrayList<>(seriesExt)
    			: seriesArranger.apply(seriesExt);

    	List<? extends RDFNode> xs = xArranger == null
    			? new ArrayList<>(categoriesExt)
    			: xArranger.apply(categoriesExt);

        // Partition resources by their series
        Map<RDFNode, Collection<Resource>> seriesToData =
                partition(seriesData, (r) -> r.getProperty(CV.series).getObject());

        if(seriesArranger != null) {
        	seriesToData = DimensionMap.wrap(seriesToData, seriesArranger);
        }

        Entry<Object, Number> defaultE = new SimpleEntry<>(0.0, 0.0);

        int n = xs.size();
        List<Object> xData = xs.stream()
        		.map(x -> getValue(x, xToLabel))
        		.collect(Collectors.toList());

        for(RDFNode s : series) {

        	String seriesName = getLabel(s, seriesToLabel);

            List<Number> yData = new ArrayList<>(n);
            List<Number> errorBars = hasErrorBars ? new ArrayList<>(n) : null;

        	for(RDFNode x : xs) {
        		Entry<Object, Number> e =
        				seriesToCatToCell.getOrDefault(s, Collections.emptyMap())
        				.getOrDefault(x, defaultE);

        		yData.add(e.getValue());
        		if(errorBars != null) {
        			errorBars.add(e.getValue());
        		}
        	}

        	if(hasErrorBars) {
        		chart.addSeries(seriesName, xData, yData, errorBars);
        	} else {
        		chart.addSeries(seriesName, xData, yData);
        	}

        }
//
//        seriesToData.forEach((seriesName, items) -> {
////
//
//
//            for(Resource r : items) {
//                Object x = r.getProperty(CV.categoryLabel).getObject().asLiteral().getValue();
//                Number y = (Number)r.getProperty(CV.value).getObject().asLiteral().getValue();
//                Number errorBar = 0;
//
//                Statement errP = r.getProperty(CV.stDev);
//                if(errP != null) {
//                    hasErrorBars = true;
//                    errorBar = r.getProperty(CV.stDev).getDouble();
//                }
//
//                xData.add(x);
//                yData.add(y);
//                errorBars.add(errorBar);
//            }
//
////            System.out.println("series" + seriesName + " xData: " + xData);
////            System.out.println("series" + seriesName + " yData: " + yData);
//
//            hasErrorBars = false;
//
//            if(hasErrorBars) {
//                chart.addSeries(seriesName, xData, yData, errorBars);
//            } else {
//                chart.addSeries(seriesName, xData, yData);
//            }
//
//        });
    }


}
