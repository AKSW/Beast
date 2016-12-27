package org.aksw.beast.examples;

import java.io.File;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.TransactionHandler;
import org.apache.jena.graph.compose.Delta;
import org.apache.jena.riot.RDFFormat;

public class DeltaWithTxHandler
	extends Delta
{
	protected TransactionHandler txh;

	public DeltaWithTxHandler(Graph base, File additionsFile, File deletionsFile, RDFFormat format) {
		super(base);
		txh = new TransactionHandlerFile(this, additionsFile, deletionsFile, format);
	}
	
	@Override
	public TransactionHandler getTransactionHandler() {
		return txh;
	}
}