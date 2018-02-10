package org.aksw.beast.viz.xchart;

import java.util.Optional;
import java.util.function.Function;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class AttributeAccessorRdf
	extends ModularAttributeAccessorGeneric<Resource, RDFNode>
{
	public AttributeAccessorRdf setProperty(Property property) {
		if(property == null) {
			this.valueAccessor = null;
		} else {
			this.valueAccessor = (r) -> Optional.of(r)
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
	
	public AttributeAccessorRdf setLabelPropery(Property property) {
		if(property == null) {
			this.labelAccessor = null;
		} else {
			this.labelAccessor = r -> Optional.of(r)
					.filter(RDFNode::isResource)
					.map(RDFNode::asResource)
					.map(x -> x.hasProperty(property) ? x.getProperty(property).getObject() : (RDFNode)x)
					.map(x -> x.isLiteral()
							? x.asLiteral().getValue()
							: (Object)(r.isURIResource() ? r.asResource().getLocalName() : "" + x))
					.orElse(null);
		}

		
		return this;
	}

	

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