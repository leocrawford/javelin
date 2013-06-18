#Javelin

##Vision

Imagine a world where it's possible to quickly create a local database with whatever schema you choose, and then sync 
whichever bits you want to as many, or as few, other stores as you want.

Some potential uses:

* Distributed social networking (don't let your personal information anywhere near the cloud)
* Photo sharing (no limit to storage)
* Friend-to-friend (F2F) backup (or anything else)
* Distributed change control system (does for defect tracking what git does for source code)

My goal is to write a distributed photo sharing system, with social networking functionality - but getting the distributed 
database working first is essential. 

Therefore I'm working on this project (Javelin) which is a database for managing structured data in the form of JSON 
in a fully versioned, eventually consistent way.

##Benefits

The benefits of a distributed, versioned database include:

* No history is ever deleted, so you get the best auditing possible
* You can roll back to any previous version with ease
* You can branch the database, in order to make concurrent changes
* You can merge branches 
* The branches can be synced over any transport mechanism
* The cost of hosting a database becomes trivial. You don't need a server, or high up-time

##Usage

It allows you to write things like the following (the API is changing, so this is more psuedo-code)

```java
Repository repo = new DiskRepoistory(directory);
Anchor a = new Anchor("ROOT");
a.write("[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]");
System.out.println(a.navigate("[2].a")); // 1000.21
Anchor b = a.branch()
b.navigate("[2].c").write("hello");
System.out.println(b.navigate("[2]")); // {a:1000.21,b:6,c:hello}
System.out.println(a.navigate("[2]")); // {a:1000.21,b:6}
a.navigate("[2].d").write("bye");
Patch p = a.createPatchTo(b); // in case we wanted to ship it off somewhere
a.mergeIn(b);
System.out.println(a.navigate("[2]")); // {a:1000.21,b:6,c:hello},d:bye   
System.out.println(a.getHistory()); // a timestamped set of commits with parents
a.getObjects(); // return the set of object in branch a (so they can be shipped off elsewhere)
```
    
##Inspiration

There are a number of interesting projects looking at a very similar problem. These include:

* http://camlistore.org/
* <need to look these up>

In terms of technology, the things that shaped my thinking are:

* git
* darcs


##Resources

* http://en.wikibooks.org/wiki/Understanding_Darcs/Patch_theory
    
##Features

Done:

* JSON parsing and generation
* Access to JSON tree via lazily instantiated objects
 * Lazy Json objects to allow write [todo]
 * Navigate through data structure without reading the whole tree, and allow navigation to non existant nodes [todo]
* Storage to pluggable content addressable storage
 * memory 
 * disk [todo]
* branching
 * create branches
 * merge branches [todo]
* patching
 * create patch
 * apply patch
 * revert patch [todo]
 * pluggable conflict resolution
* export
 * patch [todo]
 * branch [todo]
 * objects [todo]


It isn't
--------

* A JDBC database
* Designed to be particularly performant
* Scalable (on it's own - but the eventual consistency means you can distribute it very easily)
* Remoteable - though the simple JSON interface means it can be made remotable with great ease

Design
------

Storage:

* CAS
* KAS
* Implementations

Versioning:

* Anchor
* Branch
* History
* Commit
* Patching
 * Diff
 * 3 way diff
