package com.crypticbit.javelin.merkle;

import java.util.*;
import java.util.Map.Entry;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.crypticbit.javelin.diff.Snapshot;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

/**
 * Immutable class representing a Commit, backed by CommitDao. This class adds all the logic that requires access to the
 * database and other services
 */
public class Commit implements Comparable<Commit> {

    /** The data access object representing this commit */
    private final CommitDao commitDao;
    /** The location that this commit is stored at (its hash) */
    private final Key commitDaoKey;
    private final CommitFactory commitFactory;

    Commit(CommitFactory commitFactory, Key commitDaoKey, CommitDao commitDao) {
	assert (commitDao != null);
	assert (commitDaoKey != null);

	this.commitDao = commitDao;
	this.commitDaoKey = commitDaoKey;
	this.commitFactory = commitFactory;

    }

    @Override
    public int compareTo(Commit o) {
	return commitDaoKey.compareTo(o.commitDaoKey);
    }

    public ThreeWayDiff createChangeSet(Commit other) throws MergeException, CorruptTreeException {

	DirectedGraph<Commit, DefaultEdge> x = getAsGraphToRoots(new Commit[] { this, other });

	Commit lca = new NaiveLcaFinder<Commit, DefaultEdge>(x).findLca(this, other);

	if (lca == null) {
	    throw new MergeException("No common ancestor: " + x.toString() + "," + other + "," + this + ","
		    + findRoot() + "," + other.equals(this));
	}

	Collection<GraphPath<Commit, DefaultEdge>> pathsToValues = new LinkedList<>();
	pathsToValues.add(getShortestPath(x, lca, this));
	pathsToValues.add(getShortestPath(x, lca, other));

	ThreeWayDiff diff = new ThreeWayDiff(lca.getAsObject());
	addCommitToTreeMap(x, diff, pathsToValues);
	return diff;
    }

    public void debug() {
	debug(0);
    }

    @Override
    public boolean equals(Object obj) {
	return obj instanceof Commit && commitDaoKey.equals(((Commit) obj).commitDaoKey);
    }

    public JsonElement getAsElement() {
	return commitFactory.getJsonElementStoreAdapter().read(commitDao.getHead());
    }

    /**
     * Generate a graph from this node to root. This allows us to treat the commit tree like a graph, and use standard
     * graph operations rather than coding our own
     *
     * @throws CorruptTreeException
     * @throws TreeMapperException
     */
    public DirectedGraph<Commit, DefaultEdge> getAsGraphToRoot() throws CorruptTreeException {
	return getAsGraphToRoots(this);
    }

    public Object getAsObject() {
	return commitFactory.getObjectStoreAdapter().read(commitDao.getHead());
    }

    public Set<Commit> getParents() throws CorruptTreeException {
	Set<Commit> parents = new TreeSet<>();
	for (Key parent : commitDao.getParents()) {
	    parents.add(commitFactory.getCommit(parent));
	}
	return parents;
    }

    /** Return the shortest history from the root to this commit, i.e. the fewest commits in between */
    public LinkedList<Commit> getShortestHistory() throws CorruptTreeException {
	LinkedList<Commit> shortest = null;
	for (Commit c : getParents()) {
	    LinkedList<Commit> consider = c.getShortestHistory();
	    if (shortest == null || shortest.size() > consider.size()) {
		shortest = consider;
	    }
	}
	if (shortest == null) {
	    shortest = new LinkedList<>();
	}
	shortest.addFirst(this);
	return shortest;
    }

    public String getUser() {
	return commitDao.getUser();
    }

    @Override
    public int hashCode() {
	return commitDaoKey.hashCode();
    }

    public Object navigate(String path) {
	JsonPath compiledPath = new JsonPath(path, new Filter[] {});
	return compiledPath.read(getAsObject());
    }

    @Override
    public String toString() {
	return commitDao.toString();
    }
    
    public Commit getFirstParent() throws CorruptTreeException {
	if(commitDao.getParents().length >= 1)
	    return commitFactory.getCommit(commitDao.getParents()[0]);
	else
	    return null;
		
    }

    // FIXME - baffling method
    private void addCommitToTreeMap(Graph<Commit, DefaultEdge> x, ThreeWayDiff<Object> twd,
	    Collection<GraphPath<Commit, DefaultEdge>> paths) {

	Multimap<Date, Snapshot<Object>> multimap = Multimaps.newListMultimap(Maps
		.<Date, Collection<Snapshot<Object>>> newTreeMap(), new Supplier<List<Snapshot<Object>>>() {
	    @Override
	    public List<Snapshot<Object>> get() {
		return Lists.newLinkedList();
	    }
	});

	for (GraphPath<Commit, DefaultEdge> path : paths) {
	    for (DefaultEdge e : path.getEdgeList()) {
		Commit end = x.getEdgeTarget(e);
		multimap.put(end.commitDao.getWhen(), new Snapshot<Object>(end.getAsObject(), path));
	    }
	}

	for (Entry<Date, Snapshot<Object>> entry : multimap.entries()) {
	    twd.addBranchSnapshot(entry.getValue());
	}

    }

    private void debug(int indent) {
	try {
	    System.out.print(indent(indent) + "Commit " + commitDao.getHead() + ": ");
	    System.out.println(getAsElement().toString());
	    for (Commit parent : getParents()) {
		parent.debug(indent + 1);
	    }
	}
	catch (Exception e) {
	    System.out.println("<Error>");
	    e.printStackTrace();
	}
    }

    /**
     * Find very first commit in tree
     */
    private Commit findRoot() throws CorruptTreeException {
	return getShortestHistory().getLast();
    }

    private CommitDao getDao() {
	return commitDao;
    }

    public static DirectedGraph<Commit, DefaultEdge> getAsGraphToRoots(Commit... commits) throws CorruptTreeException {
	DirectedGraph<Commit, DefaultEdge> graph = new SimpleDirectedGraph<Commit, DefaultEdge>(DefaultEdge.class);
	for (Commit c : commits) {
	    addToGraph(graph, c);
	}
	return graph;
    }

    /**
     * Recursive method to add a node to a graph - and all parents nodes (together with links between them)
     *
     * @throws CorruptTreeException
     * @throws StoreException
     */
    private static void addToGraph(DirectedGraph<Commit, DefaultEdge> graph, Commit commit) throws CorruptTreeException {
	graph.addVertex(commit);
	for (Commit c : commit.getParents()) {
	    addToGraph(graph, c);
	    graph.addEdge(c, commit);
	}
    }

    private static GraphPath<Commit, DefaultEdge> getShortestPath(Graph<Commit, DefaultEdge> graph, Commit start,
	    Commit end) {
	return new DijkstraShortestPath<Commit, DefaultEdge>(graph, start, end).getPath();
    }

    private static String indent(int indent) {
	return new String(new char[indent]).replace("\0", " ");
    }

}
