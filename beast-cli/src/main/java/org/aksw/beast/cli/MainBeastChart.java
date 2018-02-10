package org.aksw.beast.cli;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.aksw.beast.chart.ChartTransform;
import org.aksw.beast.chart.model.StatisticalBarChart;
import org.aksw.beast.viz.xchart.ChartModelConfigurerXChart;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.SwingWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class MainBeastChart {
	
    private static final Logger logger = LoggerFactory.getLogger(MainBeastChart.class);


    protected OptionParser parser = new OptionParser();

    protected OptionSpec<File> inputOs;
//    protected OptionSpec<File> outputOs;
//    protected OptionSpec<String> logFormatOs;
//    protected OptionSpec<String> outFormatOs;
//    protected OptionSpec<String> rdfizerOs;

    public MainBeastChart() {
    	parser.allowsUnrecognizedOptions();
    	
    
    	
		// TODO Allow as non-option argument
//    	inputOs = parser
//                .acceptsAll(Arrays.asList("f", "file"), "File containing input data")
//                .withRequiredArg()
//                .ofType(File.class)
//                ;

    
    
    }
    
    public void run(String[] args) throws Exception {
    	OptionSet options = parser.parse(args);
    	
    	List<?> inputResources = options.nonOptionArguments();

    	Model model = ModelFactory.createDefaultModel();
    	for(Object inputResource : inputResources) {
    		RDFDataMgr.read(model, Objects.toString(inputResource));
    	}

    	
    	List<Entry<StatisticalBarChart, Model>> chartSpecs = ChartTransform.transform(model);
    	
    	for(Entry<StatisticalBarChart, Model> chartSpec : chartSpecs) {
            CategoryChart xChart = ChartModelConfigurerXChart.toChart(chartSpec.getValue(), chartSpec.getKey());

            new SwingWrapper<CategoryChart>(xChart).displayChart();
    	}
    	
    }
    
    
	public static void main(String[] args) throws Exception {
		new MainBeastChart().run(args);
	}
}
