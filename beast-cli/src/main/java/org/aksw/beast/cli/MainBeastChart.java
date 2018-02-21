package org.aksw.beast.cli;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.aksw.beast.chart.ChartTransform;
import org.aksw.beast.chart.model.StatisticalBarChart;
import org.aksw.beast.viz.xchart.ChartModelConfigurerXChart;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;
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

    protected OptionSpec<?> pngOs;
    protected OptionSpec<?> svgOs;
    protected OptionSpec<?> jgpOs;    
    protected OptionSpec<?> guiOs;

    protected OptionSpec<String> outputFolderOs;
    protected OptionSpec<?> helpOs;
    
//    protected OptionSpec<File> outputOs;
//    protected OptionSpec<String> logFormatOs;
//    protected OptionSpec<String> outFormatOs;
//    protected OptionSpec<String> rdfizerOs;

    public MainBeastChart() {
    	parser.allowsUnrecognizedOptions();
    	    
    	pngOs = parser.acceptsAll(Arrays.asList("png"), "Output charts in png format (Default if no other format is given)");
    	svgOs = parser.acceptsAll(Arrays.asList("svg"), "Output charts in svg format");
    	jgpOs = parser.acceptsAll(Arrays.asList("jgp"), "Output charts in jpg format");
    	guiOs = parser.acceptsAll(Arrays.asList("gui"), "Display charts in a window");
    	
    	outputFolderOs = parser.acceptsAll(Arrays.asList("o", "output"), "Output folder").withRequiredArg().ofType(String.class);
    }
    
    public static String fileNameWithoutExtension(String fileName) {
    	int dotIndex = fileName.lastIndexOf(".");
    	String result = dotIndex >= 0 ? fileName.substring(0, dotIndex) : fileName;
    	return result;
    }
    
    public static String deriveFileName(String fileOrUri) throws MalformedURIException {
		URI uri;
		try {
			uri = new URI(fileOrUri);
		} catch(Exception e) {
			String str = IRILib.filenameToIRI(fileOrUri);
			uri = new URI(str);
		}
		
		String path = uri.getPath();
		int offset = path.lastIndexOf('/') + 1;
		String result = offset > 0 ? path.substring(offset) : null;
		
		return result;
		//System.out.println("Derived folder name: " + folderName);
    }
    
    public void run(String[] args) throws Exception {
    	OptionSet options = parser.parse(args);
    	    	
    	List<?> inputResources = options.nonOptionArguments();

		if(inputResources.isEmpty()) {
			logger.error("No input resources (files or URLs) provided"); 
		}

    	if(options.has(helpOs) || inputResources.isEmpty()) {
    		parser.printHelpOn(System.err);
    		return;
    	}

    	// TODO We could manage output formats in a list of output specification objects
    	boolean isPngOutputEnabled = options.has(pngOs);
    	boolean isSvgOutputEnabled = options.has(svgOs);
    	boolean isJgpOutputEnabled = options.has(jgpOs);
    	boolean isGuiOutputEnabled = options.has(guiOs);
    	
    	boolean isAnyOutputEnabled = isSvgOutputEnabled || isPngOutputEnabled || isJgpOutputEnabled || isGuiOutputEnabled;
    	
    	if(!isAnyOutputEnabled) {
    		isPngOutputEnabled = true;
    	}
    	
    	boolean isOutputFolderNeeded = isSvgOutputEnabled || isPngOutputEnabled;
    	
    	Model model = ModelFactory.createDefaultModel();
    	
    	for(Object inputResource : inputResources) {
    		RDFDataMgr.read(model, Objects.toString(inputResource));
    	}

    	String outputFolderName = null;
    	File outputFolder = null;

    	if(isOutputFolderNeeded) {
    		String fileName;
    		outputFolderName = outputFolderOs.value(options);
	    	if(outputFolderName == null) {
		    	String tmpName = inputResources.stream().map(Objects::toString).findFirst().orElse(null);
		
		    	fileName = tmpName != null ? deriveFileName(tmpName) : null;
		
		    	if(fileName == null) {
		    		throw new RuntimeException("Could not derive a folder name from " + tmpName + ". Please specify one via -o Option");
		    	}

		    	outputFolderName = fileNameWithoutExtension(fileName);
		    	outputFolderName += "-charts";
	    	}	    	

	    	outputFolder = new File(outputFolderName);
	    	
	    	logger.info("Writing output to " + outputFolder.getAbsolutePath());
	    	
	    	outputFolder.mkdir();
    	}
    	    	
    	
    	List<Entry<StatisticalBarChart, Model>> chartSpecs = ChartTransform.transform(model);
    	
    	for(Entry<StatisticalBarChart, Model> chartSpec : chartSpecs) {
            CategoryChart xChart = ChartModelConfigurerXChart.toChart(chartSpec.getValue(), chartSpec.getKey());
            
            String baseFileName = xChart.getTitle();

            // TODO Check filenames more thoroughly
            
            if(isPngOutputEnabled) {
            	File outFile = new File(outputFolder, baseFileName);
            	BitmapEncoder.saveBitmap(xChart, outFile.getPath(), BitmapFormat.PNG);
            }

            if(isSvgOutputEnabled) {
            	File outFile = new File(outputFolder, baseFileName);
            	VectorGraphicsEncoder.saveVectorGraphic(xChart, outFile.getPath(), VectorGraphicsFormat.SVG);
            }
            
            if(isJgpOutputEnabled) {
            	File outFile = new File(outputFolder, baseFileName);
            	BitmapEncoder.saveBitmap(xChart, outFile.getPath(), BitmapFormat.JPG);
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
