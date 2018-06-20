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
    public static final String ns = "http://aksw.org/chart-vocab/";

    public static Resource resource(String localName) { return ResourceFactory.createResource(ns + localName); }
    public static Property property(String localName) { return ResourceFactory.createProperty(ns + localName); }

    public static final Resource StatisticalBarChart = resource("StatisticalBarChart");
    public static final Resource ConceptBasedSeries = resource("ConceptBasedSeries");
    
    
    public static final Resource DataItem = resource("DataItem");

    public static final Property sliceProperty = property("sliceProperty");
    
    // TODO series has 3 overleads - separate them
    // - it connects a series spec to a chart resource
    // - it denotes the series in the transformed dataset which serves as input to the chart
    // - it denotes a constant series within a series spec
    public static final Property series = property("series");
    public static final Property category = property("category");
    public static final Property value = property("value");
    public static final Property error = property("error");
    
    public static final Property seriesLabel = property("seriesLabel");
    public static final Property categoryLabel = property("categoryLabel");

    public static final Property stDev = property("stdDev");

    public static final Property seriesProperty = property("seriesProperty");
    public static final Property categoryProperty = property("categoryProperty");
    public static final Property valueProperty = property("valueProperty");
    public static final Property errorProperty = property("errorProperty");


}
