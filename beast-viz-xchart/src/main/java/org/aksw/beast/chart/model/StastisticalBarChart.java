package org.aksw.beast.chart.model;

import org.aksw.jena_sparql_api.mapper.annotation.Iri;
import org.aksw.jena_sparql_api.mapper.annotation.IriNs;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;

@RdfType("cv:StatisticalBarChart")
public class StastisticalBarChart {
	@IriNs("cv")
	protected Integer width;
	
	@IriNs("cv")
	protected Integer height;

	@Iri("rdfs:label")
	protected String title;

//	@IriNs("cv")
//	protected Axis xAxis;
//
//	@IriNs("cv")
//	protected Axis yAxis;

	
	@IriNs("cv")
	protected String xAxisTitle;
	
	@IriNs("cv")
	protected String yAxisTitle;
	
	
	@IriNs("cv")
	protected ChartStyle style;
	
	@IriNs("cv")
	protected Object series;
	
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

//	public Axis getxAxis() {
//		return xAxis;
//	}
//
//	public void setxAxis(Axis xAxis) {
//		this.xAxis = xAxis;
//	}
//
//	public Axis getyAxis() {
//		return yAxis;
//	}
//
//	public void setyAxis(Axis yAxis) {
//		this.yAxis = yAxis;
//	}

	public Object getSeries() {
		return series;
	}

	public void setSeries(Object series) {
		this.series = series;
	}

	public String getxAxisTitle() {
		return xAxisTitle;
	}

	public void setxAxisTitle(String xAxisTitle) {
		this.xAxisTitle = xAxisTitle;
	}

	public String getyAxisTitle() {
		return yAxisTitle;
	}

	public void setyAxisTitle(String yAxisTitle) {
		this.yAxisTitle = yAxisTitle;
	}

	public ChartStyle getStyle() {
		return style;
	}

	public void setStyle(ChartStyle style) {
		this.style = style;
	}

	@Override
	public String toString() {
		return "StastisticalBarChart [width=" + width + ", height=" + height + ", title=" + title + ", xAxisTitle="
				+ xAxisTitle + ", yAxisTitle=" + yAxisTitle + ", chartStyle=" + style + ", series=" + series + "]";
	}

	
}
