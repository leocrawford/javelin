package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.SpecialPatch;
import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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
    private SimpleCasAccessInterface<CommitDao> commitFactory;
    private DereferencedCasAccessInterface jsonFactory;

    /** The internal gson object we use, which will write out Digest values properly */
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Digest.class, new TypeAdapter<Digest>() {

	@Override
	public Digest read(JsonReader in) throws IOException {
	    return new Digest(in.nextString());
	}

	@Override
	public void write(JsonWriter out, Digest value) throws IOException {
	    if (value != null) {
		out.value(value.getDigestAsString());
	    }
	}
    }).create();

    /** Create a new json data structure with a random anchor, which can be retrieved using <code>getAnchor</code> */
    public JsonCasAdapter(CasKasStore store) {
	this.store = store;
	anchor = new Anchor(store);
	commitFactory = new SimpleCasAccessInterface<CommitDao>(store, gson, CommitDao.class);
	jsonFactory = new DereferencedCasAccessInterface(store, gson);
    }

    public JsonCasAdapter(CasKasStore store, Digest anchor) {
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
	commit = new Commit(commitFactory.read(anchor.read()), jsonFactory, commitFactory);
	element = commit.getElement();
	if (LOG.isLoggable(Level.FINER)) {
	    LOG.log(Level.FINER, "Reading commit: " + commit);
	}
	return this;
    }

    // FIXME - horrible use of GeneralPersistableResource
    // FIXME - cast Digest
    public synchronized JsonCasAdapter commit() throws StoreException, IOException {
	Identity valueIdentity = jsonFactory.write(element);
	CommitDao tempCommit = new CommitDao((Digest) valueIdentity, new Date(), "temp", (Digest) anchor.get());
	Identity tempDigest = commitFactory.write(tempCommit);
	anchor.write(tempDigest);
	commit = new Commit(tempCommit, jsonFactory, commitFactory);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
	}
	return this;
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

    public JsonCasAdapter merge(JsonCasAdapter other) throws JsonSyntaxException, StoreException, PatchFailedException, IOException {
	// FIXME - check no checkin needed
	CommitPatch patch = commit.createChangeSet(other.commit);
	Object result = patch.apply();
	System.out.println("-->"+result);
	// COPY and paste from commit - horrible
	Identity valueIdentity = jsonFactory.writeAsObjects(result);
	
	CommitDao tempCommit = new CommitDao((Digest) valueIdentity, new Date(), "temp", (Digest) anchor.get());
	Identity tempDigest = commitFactory.write(tempCommit);
	anchor.write(tempDigest);
	commit = new Commit(tempCommit, jsonFactory, commitFactory);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
	}
	return this;
    }

    public JsonElement read() {
	return element;
    }

    public JsonCasAdapter write(String string) {
	element = new JsonParser().parse(string);
	return this;
    }
}
