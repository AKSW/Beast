package org.aksw.beast.viz.xchart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    

    public static final double eps = 0.00001;

    public static <R, S, C, V, E> IndexedStatisticalCategoryDataset<S, C> createDatasetFromObservations(
            Collection<R> observations,
            AttributeAccessor<R, S> seriesAccessor,
            AttributeAccessor<R, C> categoryAccessor,
            Function<? super R, ?> valueAccessor,
            Function<? super R, ?> errorAccessor
        ) {

    	boolean hasErrorValues = errorAccessor != null;
    	
    	IndexedStatisticalCategoryDataset<S, C> result = new IndexedStatisticalCategoryDataset<>(hasErrorValues);

        for(R r : observations) {
        	S s = seriesAccessor.getValue(r);
        	C x = categoryAccessor.getValue(r);
        	
        	Number value = (Number)valueAccessor.apply(r);
        	value = value == null || Double.isNaN(value.doubleValue()) ? null : value;        	

        	Number error = errorAccessor == null ? null : (Number)errorAccessor.apply(r);
        	error = error == null || Double.isNaN(error.doubleValue()) ? null : error;        	

        	if(value != null) {
        		result.putValue(s, x, value);
        	}
        	
        	if(error != null) {
        		result.putError(s, x, error);
        	}
        }

        Set<S> serieses = new LinkedHashSet<>(seriesAccessor.arrange(result.getSerieses()));
        result.setSerieses(serieses);
        
        Set<C> categories = new LinkedHashSet<>(categoryAccessor.arrange(result.getCategories()));
        result.setCategories(categories);
        

        for(C category : categories) {
        	String label = "" + categoryAccessor.getLabel(category);
        	label = StringUtils.isEmpty(label) ? "(no category label)" : label;
        	result.setCategoryLabel(category, label);
        }
        
        for(S series : serieses) {
        	String label = "" + seriesAccessor.getLabel(series);
        	label = StringUtils.isEmpty(label) ? "(no category label)" : label;
        	result.setSeriesLabel(series, label);
        }

        return result;
    }
    
    public static Double combinedMinValue(Number value, Number error) {
    	Double result = null;
    	if(value != null) {
    		double v = value.doubleValue();

    		result = error == null
    			? v
    			: v - Math.abs(error.doubleValue());
    	}
    	
    	return result;
    }

    public static Double combinedMaxValue(Number value, Number error) {
    	Double result = null;
    	if(value != null) {
    		double v = value.doubleValue();

    		result = error == null
    			? v
    			: v + Math.abs(error.doubleValue());
    	}
    	
    	return result;
    }

    public static <S, C> void inPlaceSetMissingValues(IndexedStatisticalCategoryDataset<S, C> dataset) {
    	for(S s : dataset.getSerieses()) {
    		for(C c : dataset.getCategories()) {

    			Number value = dataset.getValue(s, c);
    			if(value == null) {
    				dataset.putValue(s, c, 0.0);
    			}
    			
    			Number error = dataset.getError(s, c);
    			if(error == null && dataset.hasErrors()) {
    				dataset.putError(s, c, 0.0);
    			}
    		}
    	}
    }

    public static <S, C> void inPlaceAdjustForLogarithmicChart(IndexedStatisticalCategoryDataset<S, C> dataset) {    	    	
    	for(S s : dataset.getSerieses()) {
    		for(C c : dataset.getCategories()) {

    			Number value = dataset.getValue(s, c);
    			Number error = dataset.getError(s, c);

    			if(value != null) {    			
    				double v = value.doubleValue();                
                
            		if(Math.abs(v) < eps) {
            			System.out.println("[WARN] Adjusted value " + value + " to " + eps);
            			v = eps;
            			
            			dataset.putValue(s, c, v);
            		}
            		
            		// If combining the value with the error is below zero,
            		// adjust the error bar to touch the eps value
            		// TODO Print out a warning as this may give a false impression
            		if(error != null) {
	            		double e = error.doubleValue();
	            		if(v - e < eps) {
	            			e = v - eps;
	            			System.out.println("[WARN] Adjusted error " + error + " to " + e);
	            		}
	            		
	            		dataset.putError(s, c, e);
            		}
                }
            }
    	}
    }

// Adjust chart minimum!!
//	if(Math.abs(vmin) < eps) {
//	vmin = eps;
//}
    
//            double vmin = v - errorBar;
//            double vmax = v + errorBar;
//
//            
//            dataYMin = dataYMin == null ? vmin : Math.min(dataYMin, vmin);
//            dataYMax = dataYMax == null ? vmax : Math.max(dataYMax, vmax);
//
//            seriesToCategoryToValue.put(s, x, value);
//            seriesToCategoryToError.put(s, x, errorBar);
//
////            seriesToCatToCell
////                .computeIfAbsent(s, (_s) -> new HashMap<>())
////                .put(x, new SimpleEntry<>(value, errorBar));
//
//            seriesExt.add(s);
//            categoryExt.add(x);
//    			
//    		}
//    	}
//    }
    
    
    public static <S, C> void configureChartFromDataset(
            CategoryChart chart,
            IndexedStatisticalCategoryDataset<S, C> dataset) {
        
		dataset = IndexedStatisticalCategoryDataset.copy(dataset);

		inPlaceSetMissingValues(dataset);
        // TODO Make this chart configuration separate from the dataset extraction

    	
    	boolean isYAxisLogarithmic = chart.getStyler().isYAxisLogarithmic();

    	if(isYAxisLogarithmic) {
    		inPlaceAdjustForLogarithmicChart(dataset);
    	}

    	configureChartFromDatasetRaw(chart, dataset);
    }
    
    public static <S, C> void configureChartFromDatasetRaw(
            CategoryChart chart,
            IndexedStatisticalCategoryDataset<?, ?> dataset) {

    	boolean isYAxisLogarithmic = chart.getStyler().isYAxisLogarithmic();

        Double yMin = chart.getStyler().getYAxisMin();
        Double yMax = chart.getStyler().getYAxisMax();

        
//    	if(isYAxisLogarithmic && yMin == null || Math.abs(yMin.doubleValue()) < eps) {
//    		yMin = eps;
//    		chart.getStyler().setYAxisMin(yMin);
//    		//vmin = eps;
//    	}
        
        boolean isAutoRangeYMin = yMin == null;
        boolean isAutoRangeYMax = yMax == null;

        Double dataYMin = IndexedStatisticalCategoryDataset.minCombinedValue(dataset).map(Number::doubleValue).orElse(null);
        Double dataYMax = IndexedStatisticalCategoryDataset.maxCombinedValue(dataset).map(Number::doubleValue).orElse(null);
        


        if(isAutoRangeYMin && dataYMin != null) {
        	if(isYAxisLogarithmic) {        		
        		yMin = Math.min(Math.abs(dataYMin), eps);
        		yMin = Math.pow(10, Math.floor(Math.log10(yMin)));
        	} else {
        		yMin = dataYMin;
        	}
        }

        yMax = isAutoRangeYMax && dataYMax != null ? dataYMax : yMax;
        
        System.out.println("yMin=" + yMin + ", yMax=" + yMax);
        System.out.println("dataYMin=" + dataYMin + ", dataYMax=" + dataYMax);
        
        
        
        // Charts seemingly look better without adjusting the maximum to the next label
        if(isAutoRangeYMax) {
            //max = Math.pow(10, Math.floor(Math.log10(max)));
        }

	    if(isAutoRangeYMin && yMin != null) {
	        chart.getStyler().setYAxisMin(yMin);
	    }
	    
	    if(isAutoRangeYMax && yMax != null) {
	        chart.getStyler().setYAxisMax(yMax);
	    }

        int n = dataset.getCategories().size();

        List<String> categoryLabels = dataset.getCategories().stream()
        		.map(category -> Optional.ofNullable(dataset.getCategoryLabel(category)).orElse("(no label)"))
        		.collect(Collectors.toList());
        
        for(Object series : dataset.getSerieses()) {
        	String seriesLabel = Optional.ofNullable(dataset.getSeriesLabel(series)).orElse("(no label)");
            
            
            List<Number> valueData = new ArrayList<>(n);
            List<Number> errorData = dataset.hasErrors() ? new ArrayList<>(n) : null;

            for(Object category : dataset.getCategories()) {
            	Number value = dataset.getValue(series, category);
            	Number error = dataset.getError(series, category);

                valueData.add(value);
                if(errorData != null) {
                    errorData.add(error);
                }
            }

//            System.out.println(yData);

            if(errorData != null) {
                chart.addSeries(seriesLabel, categoryLabels, valueData, errorData);
            } else {
                chart.addSeries(seriesLabel, categoryLabels, valueData);
            }

        }
    }


}
