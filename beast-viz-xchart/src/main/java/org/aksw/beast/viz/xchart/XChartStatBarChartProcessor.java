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

import org.aksw.beast.chart.accessor.AttributeAccessor;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchart.CategoryChart;


//
//class AttributeAccessorRdfBase
//	extends AttributeAccessorBase<Resource, RDFNode>
//{
//	protected ModulerAttributeAccessor<? super Resource, ? extends RDFNode> delegate;
//	
//	// must not be null
//	protected Property valueProperty;
//	
//	// if null, the label is the attribute value
//	protected Property labelProperty;
//	
//	protected Property seriesProperty;
//	
//	
//	@Override
//	public RDFNode getValue(Resource item) {
//		
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	@Override
//	public Object getLabel(RDFNode value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public List<RDFNode> arrange(Collection<? extends RDFNode> values) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}



//class ResourceTransformerBase<R, S, C> {
//	protected AttributeAccessor<R, S> series;
//	protected AttributeAccessor<R, C> category;
//	
//    protected Function<? super R, Number> getValue;
//    protected Function<? super R, Object> getError;
//}
//
//class ResourceTransformer {
//	
//	
//	public void setCategoryAccessor(Property property) {
//		
//	}
//}
//
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

//    
//    public static void addSeries(
//            CategoryChart chart,
//            Collection<Resource> seriesData,
//            
//            Property seriesProperty,
//            Property categoryProperty,
//            
//            
//            Function<? super RDFNode, String> seriesToLabel,
//            Function<? super RDFNode, ?> xToLabel,
//            Function<Set<RDFNode>, List<RDFNode>> seriesArranger,
//            Function<Set<RDFNode>, List<RDFNode>> xArranger,
//            
//            boolean autoRange,
//            boolean isErrorBarsEnabled) {    		
//    ) {
//    }    
//    
//    public static void addSeries(
//            CategoryChart chart,
//            Collection<Resource> seriesData,
//            
//            Function<? super RDFNode, String> seriesToLabel,
//            Function<? super RDFNode, ?> xToLabel,
//            Function<Set<RDFNode>, List<RDFNode>> seriesArranger,
//            Function<Set<RDFNode>, List<RDFNode>> xArranger,
//            
//            boolean autoRange,
//            boolean isErrorBarsEnabled) {    		
//    ) {
//    	
//    	addSeriesCore(
//    			chart,
//    			seriesData,
//    			
//    			r -> r.getProperty(CV.series).getObject(),
//    			r -> r.getProperty(CV.category).getObject(),
//    			
//    			r -> (Number)r.getProperty(CV.value).getObject().asLiteral().getValue(),
//    			r -> 
//    			
//    			seriesToLabel,
//    			xToLabel,
//    			seriesArranger,
//    			xArranger
//    	);
//    }

    public static <R, S, C, V, E> void addSeries(
            CategoryChart chart,
            Collection<R> seriesData,

            AttributeAccessor<R, S> seriesAccessor,
            AttributeAccessor<R, C> categoryAccessor,
            Function<? super R, ?> valueAccessor,
            Function<? super R, ?> errorAccessor,
//
//            
//            Function<? super S, String> seriesToLabel,
//            Function<? super C, ?> xToLabel,
//            Function<Set<S>, List<S>> seriesArranger,
//            Function<Set<C>, List<C>> xArranger,
//
//          Function<? super R, S> getSeries,
//          Function<? super R, C> getCategory,
            
            boolean autoRange,
            boolean isErrorBarsEnabled) {

    	boolean logarithmic = chart.getStyler().isYAxisLogarithmic();
//	    MergeStrategy<C> ms = new MergeStrategy<>();
//	    ms.setMergeEleCmp(StringPrettyComparator::doCompare);
//	    ms.setMergeStrategy(MergeStrategy.MIX);
//	    DimensionArranger<C> arr = new DimensionArranger<>(ms);
//
//	      xArranger = xArranger != null ? xArranger : arr;
//	      
//	      
//		    MergeStrategy<S> msS = new MergeStrategy<>();
//		    msS.setMergeEleCmp(StringPrettyComparator::doCompare);
//		    msS.setMergeStrategy(MergeStrategy.MIX);
//		    DimensionArranger<S> arrS = new DimensionArranger<>(msS);
//
//	      seriesArranger = seriesArranger != null ? seriesArranger : arrS;

        // Collect the extensions of the series and category dimensions
        // and index data points
        Set<S> seriesExt = new LinkedHashSet<>();
        Set<C> xExt = new LinkedHashSet<>();
        Map<S, Map<C, Entry<Number, Number>>> seriesToCatToCell = new HashMap<>();

        //boolean hasErrorBars = true;
        Double min = chart.getStyler().getYAxisMin();
        Double max = chart.getStyler().getYAxisMax();

        for(R r : seriesData) {
        	S s = seriesAccessor.getValue(r);
        	C x = categoryAccessor.getValue(r);
        	
//            RDFNode s = r.getProperty(CV.series).getObject();
//            RDFNode x = r.getProperty(CV.category).getObject();

//            Number value = (Number)r.getProperty(CV.value).getObject().asLiteral().getValue();
//            Statement errP = r.getProperty(CV.stDev);

        	Number value = (Number)valueAccessor.apply(r);
        	Number errP = errorAccessor == null ? null : (Number)errorAccessor.apply(r);
        	
        	double eps = 0.00001;
        	if(logarithmic && Math.abs(value.doubleValue()) < eps) {
        		value = eps;
        	}
        	
            double errorBar = 0.0;
            if(errP != null) {
                //errorBar = Math.abs(errP.getDouble());
            	errorBar = Math.abs(errP.doubleValue());
            }

            
            if(value instanceof Number) {
                double v = ((Number)value).doubleValue();

                double vmin = v - errorBar;
                double vmax = v + errorBar;
                
                
                if(logarithmic && Math.abs(vmin) < eps) {
                	vmin = eps;
                }
                
                min = min == null ? vmin : Math.min(min, vmin);
                max = max == null ? vmax : Math.max(max, vmax);
            }


            seriesToCatToCell
                .computeIfAbsent(s, (_s) -> new HashMap<>())
                .put(x, new SimpleEntry<>(value, errorBar));

            seriesExt.add(s);
            xExt.add(x);
        }

//        System.out.println(seriesToCatToCell);

        // Arrange the dimensions
        List<S> series = seriesAccessor.arrange(seriesExt);
//        List<S> series = seriesArranger == null
//                ? new ArrayList<>(seriesExt)
//                : seriesArranger.apply(seriesExt);
//
        List<C> xs = categoryAccessor.arrange(xExt);
//        List<C> xs = xArranger == null
//                ? new ArrayList<>(xExt)
//                : xArranger.apply(xExt);

        Entry<Number, Number> defaultE = new SimpleEntry<>(0.0, 0.0);

        
        int n = xs.size();
        List<Object> xLabels = xs.stream()
                //getValue(x, xToLabel)
                //.map(x -> "" + xToLabel.apply(x))
        		.map(x -> "" + categoryAccessor.getLabel(x))
                .map(x -> StringUtils.isEmpty(x) ? "(no category label)" : x)
                .collect(Collectors.toList());

        for(S s : series) {
            Map<C, Entry<Number, Number>> catToCell = seriesToCatToCell.getOrDefault(s, Collections.emptyMap());

            //String seriesName = getLabel(s, seriesToLabel);
            String seriesName = "" + seriesAccessor.getLabel(s); //seriesToLabel.apply(s);
            
            if(StringUtils.isEmpty(seriesName)) {
            	seriesName = "no series name";
            }
            
            List<Number> yData = new ArrayList<>(n);
            List<Number> errorBars = isErrorBarsEnabled ? new ArrayList<>(n) : null;

            for(C x : xs) {
                Entry<Number, Number> e = catToCell.getOrDefault(x, defaultE);

                yData.add(e.getKey());
                if(errorBars != null) {
                    errorBars.add(e.getValue());
                }
            }

//            System.out.println(yData);

            if(isErrorBarsEnabled) {
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
