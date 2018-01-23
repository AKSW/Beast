package org.aksw.beast.viz.xchart;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.aksw.beast.vocabs.CV;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.knowm.xchart.CategoryChart;

public class XChartStatBarChartBuilder {
    protected CategoryChart chart;
    protected Collection<Resource> seriesData;
    protected Function<? super RDFNode, String> seriesToLabel;
    protected Function<? super RDFNode, Object> xToLabel;
    protected Function<Set<RDFNode>, List<RDFNode>> seriesArranger;
    protected Function<Set<RDFNode>, List<RDFNode>> xArranger;
    protected boolean autoRange = true;
	
    protected boolean isErrorBarsEnabled = false;
    
    public static XChartStatBarChartBuilder from(CategoryChart chart) {
    	XChartStatBarChartBuilder result = new XChartStatBarChartBuilder();
    	result.setChart(chart);
    	
    	return result;
    }

    public CategoryChart getChart() {
		return chart;
	}
	
	public XChartStatBarChartBuilder setChart(CategoryChart chart) {
		this.chart = chart;
		return this;
	}
	
	public Collection<Resource> getSeriesData() {
		return seriesData;
	}
	
	public XChartStatBarChartBuilder setSeriesData(Collection<Resource> seriesData) {
		this.seriesData = seriesData;
		return this;
	}
	
	public Function<? super RDFNode, String> getSeriesToLabel() {
		return seriesToLabel;
	}
	
	public XChartStatBarChartBuilder setSeriesToLabel(Function<? super RDFNode, String> seriesToLabel) {
		this.seriesToLabel = seriesToLabel;
		return this;
	}
	
	public Function<? super RDFNode, Object> getxToLabel() {
		return xToLabel;
	}
	
	public XChartStatBarChartBuilder setxToLabel(Function<? super RDFNode, Object> xToLabel) {
		this.xToLabel = xToLabel;
		return this;
	}
	
	public Function<Set<RDFNode>, List<RDFNode>> getSeriesArranger() {
		return seriesArranger;
	}
	
	public XChartStatBarChartBuilder setSeriesArranger(Function<Set<RDFNode>, List<RDFNode>> seriesArranger) {
		this.seriesArranger = seriesArranger;
		return this;
	}
	
	public Function<Set<RDFNode>, List<RDFNode>> getxArranger() {
		return xArranger;
	}
	
	public XChartStatBarChartBuilder setxArranger(Function<Set<RDFNode>, List<RDFNode>> xArranger) {
		this.xArranger = xArranger;
		return this;
	}
	
	public boolean isAutoRange() {
		return autoRange;
	}
	
	public XChartStatBarChartBuilder setAutoRange(boolean autoRange) {
		this.autoRange = autoRange;
		return this;
	}

	public boolean isErrorBarsEnabled() {
		return isErrorBarsEnabled;
	}

	public XChartStatBarChartBuilder setErrorBarsEnabled(boolean isErrorBarsEnabled) {
		this.isErrorBarsEnabled = isErrorBarsEnabled;
		return this;
	}

	public XChartStatBarChartBuilder processSeries(Collection<Resource> seriesData) {
		
//		if(seriesToLabel == null) {
//			seriesToLabel = (r) -> r.asResource().getProperty(RDFS.label).getString();
//		}
//		
//		if(xToLabel == null) {
//			// Return the workload's label
//			xToLabel = (r) -> r.asResource().getProperty(RDFS.label).getString();
//		}
		
		XChartStatBarChartProcessor.addSeries(
				chart,
				seriesData,
				seriesToLabel,
				xToLabel,
				seriesArranger,
				xArranger,
				autoRange,
				isErrorBarsEnabled
				);
		
		return this;
	}

    
    
}
