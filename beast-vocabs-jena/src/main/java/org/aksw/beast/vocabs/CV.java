package org.aksw.beast.vocabs;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Chart vocabulary
 *
 * This is just a convenience vocab (aka useful for hacking).
 * Avoid exposing data modeled with it to the public.
 *
 * @author raven
 *
 */
public class CV {
    public static final String ns = "http://example.org/chart-vocab/";

    public static Resource resource(String localName) { return ResourceFactory.createResource(ns + localName); }
    public static Property property(String localName) { return ResourceFactory.createProperty(ns + localName); }

    public static final Property series = property("series");
    public static final Property category = property("category");

    public static final Property seriesLabel = property("seriesLabel");
    public static final Property categoryLabel = property("categoryLabel");

    public static final Property value = property("value");
    public static final Property stDev = property("stdDev");
}
