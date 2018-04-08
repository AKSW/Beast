package org.aksw.beast.viz.xchart;

import java.util.Collection;
import java.util.function.Function;

import org.aksw.beast.chart.accessor.AttributeAccessorRdf;
import org.aksw.beast.chart.model.ChartStyle;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.knowm.xchart.CategoryChart;


public class XChartStatBarChartBuilder {
    protected CategoryChart chart;
    //protected XYChart chart;
    
    protected Collection<Resource> seriesData;

    protected AttributeAccessorRdf seriesAccessor = new AttributeAccessorRdf();
    protected AttributeAccessorRdf categoryAccessor = new AttributeAccessorRdf();
    
    protected Function<? super RDFNode, ?> valueAccessor;
    protected Function<? super RDFNode, ?> errorAccessor;
    
//    protected Function<? super RDFNode, String> seriesToLabel;
//    protected Function<? super RDFNode, Object> xToLabel;
//    protected Function<Set<RDFNode>, List<RDFNode>> seriesArranger;
//    protected Function<Set<RDFNode>, List<RDFNode>> xArranger;

//    protected boolean autoRange = true;
	
//    protected boolean isErrorBarsEnabled = false;
    //protected boolean isErrorBarsDisabled = false;
  
    protected ChartStyle style = new ChartStyle();
    
    public static XChartStatBarChartBuilder from(CategoryChart chart) {
    	XChartStatBarChartBuilder result = new XChartStatBarChartBuilder();
    	result.setChart(chart);
    	
    	return result;
    }

    public CategoryChart getChart() {
		return chart;
	}

//    public XYChart getChart() {
//		return chart;
//	}
	
	public XChartStatBarChartBuilder setChart(CategoryChart chart) {
		this.chart = chart;
		return this;
	}

//	public XChartStatBarChartBuilder setChart(XYChart chart) { //CategoryChart chart) {
//	this.chart = chart;
//	return this;
//}

	public Collection<Resource> getSeriesData() {
		return seriesData;
	}
	
	public XChartStatBarChartBuilder setSeriesData(Collection<Resource> seriesData) {
		this.seriesData = seriesData;
		return this;
	}
	
	public AttributeAccessorRdf seriesAccessor() {
		return seriesAccessor;
	}
	
	public AttributeAccessorRdf categoryAccessor() {
		return categoryAccessor;
	}
	
//	public AttributeAccessorRdf errorAccessor() {
//		return valueAccessor;
//	}
//	
//	public AttributeAccessorRdf valueAccessor() {
//		return errorAccessor;
//	}
	
	public XChartStatBarChartBuilder setValueProperty(Property property) {
		setValueAccessor(r -> r.asResource().getRequiredProperty(property).getLiteral().getValue());
		return this;
	}

	public XChartStatBarChartBuilder setErrorProperty(Property property) {
		setErrorAccessor(property == null ? null : r -> r.asResource().getRequiredProperty(property).getLiteral().getValue());
		return this;
	}

	
	
	public Function<? super RDFNode, ?> getValueAccessor() {
		return valueAccessor;
	}

	public void setValueAccessor(Function<? super RDFNode, ?> valueAccessor) {
		this.valueAccessor = valueAccessor;
	}

	public Function<? super RDFNode, ?> getErrorAccessor() {
		return errorAccessor;
	}

	public void setErrorAccessor(Function<? super RDFNode, ?> errorAccessor) {
		this.errorAccessor = errorAccessor;
	}

	//	public Function<? super RDFNode, String> getSeriesToLabel() {
//		return seriesToLabel;
//	}
//	
//	public XChartStatBarChartBuilder setSeriesToLabel(Function<? super RDFNode, String> seriesToLabel) {
//		this.seriesToLabel = seriesToLabel;
//		return this;
//	}
//	
//	public Function<? super RDFNode, Object> getxToLabel() {
//		return xToLabel;
//	}
//	
//	public XChartStatBarChartBuilder setxToLabel(Function<? super RDFNode, Object> xToLabel) {
//		this.xToLabel = xToLabel;
//		return this;
//	}
//	
//	public Function<Set<RDFNode>, List<RDFNode>> getSeriesArranger() {
//		return seriesArranger;
//	}
//	
//	public XChartStatBarChartBuilder setSeriesArranger(Function<Set<RDFNode>, List<RDFNode>> seriesArranger) {
//		this.seriesArranger = seriesArranger;
//		return this;
//	}
//	
//	public Function<Set<RDFNode>, List<RDFNode>> getxArranger() {
//		return xArranger;
//	}
//	
//	public XChartStatBarChartBuilder setxArranger(Function<Set<RDFNode>, List<RDFNode>> xArranger) {
//		this.xArranger = xArranger;
//		return this;
//	}
//	
//	public boolean isAutoRange() {
//		return autoRange;
//	}
//	
//	public XChartStatBarChartBuilder setAutoRange(boolean autoRange) {
//		this.autoRange = autoRange;
//		return this;
//	}
//
//	public boolean isErrorBarsEnabled() {
//		return isErrorBarsEnabled;
//	}
//
//	public XChartStatBarChartBuilder setErrorBarsEnabled(boolean isErrorBarsEnabled) {
//		this.isErrorBarsEnabled = isErrorBarsEnabled;
//		return this;
//	}

	public ChartStyle getStyle() {
		return style;
	}

	public void setStyle(ChartStyle style) {
		this.style = style;
	}

	
	
	
	public XChartStatBarChartBuilder processObservations(Collection<Resource> observations) {

		IndexedStatisticalCategoryDataset<RDFNode, RDFNode> dataset = XChartStatBarChartProcessor.createDatasetFromObservations(
				observations,
				seriesAccessor,
				categoryAccessor,				
				valueAccessor,
				errorAccessor);
				
		IndexedStatisticalCategoryDataset.toCsv(dataset);
		
		XChartStatBarChartProcessor.configureChartFromDataset(chart, dataset);
		
		return this;
	}
    
}
