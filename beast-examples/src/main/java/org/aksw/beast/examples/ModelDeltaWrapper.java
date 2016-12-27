package org.aksw.beast.examples;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.Delta;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 * This class is a function that given a key,
 * returns a function that wraps a model with delta-information
 * loaded from file and capabilities to write deltas out on commit.
 * 
 * @author raven
 *
 */
public class ModelDeltaWrapper
	implements Function<String, Function<Model, Model>>
{
	protected File basePath;

	public ModelDeltaWrapper(File basePath) {
		super();
		this.basePath = basePath;
		
		if(!(basePath.exists() && basePath.isDirectory())) {
			throw new IllegalArgumentException("Directory required. Got: " + basePath);
		}
	}
	
	@Override
	public Function<Model, Model> apply(String url) {
		return (model) -> {
			String fileName;
			try {
				fileName = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			//File baseFile = new File(fileName);
			File additionsFile = new File(basePath, fileName + "-added.nt");
			File deletionsFile = new File(basePath, fileName + "-removed.nt");
	
			
			Graph baseGraph = model.getGraph();
			Delta graph = new DeltaWithTxHandler(baseGraph, additionsFile, deletionsFile, RDFFormat.NTRIPLES);
			if(additionsFile.exists()) {
				RDFDataMgr.read(graph.getAdditions(), additionsFile.getAbsolutePath());
			}
			
			if(deletionsFile.exists()) {
				RDFDataMgr.read(graph.getDeletions(), deletionsFile.getAbsolutePath());
			}
			
			Model result = ModelFactory.createModelForGraph(graph);
			return result;
		};
	}
	
}