package com.crypticbit.javelin.js;

import java.io.IOException;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonStoreAdapterFactory {

    private DataAccessInterface<JsonElement> jea;
    private DataAccessInterface<Object> joa;

    private ContentAddressableStorage cas;

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

    JsonStoreAdapterFactory(ContentAddressableStorage cas) {
	jea = new JsonElementStoreAdapter(cas, this);
	joa = new JsonObjectStoreAdapter(cas, this);
	this.cas = cas;
    }

    public Gson getGson() {
	return gson;
    }

    public DataAccessInterface<JsonElement> getJsonElementAdapter() {
	return jea;
    }

    public DataAccessInterface<Object> getJsonObjectAdapter() {
	return joa;
    }

    public <T> DataAccessInterface<T> getSimpleObjectAdapter(Class<T> clazz) {
	return new JsonSimpleClassAdapter<T>(cas, clazz, this);
    }

}
