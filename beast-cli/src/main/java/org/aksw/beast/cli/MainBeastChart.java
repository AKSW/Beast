package org.aksw.beast.cli;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joptsimple.OptionParser;
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
		// TODO Allow as non-option argument
    	inputOs = parser
                .acceptsAll(Arrays.asList("f", "file"), "File containing input data")
                .withRequiredArg()
                .ofType(File.class)
                ;
    }
    
    public void run(String[] args) {
    	
    }
    
    
	public static void main(String[] args) {
		
	}
}
