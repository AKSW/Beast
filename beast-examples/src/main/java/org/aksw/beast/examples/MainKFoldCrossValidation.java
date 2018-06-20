package org.aksw.beast.examples;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.aksw.beast.collections.crossvalidation.Fold;
import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.vocabs.EX;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.QB;
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
                configureFoldParser(3, EX.positive, EX.negative, (rdfNode) -> rdfNode.toString());

        RdfStream.start()
            .flatMap(workloadRes ->
                    (Stream<ResourceEnh>)StreamUtils.zipWithIndex(foldParser.apply(workloadRes).stream())
                    .map(indexed ->
                        ResourceEnh.copyClosure(workloadRes).getModel().createResource().as(ResourceEnh.class)
                        .addTag(indexed.getValue())
                        .addProperty(RDF.type, QB.Observation)
                        .addLiteral(IV.phase, indexed.getIndex())
                        .as(ResourceEnh.class)))

            .peek(phaseRes -> logger.info("Executing phase: "
                        + phaseRes.getProperty(IV.phase).getInt() + ": "
                        + phaseRes.getTag(Fold.class).get()))

            .repeat(2, IV.run, 1)
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

            List<T> ps = r.getProperty(positive).getObject().as(RDFList.class).iterator().mapWith(itemParser).toList();
            List<T> ns = r.getProperty(negative).getObject().as(RDFList.class).iterator().mapWith(itemParser).toList();

            Collections.shuffle(ps);
            Collections.shuffle(ns);
            logger.info("Fold parser invoked - shuffled examples:");
            logger.info("Positives: " + ps);
            logger.info("Negatives: " + ns);

            List<Fold<T>> result = Fold.createFolds(ps, ns, n);
            return result;
        };
    }

}
