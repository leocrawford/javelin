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
    private JsonStoreAdapterFactory jsonFactory;

    Commit(CommitDao dao, JsonStoreAdapterFactory jsonFactory) {
	assert (dao != null);

	this.dao = dao;
	this.jsonFactory = jsonFactory;
    }

    @Override
    public int compareTo(Commit o) {
	return this.getDate().compareTo(o.getDate());
    }

    public ThreeWayDiff createChangeSet(Commit other) throws JsonSyntaxException, UnsupportedEncodingException,
	    StoreException {

	Graph<CommitDao, DefaultEdge> x = getAsGraphToRoots(new Commit[] { this, other });
	CommitDao lca = new TarjanLowestCommonAncestor<CommitDao, DefaultEdge>(x).calculate(findRoot().dao, this.dao,
		other.dao);
	Collection<GraphPath<CommitDao, DefaultEdge>> pathsToValues = new LinkedList<>();
	pathsToValues.add(getShortestPath(x, lca, this.dao));
	pathsToValues.add(getShortestPath(x, lca, other.dao));

	ThreeWayDiff twd = new ThreeWayDiff(wrap(lca).getObject());
	addCommitToTreeMap(x, twd, pathsToValues);
	return twd;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	Commit other = (Commit) obj;
	if (dao == null) {
	    if (other.dao != null) {
		return false;
	    }
	}
	else if (!dao.equals(other.dao)) {
	    return false;
	}
	return true;
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
	    parents.add(wrap(simpleObjectAdapter.read(parent)));
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

    public GraphPath<CommitDao, DefaultEdge> getShortestPath(Graph<CommitDao, DefaultEdge> graph, CommitDao start,
	    CommitDao end) {
	return new DijkstraShortestPath<CommitDao, DefaultEdge>(graph, start, end).getPath();
    }

    public String getUser() {
	return dao.getUser();
    }

    @Override
    public int hashCode() {
	return dao.hashCode();
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

    private void addCommitToTreeMap(Graph<CommitDao, DefaultEdge> x, ThreeWayDiff<Object> twd,
	    Collection<GraphPath<CommitDao, DefaultEdge>> paths) throws UnsupportedEncodingException, StoreException {
	Multimap<Date, Snapshot<Object>> multimap = Multimaps.newListMultimap(Maps
		.<Date, Collection<Snapshot<Object>>> newTreeMap(), new Supplier<List<Snapshot<Object>>>() {
	    public List<Snapshot<Object>> get() {
		return Lists.newLinkedList();
	    }
	});

	for (GraphPath<CommitDao, DefaultEdge> path : paths) {
	    for (DefaultEdge e : path.getEdgeList()) {
		Commit end = wrap(x.getEdgeTarget(e));
		multimap.put(end.getDate(), new Snapshot<Object>(end.getObject(), path));
	    }
	}

	for (Entry<Date, Snapshot<Object>> entry : multimap.entries()) {
	    twd.addBranchSnapshot(entry.getValue());
	}

    }

    // FIXME - should we try and find an existing instance?
    private Commit wrap(CommitDao dao) {
	return new Commit(dao, jsonFactory);
    }

    public static Graph<CommitDao, DefaultEdge> getAsGraphToRoots(Commit[] commits) throws JsonSyntaxException,
	    UnsupportedEncodingException, StoreException {
	Graph<CommitDao, DefaultEdge> result = null;
	for (Commit c : commits) {
	    if (result == null) {
		result = c.getAsGraphToRoot();
	    }
	    else {
		result = new GraphUnion<CommitDao, DefaultEdge, Graph<CommitDao, DefaultEdge>>(result, c
			.getAsGraphToRoot());
	    }
	}
	return result;
    }
}
