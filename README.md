# BEAST - Benchmarking, Evaluation, and Analysis Stack

Beast makes it easy to build RDF-in/RDF-out workflows using Java8 streams and Jena.
For instance, if you want to execute a set of tasks described in RDF, Beast easily lets you create workflows that execute them as often as desired and record 
any measurements directly in  RDF using the vocabulary of your choice (such as DataCube).

## Features

* Construction of Resource-centric Java streams. Hence, plain RDF properties can be attached to resources as part of the stream execution.
* Extension to Jena which enhances Resources with support for attaching and retrieving Java objects by class. This means you can e.g. attach a parsed Jena Query object to a resource that represents a SPARQL query string.
* Looping with the loops state getting attached to the resource.
* No need to know the URI for resources in advance. You can painlessly give them a proper name *at the end* of the workflow *based on its properties*.


While technically Beast essentially provides utilities for chaining functions and streams, a great share of Beast's contribution lies in its the conceptual considerations.


## Examples

* [Performance Measurement](beast-examples/src/main/java/org/aksw/beast/examples/MainQueryPerformance.java)
* [KFoldCrossValidation](beast-examples/src/main/java/org/aksw/beast/examples/MainKFoldCrossValidation.java)


## Dependencies

Beast only aggregates features from other (lower-level) projects, among them:

* aksw-commons
* jena-sparql-api





