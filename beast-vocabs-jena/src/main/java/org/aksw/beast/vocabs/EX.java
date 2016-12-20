package org.aksw.beast.vocabs;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class EX {
	public static final String ns = "http://example.org/";

	public static Resource resource(String localName) { return ResourceFactory.createResource(ns + localName); }
	public static Property property(String localName) { return ResourceFactory.createProperty(ns + localName); }

	//public static final Resource Observation = ResourceFactory.createResource("http://purl.org/linked-data/cube#Observation");
	public static final Property positive = property("positive");
	public static final Property negative = property("negative");

	public static final Property Sample = property("Sample");

}
