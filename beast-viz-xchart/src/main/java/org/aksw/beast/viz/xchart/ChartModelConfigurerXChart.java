package org.aksw.beast.viz.xchart;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.aksw.beast.chart.model.ChartStyle;
import org.aksw.beast.chart.model.StatisticalBarChart;
import org.aksw.beast.vocabs.CV;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.AxesChartStyler;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.Styler.TextAlignment;


public class ChartModelConfigurerXChart {
	
//	public static <T> T applyIfPresent(Supplier<? extends T> getter, Function<? super T, ?> setter) {
//		T result = applyIfPresent(getter, v -> { setter.apply(v); });
//		return result;
//	}

	public static <T> T applyIfPresent(Supplier<? extends T> getter, Consumer<? super T> setter) {
		T value = getter.get();
		if(value != null) {
			setter.accept(value);
		}

		return value;
	}

	public static CategoryChart toChart(Model dataModel, StatisticalBarChart chartModel) {
	      CategoryChart result = new CategoryChartBuilder()
		//XYChart result = new XYChartBuilder()
			.width(chartModel.getWidth())
			.height(chartModel.getHeight())
			.title(chartModel.getTitle())
			.xAxisTitle(chartModel.getxAxisTitle())
			.yAxisTitle(chartModel.getyAxisTitle())
			.build();

        
	    //CategoryStyler styler = result.getStyler();
		AxesChartStyler styler = result.getStyler();
		ChartStyle style = chartModel.getStyle();
	      

	    applyIfPresent(style::getyAxisDecimalPattern, styler::setYAxisDecimalPattern);
        applyIfPresent(style::getLegendPosition, str -> styler.setLegendPosition(LegendPosition.valueOf(str)));
	    applyIfPresent(style::getxAxisLabelRotation, v -> { styler.setXAxisLabelRotation(v.intValue()); });
        applyIfPresent(style::isyAxisLogarithmic, styler::setYAxisLogarithmic);
	    applyIfPresent(style::getyAxisDecimalPattern, styler::setYAxisDecimalPattern);
	    applyIfPresent(style::getyAxisMin, styler::setYAxisMin);
	    applyIfPresent(style::getyAxisMax, styler::setYAxisMax);	      
	      	      
	    //applyIfPresent(style::getyAxisMax, styler::tick);
	    styler.setXAxisLabelAlignment(TextAlignment.Right);
	      
	    XChartStatBarChartBuilder builder = XChartStatBarChartBuilder
	    	.from(result);

	      	//.setAutoRange(true);
	      
	    builder.seriesAccessor().setProperty(CV.series).setLabelPropery(RDFS.label);
	    builder.categoryAccessor().setProperty(CV.category).setLabelPropery(RDFS.label);
	    builder.setValueProperty(CV.value);
	      
	    // Check if there is any error property present in the chart data
	    boolean isErrorDataPresent = dataModel.listSubjectsWithProperty(CV.error).nextOptional().isPresent();
	      
	    if(isErrorDataPresent && !style.isErrorBarsDisabled()) {	      
	        builder.setErrorProperty(CV.error);
	    } else {
	        builder.setErrorProperty(null);
	    }
	      
	      //builder.setErrorBarsEnabled(isErrorDataPresent);
	      
	      
	      //ConceptBasedSeries seriesSpec = (ConceptBasedSeries)chartModel.getSeries();
	      //String conceptStr = seriesSpec.getConcept();
	      //Concept concept = Concept.parse(conceptStr, dataModel); // NOTE Model *is* a PrefixMapping - although this design decision is regretted in its comments
//	      Concept concept = ConceptUtils.createSubjectConcept();
//	      
//	      QueryExecutionFactory qef = FluentQueryExecutionFactory.from(dataModel).create();
//	      List<Node> nodes = ServiceUtils.fetchList(qef, concept);
//
//    	  Property seriesProperty = dataModel.createProperty(seriesSpec.getSeriesProperty());
//    	  Property categoryProperty = dataModel.createProperty(seriesSpec.getSeriesProperty());
//
//    	  
//    	  builder.seriesAccessor().setProperty(seriesProperty);
//    	  builder.categoryAccessor().setProperty(categoryProperty);
//    	  
//    	  //AttributeAccessor
//    	  //PropertyAcc
    	  
	    Collection<Resource> s = dataModel.listSubjectsWithProperty(RDF.type, CV.DataItem).toSet();
	      
//    	  List<Resource> s = nodes.stream()
//    			  .map(node -> ModelUtils.convertGraphNodeToRDFNode(node, dataModel))
//    			  .map(RDFNode::asResource)
//    			  .collect(Collectors.toList());

    	  builder.processObservations(s);
    	  
//    	  result.getStyler().setYAxisMax(1.0);
//    	  result.getStyler().setYAxisLabelAlignment(TextAlignment.Left);
    	  //result.getStyler().setYAxisTicksVisible(false);
    	  //result.getStyler().setYAxisLogarithmic(false);
//	      for(Node node : nodes) {
//	    	  Resource r = ModelUtils.convertGraphNodeToRDFNode(node, dataModel).asResource();
//	    	  
//	    	  
//	    	  r.getProperty(seriesProperty).ge
//	    	  
//	      }
	      
	      //builder.processSeries(avgs);
	      
	    return result;
	}
}
