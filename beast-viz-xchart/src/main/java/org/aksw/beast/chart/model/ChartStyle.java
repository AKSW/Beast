package org.aksw.beast.chart.model;

import org.aksw.jena_sparql_api.mapper.annotation.DefaultIri;
import org.aksw.jena_sparql_api.mapper.annotation.IriNs;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;

@RdfType("cv:ChartStyle")
@DefaultIri("cv:chartstyle")
public class ChartStyle {
	
	@IriNs("cv")
	protected String legendPosition;

	@IriNs("cv")
	protected boolean yAxisLogarithmic;

	@IriNs("cv")
	protected boolean yAxisTicksVisible;

	@IriNs("cv")
	protected double xAxisLabelRotation;

	@IriNs("cv")
	protected String yAxisDecimalPattern;

	public String getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(String legendPosition) {
		this.legendPosition = legendPosition;
	}

	public boolean isyAxisLogarithmic() {
		return yAxisLogarithmic;
	}

	public void setyAxisLogarithmic(boolean yAxisLogarithmic) {
		this.yAxisLogarithmic = yAxisLogarithmic;
	}

	public boolean isyAxisTicksVisible() {
		return yAxisTicksVisible;
	}

	public void setyAxisTicksVisible(boolean xAxisTicksVisible) {
		this.yAxisTicksVisible = xAxisTicksVisible;
	}

	public double getxAxisLabelRotation() {
		return xAxisLabelRotation;
	}

	public void setxAxisLabelRotation(double xAxisLabelRotation) {
		this.xAxisLabelRotation = xAxisLabelRotation;
	}

	public String getyAxisDecimalPattern() {
		return yAxisDecimalPattern;
	}

	public void setyAxisDecimalPattern(String yAxisDecimalPattern) {
		this.yAxisDecimalPattern = yAxisDecimalPattern;
	}

	@Override
	public String toString() {
		return "ChartStyle [legendPosition=" + legendPosition + ", yAxisLogarithmic=" + yAxisLogarithmic
				+ ", yAxisTicksVisible=" + yAxisTicksVisible + ", xAxisLabelRotation=" + xAxisLabelRotation
				+ ", yAxisDecimalPattern=" + yAxisDecimalPattern + "]";
	}
	
}
