package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

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

    public JsonCasAdapter branch() throws StoreException, IOException {
	return new JsonCasAdapter(store, new Anchor(store, anchor));
    }

    public synchronized JsonCasAdapter checkout() throws StoreException, JsonSyntaxException,
	    UnsupportedEncodingException {
	Identity daoDigest = anchor.read();
	commit = new Commit(commitFactory.read(daoDigest),daoDigest, jsonFactory);
	element = commit.getElement();
	if (LOG.isLoggable(Level.FINER)) {
	    LOG.log(Level.FINER, "Reading commit: " + commit);
	}
	return this;
    }

    public synchronized JsonCasAdapter commit() throws StoreException, IOException {
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

    public Object lazyRead() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	return commit.getObject();
    }

    public synchronized JsonCasAdapter merge(JsonCasAdapter other) throws JsonSyntaxException, StoreException,
	    PatchFailedException, IOException {
	ThreeWayDiff patch = commit.createChangeSet(other.commit);
	Identity valueIdentity = jsonFactory.getJsonObjectAdapter().write(patch.apply());
	writeIdentity(valueIdentity, anchor.get(), other.anchor.get());
	return checkout();
    }

    private void writeIdentity(Identity valueIdentity, Identity... parents) throws StoreException, IOException {
	CommitDao tempCommit = new CommitDao(valueIdentity, new Date(), "auser", parents);
	Identity tempDigest = commitFactory.write(tempCommit);
	anchor.write(tempDigest);
	commit = new Commit(tempCommit, tempDigest, jsonFactory);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
	}
    }

    public JsonElement read() {
	return element;
    }

    public JsonCasAdapter write(String string) {
	element = new Gson().fromJson(string, JsonElement.class);
	return this;
    }
}
