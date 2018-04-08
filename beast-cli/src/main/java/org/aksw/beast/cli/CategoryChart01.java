package org.aksw.beast.cli;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;

/**
 * Logarithmic Y-Axis
 * 
 * Demonstrates the following:
 * <ul>
 * <li>Logarithmic Y-Axis
 * <li>Building a Chart with ChartBuilder
 * <li>Place legend at Inside-NW position
 */
public class CategoryChart01 {

  public static void main(String[] args) {

	CategoryChart01 exampleChart = new CategoryChart01();
	Chart<?, ?> chart = exampleChart.getChart();
    new SwingWrapper<>(chart).displayChart();
  }

  public Chart<?, ?> getChart() {

    // generates Log data
    List<Integer> xData = new ArrayList<Integer>();
    List<Double> yData = new ArrayList<Double>();
    for (int i = -3; i <= 3; i++) {
      xData.add(i);
      yData.add(Math.pow(10, i));
    }

    // Create Chart
//    XYChart chart = new XYChartBuilder().width(800).height(600).title("Powers of Ten").xAxisTitle("Power").yAxisTitle("Value").build();
    XYChart chart = new XYChartBuilder().width(800).height(600).title("Powers of Ten").xAxisTitle("Power").yAxisTitle("Value").build();

    // Customize Chart
    chart.getStyler().setChartTitleVisible(true);
    chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
    chart.getStyler().setYAxisLogarithmic(true);
    
    chart.getStyler().setYAxisMin(0.001);
    chart.getStyler().setYAxisMax(1000.0);

    // Series
    chart.addSeries("10^x", xData.stream().mapToDouble(x -> x).toArray(), yData.stream().mapToDouble(x -> x).toArray());

    return chart;
  }
}