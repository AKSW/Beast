package org.aksw.beast.viz.xchart;

import java.util.Arrays;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.LegendPosition;

public class TestXchart {
	public static void main(String[] args) {
	    // Create Chart
	    CategoryChart chart = new CategoryChartBuilder()
	    		.width(800)
	    		.height(600)
	    		.title("Score Histogram")
	    		.xAxisTitle("Score")
	    		.yAxisTitle("Number")
	    		.build();

	    // Customize Chart
	    chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
	    chart.getStyler().setHasAnnotations(true);

	    // Series
	    chart.addSeries("test 1", Arrays.asList("cat1", "cat2", "cat3"), Arrays.asList(4, 5, 9), Arrays.asList(1, 1, 1));

	    new SwingWrapper<CategoryChart>(chart).displayChart();
	}
}
