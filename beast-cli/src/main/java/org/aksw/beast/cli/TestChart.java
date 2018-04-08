package org.aksw.beast.cli;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.LegendPosition;

public class TestChart {
	public static void main(String[] args) {
	      CategoryChart chart = new CategoryChartBuilder()
	              .width(640)
	              .height(480)
	              .title("Title")
	              .xAxisTitle("xAxis")
	              .yAxisTitle("yAxis")
	              .build();

	      chart.getStyler()
	      	//.setYAxisMin(0.01)
	      	//.setYAxisMax(1.0)
	      	.setYAxisLogarithmic(true)
	        //.setYAxisDecimalPattern("###,###,###,###,###.#####")
	      	//.setYAxisTicksVisible(true)
	      	.setLegendPosition(LegendPosition.InsideNW)
	      	//.setAntiAlias(true)
	      	;

//	      chart.addSeries("seriesName",
//	    		  Arrays.asList("cat1"),
//	    		  Arrays.asList(0.26),
//	    		  Arrays.asList(0.01));

//	      chart.addSeries("seriesName",
//	    		  Arrays.asList("cat1", "cat2", "cat3", "cat4"),
//	    		  Arrays.asList(0.1, 0.25, 0.5, 1));

	      // generates Log data
	      List<Integer> xData = new ArrayList<Integer>();
	      List<Double> yData = new ArrayList<Double>();
	      for (int i = -3; i <= 3; i++) {
	        xData.add(i);
	        yData.add(Math.pow(10, i));
	      }
	      
	      chart.addSeries("10^x", xData, yData);
	      
          new SwingWrapper<CategoryChart>(chart).displayChart();

	}
}
