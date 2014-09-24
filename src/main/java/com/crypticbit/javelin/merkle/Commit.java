package com.crypticbit.javelin.merkle;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.convert.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.diff.Snapshot;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

public class Commit implements Comparable<Commit> {

	/** The data access object representing this commit */
	private CommitDao dao;
	/** The location that this commit is stored at (its hash) */
	private Key daoKey;
	private JsonStoreAdapterFactory jsonFactory;
	private AddressableStorage store;

	Commit(CommitDao dao, Key daoDigest, JsonStoreAdapterFactory jsonFactory,
			AddressableStorage store) {
		assert (dao != null);
		assert (daoDigest != null);

		this.dao = dao;
		this.daoKey = daoDigest;
		this.jsonFactory = jsonFactory;
		this.store = store;
	}

	@Override
	public int compareTo(Commit o) {
		return daoKey.compareTo(o.daoKey);
	}

	public ThreeWayDiff createChangeSet(Commit other)
			throws JsonSyntaxException, StoreException, VisitorException {

		DirectedGraph<Commit, DefaultEdge> x = getAsGraphToRoots(new Commit[] {
				this, other });

		Commit lca = new NaiveLcaFinder<Commit, DefaultEdge>(x).findLca(this,
				other);

		if (lca == null)
			// FIXME - better exception
			throw new RuntimeException("No common ancestor: " + x.toString()
					+ "," + other + "," + this + "," + findRoot() + ","
					+ other.equals(this));

		Collection<GraphPath<Commit, DefaultEdge>> pathsToValues = new LinkedList<>();
		pathsToValues.add(getShortestPath(x, lca, this));
		pathsToValues.add(getShortestPath(x, lca, other));

		ThreeWayDiff twd = new ThreeWayDiff(lca.getObject());
		addCommitToTreeMap(x, twd, pathsToValues);
		return twd;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Commit && daoKey.equals(((Commit) obj).daoKey);
	}

	/**
	 * Generate a graph from this node to root. This allows us to treat the
	 * commit tree like a graph, and use standard graph operations rather than
	 * coding our own
	 * 
	 * @throws VisitorException
	 */
	public DirectedGraph<Commit, DefaultEdge> getAsGraphToRoot()
			throws JsonSyntaxException, StoreException, VisitorException {
		return getAsGraphToRoots(this);
	}

	public static DirectedGraph<Commit, DefaultEdge> getAsGraphToRoots(
			Commit... commits) throws JsonSyntaxException, StoreException,
			VisitorException {

		DirectedGraph<Commit, DefaultEdge> graph = new SimpleDirectedGraph<Commit, DefaultEdge>(
				DefaultEdge.class);
		for (Commit c : commits) {
			addToGraph(graph, c);
		}
		return graph;
	}

	/**
	 * Recursive method to add a node to a graph - and all parents nodes
	 * (together with links between them)
	 */
	private static void addToGraph(DirectedGraph<Commit, DefaultEdge> graph,
			Commit commit) throws JsonSyntaxException, StoreException,
			VisitorException {
		graph.addVertex(commit);
		for (Commit c : commit.getParents()) {
			addToGraph(graph, c);
			graph.addEdge(c, commit);
		}
	}

	public JsonElement getElement() throws JsonSyntaxException, StoreException,
			VisitorException {
		return jsonFactory.getJsonElementAdapter().read(dao.getHead());
	}

	public Key getHead() {
		return dao.getHead();
	}

	public Key getKey() {
		return daoKey;
	}

	public Object getObject() throws JsonSyntaxException, StoreException,
			VisitorException {
		return jsonFactory.getJsonObjectAdapter().read(dao.getHead());
	}

	public Set<Commit> getParents() throws JsonSyntaxException, StoreException,
			VisitorException {
		Set<Commit> parents = new TreeSet<>();
		for (Key parent : dao.getParents()) {
			Commit wrap = wrap(store.get(parent, CommitDao.class), parent);
			parents.add(wrap);
		}
		return parents;
	}

	public List<Commit> getShortestHistory() throws JsonSyntaxException,
			StoreException, VisitorException {

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

	public GraphPath<Commit, DefaultEdge> getShortestPath(
			Graph<Commit, DefaultEdge> graph, Commit start, Commit end) {
		return new DijkstraShortestPath<Commit, DefaultEdge>(graph, start, end)
				.getPath();
	}

	public String getUser() {
		return dao.getUser();
	}

	@Override
	public int hashCode() {
		return daoKey.hashCode();
	}

	public Object navigate(String path) throws JsonSyntaxException,
			StoreException, VisitorException {
		JsonPath compiledPath = new JsonPath(path, new Filter[] {});
		return compiledPath.read(getObject());
	}

	@Override
	public String toString() {
		return "Commit: " + this.getKey();
	}

	// FIXME - should we try and find an existing instance?
	Commit wrap(CommitDao dao, Key digest) {
		return new Commit(dao, digest, jsonFactory, store);
	}

	/**
	 * Find very first commit in tree
	 * 
	 * @throws StoreException
	 * @throws UnsupportedEncodingException
	 * @throws JsonSyntaxException
	 * @throws VisitorException
	 */
	protected Commit findRoot() throws JsonSyntaxException, StoreException,
			VisitorException {
		List<Commit> shortestHistory = getShortestHistory();
		return shortestHistory.get(shortestHistory.size() - 1);
	}

	private void addCommitToTreeMap(Graph<Commit, DefaultEdge> x,
			ThreeWayDiff<Object> twd,
			Collection<GraphPath<Commit, DefaultEdge>> paths)
			throws StoreException, JsonSyntaxException, VisitorException {
		Multimap<Date, Snapshot<Object>> multimap = Multimaps.newListMultimap(
				Maps.<Date, Collection<Snapshot<Object>>> newTreeMap(),
				new Supplier<List<Snapshot<Object>>>() {
					@Override
					public List<Snapshot<Object>> get() {
						return Lists.newLinkedList();
					}
				});

		for (GraphPath<Commit, DefaultEdge> path : paths) {
			for (DefaultEdge e : path.getEdgeList()) {
				Commit end = x.getEdgeTarget(e);
				multimap.put(end.dao.getWhen(),
						new Snapshot<Object>(end.getObject(), path));
			}
		}

		for (Entry<Date, Snapshot<Object>> entry : multimap.entries()) {
			twd.addBranchSnapshot(entry.getValue());
		}

	}

	CommitDao getDao() {
		return dao;
	}

	private static String indent(int indent) {
		return new String(new char[indent]).replace("\0", " ");
	}

	protected void debug(int indent) {
		try {
			System.out.print(indent(indent) + "Commit " + getHead() + ": ");
			System.out.println(getElement().toString());
			for (Commit parent : getParents()) {
				parent.debug(indent + 1);
			}
		} catch (Exception e) {
			System.out.println("<Error>");
			e.printStackTrace();
		}
	}

	public void debug() {
		debug(0);
	}

}
