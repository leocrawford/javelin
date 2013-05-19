package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.*;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonCasAdapter {

    // private static final Type DIGEST_COLLECTION_TYPE = new TypeToken<Collection<Digest>>() {
    // }.getType();

    private Identity id = new Digest();
    private JsonElement element;
    private Identity lastReadDigest;
    private CasKasStore store;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Digest.class, new TypeAdapter<Digest>() {

	@Override
	public void write(JsonWriter out, Digest value) throws IOException {
	    out.value(value.getDigestAsString());
	}

	@Override
	public Digest read(JsonReader in) throws IOException {
	    return new Digest(in.nextString());
	}
    }).create();

    public JsonCasAdapter(CasKasStore store) {
	this.store = store;
    }

    public JsonCasAdapter(CasKasStore store, Identity id) {
	this(store);
	this.id = id;
    }

    public Identity getIdentity() {
	return id;
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

    public static JsonElement read(Identity digest, ContentAddressableStorage cas) throws JsonSyntaxException,
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

    public JsonElement read() throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	if (store.check(id)) {
	    lastReadDigest = new Digest(store.get(id).getBytes());
	    element = read(lastReadDigest, store);
	}
	return element;
    }

    public void write() throws StoreException, IOException {
	Identity tempDigest = write(element, store);
	store.store(id, lastReadDigest, tempDigest);
	lastReadDigest = tempDigest; // only happens if no exception thrown
    }

    public JsonElement getElement() {
	return element;
    }

}
