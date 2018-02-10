package org.aksw.beast.chart.model;

import java.util.List;

import org.aksw.jena_sparql_api.mapper.annotation.Iri;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;
import org.apache.jena.graph.Node;

@RdfType("qb:SliceKey")
public class SliceKey {

	@Iri("qb:componentProperty")
	protected List<Node> componentProperties;

	public List<Node> getComponentProperties() {
		return componentProperties;
	}

	public void setComponentProperties(List<Node> componentProperties) {
		this.componentProperties = componentProperties;
	}	
}
