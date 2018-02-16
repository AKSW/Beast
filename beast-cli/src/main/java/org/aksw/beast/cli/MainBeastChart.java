package org.aksw.beast.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.aksw.beast.chart.ChartTransform;
import org.aksw.beast.chart.model.StatisticalBarChart;
import org.aksw.beast.viz.xchart.ChartModelConfigurerXChart;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class MainBeastChart {
	
    private static final Logger logger = LoggerFactory.getLogger(MainBeastChart.class);


    protected OptionParser parser = new OptionParser();

    protected OptionSpec<?> svgOs;
    protected OptionSpec<?> pngOs;    
    protected OptionSpec<?> guiOs;    
    
//    protected OptionSpec<File> outputOs;
//    protected OptionSpec<String> logFormatOs;
//    protected OptionSpec<String> outFormatOs;
//    protected OptionSpec<String> rdfizerOs;

    public MainBeastChart() {
    	parser.allowsUnrecognizedOptions();
    	    
    	pngOs = parser.acceptsAll(Arrays.asList("png"), "Output charts in png format (Default if no other format is given)");
    	svgOs = parser.acceptsAll(Arrays.asList("svg"), "Output charts in svg format");
    	guiOs = parser.acceptsAll(Arrays.asList("gui"), "Display charts in a window");
    }
    
    public void run(String[] args) throws Exception {
    	OptionSet options = parser.parse(args);
    	
    	List<?> inputResources = options.nonOptionArguments();

    	boolean isSvgOutputEnabled = options.has(svgOs);
    	boolean isPngOutputEnabled = options.has(pngOs);
    	boolean isGuiOutputEnabled = options.has(guiOs);
    	
    	Model model = ModelFactory.createDefaultModel();
    	for(Object inputResource : inputResources) {
    		RDFDataMgr.read(model, Objects.toString(inputResource));
    	}

    	
    	List<Entry<StatisticalBarChart, Model>> chartSpecs = ChartTransform.transform(model);
    	
    	for(Entry<StatisticalBarChart, Model> chartSpec : chartSpecs) {
            CategoryChart xChart = ChartModelConfigurerXChart.toChart(chartSpec.getValue(), chartSpec.getKey());

            VectorGraphicsEncoder.saveVectorGraphic(xChart, "/tmp/Sample_Chart", VectorGraphicsFormat.SVG);
            
            if(isSvgOutputEnabled) {
            	BitmapEncoder.saveBitmap(xChart, "/tmp/Sample_Chart", BitmapFormat.PNG);
            }
            
            if(isPngOutputEnabled) {
            	BitmapEncoder.saveBitmap(xChart, "/tmp/Sample_Chart", BitmapFormat.JPG);
            }
            
            if(isGuiOutputEnabled) {
                new SwingWrapper<CategoryChart>(xChart).displayChart();
            }            
    	}
    	
    }
    
    
	public static void main(String[] args) throws Exception {
		new MainBeastChart().run(args);
	}
}
