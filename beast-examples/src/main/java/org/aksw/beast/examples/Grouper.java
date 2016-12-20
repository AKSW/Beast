package org.aksw.beast.examples;

import java.lang.reflect.Constructor;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aksw.jena_sparql_api.mapper.AccJenaWrapper;
import org.aksw.jena_sparql_api.mapper.Accumulator;
import org.aksw.jena_sparql_api.rdf_stream.enhanced.ModelFactoryEnh;
import org.aksw.jena_sparql_api.rdf_stream.enhanced.ResourceEnh;
import org.aksw.jena_sparql_api.utils.Vars;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.util.ModelUtils;

import com.codepoetics.protonpack.StreamUtils;

public class Grouper<I extends Resource, O extends Resource>
	implements Function<Stream<I>, Stream<O>>
{
	/**
	 * Create a grouper which directly returns enhanced resources.
	 *
	 * @return
	 */
	public static <X extends Resource> Grouper<X, ResourceEnh> enh() {
		return new Grouper<X, ResourceEnh>(Grouper::createGroupResource);
	}

	public Grouper(Supplier<O> resourceSupplier) {
		super();
		this.resourceSupplier = resourceSupplier;
	}



	/**
	 * This map corresponds to the GROUP BY clause.
	 * - The key is the result property that will be attached to the group resource.
	 * - The value is the property accessor for an attribute of the member resource.
	 *
	 * The group key is list of values obtained invoking all property accessors on a member.
	 */
	protected Map<Property, Function<Resource, RDFNode>> groupAttrToExpr = new LinkedHashMap<>();

	/**
	 * This map corresponds to the SELECT clause and holds the aggregation expressions.
	 * - The key is the result property tha will be attached to the group resource.
	 *
	 *
	 * Note: The types Node and NodeValue are directly compatible with jena's accumulator
	 */
	protected Map<Property, Entry<Function<Resource, Binding>, Supplier<Accumulator<Binding, NodeValue>>>> keysToAgg = new LinkedHashMap<>();

	protected BiConsumer<O, I> callback = null;
	protected Supplier<O> resourceSupplier;

	//protected <G extends Resource, M extends Resource> BiConsumer<G, M> callback = null;

	public Grouper<I, O> on(Property property) {

		on(property, property);

		return this;
	}

	public Grouper<I, O> on(Property tgtProperty, Property srcProperty) {

		on(tgtProperty, (r) -> r.getProperty(srcProperty).getObject());

		return this;
	}

	public Grouper<I, O> on(Property tgtProperty, Function<Resource, RDFNode> propertyAccessor) {

		groupAttrToExpr.put(tgtProperty, propertyAccessor);

		return this;
	}

	/**
	 * Peek is called whenever a member resource's corresponding group
	 * has been identified.
	 * At this time, the group resource already has its key attributes associated,
	 * but aggregation has not been performed yet.
	 *
	 * @param cb
	 * @return
	 */
	public Grouper<I, O> peek(BiConsumer<O, I> cb) {
		callback = callback == null ? cb : callback.andThen(cb);

		return this;
	}

	public static RDFNode getObject(Resource r, Property p) {
		Statement stmt = r.getProperty(p);
		RDFNode result = stmt == null ? null : stmt.getObject();
		return result;
	}

	public Grouper<I, O> agg(Property tgtProperty, Property srcProperty, Class<? extends org.apache.jena.sparql.expr.aggregate.Aggregator> aggClazz) {

		agg(tgtProperty, (r) -> getObject(r, srcProperty), aggClazz);

		return this;
	}

	/**
	 * Function to re-use jena aggregators
	 *
	 * agg(MyVocab.avg, MyVocab.measure, AggAvg.class)
	 *
	 * @param tgtProperty
	 * @param aggClazz
	 * @return
	 */
	public Grouper<I, O> agg(Property tgtProperty, Function<Resource, RDFNode> propertyAccessor, Class<? extends org.apache.jena.sparql.expr.aggregate.Aggregator> aggClazz) {

		Function<Resource, Binding> bindingAccessor = (r) -> {
			RDFNode rdfNode = propertyAccessor.apply(r);
			Node node = rdfNode == null ? null : rdfNode.asNode();
			BindingHashMap binding = new BindingHashMap();
			if(node != null) {
				binding.add(Vars.s, node);
			}
			return binding;
		};

		try {
			try {
				Constructor<? extends Aggregator> x = aggClazz.getConstructor(Expr.class);
				ExprVar s = new ExprVar(Vars.s);

				Aggregator a = x.newInstance(s);

				Map<Var, Function<Node, Node>> mapper = new HashMap<>();
				mapper.put(Vars.s, (node) -> node);

				Supplier<Accumulator<Binding, NodeValue>> agg = () -> new AccJenaWrapper(a.createAccumulator());

				keysToAgg.put(tgtProperty, new SimpleEntry<>(bindingAccessor, agg));

			} catch(NoSuchMethodException e) {

			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	/**
	 * Compute the value vector of the grouping properties
	 *
	 * @param r
	 * @param groupAttrToExpr
	 * @return
	 */
	public static List<RDFNode> computeValues(Resource r, Map<Property, Function<Resource, RDFNode>> groupAttrToExpr) {
		List<RDFNode> result = groupAttrToExpr.values().stream()
				.map(fn -> fn.apply(r))
				.collect(Collectors.toList());
		return result;
	}

	public static ResourceEnh createGroupResource() {
		return ModelFactoryEnh.createModel().createResource().as(ResourceEnh.class);
	}

	protected O getOrCreateGroup(
			I member,
			Map<Property, Function<Resource, RDFNode>> groupAttrToExpr,
			Map<List<RDFNode>, O> keysToRes,
			Map<O, List<Accumulator<Binding, NodeValue>>> groupToAccs
			) {

		List<RDFNode> keys = computeValues(member, groupAttrToExpr);

		O result = keysToRes.computeIfAbsent(keys, (ks) -> resourceSupplier.get());


		// Attach key properties
		StreamUtils
			.zip(groupAttrToExpr.keySet().stream(), keys.stream(), SimpleEntry<Property, RDFNode>::new)
			.forEach(e -> {
				Property p = e.getKey();
				RDFNode value = e.getValue();
				if(value != null) {
					result.addProperty(p, value);
				}
			});


		// Get or create the accumulators for the group
		List<Accumulator<Binding, NodeValue>> accs = groupToAccs.get(result);

		if(accs == null) {
			// Allocate the accumulators for that group
			accs = keysToAgg.values().stream()
				.map(Entry::getValue)
				.map(Supplier::get)
				.collect(Collectors.toList());
//ModelUtils.convertGraphNodeToRDFNode(n, model)
			groupToAccs.put(result, accs);
		}

		// Zip property accessors and accumulators
		// Invoke the accessors and put the values into the accumulators
		StreamUtils
			.zip(
				keysToAgg.values().stream().map(Entry::getKey),
				accs.stream(),
				SimpleEntry<Function<Resource, Binding>, Accumulator<Binding, NodeValue>>::new)
			.forEach(e -> {
				Binding b = e.getKey().apply(member);
				e.getValue().accumulate(b);
			});


		//accs.forEach(acc -> acc.accumulate(member));

		if(callback != null) {
			callback.accept(result, member);
		}

		return result;
	}

	public void postProcessGroup(
			O group,
			Map<O, List<Accumulator<Binding, NodeValue>>> groupToAccs,
			Stream<Property> properties) {

		Stream<RDFNode> values = groupToAccs.get(group).stream()
			.map(Accumulator::getValue)
			.map(nodeValue -> ModelUtils.convertGraphNodeToRDFNode(nodeValue.asNode(), group.getModel()));

		StreamUtils.zip(properties, values, SimpleEntry<Property, RDFNode>::new)
			.forEach(e -> group.addProperty(e.getKey(), e.getValue()));
	}

	@Override
	public Stream<O> apply(Stream<I> stream) {

		// Map each group's value vector to the group's representing resource
		Map<List<RDFNode>, O> keysToRes = new HashMap<>();

		Map<O, List<Accumulator<Binding, NodeValue>>> groupToAccs = new HashMap<>();
		// Create a map from group to members
		;
		Set<O> tmp = stream
			.map(member -> getOrCreateGroup(member, groupAttrToExpr, keysToRes, groupToAccs))
			.collect(Collectors.toSet());

		// Post process all groups
		tmp.forEach(g -> postProcessGroup(g, groupToAccs, keysToAgg.keySet().stream()));

		Stream<O> result = tmp.stream();

		return result;
	}

}
