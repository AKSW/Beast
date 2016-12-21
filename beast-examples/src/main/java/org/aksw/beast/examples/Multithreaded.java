package org.aksw.beast.examples;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.aksw.beast.benchmark.performance.BenchmarkTime;
import org.aksw.beast.concurrent.ParallelStreams;
import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfGroupBy;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.viz.jfreechart.ChartUtilities2;
import org.aksw.beast.viz.jfreechart.IguanaDatasetProcessors;
import org.aksw.beast.viz.jfreechart.RdfStatisticalDatasetAccessor;
import org.aksw.beast.viz.jfreechart.StatisticalCategoryDatasetBuilder;
import org.aksw.beast.vocabs.CV;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.OWLTIME;
import org.aksw.iguana.vocab.IguanaVocab;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.expr.aggregate.AggAvg;
import org.apache.jena.sparql.expr.aggregate.lib.AccStatStdDevPopulation;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Multithreaded {

    private static final Logger logger = LoggerFactory.getLogger(Multithreaded.class);

    public static void main(String[] args) throws Exception {

        // Number of workflows to generate
        int n = 3;

        // Set up workloads and workflows

        Model m = RDFDataMgr.loadModel("queries.ttl");
        List<Resource> workloads = m.listSubjectsWithProperty(LSQ.text).toList();

        // Fake query execution
        Random rand = new Random();
        BiConsumer<Resource, Query> queryAnalyzer = (observationRes, query) -> {
            logger.debug("Faking query execution: " + observationRes + " with " + query);
            BenchmarkTime.benchmark(observationRes, () -> Thread.sleep(rand.nextInt(100)));
        };

        RdfStream<Resource, ResourceEnh> workflowTemplate =
                MainQueryPerformance.createQueryPerformanceEvaluationWorkflow(queryAnalyzer, 2, 3);

        // Create a stream where each element is an instanciation of the workflowTemplate with our workload
        // Further, attach an identifier for the thread and craft the final IRI
        Stream<Stream<Resource>> workflowGen =
                IntStream.range(0, n).mapToObj(i ->
                    workflowTemplate.apply(() -> workloads.stream()).get()
                    .peek(r -> r.addLiteral(IV.thread, i + 1))
                    .map(r -> r.rename("http://ex.org/thread{0}-run{1}-query{2}", IV.thread, IV.run, IV.job)));

        List<Resource> observations = ParallelStreams.join(workflowGen)
            //.peek(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS))
            .collect(Collectors.toList());


//        avgAndStdDev(observations.stream(), OWLTIME.numericDuration, Arrays.asList(IguanaVocab.workload, IV.job))
//                .forEach(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS));


        // TODO How to run nested aggregation expressions?
        // Well, evaluate inner expression first, and substitute the value for the rest...
        // e.g. SUM(x - AVG(x))

        List<Resource> avgs =
        RdfGroupBy.enh()
            .on(IguanaVocab.workload)
            .on(IV.job)
            .agg(CV.value, OWLTIME.numericDuration, AggAvg.class)
            //.agg(CV.stDev, OWLTIME.numericDuration, AggStDev.class)
            .agg(CV.stDev, OWLTIME.numericDuration, AccStatStdDevPopulation.class)
            //.agg(IV.phase, OWLTIME.numericDuration, AccStatStdDevSample.class)
            .apply(observations.stream())
            .map(g -> g.rename("http://ex.org/avg/query-{0}", IV.job))
            .peek(g -> g
                    .addProperty(CV.series, g.getProperty(IguanaVocab.workload).getObject())
                    .addProperty(CV.category, "cat")
                    .addProperty(CV.categoryLabel, "catlabel")
                    .addProperty(CV.seriesLabel, g.getProperty(IV.job).getObject())
             )

            .collect(Collectors.toList());

        avgs
            .forEach(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS));

        StatisticalCategoryDataset dataset =
        StatisticalCategoryDatasetBuilder.create(RdfStatisticalDatasetAccessor.create())
            .apply(avgs.stream());

        JFreeChart chart = IguanaDatasetProcessors.createStatisticalBarChart(dataset);
        ChartUtilities2.saveChartAsPDF(new File("/home/raven/tmp/beast.pdf"), chart, 1000, 500);

    }
//
//    public static Stream<Resource> avgAndStdDev(Stream<Resource> observations, Property valueProperty, List<Object> groupProperties) {
//        return RdfGroupBy.enh()
//        .on(IguanaVocab.workload)
//        .on(IV.job)
//
////      These lines would copy the triples of each member to that of the group, and linke the group back to the members
////      .peek((group, member) -> group.getModel().add(member.getModel()))
////      .peek((group, member) -> group.addProperty(RDFS.seeAlso, member.inModel(group.getModel())))
//
//        .agg(IV.experiment, OWLTIME.numericDuration, AggAvg.class)
//        .apply(observations)
//        .map(g -> g.rename("http://ex.org/avg/query-{0}", IV.job))
//        ;
//
//    }

}
