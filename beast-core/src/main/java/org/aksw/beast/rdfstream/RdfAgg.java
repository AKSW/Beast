package org.aksw.beast.rdfstream;

import java.util.Map;

import org.apache.jena.rdf.model.Property;

/**
 * A facade for common aggregation tasks on RDF data
 *
 * @author raven
 *
 */
public class RdfAgg {
    Map<Property, Object> properties;

//    public static RdfAgg createDefault(Property srcProperty) {
//        .on(IV.job) // This is just the local name of the workload
//        .agg(RDFS.label, OWLTIME.numericDuration, AggSum.class) // total time
//        .agg(CV.value, OWLTIME.numericDuration, AggAvg.class)
//        .agg(CV.stDev, OWLTIME.numericDuration, AccStatStdDevPopulation.class)
//
//    }
}
