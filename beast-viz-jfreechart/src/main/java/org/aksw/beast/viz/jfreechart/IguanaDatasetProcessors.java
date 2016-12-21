package org.aksw.beast.viz.jfreechart;

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;


public class IguanaDatasetProcessors {
//
//    public static void main(String[] args) throws DocumentException, IOException {
//
//        Model m = RDFDataMgr.loadModel("/home/raven/tmp/iguana-test-data.ttl");
//
//
//
//
//        PrefixMapping pm = new PrefixMappingImpl();
//        pm.setNsPrefixes(PrefixMappingImpl.Extended);
//        pm.setNsPrefix("ex", "http://example.org/ontology#");
//        pm.setNsPrefix("ig", IguanaVocab.ns);
//        pm.setNsPrefix("lsq", LSQ.ns);
//        pm.setNsPrefix("time", OWLTIME.ns);
//        pm.setNsPrefix("qb", "http://datacube.org/ontology#");
//
//
//        Function<Query, Query> queryTransform = new QueryTransformPrefix(pm);
//
//        QueryExecutionFactory qef = FluentQueryExecutionFactory.from(m)
//                .config()
//                    .withQueryTransform(queryTransform)
//                    .withParser(SparqlQueryParserImpl.create(Syntax.syntaxARQ, new Prologue(pm)))
//                .end()
//                .create();
//
//
//        enrichWithAvgAndStdDeviation(m);
//        CategoryDataset dataset = createDataset(m);
//        //CategoryDataset dataset = createTestDataset();
//
//        JFreeChart chart = createStatisticalBarChart(dataset);
//        ChartUtilities2.saveChartAsPDF(new File("/home/raven/tmp/test.pdf"), chart, 1000, 500);
//        //ChartUtilities.saveChartAsPNG(new File("/home/raven/tmp/test.png"), chart, 50, 50);
//
//    }
//
//
//    public static QueryExecutionFactory createQef(Model model) {
//        PrefixMapping pm = new PrefixMappingImpl();
//        pm.setNsPrefixes(PrefixMappingImpl.Extended);
//        pm.setNsPrefix("ex", "http://example.org/ontology#");
//        pm.setNsPrefix("ig", IguanaVocab.ns);
//        pm.setNsPrefix("lsq", LSQ.ns);
//        pm.setNsPrefix("time", OWLTIME.ns);
//        pm.setNsPrefix("qb", "http://datacube.org/ontology#");
//
//
//        Function<Query, Query> queryTransform = new QueryTransformPrefix(pm);
//
//        QueryExecutionFactory qef = FluentQueryExecutionFactory.from(model)
//                .config()
//                    .withQueryTransform(queryTransform)
//                    .withParser(SparqlQueryParserImpl.create(Syntax.syntaxARQ, new Prologue(pm)))
//                .end()
//                .create();
//
//        return qef;
//    }
//
//    public static void enrichWithAvgAndStdDeviation(Model model) {
//        QueryExecutionFactory qef = createQef(model);
//
//        //CategoryDataset dataset = createDataset(m, qef);
//        // (avgExecutionTime, executorLabel, queryId)
//        // Create the avg execution time for each executor
//
//        qef.createQueryExecution(computeAvg).execConstruct(model);
//        qef.createQueryExecution(computeStdDev).execConstruct(model);
//
//        //model.write(System.out, "TURTLE");
//
//    }
//
//    /**
//     * The problemis, that we would have to generate URIs for (workload, series) combinations...
//     * But actually, we could for each run just attach a series attribute.
//     *
//     *
//     * Compute a set of mean values for
//     * - a given set of observations
//     * - projected onto a related set of resourecs
//     * - with each grouping corresponding to a data series
//     * - having measure property 'measure'
//     *
//     * Select ?s (Count(DISTINCT ?run) / SUM(?runTime) As ?avg) {
//     *   observations(?o)
//     *   projections(?o -> ?s)
//     *   dataseries(?o -> ?d)
//     *
//     *
//     * } Group by ?d
//     *
//     *
//     *
//     * @param model
//     * @param observations
//     * @param subjects
//     * @param groupings
//     * @param measure
//     */
////	public static void enrichMean(Model model, Concept observations, Relation projection, Relation measure, Relation series) {
////		ConceptUtils.createRelatedConcept(obs, projection);
////	}
//
//    /**
//     * Allocate the URIs for the data series and attach the id atributes
//     * x A Series ; x workloadId ?w ; x seriesId
//     */
//    public static final String computeAvg = String.join("\n",
//            "CONSTRUCT {",
//            "  ?s",
//            "    qb:dataset ?d ;",
//            "    ig:workload ?w ;",
//            "    ig:avg ?avg ;",
//            "    .",
//            "",
//            "}",
//            "{",
//            "  { SELECT (IRI(CONCAT('http://avg-', STR(?did), '-', STR(?wid))) AS ?s) ?d ?w (AVG(?m) AS ?avg) {",
//            "    ?x",
//            "      ig:workload ?w ;",
//            "      qb:dataset ?d ;",
//            "      time:numericDuration ?m ;",
//            "    .",
//            "    ?d rdfs:label ?did .",
//            "    ?w rdfs:label ?wid .",
//            "  } GROUP BY ?d ?w ?did ?wid}",
//            "}");
//
////	/**
////	 * Computes the average for each workload over all series (in all experiments)
////	 *
////	 *
////	 */
////	public static final String computeAvg = String.join("\n",
////		"CONSTRUCT {",
////		"    ?i ig:avg ?avg",
////		"}",
////		"{",
////		"  { SELECT (IRI(CONCAT('http://foo-', STR(?i), '-', STR(?r))) AS ?s) ?i ?r (AVG(?m) AS ?avg) {",
////		"    ?x",
////		"      ig:workload ?i ;",
////		"      time:numericDuration ?m ;",
////		"      .",
////		"  } GROUP BY ?i ?r }",
////		"}");
//
//
//    public static final String computeStdDev = String.join("\n",
//        "CONSTRUCT {",
//        "    ?i ig:stdDeviation ?stdDev",
//        "}",
//        "{",
//        "  { SELECT ?i (SUM(?sqErr) AS ?stdDev) {",
//        "    ?i",
//        "      ig:avg ?a ;",
//        "      qb:dataset ?d ;",
//        "      ig:workload ?w ;",
//        "    .",
//        "    ?x",
//        "      qb:dataset ?d ;",
//        "      ig:workload ?w ;",
//        "      time:numericDuration ?m ;",
//        "      .",
//        "    BIND(?m - ?a AS ?err)",
//        "    BIND(?err * ?err AS ?sqErr)",
//        "  } GROUP BY ?i }",
//        "}");
//
//    /**
//     * Prepare data for a stacked bar-chart
//     * for each query id, with different execution strategy, the taken time
//     *
//     *
//     * @param model
//     * @return
//     */
//    public static StatisticalCategoryDataset createDataset(Model model) {//Model model) {
//
//        String queryStr = String.join("\n",
//            "SELECT ?a ?sd ?did ?wid {",
//            "  ?s",
//            "    ig:avg ?a ;",
//            "    ig:stdDeviation ?sd ;",
//            "    qb:dataset/rdfs:label ?did ;",
//            "    ig:workload/rdfs:label ?wid ;", // ; lsq:text ?w]",
//            "    .",
//            "} ORDER BY ASC(?did) ASC(?wid)");
//
//        // standardabweichung := summe der quadratischen abweichungen vom mittelwert
//
//        //QueryExecutionFactoryQueryTransform
//
//        // get result vars and interpret them according to the order
//        QueryExecution qe = createQef(model).createQueryExecution(queryStr);
//        ResultSet rs = qe.execSelect();
//
////
//        //System.out.println("results:");
//        Set<String> seenSeries = new TreeSet<>(StringPrettyComparator::doCompare);
//        Set<String> seenWorkloads = new TreeSet<>(StringPrettyComparator::doCompare);
//        Map<List<String>, List<Number>> keyToValue = new HashMap<>();
//
//        while(rs.hasNext()) {
//            QuerySolution qs = rs.nextSolution();
//            Number a = qs.get("a").asLiteral().getDouble();
//            Number sd = qs.get("sd").asLiteral().getDouble();
//
//            String series = qs.get("did").asLiteral().getString();
//            String workload = qs.get("wid").asLiteral().getString();
//
//            seenSeries.add(series);
//            seenWorkloads.add(workload);
//
//            keyToValue.put(Arrays.asList(series, workload), Arrays.asList(a, sd));
//        }
//
//        DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
//        for(Comparable<?> serie : seenSeries) {
//            for(Comparable<?> workload : seenWorkloads) {
//                List<Number> vals = keyToValue.getOrDefault(Arrays.asList(serie, workload), Arrays.<Number>asList(0.0, 0.0));
//
//                result.add(vals.get(0), vals.get(1), serie, workload);
//            }
//        }
//
//        return result;
//    }
//
//    public static StatisticalCategoryDataset createTestDataset() {
//
//        final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
//
//        result.add(32.5, 17.9, "Series 1", "Type 1");
//        result.add(27.8, 11.4, "Series 1", "Type 2");
//        result.add(29.3, 14.4, "Series 1", "Type 3");
//        result.add(37.9, 10.3, "Series 1", "Type 4");
//
//        result.add(22.9,  7.9, "Series 2", "Type 1");
//        result.add(21.8, 18.4, "Series 2", "Type 2");
//        result.add(19.3, 12.4, "Series 2", "Type 3");
//        result.add(30.3, 20.7, "Series 2", "Type 4");
//
//        result.add(12.5, 10.9, "Series 3", "Type 1");
//        result.add(24.8,  7.4, "Series 3", "Type 2");
//        result.add(19.3, 13.4, "Series 3", "Type 3");
//        result.add(17.1, 10.6, "Series 3", "Type 4");
//
//        return result;
//
//    }
//
//
    public static JFreeChart createStatisticalBarChart(CategoryDataset dataset) {
        final CategoryAxis xAxis = new CategoryAxis("Type");
        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.3d); // percentage of space between
                                        // categories
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        final LogarithmicAxis yAxis = new LogarithmicAxis("Time (s)");//new NumberAxis("Value");
        yAxis.setAutoRange(true);
        //yAxis.setAutoRangeMinimumSize(/size);
        yAxis.setLowerBound(0.0001);
        yAxis.setMinorTickMarksVisible(true);
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setStrictValuesFlag(false);

        // define the plot
        final CategoryItemRenderer renderer = new StatisticalBarRenderer();
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart("Performance Comparison", new Font("Helvetica", Font.BOLD, 14),
                plot, true);

        return chart;
    }

}
