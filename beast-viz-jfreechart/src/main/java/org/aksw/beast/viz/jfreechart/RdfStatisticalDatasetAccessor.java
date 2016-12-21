package org.aksw.beast.viz.jfreechart;

import java.util.function.Function;

import org.aksw.beast.vocabs.CV;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class RdfStatisticalDatasetAccessor
    extends StatisticalDatasetAccessorModular<Resource, String, String>
{
    protected Function<Object, Function<Resource, RDFNode>> propertyResolver = (p) -> ((r) -> r.getProperty((Property)p).getObject());

    protected Number toNumber(RDFNode o) {
        return o.asLiteral().getDouble();
    }

    public static RdfStatisticalDatasetAccessor create() {
        RdfStatisticalDatasetAccessor result = new RdfStatisticalDatasetAccessor();

        result
            .categoryFrom(CV.category)
            .seriesFrom(CV.series)
            .categoryLabelFrom(CV.categoryLabel)
            .seriesLabelFrom(CV.seriesLabel)
            .valueFrom(CV.value)
            .stdDevFrom(CV.stDev);

        return result;
    }


    public RdfStatisticalDatasetAccessor categoryFrom(Object p) {
        this.setCategoryAccessor(propertyResolver.apply(p).andThen(x -> x.toString()));
        return this;
    }

    public RdfStatisticalDatasetAccessor seriesFrom(Object p) {
        this.setSeriesAccessor(propertyResolver.apply(p).andThen(x -> x.toString()));
        return this;
    }

    public RdfStatisticalDatasetAccessor categoryLabelFrom(Object p) {
        this.setCategoryLabelAccessor(propertyResolver.apply(p).andThen(x -> x.asLiteral().getString()));
        return this;
    }

    public RdfStatisticalDatasetAccessor seriesLabelFrom(Object p) {
        this.setSeriesLabelAccessor(propertyResolver.apply(p).andThen(x -> x.asLiteral().getString()));
        return this;
    }

    public RdfStatisticalDatasetAccessor valueFrom(Object p) {
        this.setValueAccessor(propertyResolver.apply(p).andThen(x -> toNumber(x)));
        return this;
    }

    public RdfStatisticalDatasetAccessor stdDevFrom(Object p) {
        this.setStdDevAccessor(propertyResolver.apply(p).andThen(x -> toNumber(x)));
        return this;
    }

}
