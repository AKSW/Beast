package org.aksw.iguana.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class IguanaVocab {
    public static final String ns = "http://iguana.aksw.org/ontology#";

    public static Resource resource(String local) { return ResourceFactory.createResource(ns + local); }
    public static Property property(String local) { return ResourceFactory.createProperty(ns + local); }

    // Used internally for the hypergraph representation - not part of the public vocab
    public static final Property queryId = property("queryId");
    public static final Property workload = property("workload");
    public static final Property run = property("run");
    //public static final Property numericDuration = property(ns + "numericDuration");


}