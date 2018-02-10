package org.aksw.beast.chart.model;

import org.aksw.jena_sparql_api.mapper.annotation.Iri;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;

@RdfType("cv:Axis")
public class Axis {
	
	@Iri("rdfs:label")
	protected String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "Axis [label=" + label + "]";
	}
}
