package org.aksw.beast.chart.model;

import org.aksw.beast.chart.ChartTransform;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;



public class TestChartMapper {
	
	@Test
	public void testChartRendering() throws Exception {
        Model model = RDFDataMgr.loadModel("statistical-data.ttl");
        ChartTransform.transform(model);
	}

}
