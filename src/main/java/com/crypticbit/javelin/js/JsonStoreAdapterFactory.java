package com.crypticbit.javelin.js;

import java.io.IOException;
import java.util.Set;

import com.crypticbit.javelin.js.convert.*;
import com.crypticbit.javelin.store.ContentAddressableStorage;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
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

    /**
     * The internal gson object we use, which will write out Digest values properly
     */
    private static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Identity.class,
	    new TypeAdapter<Identity>() {

		@Override
		public Digest read(JsonReader in) throws IOException {
		    return new Digest(in.nextString());
		}

		@Override
		public void write(JsonWriter out, Identity value) throws IOException {
		    if (value != null) {
			out.value(value.getDigestAsString());
		    }
		}
	    }).create();

    public JsonStoreAdapterFactory(ContentAddressableStorage cas) {
	JsonVisitorObjectAdapter jsonObjectAdapter = new JsonVisitorObjectAdapter(this);
	joa = new CasDai(cas, this, jsonObjectAdapter, jsonObjectAdapter);
	JsonVisitorElementAdapter jsonElementAdapter = new JsonVisitorElementAdapter(this);
	jea = new CasDai(cas, this, jsonElementAdapter, jsonElementAdapter);
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

    public JsonVisitor<Set<Identity>, Set<Identity>, Identity, JsonElement> getKeyAdapter() {
	JsonVisitorKeyAdapter jsonKeyAdapter = new JsonVisitorKeyAdapter();
	return new JsonVisitor<Set<Identity>, Set<Identity>, Identity, JsonElement>(jsonKeyAdapter,
		new JsonVisitorCasAdapter(cas, gson));

    }

    public <T> DataAccessInterface<T> getSimpleObjectAdapter(Class<T> clazz) {
	return new JsonSimpleClassAdapter<T>(cas, clazz, this);
    }

    private final class CasDai<T, F, I, B> extends DataAccessInterface<Object> {
	private JsonVisitorCasAdapter casAdapter = new JsonVisitorCasAdapter(cas, jsa.getGson());

	private JsonVisitorSource<Object, B> source;
	private JsonVisitorDestination<T, F, Identity> dest;

	private CasDai(ContentAddressableStorage cas, JsonStoreAdapterFactory jsa, JsonVisitorSource<Object, B> source,
		JsonVisitorDestination<T, F, Identity> dest) {
	    super(cas, jsa);
	    this.source = source;
	    this.dest = dest;
	}

	@Override
	public Object read(Identity commitId) throws VisitorException {
	    JsonVisitor<T, F, Identity, JsonElement> sv = new JsonVisitor<>(dest, casAdapter);
	    return sv.visit(commitId);
	}

	@Override
	public Identity write(Object object) throws VisitorException {
	    JsonVisitor<Identity, Identity, Object, B> sv = new JsonVisitor<Identity, Identity, Object, B>(casAdapter,
		    source);
	    return sv.visit(object);
	}
    }

}
