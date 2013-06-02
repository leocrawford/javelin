package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonCasAdapter {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.js");

    /**
     * The "anchor" of this element, which can either be set manually or a random one generated. After every write the
     * anchor is updated with the reference to the head of the write. It's expected that one node will be hard-coded
     * (set manually) which will represent the head or root node, and all others will be referenced from within that
     * data structure.
     */
    private Identity anchor = new Digest();
    /** The current head of the Json data structure */
    private JsonElement element;
    /** The last known head of the actual data structure, set after a read or write operation */
    private Commit commit;
    private Identity commitId;
    /** The underlying data store */
    private CasKasStore store;
    private CommitFactory commitFactory;
    private JsonFactory jsonFactory;

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
	commitFactory = new CommitFactory(store, gson);
	jsonFactory = new JsonFactory(store, gson);
    }

    /**
     * Create a new json data structure with a specified anchor. This will usually only be used once to create the very
     * head of a data structure.
     */
    public JsonCasAdapter(CasKasStore store, Identity anchor) {
	this(store);
	this.anchor = anchor;
    }

    public Identity getAnchor() {
	return anchor;
    }

    public Commit getCommit() {
	return commit;
    }

    public JsonElement read() {
	return element;
    }
    
    public void write(String string) {
	element = new JsonParser().parse(string);
    }

    public synchronized void checkout() throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	if (store.check(anchor)) {
	    commitId = new Digest(store.get(anchor).getBytes());
	    commit = new Commit(commitFactory.read(commitId), jsonFactory, commitFactory);
	    element = commit.getElement();
	    if (LOG.isLoggable(Level.FINER)) {
		LOG.log(Level.FINER, "Reading commit: " + commit);
	    }
	}
    }

    // FIXME - horrible use of GeneralPersistableResource
    // FIXME - cast Digest
    public synchronized void commit() throws StoreException, IOException {
	Identity valueIdentity = jsonFactory.write(element);
	CommitDao tempCommit = new CommitDao((Digest) valueIdentity, new Date(), "temp", (Digest) commitId);
	Identity tempDigest = commitFactory.write(tempCommit);
	store.store(anchor, commitId, tempDigest);
	commitId = tempDigest; // only happens if no exception thrown
	commit = new Commit(tempCommit,jsonFactory,commitFactory);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
	}
    }

}
