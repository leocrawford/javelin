package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.*;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.crypticbit.javelin.store.cas.PersistableResource;
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
    /** The internal gson object we use, which will write out Digest values properly */
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Digest.class, new TypeAdapter<Digest>() {

	@Override
	public void write(JsonWriter out, Digest value) throws IOException {
	    if(value != null)
	    out.value(value.getDigestAsString());
	}

	@Override
	public Digest read(JsonReader in) throws IOException {
	    return new Digest(in.nextString());
	}
    }).create();

    /** Create a new json data structure with a random anchor, which can be retrieved using <code>getAnchor</code> */
    public JsonCasAdapter(CasKasStore store) {
	this.store = store;
    }

    /**
     * Create a new json data structure with a specified anchor./ this will usually only be used once to create the very
     * head of a data structure.
     */
    public JsonCasAdapter(CasKasStore store, Identity anchor) {
	this(store);
	this.anchor = anchor;
    }

    public Identity getAnchor() {
	return anchor;
    }

    public void setJson(String string) {
	element = new JsonParser().parse(string);
    }

    public static Identity write(JsonElement element, ContentAddressableStorage cas) throws StoreException, IOException {
	if (element.isJsonArray()) {
	    LinkedList<Identity> array = new LinkedList<>();
	    for (JsonElement e : element.getAsJsonArray()) {
		array.add(write(e, cas));
	    }
	    return cas.store(new GeneralPersistableResource(gson.toJson(array)));
	}
	else if (element.isJsonObject()) {
	    Map<String, Identity> map = new HashMap<>();
	    for (Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
		map.put(e.getKey(), write(e.getValue(), cas));
	    }
	    return cas.store(new GeneralPersistableResource(gson.toJson(map)));
	}
	else
	    return cas.store(new GeneralPersistableResource(gson.toJson(element)));
    }

    private static JsonElement read(Identity digest, ContentAddressableStorage cas) throws JsonSyntaxException,
	    UnsupportedEncodingException, StoreException {
	JsonElement in = new JsonParser().parse(cas.get(digest).getAsString());
	if (in.isJsonArray()) {
	    JsonArray r = new JsonArray();
	    for (JsonElement e : in.getAsJsonArray()) {
		r.add(read(gson.fromJson(e, Digest.class), cas));
	    }
	    return r;
	}
	else if (in.isJsonObject()) {
	    JsonObject o = new JsonObject();
	    for (Entry<String, JsonElement> e : in.getAsJsonObject().entrySet()) {
		o.add(e.getKey(), read(gson.fromJson(e.getValue(), Digest.class), cas));
	    }
	    return o;
	}
	else
	    return in;
    }

    public synchronized JsonElement read() throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	if (store.check(anchor)) {
	    commitId = new Digest(store.get(anchor).getBytes());
	    PersistableResource commitAsEncodedJson = store.get(commitId);
	    commit = gson.fromJson(commitAsEncodedJson.getAsString(), Commit.class);
	    if (LOG.isLoggable(Level.FINER))
		LOG.log(Level.FINER, "Reading commit: " + commit);
	    element = read(commit.getHead(), store);
	}
	return element;
    }

    // FIXME - horrible use of GeneralPersistableResource
    // FIXME - factor out to commit
    // FIXME - cast Digest
    public synchronized void write() throws StoreException, IOException {
	Identity valueIdentity = write(element, store);
	Commit tempCommit = new Commit((Digest) valueIdentity, new Date(), "temp",(Digest) commitId);
	String commitAsJson = gson.toJson(tempCommit);
	Identity tempDigest = store.store(new GeneralPersistableResource(commitAsJson));
	store.store(anchor, commitId, tempDigest);
	commitId = tempDigest; // only happens if no exception thrown
	commit = tempCommit;
	if (LOG.isLoggable(Level.FINEST))
	    LOG.log(Level.FINEST, "Updating id -> " + tempDigest);
    }

    public JsonElement getElement() {
	return element;
    }

    public Commit getCommit() {
	return commit;
    }

}
