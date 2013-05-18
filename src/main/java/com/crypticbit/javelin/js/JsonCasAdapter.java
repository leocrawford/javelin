package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.crypticbit.javelin.store.cas.Digest;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonCasAdapter {

    // private static final Type DIGEST_COLLECTION_TYPE = new TypeToken<Collection<Digest>>() {
    // }.getType();

    private JsonElement element;
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

    public JsonCasAdapter(String string) {
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

    public Identity write(ContentAddressableStorage cas) throws StoreException, IOException {
	return write(element, cas);
    }

    public JsonElement getElement() {
	return element;
    }

}
