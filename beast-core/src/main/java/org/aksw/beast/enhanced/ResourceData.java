package org.aksw.beast.enhanced;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.ResourceUtils;

/**
 * A Resource implementation with a field for data.
 * The data is transient - i.e. it is not reconstructed from the underlying RDF graph
 *
 * This class is to bundle a Java object with something that can be described and referenced from RDF.
 * Main use case is for RDF reporting of execution of Java tasks, where a ResourceData can
 * be used in reporting anything related to the workload/payload/task data.
 *
 *
 *
 * @author Claus Stadler
 *
 */
public class ResourceData<T>
    extends ResourceImpl
{
    protected T data;

    public ResourceData(Node n, EnhGraph m) {
        this(n, m, null);
    }

    public ResourceData(Node n, EnhGraph m, T data) {
        super(n, m);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return super.toString() + ", data: " + data;
    }

    public <X extends T> ResourceData<X> castTo(Class<X> clazz) {
        return new ResourceData<>(node, enhGraph, (X)data);
    }

    public ResourceData<T> rename(String uriTemplate, Object ... objs) {
        String uri = objs.length == 0 ? uriTemplate : ResourceEnh.interpolate(this, uriTemplate, objs);


        Resource tmp = ResourceUtils.renameResource(this, uri);//.as(ResourceEnh.class);
        ResourceData<T> result = new ResourceData<>(tmp.asNode(), enhGraph);

        return result;
    }
//
//    final static public Implementation factory = new Implementation() {
//        @Override
//        public boolean canWrap( Node n, EnhGraph eg )
//            { return !n.isLiteral(); }
//        @Override
//        public EnhNode wrap(Node n,EnhGraph eg) {
//            if (n.isLiteral()) throw new ResourceRequiredException( n );
//            return new ResourceData<Object>(n,eg);
//        }
//    };
}
