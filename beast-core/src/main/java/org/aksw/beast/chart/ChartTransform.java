package org.aksw.beast.chart;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.Root;

import org.aksw.beast.chart.model.ConceptBasedSeries;
import org.aksw.beast.chart.model.StatisticalBarChart;
import org.aksw.beast.vocabs.CV;
import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.core.SparqlService;
import org.aksw.jena_sparql_api.mapper.jpa.core.RdfEntityManager;
import org.aksw.jena_sparql_api.mapper.jpa.core.SparqlEntityManagerFactory;
import org.aksw.jena_sparql_api.mapper.util.JpaUtils;
import org.aksw.jena_sparql_api.shape.ResourceShape;
import org.aksw.jena_sparql_api.shape.ResourceShapeBuilder;
import org.aksw.jena_sparql_api.shape.lookup.MapServiceResourceShape;
import org.aksw.jena_sparql_api.update.FluentSparqlService;
import org.aksw.jena_sparql_api.utils.Vars;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.vocabulary.RDF;

public class ChartTransform {
	public static List<Entry<StatisticalBarChart, Model>> transform(Model model) throws Exception {
		List<Entry<StatisticalBarChart, Model>> result = new ArrayList<>();

        //Model model = RDFDataMgr.loadModel("statistical-data.ttl");

        
        SparqlService sparqlService = FluentSparqlService.from(model).create();
        RdfEntityManager em = new SparqlEntityManagerFactory()
        		.setPrefixMapping(new PrefixMappingImpl()
        				.setNsPrefixes(PrefixMapping.Extended)
        				.setNsPrefix("qb", "http://purl.org/linked-data/cube#")
        	            .setNsPrefix("cv", "http://aksw.org/chart-vocab/"))
        		.addScanPackageName(StatisticalBarChart.class.getPackage().getName())
        		.setSparqlService(sparqlService)
        		.getObject();


        List<StatisticalBarChart> matches = JpaUtils.getResultList(em, StatisticalBarChart.class, (cb, cq) -> {
            Root<StatisticalBarChart> r = cq.from(StatisticalBarChart.class);
            cq.select(r);
        });
        
        
        for(StatisticalBarChart c : matches) {
            System.out.println("Matched: " + c);
            
            //Object series = c.getSeries();
            // Get the URI of the series object, so we can retrieve the constraints based on the slice properties
            Node iri = NodeFactory.createURI(em.getIri(c));
            
            ResourceShapeBuilder rsb = new ResourceShapeBuilder();
            	
            ResourceShapeBuilder tmp = rsb.out(CV.series);

            ConceptBasedSeries series = (ConceptBasedSeries)c.getSeries();
            
            for(Node sliceProperty : series.getSliceProperties()) {
            	tmp.out(sliceProperty);
            }
            
            ResourceShape shape = rsb.getResourceShape();

            Graph data = MapServiceResourceShape.createLookupService(sparqlService.getQueryExecutionFactory(), shape)
            		.apply(Collections.singleton(iri)).get(iri);
            
            Model constraintModel = ModelFactory.createModelForGraph(data);
            RDFDataMgr.write(System.out, constraintModel, RDFFormat.TURTLE_PRETTY);
            System.out.println("Iri:  " + em.getIri(c));
        
            
            // Create a concept from the constraintModel
            
        	BasicPattern basicPattern = new BasicPattern();
            for(Node sliceProperty : series.getSliceProperties()) {
            	Set<Node> values = constraintModel.listObjectsOfProperty(ResourceFactory.createProperty(sliceProperty.getURI())).mapWith(o -> o.asNode()).toSet();
            	
            	for(Node value : values) {
            		basicPattern.add(new Triple(Vars.s, sliceProperty, value));
            	}
            }
            
            Concept concept = new Concept(new ElementTriplesBlock(basicPattern), Vars.s);

           
            
            // Now create the construct template to map observation dimensions/measures to the chart model
            BasicPattern attr = new BasicPattern();
            BasicPattern template = new BasicPattern();
            
            
            Node seriesNode = series.getSeriesProperty() != null
            		? Vars.x
            		: Optional.ofNullable(series.getSeries()).orElse(NodeFactory.createLiteral(""));
            
            Node errorNode = series.getErrorProperty() != null
            		? Vars.o
            		: null;
            
            Node categoryNode = series.getCategoryProperty() != null
            		? Vars.y
            		: Vars.s
            		;
            		
            template.add(new Triple(Vars.s, RDF.type.asNode(), CV.DataItem.asNode()));
            template.add(new Triple(Vars.s, CV.series.asNode(), seriesNode));
            template.add(new Triple(Vars.s, CV.category.asNode(), categoryNode));
            template.add(new Triple(Vars.s, CV.value.asNode(), Vars.z));
            
            if(errorNode != null) {
            	template.add(new Triple(Vars.s, CV.error.asNode(), errorNode));
            }

            // TODO We could send a select query to the server, and instantiate the template on the client side
            // this would reduce network traffic
            
            if(seriesNode.isVariable()) {
            	attr.add(new Triple(Vars.s, NodeFactory.createURI(Optional.ofNullable(series.getSeriesProperty()).orElse(CV.series.getURI())), seriesNode));
            }
            
            if(!categoryNode.equals(Vars.s)) {
            	attr.add(new Triple(Vars.s, NodeFactory.createURI(Optional.ofNullable(series.getCategoryProperty()).orElse(CV.category.getURI())), categoryNode));
            }

            attr.add(new Triple(Vars.s, NodeFactory.createURI(Optional.ofNullable(series.getValueProperty()).orElse(CV.value.getURI())), Vars.z));
            
            if(errorNode != null) {
            	attr.add(new Triple(Vars.s, NodeFactory.createURI(Optional.ofNullable(series.getErrorProperty()).orElse(CV.error.getURI())), errorNode));
            }

            Query query = new Query();
            query.setQueryConstructType();

            ElementGroup group = new ElementGroup();
            group.addElement(concept.getElement());
            group.addElement(new ElementTriplesBlock(attr));
            query.setQueryPattern(group);
            query.setConstructTemplate(new Template(template));
            
            
            System.out.println(query);

            Model chartDataSet = sparqlService.getQueryExecutionFactory().createQueryExecution(query).execConstruct();
            RDFDataMgr.write(System.out, chartDataSet, RDFFormat.TURTLE_PRETTY);
        
        
            result.add(new SimpleEntry<>(c, chartDataSet));
        }
        
        
        return result;
	}
}
