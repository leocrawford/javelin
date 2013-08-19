package com.crypticbit.javelin.js;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
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
public class JsonCasAdapter {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.js");

    private Anchor anchor;
    /** The current head of the Json data structure */
    private JsonElement element;
    /** The last known head of the actual data structure, set after a read or write operation */
    private Commit commit;
    /** The underlying data store */
    private CasKasStore store;
    private DataAccessInterface<CommitDao> commitFactory;
    private JsonStoreAdapterFactory jsonFactory;

    /** Create a new json data structure with a random anchor, which can be retrieved using <code>getAnchor</code> */
    public JsonCasAdapter(CasKasStore store) {
	this.store = store;
	anchor = new Anchor(store);
	jsonFactory = new JsonStoreAdapterFactory(store);
	commitFactory = jsonFactory.getSimpleObjectAdapter(CommitDao.class);

    }

    public JsonCasAdapter(CasKasStore store, Identity anchor) {
	this(store);
	this.anchor = new Anchor(store, anchor);
    }

    /**
     * Create a new json data structure with a specified anchor. This will usually only be used once to create the very
     * head of a data structure.
     */
    private JsonCasAdapter(CasKasStore store, Anchor anchor) {
	this(store);
	this.anchor = anchor;
    }

    public JsonCasAdapter branch() throws StoreException {
	return new JsonCasAdapter(store, new Anchor(store, anchor));
    }

    public synchronized JsonCasAdapter checkout() throws StoreException, JsonSyntaxException {
	Identity daoDigest = anchor.read();
	commit = new Commit(commitFactory.read(daoDigest), daoDigest, jsonFactory);
	element = commit.getElement();
	if (LOG.isLoggable(Level.FINER)) {
	    LOG.log(Level.FINER, "Reading commit: " + commit);
	}
	return this;
    }

    public synchronized JsonCasAdapter commit() throws StoreException {
	Identity write = jsonFactory.getJsonElementAdapter().write(element);
	writeIdentity(write, anchor.get());
	return checkout();
    }

    public Identity getAnchor() {
	return anchor.getDigest();
    }

    public Commit getCommit() {
	return commit;
    }

    public Object lazyRead() throws JsonSyntaxException, StoreException {
	return commit.getObject();
    }

    public synchronized JsonCasAdapter merge(JsonCasAdapter other) throws JsonSyntaxException, StoreException,
	    PatchFailedException {
	ThreeWayDiff patch = commit.createChangeSet(other.commit);
	Identity valueIdentity = jsonFactory.getJsonObjectAdapter().write(patch.apply());
	writeIdentity(valueIdentity, anchor.get(), other.anchor.get());
	return checkout();
    }

    public JsonElement read() {
	return element;
    }

    public JsonCasAdapter write(String string) {
	// FIXME resue Gson
	element = new Gson().fromJson(string, JsonElement.class);
	return this;
    }

    public void write(String path, String json) throws JsonSyntaxException, StoreException {
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
	}
	else {
	    ((Map) result).put(lastToken.getFragment(), jsonObject);
	}

	Identity valueIdentity = jsonFactory.getJsonObjectAdapter().write(originalResult);
	writeIdentity(valueIdentity, anchor.get());
	checkout();
    }

    private void writeIdentity(Identity valueIdentity, Identity... parents) throws StoreException {
	CommitDao tempCommit = new CommitDao(valueIdentity, new Date(), "auser", parents);
	Identity tempDigest = commitFactory.write(tempCommit);
	anchor.write(tempDigest);
	commit = new Commit(tempCommit, tempDigest, jsonFactory);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
	}
    }
}
