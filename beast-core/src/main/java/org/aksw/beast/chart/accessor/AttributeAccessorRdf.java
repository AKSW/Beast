package org.aksw.beast.chart.accessor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class AttributeAccessorRdf
	extends ModularAttributeAccessorGeneric<Resource, RDFNode>
{
	public AttributeAccessorRdf setProperty(Property property) {
		if(property == null) {
			this.valueAccessor = null;
		} else {
			this.valueAccessor = r -> Optional.of(r)
					.filter(RDFNode::isResource)
					.map(RDFNode::asResource)
					.filter(x -> x.hasProperty(property))
					.map(x -> x.getProperty(property).getObject())
					.orElse(null);
		}

		return this;

	}
	
	public AttributeAccessorRdf setConstant(RDFNode rdfNode) {
		this.valueAccessor = (r) -> rdfNode;
		
		return this;
	}
	
	public static RDFNode getPropertyOrSelf(RDFNode n, Property p) {
		RDFNode tmp = n.isResource()
				? Optional.ofNullable(n.asResource().getProperty(p)).map(Statement::getObject).orElse(null)
				: null;

		RDFNode result = tmp != null
				? tmp
				: n;
		
		return result;
	}
	
	public static String createSimpleLabel(RDFNode n) {
		String result = n.isURIResource()
				? n.asResource().getLocalName()
				: n.isLiteral()
					? Objects.toString(n.asLiteral().getValue())
					: Objects.toString(n)
					;
		return result;
	}
		
	public AttributeAccessorRdf setLabelPropery(Property property) {
		if(property == null) {
			this.labelAccessor = null;
		} else {
			this.labelAccessor = r -> createSimpleLabel(getPropertyOrSelf(r, property));
		}

		
		return this;
	}
	
//	public static Function<Resource, String> createPropertyAccessor(Property property) {
//		
//	}

    public static String getLabel(RDFNode node, Function<? super RDFNode, String> fn) {
        String result = node == null
                ? "(null)"
                : fn != null ? fn.apply(node) : node.toString();

        return result;
    }

    public static Object getValue(RDFNode node, Function<? super RDFNode, Object> fn) {
        Object result = node == null
                ? "(null)"
                : fn != null ? fn.apply(node) : (
                    node.isURIResource()
                        ? node.asResource().getLocalName()
                        : node.asLiteral().getValue()
                );


        return result;
    }
}