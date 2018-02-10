package org.aksw.beast.chart.model;

import java.util.List;
import java.util.Set;

import org.aksw.jena_sparql_api.mapper.annotation.Iri;
import org.aksw.jena_sparql_api.mapper.annotation.IriNs;
import org.aksw.jena_sparql_api.mapper.annotation.IriType;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;
import org.apache.jena.graph.Node;

@RdfType("cv:ConceptBasedSeries")
public class ConceptBasedSeries {
	
//	@Iri("cv:sparqlConcept")
//	protected String concept;

	@Iri("cv:condition")
	protected Set<SimpleCondition> conditions;
	
	@Iri("cv:seriesProperty")
	@IriType
	protected String seriesProperty;

	// Constant series value; mutually exclusive with seriesProperty
	@IriNs("cv")
	protected Node series;
	
	@Iri("cv:categoryProperty")
	@IriType
	protected String categoryProperty;

	@IriNs("cv")
	@IriType
	protected String valueProperty;

	@IriNs("cv")
	@IriType
	protected String errorProperty;
	
//	@Iri("cv:sliceKey")
//	protected List<SliceKey> sliceKeys;

	@Iri("cv:sliceProperty")
	protected List<Node> sliceProperties;
	
//	public String getConcept() {
//		return concept;
//	}
//
//	public void setConcept(String concept) {
//		this.concept = concept;
//	}

	public String getSeriesProperty() {
		return seriesProperty;
	}

	public void setSeriesProperty(String seriesProperty) {
		this.seriesProperty = seriesProperty;
	}

	public String getCategoryProperty() {
		return categoryProperty;
	}

	public void setCategoryProperty(String categoryProperty) {
		this.categoryProperty = categoryProperty;
	}
	
	

	public Set<SimpleCondition> getConditions() {
		return conditions;
	}

	public void setConditions(Set<SimpleCondition> conditions) {
		this.conditions = conditions;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

	public String getErrorProperty() {
		return errorProperty;
	}

	public void setErrorProperty(String errorProperty) {
		this.errorProperty = errorProperty;
	}

	public List<Node> getSliceProperties() {
		return sliceProperties;
	}

	public void setSliceProperties(List<Node> sliceProperties) {
		this.sliceProperties = sliceProperties;
	}

	@Override
	public String toString() {
		return "ConceptBasedSeries [conditions=" + conditions + ", seriesProperty=" + seriesProperty
				+ ", categoryProperty=" + categoryProperty + ", valueProperty=" + valueProperty + ", errorProperty="
				+ errorProperty + ", sliceProperties=" + sliceProperties + "]";
	}

	public Node getSeries() {
		return series;
	}

	public void setSeries(Node series) {
		this.series = series;
	}
	
}
