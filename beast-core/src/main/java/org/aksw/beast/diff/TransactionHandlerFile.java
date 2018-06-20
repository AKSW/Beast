package org.aksw.beast.diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.Delta;
import org.apache.jena.graph.impl.TransactionHandlerBase;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class TransactionHandlerFile
	extends TransactionHandlerBase
{
	protected Delta graph;

	protected File additionsFile;
	protected File deletionsFile;
	protected RDFFormat format;
	
	public TransactionHandlerFile(Delta graph, File additionsFile, File deletionsFile, RDFFormat format) {
		super();
		this.graph = graph;
		this.additionsFile = additionsFile;
		this.deletionsFile = deletionsFile;
		this.format = format;
	}

	@Override
	public boolean transactionsSupported() {
		return false;
	}
	@Override
	public void begin() {
	}

	@Override
	public void abort() {
	}

	public static void writeOut(File file, Graph graph, RDFFormat format) {
		try {
			RDFDataMgr.write(new FileOutputStream(file), graph, format);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public void commit() {
		writeOut(additionsFile, graph.getAdditions(), format);
		writeOut(deletionsFile, graph.getDeletions(), format);
	}	
}