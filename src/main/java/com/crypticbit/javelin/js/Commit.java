package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;
import java.util.*;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.TarjanLowestCommonAncestor;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphUnion;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import difflib.Patch;

public class Commit implements Comparable<Commit> {

    private CommitDao dao;
    private DereferencedCasAccessInterface jsonFactory;
    private SimpleCasAccessInterface<CommitDao> commitFactory;

    Commit(CommitDao dao, DereferencedCasAccessInterface jsonFactory, SimpleCasAccessInterface<CommitDao> commitFactory) {
	assert (dao != null);

	this.dao = dao;
	this.jsonFactory = jsonFactory;
	this.commitFactory = commitFactory;

    }

    public JsonElement getElement() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	return jsonFactory.read(dao.getHead());
    }

    public Object getObject() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	return jsonFactory.readAsObjects(dao.getHead());
    }

    /**
     * Generate a graph from this node to root. This allows us to treat the commit tree like a graph, and use standard
     * graph operations rather than coding our own
     */
    public Graph<CommitDao, DefaultEdge> getAsGraphToRoot() throws JsonSyntaxException, UnsupportedEncodingException,
	    StoreException {
	// FIXME - we should consider caching results

	DirectedGraph<CommitDao, DefaultEdge> thisAndParentsAsGraph = new SimpleDirectedGraph<CommitDao, DefaultEdge>(
		DefaultEdge.class);
	thisAndParentsAsGraph.addVertex(dao);
	for (Commit c : getParents()) {
	    thisAndParentsAsGraph.addVertex(c.dao);
	    // FIXME - which way are we directing our graph
	    thisAndParentsAsGraph.addEdge(c.dao, this.dao);
	}
	Graph<CommitDao, DefaultEdge> thisAndParentsTreeAsGraph = thisAndParentsAsGraph;
	for (Commit c : getParents()) {
	    thisAndParentsTreeAsGraph = new GraphUnion<CommitDao, DefaultEdge, Graph<CommitDao, DefaultEdge>>(
		    thisAndParentsTreeAsGraph, c.getAsGraphToRoot());
	}
	return thisAndParentsTreeAsGraph;
    }

    public static Graph<CommitDao, DefaultEdge> getAsGraphToRoots(Commit[] commits) throws JsonSyntaxException,
	    UnsupportedEncodingException, StoreException {
	Graph<CommitDao, DefaultEdge> result = null;
	for (Commit c : commits) {
	    if (result == null)
		result = c.getAsGraphToRoot();
	    else
		result = new GraphUnion<CommitDao, DefaultEdge, Graph<CommitDao, DefaultEdge>>(result, c
			.getAsGraphToRoot());
	}
	return result;
    }

    public List<Commit> getShortestHistory() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {

	List<Commit> shortest = null;
	for (Commit c : getParents()) {
	    List<Commit> consider = c.getShortestHistory();
	    if (shortest == null || shortest.size() > consider.size())
		shortest = consider;
	}
	if (shortest == null)
	    shortest = new LinkedList<>();
	shortest.add(0, this);
	return shortest;
    }

    public String getUser() {
	return dao.getUser();
    }

    public Date getDate() {
	return dao.getWhen();
    }

    public Set<Commit> getParents() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	Set<Commit> parents = new TreeSet<>();
	for (Digest parent : dao.getParents()) {
	    parents.add(wrap(commitFactory.read(parent)));
	}
	return parents;
    }

    @Override
    public String toString() {
	return dao.toString();
    }

    // FIXME - should we try and find an existing instance?
    private Commit wrap(CommitDao dao) {
	return new Commit(dao, jsonFactory, commitFactory);
    }

    @Override
    public int compareTo(Commit o) {
	return this.getDate().compareTo(o.getDate());
    }

    public Patch createChangeSet(Commit other) throws JsonSyntaxException, UnsupportedEncodingException, StoreException {

	Commit lca = findLca(other);

	// Patch patch = DiffUtils.diff((List) lazyRead(), (List) other.lazyRead());

	// for (Delta delta : patch.getDeltas()) {
	// System.out.println(delta);
	// }
	return null;
    }

    /**
     * Find very first commit in tree
     * 
     * @throws StoreException
     * @throws UnsupportedEncodingException
     * @throws JsonSyntaxException
     */
    protected Commit findRoot() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	List<Commit> shortestHistory = getShortestHistory();
	return shortestHistory.get(shortestHistory.size()-1);
    }

    /*
     * FIXME - should we not be public
     * FIXME - handle not found
     */
    public Commit findLca(Commit other) throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	Graph<CommitDao, DefaultEdge> x = getAsGraphToRoots(new Commit[] { this, other });
	CommitDao lca = new TarjanLowestCommonAncestor<CommitDao, DefaultEdge>(x).calculate(findRoot().dao, this.dao,
		other.dao);
	return wrap(lca);

    }
}
