package com.crypticbit.javelin.js;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.PersistableResource;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.HackedJsonPath;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;
import com.jayway.jsonpath.internal.filter.PathTokenFilter;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

import difflib.PatchFailedException;

// what is immutable? commit? element? anchors?
// multiple anchors?
// FIXME tidy up exceptions
public class DataStructure {

	private static final Logger LOG = Logger
			.getLogger("com.crypticbit.javelin.js");

	/** The current head of the Json data structure */
	private Anchor selectedAnchor;
	private LabelsDao labels;
	private JsonElement element;
	/**
	 * The last known head of the actual data structure, set after a read or
	 * write operation
	 */
	private Commit commit;
	/** The underlying data store */
	private CasKasStore store;
	private DataAccessInterface<CommitDao> commitFactory;
	private JsonStoreAdapterFactory jsonFactory;

	/**
	 * Create a new json data structure with a random anchor, which can be
	 * retrieved using <code>getAnchor</code>
	 */
	public DataStructure(CasKasStore store) {
		this.store = store;
		labels = new LabelsDao();
		selectedAnchor = labels.addAnchor("HEAD");
		jsonFactory = new JsonStoreAdapterFactory(store);
		commitFactory = jsonFactory.getSimpleObjectAdapter(CommitDao.class);

	}

	/*
	 * public DataStructure(CasKasStore store, Identity identity) { this(store);
	 * this.selectedAnchor = new Anchor(identity); }
	 */

	/**
	 * Create a new json data structure with a specified anchor. This will
	 * usually only be used once to create the very head of a data structure.
	 */
	 DataStructure(CasKasStore store, LabelsDao labels, String label) {
		this(store);
		this.labels = labels;
		if (labels.hasAnchor(label))
			this.selectedAnchor = labels.getAnchor(label);
		else
			// FIXME exception handling, and thought about changing to unknown branch
			throw new Error("Labels don't exist");
//			this.selectedAnchor = labels.addAnchor(label);
	} 
	
	DataStructure(CasKasStore store, LabelsDao labels, Anchor selAnchor) throws StoreException {
		this(store);
		this.labels = labels;
		this.selectedAnchor = new Anchor(store, selAnchor);
		
	}

	// FIXME - what about persisting shared labels? Would they be betetr stored
	// as our own mergable data structure?
	/* public DataStructure branch(String newLabel) throws StoreException {
		return new DataStructure(store, labels, newLabel);
	} */

	public DataStructure branch() throws StoreException {
		return new DataStructure(store, labels, selectedAnchor);
	}

	public void saveLabel(String label) {
		labels.addAnchor(label,this.selectedAnchor);
	}
	
	
	public synchronized DataStructure checkout() throws StoreException,
			JsonSyntaxException, VisitorException {
		Identity daoDigest = selectedAnchor.read(store);
		commit = new Commit(commitFactory.read(daoDigest), daoDigest,
				jsonFactory);
		element = commit.getElement();
		if (LOG.isLoggable(Level.FINER)) {
			LOG.log(Level.FINER, "Reading commit: " + commit);
		}
		return this;
	}

	public synchronized DataStructure commit() throws StoreException,
			VisitorException {
		Identity write = jsonFactory.getJsonElementAdapter().write(element);
		writeIdentity(write, selectedAnchor.get());
		return checkout();
	}

	/*
	 * public Identity getAnchor() { return selectedAnchor.getDigest(); }
	 */

	public LabelsDao getLabels() {
		return labels;
	}

	public Commit getCommit() {
		return commit;
	}

	public Object lazyRead() throws JsonSyntaxException, StoreException,
			VisitorException {
		return commit.getObject();
	}

	public synchronized DataStructure merge(DataStructure other)
			throws JsonSyntaxException, StoreException, PatchFailedException,
			VisitorException {
		ThreeWayDiff patch = commit.createChangeSet(other.commit);
		Identity valueIdentity = jsonFactory.getJsonObjectAdapter().write(
				patch.apply());
		writeIdentity(valueIdentity, selectedAnchor.get(),
				other.selectedAnchor.get());
		return checkout();
	}

	public JsonElement read() {
		return element;
	}

	public DataStructure write(String string) {
		// FIXME resue Gson
		element = new Gson().fromJson(string, JsonElement.class);
		return this;
	}

	public void write(String path, String json) throws JsonSyntaxException,
			StoreException, VisitorException {
		HackedJsonPath compiledPath = new HackedJsonPath(path, new Filter[] {});
		// code copied from jsonpath
		// FIXME reuse JSON
		// FIXME should this be in JCA?
		Object jsonObject = new Gson().fromJson(json, Object.class);
		System.out.println(jsonObject.getClass());
		if (!(jsonObject instanceof Map) && !(jsonObject instanceof List)) {
			throw new IllegalArgumentException("Invalid container object");
		}
		JsonProvider jsonProvider = JsonProviderFactory.createProvider();
		Object originalResult, result;
		originalResult = result = lazyRead();

		PathTokenizer tokenizer = compiledPath.getTokenizer();
		PathToken lastToken = tokenizer.removeLastPathToken();
		for (PathToken pathToken : tokenizer) {
			PathTokenFilter filter = pathToken.getFilter();
			result = filter.filter(result, jsonProvider);
		}

		if (lastToken.isArrayIndexToken()) {
			((List) result).set(lastToken.getArrayIndex(), jsonObject);
		} else {
			((Map) result).put(lastToken.getFragment(), jsonObject);
		}

		Identity valueIdentity = jsonFactory.getJsonObjectAdapter().write(
				originalResult);
		writeIdentity(valueIdentity, selectedAnchor.get());
		checkout();
	}

	private void writeIdentity(Identity valueIdentity, Identity... parents)
			throws StoreException, VisitorException {
		// FIXME hardcoded user
		CommitDao tempCommit = new CommitDao(valueIdentity, new Date(),
				"auser", parents);
		Identity tempDigest = commitFactory.write(tempCommit);
		selectedAnchor.write(store, tempDigest);
		commit = new Commit(tempCommit, tempDigest, jsonFactory);
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
		}
	}

	public void exportAll(OutputStream outputStream)
			throws JsonSyntaxException, StoreException, VisitorException,
			IOException {
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		DirectedGraph<Commit, DefaultEdge> x = getCommit().getAsGraphToRoot();
		Set<Identity> temp = new HashSet<>();
		for (DefaultEdge e : x.edgeSet()) {
			temp.addAll(x.getEdgeSource(e).getAllIdentities());
			temp.addAll(x.getEdgeTarget(e).getAllIdentities());
		}
		Map<Identity, PersistableResource> result = new HashMap<>();
		for (Identity i : temp) {
			result.put(i, store.get(i));
		}
		oos.writeObject(result);

	}

	public void importAll(FileInputStream inputStream) throws IOException,
			ClassNotFoundException, StoreException {
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		Map<Identity, PersistableResource> result = (Map<Identity, PersistableResource>) ois
				.readObject();
		for (Entry<Identity, PersistableResource> x : result.entrySet()) {
			Identity idOfValueWrittenToStore = store.store(x.getValue());
			if (!idOfValueWrittenToStore.equals(x.getKey()))
				throw new IllegalStateException("The entry " + x.getKey()
						+ " produced a new key on store to local");
		}

	}
}
