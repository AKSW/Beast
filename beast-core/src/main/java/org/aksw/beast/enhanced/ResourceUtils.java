package org.aksw.beast.enhanced;

import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.Path;

import com.github.andrewoma.dexx.collection.Function;

/**
 * A collection of utiltity functions for resolving (indirect) property values of resources
 *
 * @author raven
 *
 */
public class ResourceUtils {
    /**
     * Create a function that retrieves a property
     *
     * @param path
     * @return
     */
    public Function<Resource, Set<RDFNode>> createPropertyResolver(Path path) {

        return null;
    }

    public Function<Resource, Set<RDFNode>> createPropertyResolver(Property property) {
        return null;
    }
}
