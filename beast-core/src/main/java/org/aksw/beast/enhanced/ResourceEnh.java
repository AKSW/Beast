package org.aksw.beast.enhanced;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.ext.com.google.common.base.Optional;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.ResourceUtils;

import com.google.common.collect.MutableClassToInstanceMap;

/**
 * Enhancable resource which enables painless association of Java objects to it.
 * Useful for passing resources through a workflow, where on output should be directly attached
 * to the resources based on processing data stored in Java objects.
 *
 * The ResourceEnh will only allocate an entry for its java objects, and will
 * remove its entry once all associated objects have been removed.
 *
 * <pre>
 * {@code
 * Stream<Resource> stream
 *   .map(r -> r.as(EnhResource.class))
 *   .peek(r ->r.addTag(QueryFactory.parse(r.getProperty(LSQ.text, ...)))
 *   .peek(r -> benchmarkQuery(r, r.getTag(Query.class), service)
 *   .foreach(r.getModel().write(System.out, "TURTLE")))
 * }
 * </pre>
 *
 *
 * r.addTag("hello")
 * r.addTag(Base.class, new Derived())
 *
 * r.getTag(String.class)
 * r.removeTag(String.class)
 *
 * Note: r.addTrait(o) is equivalent to r.addTrait(o.getClass(), o);
 *
 * @author Claus Stadler
 *
 */
public class ResourceEnh
    extends ResourceImpl
{
    protected Map<Node, MutableClassToInstanceMap<Object>> meta;

    public ResourceEnh(Node n, EnhGraph m, Map<Node, MutableClassToInstanceMap<Object>> meta) {
        super(n, m);
        Objects.requireNonNull(meta);
        this.meta = meta;
    }

    @SuppressWarnings("deprecation")
    public ResourceEnh copyTagsFrom(Resource other) {
        if(other.canAs(ResourceEnh.class)) {
            Node tmp = other.asNode();
            MutableClassToInstanceMap<Object> srcMap = meta.get(tmp);

            if(srcMap != null) {
                MutableClassToInstanceMap<Object> tgtMap = getOrCreateTagMap();
                tgtMap.putAll(srcMap);
            }
        }

        return this;
    }

    public MutableClassToInstanceMap<Object> getOrCreateTagMap() {
        MutableClassToInstanceMap<Object> map = meta.get(node);
        if(map == null) {
            map = MutableClassToInstanceMap.create();
            meta.put(node, map);
        }

        return map;
    }

    public ResourceEnh addTag(Object o) {
        Objects.requireNonNull(o);

        addTag(o.getClass(), o);

        return this;
    }

    @SuppressWarnings("deprecation")
    public ResourceEnh addTag(Class<?> clazz, Object o) {
        MutableClassToInstanceMap<Object> map = getOrCreateTagMap();
        map.put(clazz, o);

        return this;
    }

    public ResourceEnh removeTag(Object clazz) {
        MutableClassToInstanceMap<Object> map = meta.get(node);
        if(map != null) {
            map.remove(clazz);

            if(map.isEmpty()) {
                meta.remove(node);
            }
        }

        return this;
    }

    public <T> Optional<T> getTag(Class<T> clazz) {
        MutableClassToInstanceMap<Object> map = meta.get(node);
        T tmp = map == null ? null : map.getInstance(clazz);
        return Optional.fromNullable(tmp);
    }

    public ResourceEnh clearTag() {
        MutableClassToInstanceMap<Object> map = meta.get(node);
        if(map != null) {
            map.clear();
            meta.remove(node);
        }

        return this;
    }

    public static Object resolve(Resource r, Object o) {
        Object result;
        if(o instanceof Property) {
            result = r.getRequiredProperty((Property)o).getObject().asLiteral().getValue();
        } else if(o instanceof Supplier) {
            Object tmp = ((Supplier<?>)o).get();
            result = resolve(r, tmp);
        } else {
            result = o;
        }
        return result;
    }

    public static String interpolate(Resource r, String template, Object ... objs) {

        List<Object> tmp = Arrays.asList(objs).stream()
            .map(o -> resolve(r, o))
            .collect(Collectors.toList());

        Object[] xs = new Object[tmp.size()];
        tmp.toArray(xs);

        String result = MessageFormat.format(template, xs);
        return result;
    }

    public ResourceEnh rename(String uriTemplate, Object ... objs) {
        String uri = objs.length == 0 ? uriTemplate : interpolate(this, uriTemplate, objs);


        ResourceEnh result = ResourceUtils.renameResource(this, uri).as(ResourceEnh.class);

        MutableClassToInstanceMap<Object> srcMap = meta.get(node);
        if(srcMap != null) {
            Node tgtNode = this.getModel().createResource(uri).asNode();
            MutableClassToInstanceMap<Object> tgtMap = meta.get(tgtNode);

            if(tgtMap != null) {
                tgtMap.putAll(srcMap);
            } else {
                meta.put(tgtNode, srcMap);
            }
        }

        return result;
    }

    public static ResourceEnh copyClosure(Resource task) {
        Model m = ModelFactoryEnh.createModel();
        m.add(ResourceUtils.reachableClosure(task));
        ResourceEnh result = task.inModel(m).as(ResourceEnh.class);
        return result;
    }

}
