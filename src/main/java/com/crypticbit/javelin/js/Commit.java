package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.TarjanLowestCommonAncestor;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphUnion;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.crypticbit.javelin.diff.Snapshot;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class Commit implements Comparable<Commit> {

    private CommitDao dao;
    private Identity daoDigest;
    private JsonStoreAdapterFactory jsonFactory;

    Commit(CommitDao dao, Identity daoDigest, JsonStoreAdapterFactory jsonFactory) {
	assert (dao != null);

	this.dao = dao;
	this.daoDigest = daoDigest;
	this.jsonFactory = jsonFactory;
    }

    @Override
    public int compareTo(Commit o) {
	return daoDigest.compareTo(o.daoDigest);
    }

    public ThreeWayDiff createChangeSet(Commit other) throws JsonSyntaxException, UnsupportedEncodingException,
	    StoreException {

	Graph<Commit, DefaultEdge> x = getAsGraphToRoots(new Commit[] { this, other });
	Commit lca = new TarjanLowestCommonAncestor<Commit, DefaultEdge>(x).calculate(findRoot(), this,
		other);
	Collection<GraphPath<Commit, DefaultEdge>> pathsToValues = new LinkedList<>();
	pathsToValues.add(getShortestPath(x, lca, this));
	pathsToValues.add(getShortestPath(x, lca, other));

	ThreeWayDiff twd = new ThreeWayDiff(lca.getObject());
	addCommitToTreeMap(x, twd, pathsToValues);
	return twd;
    }

    @Override
    public boolean equals(Object obj) {
	if (getClass() != obj.getClass())
	    return false;
	return ((Commit) obj).daoDigest.equals(daoDigest);
    }

    /**
     * Generate a graph from this node to root. This allows us to treat the commit tree like a graph, and use standard
     * graph operations rather than coding our own
     */
    public Graph<Commit, DefaultEdge> getAsGraphToRoot() throws JsonSyntaxException, UnsupportedEncodingException,
	    StoreException {
	// FIXME - we should consider caching results

	DirectedGraph<Commit, DefaultEdge> thisAndParentsAsGraph = new SimpleDirectedGraph<Commit, DefaultEdge>(
		DefaultEdge.class);
	thisAndParentsAsGraph.addVertex(this);
	for (Commit c : getParents()) {
	    thisAndParentsAsGraph.addVertex(c);
	    // FIXME - which way are we directing our graph
	    thisAndParentsAsGraph.addEdge(c, this);
	}
	Graph<Commit, DefaultEdge> thisAndParentsTreeAsGraph = thisAndParentsAsGraph;
	for (Commit c : getParents()) {
	    thisAndParentsTreeAsGraph = new GraphUnion<Commit, DefaultEdge, Graph<Commit, DefaultEdge>>(
		    thisAndParentsTreeAsGraph, c.getAsGraphToRoot());
	}
	return thisAndParentsTreeAsGraph;
    }

    public Date getDate() {
	return dao.getWhen();
    }

    public JsonElement getElement() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	return jsonFactory.getJsonElementAdapter().read(dao.getHead());
    }

    public Object getObject() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	return jsonFactory.getJsonObjectAdapter().read(dao.getHead());
    }

    public Set<Commit> getParents() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	Set<Commit> parents = new TreeSet<>();
	DataAccessInterface<CommitDao> simpleObjectAdapter = jsonFactory.getSimpleObjectAdapter(CommitDao.class);
	for (Identity parent : dao.getParents()) {
	    Commit wrap = wrap(simpleObjectAdapter.read(parent),parent);
	    parents.add(wrap);
	}
	return parents;
    }
    

    public List<Commit> getShortestHistory() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {

	List<Commit> shortest = null;
	for (Commit c : getParents()) {
	    List<Commit> consider = c.getShortestHistory();
	    if (shortest == null || shortest.size() > consider.size()) {
		shortest = consider;
	    }
	}
	if (shortest == null) {
	    shortest = new LinkedList<>();
	}
	shortest.add(0, this);
	return shortest;
    }

    public GraphPath<Commit, DefaultEdge> getShortestPath(Graph<Commit, DefaultEdge> graph, Commit start,
	    Commit end) {
	return new DijkstraShortestPath<Commit, DefaultEdge>(graph, start, end).getPath();
    }

    public String getUser() {
	return dao.getUser();
    }

    @Override
    public int hashCode() {
	return daoDigest.hashCode();
    }

    @Override
    public String toString() {
	return dao.toString();
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
	return shortestHistory.get(shortestHistory.size() - 1);
    }

    private void addCommitToTreeMap(Graph<Commit, DefaultEdge> x, ThreeWayDiff<Object> twd,
	    Collection<GraphPath<Commit, DefaultEdge>> paths) throws UnsupportedEncodingException, StoreException {
	Multimap<Date, Snapshot<Object>> multimap = Multimaps.newListMultimap(Maps
		.<Date, Collection<Snapshot<Object>>> newTreeMap(), new Supplier<List<Snapshot<Object>>>() {
	    public List<Snapshot<Object>> get() {
		return Lists.newLinkedList();
	    }
	});

	for (GraphPath<Commit, DefaultEdge> path : paths) {
	    for (DefaultEdge e : path.getEdgeList()) {
		Commit end = x.getEdgeTarget(e);
		multimap.put(end.getDate(), new Snapshot<Object>(end.getObject(), path));
	    }
	}

	for (Entry<Date, Snapshot<Object>> entry : multimap.entries()) {
	    twd.addBranchSnapshot(entry.getValue());
	}

    }

    // FIXME - should we try and find an existing instance?
    Commit wrap(CommitDao dao, Identity digest) {
	return new Commit(dao, digest, jsonFactory);
    }

    public static Graph<Commit, DefaultEdge> getAsGraphToRoots(Commit[] commits) throws JsonSyntaxException,
	    UnsupportedEncodingException, StoreException {
	Graph<Commit, DefaultEdge> result = null;
	for (Commit c : commits) {
	    if (result == null) {
		result = c.getAsGraphToRoot();
	    }
	    else {
		result = new GraphUnion<Commit, DefaultEdge, Graph<Commit, DefaultEdge>>(result, c
			.getAsGraphToRoot());
	    }
	}
	return result;
    }
}
