package org.aksw.beast.chart.model;

import java.util.Collection;

import org.aksw.beast.viz.xchart.XChartStatBarChartBuilder;
import org.aksw.beast.vocabs.CV;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;


public class ChartModelConfigurerXChart {
	public static CategoryChart toChart(Model dataModel, StastisticalBarChart chartModel) {
	      CategoryChart result = new CategoryChartBuilder()
	              .width(chartModel.getWidth())
	              .height(chartModel.getHeight())
	              .title(chartModel.getTitle())
	              .xAxisTitle(chartModel.getxAxisTitle())
	              .yAxisTitle(chartModel.getyAxisTitle())
	              .build();

	      XChartStatBarChartBuilder builder = XChartStatBarChartBuilder
	      	.from(result)
	      	.setAutoRange(true);
	      
	      builder.seriesAccessor().setProperty(CV.series).setLabelPropery(RDFS.label);
	      builder.categoryAccessor().setProperty(CV.category).setLabelPropery(RDFS.label);
	      builder.setValueProperty(CV.value);
	      
	      // Check if there is any error property present in the chart data
	      boolean errorBarsEnabled = dataModel.listSubjectsWithProperty(CV.error).nextOptional().isPresent();
	      
	      if(errorBarsEnabled) {	      
	    	  builder.setErrorProperty(CV.error);
	      } else {
	    	  builder.setErrorProperty(null);
	      }
	      
	      builder.setErrorBarsEnabled(errorBarsEnabled);
	      
	      
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

    	  builder.processSeries(s);
    	  
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
