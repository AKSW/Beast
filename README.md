# BEAST - Benchmarking, Evaluation, and Analysis Stack

Beast is a lightweight framework that makes it easy to build RDF-in/RDF-out workflows using Java8 streams and Jena.
For instance, if you want to execute a set of tasks described in RDF, Beast easily lets you create workflows that execute them as often as desired and record 
any measurements directly in  RDF using the vocabulary of your choice (such as DataCube).

## Features

* Construction of Resource-centric Java streams. Hence, plain RDF properties can be attached to resources as part of the stream execution.
* Extension to Jena which enhances Resources with support for attaching and retrieving Java objects by class. This means you can e.g. attach a parsed Jena Query object to a resource that represents a SPARQL query string.
* Looping with the loops state getting attached to the resource.
* No need to know the URI for resources in advance. You can painlessly give them a proper name *at the end* of the workflow *based on its properties*.

While technically Beast essentially provides utilities for chaining functions and streams, a great share of Beast's contribution lies in its the conceptual considerations.

## Components

* Jena Extension: Attach Java objects to Jena resources by casting them to the enhanced resource `ResourceEnh`. Requires the `Model` to be created with `ModelFactoryEnh`:
```java
Model m = ModelFactoryEnh.createModel();
m.createResource().as(ResourceEnh.class)
    .addTrait(myObj.getClass(), myObj);
    // Short-hand of above version
    .addTrait(myObj);
```
* RdfStream API: Enables construction of RDF Resource based workflows using the usual streaming methods, such as *map*, *flatMap*, *peek*, and additional ones such as *repeat*.
* Analysis: Compute new resources representing observations of aggregated values such as averages and standard deviations.
* Visualization: Plot series as charts.

```java
RdfStream
    .startWithCopy()
    .peek(workloadRes -> workloadRes.as(ResourceEnh.class)
        .addTrait(QueryFactory.create(workloadRes.getProperty(LSQ.text).getString())))
    .map(workloadRes ->
        // Create the blank observation resource
        workloadRes.getModel().createResource().as(ResourceEnh.class)
        // Copy the query object attached to the workload resource over to this observation resource
        .copyTraitsFrom(workloadRes)
        // Add some properties to the observation
        .addProperty(RDF.type, QB.Observation)
        .addProperty(IguanaVocab.workload, workloadRes)
        .as(ResourceEnh.class))
    .seq(
        // Warm up run - the resources are processed, but filtered out
        RdfStream.<ResourceEnh>start().repeat(1, IV.run, 1)
            .peek(r -> r.addLiteral(IV.warmup, true))
            .filter(r -> false),
        // Actual evaluation
        RdfStream.<ResourceEnh>start().repeat(3, IV.run, 1).peek(r -> r.addLiteral(IV.warmup, false))
    )
    // Give the observation resource a proper name
    .map(r -> r.rename("http://example.org/observation/{0}-{1}", r.getProperty(IguanaVocab.workload).getResource().getLocalName(), IV.run))
    .apply(() -> workloads.stream()).get()
    // write out every observation resource
    .forEach(observationRes -> RDFDataMgr.write(System.out, observationRes.getModel(), RDFFormat.TURTLE_BLOCKS));
    ;
```

Which generates output such as:
```java
<http://example.org/observation/q1a-5>
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://purl.org/linked-data/cube#Observation> ;
        <http://iguana.aksw.org/ontology#workload>  <http://example.org/query/q1a> ;
        <http://www.w3.org/ns/prov#startedAtTime>  "2016-12-20T02:57:07.608Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
        <http://www.w3.org/ns/prov#endAtTime>  "2016-12-20T02:57:07.672Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
        <http://www.w3.org/2006/time#numericDuration>  "0.063747601"^^<http://www.w3.org/2001/XMLSchema#double> ;
        <http://iv.aksw.org/vocab#run>  "5"^^<http://www.w3.org/2001/XMLSchema#long> ;
        <http://iv.aksw.org/vocab#warmup>  false .
```

## Examples

* [Performance Measurement](beast-examples/src/main/java/org/aksw/beast/examples/MainQueryPerformance.java) - [Test Data (queries.ttl)](beast-examples/src/main/resources/queries.ttl)
* [KFoldCrossValidation](beast-examples/src/main/java/org/aksw/beast/examples/MainKFoldCrossValidation.java) - [Test Data (folds.ttl)](beast-examples/src/main/resources/folds.ttl)


## Dependencies

Beast only aggregates features from other (lower-level) projects, among them:

Core:
* jena
* guava

Visualization (Optional):
* JFreeChart
* XChart (probably in the future)





