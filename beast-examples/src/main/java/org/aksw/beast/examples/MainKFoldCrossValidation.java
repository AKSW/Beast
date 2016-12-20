package org.aksw.beast.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.aksw.commons.collections.Fold;
import org.aksw.jena_sparql_api.rdf_stream.core.RdfStream;
import org.aksw.jena_sparql_api.rdf_stream.enhanced.ResourceEnh;
import org.aksw.jena_sparql_api.vocabs.IV;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codepoetics.protonpack.StreamUtils;

public class MainKFoldCrossValidation {

	private static final Logger logger = LoggerFactory.getLogger(MainKFoldCrossValidation.class);


	public static void main(String[] args) {

		Model m = RDFDataMgr.loadModel("folds.ttl");

		List<Resource> workloads = m.listSubjectsWithProperty(RDF.type, EX.Sample).toList();

		Function<Resource, List<Fold<String>>> foldParser =
				configureFoldParser(5, EX.positive, EX.negative, (rdfNode) -> rdfNode.toString());

		RdfStream.startWithCopy()
			.flatMap(workloadRes ->
					(Stream<ResourceEnh>)StreamUtils.zipWithIndex(foldParser.apply(workloadRes).stream())
					.map(indexed ->
						(ResourceEnh)workloadRes.getModel().createResource().as(ResourceEnh.class)
						.addTrait(indexed.getValue())
						.addProperty(RDF.type, QB.Observation)
						.addLiteral(IV.phase, indexed.getIndex())
						.as(ResourceEnh.class)))

			.peek(phaseRes -> logger.info("Executing phase: "
						+ phaseRes.getProperty(IV.phase).getInt() + ": "
						+ phaseRes.getTrait(Fold.class).get()))

			.repeat(5, IV.run, 1)
			.map(phaseRes -> phaseRes.rename("http://example.org/observation/run{0}-fold{1}", IV.run, IV.phase))
			.apply(() -> workloads.stream()).get()
			.forEach(phaseRes -> RDFDataMgr.write(System.out, phaseRes.getModel(), RDFFormat.TURTLE_BLOCKS))
		;

		logger.info("Done.");
	}

	/**
	 * A small parser for obtaining a list of folds from a resource having
	 * a set of positive and negative examples attached
	 *
	 *
	 * Note: The bold may integrate fold parsing as a Personality of Model
	 *
	 * @param positive
	 * @param negative
	 * @param itemParser
	 * @return
	 */
	public static <T> Function<Resource, List<Fold<T>>> configureFoldParser(
			int n,
			Property positive,
			Property negative,
			Function<RDFNode, T> itemParser) {
		return (r) -> {
			logger.info("Fold parser invoked");

			List<T> ps = r.getProperty(positive).getObject().as(RDFList.class).iterator().mapWith(itemParser).toList();
			List<T> ns = r.getProperty(negative).getObject().as(RDFList.class).iterator().mapWith(itemParser).toList();
			//Iterable<T> it = Iterables.concat(ps, ns);
			List<T> tmp = new ArrayList<T>();
			tmp.addAll(ps);
			tmp.addAll(ns);
			Collections.shuffle(tmp);

			List<Fold<T>> result = Fold.createFolds(tmp, n);
			return result;
		};
	}

}
