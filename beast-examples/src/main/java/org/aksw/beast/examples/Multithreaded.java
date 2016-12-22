package org.aksw.beast.examples;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.aksw.beast.benchmark.performance.BenchmarkTime;
import org.aksw.beast.benchmark.performance.PerformanceBenchmark;
import org.aksw.beast.concurrent.ParallelStreams;
import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfGroupBy;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.viz.xchart.XChartStatBarChartProcessor;
import org.aksw.beast.vocabs.CV;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.OWLTIME;
import org.aksw.iguana.vocab.IguanaVocab;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.expr.aggregate.AggAvg;
import org.apache.jena.sparql.expr.aggregate.lib.AccStatStdDevPopulation;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Multithreaded {

    private static final Logger logger = LoggerFactory.getLogger(Multithreaded.class);

    public static void main(String[] args) throws Exception {

        // Number of workflows to generate
        int n = 5;

        // Set up workloads and workflows

//        Model m = RDFDataMgr.loadModel("queries.ttl");
//        List<Resource> workloads = m.listSubjectsWithProperty(LSQ.text).toList();

        //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        Resource experiment = ResourceFactory.createResource();

        // TODO Exception in query parsing is swallowed
        List<Resource> workloads = IntStream.range(0, 20)
                .mapToObj(i ->
                    ModelFactory.createDefaultModel().createResource("http://ex.org/q" + i)
                        .addProperty(LSQ.text, "SELECT * { ?s ?p ?o }"))
                .collect(Collectors.toList());

        // Fake query execution
        Random rand = new Random();
        BiConsumer<Resource, Query> queryAnalyzer = (observationRes, query) -> {
            logger.debug("Faking query execution: " + observationRes + " with " + query);
            BenchmarkTime.benchmark(observationRes, () -> Thread.sleep(rand.nextInt(50)));
        };

        RdfStream<Resource, ResourceEnh> workflowTemplate =
                PerformanceBenchmark.createQueryPerformanceEvaluationWorkflow(queryAnalyzer, 2, 3);

        // Create a stream where each element is an instanciation of the workflowTemplate with our workload
        // Further, attach an identifier for the thread and craft the final IRI
        Stream<Stream<Resource>> workflowGen =
                IntStream.range(0, n).mapToObj(i ->
                    workflowTemplate.apply(() -> workloads.stream()).get()
                    .peek(r -> r.addLiteral(IV.thread, i + 1))
                    .map(r -> r.rename("http://ex.org/thread{0}-run{1}-query{2}", IV.thread, IV.run, IV.job)));

        List<Resource> observations = ParallelStreams.join(workflowGen)
            .peek(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS))
            .collect(Collectors.toList());


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
            .collect(Collectors.toList());


        // At this point we should write out all the observations.


        // Chart specific post processing
        avgs.forEach(g -> g
                .addProperty(CV.series, g.getModel().createResource("http://example.org/Default")) // g.getProperty(IV.job).getObject()
                .addProperty(CV.category, g.getProperty(IguanaVocab.workload).getObject())
                .addProperty(CV.categoryLabel, g.getProperty(CV.category).getResource().getLocalName())
                .addProperty(CV.seriesLabel,g.getProperty(CV.series).getResource().getLocalName())
         );

        avgs
        .forEach(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS));

//        StatisticalCategoryDataset dataset =
//        StatisticalCategoryDatasetBuilder.create(RdfStatisticalDatasetAccessor.create())
//            .apply(avgs.stream());

//        File outFile = File.createTempFile("beast-", ".pdf").getAbsoluteFile();
        CategoryChart xChart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Score Histogram")
                .xAxisTitle("Score")
                .yAxisTitle("Number")
                .build();

        xChart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        //xChart.getStyler().setYAxisLogarithmic(true);
        //xChart.getStyler().setY

        XChartStatBarChartProcessor.addSeries(xChart, avgs);

        new SwingWrapper<CategoryChart>(xChart).displayChart();

//        JFreeChart chart = IguanaDatasetProcessors.createStatisticalBarChart(dataset);
//        ChartUtilities2.saveChartAsPDF(outFile, chart, 1000, 500);
//
//        logger.info("Chart written to " + outFile.getAbsolutePath());
    }


// TODO Probably we should provide static utility methods that make it easy to output RDF in the hacky chart vocabulary
// In order to quickly see some chart
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
