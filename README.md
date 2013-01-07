Javelin
=======

Javelin is a database for managing structured data in the form of JSON in a fully versioned,
eventually consistent way.

It allows you to write things like:

    db.navigate("a.b[1].c").write("hello");
    db.navigate("a.b[1].d").write("[1,2,3,\"four\"]");
    db.navigate("a.b[1].d").get(); // returns [1,2,3,"four"]
    db.navigate("a.b[1].d[1]").get(); // returns 2
    db.navigate("a.b[1].d[1]").write("two");
    db.navigate("a.b[1].d[1]").get(); // returns two
    
    db.navigate("a.b[1].d[1]").getHistory(); // returns two elements, each with a timestamp
    db.navigate("a.b[1].d[1]").getHistory()[0].get(); // returns two
    db.navigate("a.b[1].d[1]").getHistory()[1].get(); // returns 2
    
Features
--------

* JSON parsing and generation
* JSON Path navigation
* Pluggable database strategies which include:
    * Simple unversioned storage and recall
    * Versioned with Timestamp
    * Purging of old timestamped versions by either date or number of versions (TBC)
    * Vector clock / eventual consistency (TBC)
    * Purging of discarded vector clocks (TBC)
    * Random resolver on read of vector clocks (TBC) 
    * User resolver on read of vector clocks (TBC)


Coming Soon
-----------

* Various strategies (as shown above)
* JSONPath wildcards

Design
------

Neo4J graph database

Getting Started
---------------