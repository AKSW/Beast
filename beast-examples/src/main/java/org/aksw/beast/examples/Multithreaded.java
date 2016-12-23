package org.aksw.beast.examples;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JFrame;

import org.aksw.beast.benchmark.performance.BenchmarkTime;
import org.aksw.beast.benchmark.performance.PerformanceBenchmark;
import org.aksw.beast.concurrent.ParallelStreams;
import org.aksw.beast.enhanced.ResourceEnh;
import org.aksw.beast.rdfstream.RdfGroupBy;
import org.aksw.beast.rdfstream.RdfStream;
import org.aksw.beast.viz.jfreechart.IguanaDatasetProcessors;
import org.aksw.beast.viz.jfreechart.RdfStatisticalDatasetAccessor;
import org.aksw.beast.viz.jfreechart.StatisticalCategoryDatasetBuilder;
import org.aksw.beast.viz.xchart.XChartStatBarChartProcessor;
import org.aksw.beast.vocabs.CV;
import org.aksw.beast.vocabs.IV;
import org.aksw.beast.vocabs.OWLTIME;
import org.aksw.iguana.vocab.IguanaVocab;
import org.aksw.simba.lsq.vocab.LSQ;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.expr.aggregate.AggAvg;
import org.apache.jena.sparql.expr.aggregate.lib.AccStatStdDevPopulation;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat;
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

        String jobExecBaseIri = "http://example.org/jobExec/";
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());

        Resource experiment = ResourceFactory.createResource(jobExecBaseIri + timeStamp);

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
            BenchmarkTime.benchmark(observationRes, () -> Thread.sleep(rand.nextInt(500)));
        };

        RdfStream<Resource, ResourceEnh> workflowTemplate =
                PerformanceBenchmark.createQueryPerformanceEvaluationWorkflow(queryAnalyzer, 2, 3);

        // Create a stream where each element is an instanciation of the workflowTemplate with our workload
        // Further, attach an identifier for the thread and craft the final IRI
        Stream<Stream<Resource>> workflowGen =
                IntStream.range(0, n).mapToObj(i ->
                    workflowTemplate.transform(workloads)
                    .peek(r -> r.addLiteral(IV.thread, i + 1))
                    .map(r -> r.rename("http://ex.org/thread{0}-run{1}-query{2}", IV.thread, IV.run, IV.job)));

        List<Resource> observations = ParallelStreams.join(workflowGen)
            .peek(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS))
            .collect(Collectors.toList());


        List<Resource> avgs =
        RdfGroupBy.enh()
            .on(IguanaVocab.workload)
            .on(IV.job)
            .on(IV.thread)
            .agg(CV.value, OWLTIME.numericDuration, AggAvg.class)
            .agg(CV.stDev, OWLTIME.numericDuration, AccStatStdDevPopulation.class)
            .apply(observations.stream())
            //.map(g -> g.rename("http://ex.org/avg/query{0}-user{1}", IV.job, IV.thread, IV.thread))
            .map(g -> g.rename("http://ex.org/avg/query{0}-user{1}", IV.job, IV.thread, IV.thread))
            .collect(Collectors.toList());


        // At this point we should write out all the observations.


        // Chart specific post processing
        avgs.forEach(g -> g
//                .addProperty(CV.series, g.getModel().createResource("http://example.org/Default")) // g.getProperty(IV.job).getObject()
//                .addProperty(CV.seriesLabel,g.getProperty(CV.series).getResource().getLocalName())
                .addProperty(CV.category, g.getProperty(IguanaVocab.workload).getObject())
                .addProperty(CV.categoryLabel, g.getProperty(CV.category).getResource().getLocalName())
                .addLiteral(CV.series, g.getProperty(IV.thread).getInt()) // g.getProperty(IV.job).getObject()
                .addLiteral(CV.seriesLabel, g.getProperty(IV.thread).getString()) // g.getProperty(IV.job).getObject()
         );

        avgs
        .forEach(r -> RDFDataMgr.write(System.out, r.getModel(), RDFFormat.TURTLE_BLOCKS));


//        File outFile = File.createTempFile("beast-", ".pdf").getAbsoluteFile();
        CategoryChart xChart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Score Histogram")
                .xAxisTitle("Score")
                .yAxisTitle("Number")
                .build();
        //xChart.getStyler().setY

        XChartStatBarChartProcessor.addSeries(xChart, avgs);

        xChart.getStyler().setLegendPosition(LegendPosition.InsideNW);

        xChart.getStyler().setYAxisMin(0.0001);
        xChart.getStyler().setYAxisMax(1.0);
        xChart.getStyler().setYAxisLogarithmic(true);
        xChart.getStyler().setYAxisTicksVisible(true);

        VectorGraphicsEncoder.saveVectorGraphic(xChart, "/tmp/Sample_Chart", VectorGraphicsFormat.SVG);

        new SwingWrapper<CategoryChart>(xChart).displayChart();



        if(false) {
      StatisticalCategoryDataset dataset =
      StatisticalCategoryDatasetBuilder.create(RdfStatisticalDatasetAccessor.create())
          .apply(avgs.stream());

      JFreeChart jChart = IguanaDatasetProcessors.createStatisticalBarChart(dataset);
      ChartPanel chartPanel = new ChartPanel(jChart);
      chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
      JFrame frame = new JFrame();
      frame.add(chartPanel);
      frame.pack();
      frame.setVisible(true);
        }

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
