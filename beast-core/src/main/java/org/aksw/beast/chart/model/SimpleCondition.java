package org.aksw.beast.chart.model;

import org.aksw.jena_sparql_api.mapper.annotation.IriNs;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;
import org.apache.jena.graph.Node;

@RdfType("cv:SimpleCondition")
public class SimpleCondition {
	@IriNs("cv")
	protected Node onProperty;

	@IriNs("cv")
	protected Node toValue;

	public Node getOnProperty() {
		return onProperty;
	}

	public void setOnProperty(Node onProperty) {
		this.onProperty = onProperty;
	}

	public Node getToValue() {
		return toValue;
	}

	public void setToValue(Node toValue) {
		this.toValue = toValue;
	}

	@Override
	public String toString() {
		return "SimpleCondition [onProperty=" + onProperty + ", toValue=" + toValue + "]";
	}
}
