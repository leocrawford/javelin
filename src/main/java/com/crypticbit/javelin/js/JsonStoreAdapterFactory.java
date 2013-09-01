package com.crypticbit.javelin.js;

import java.io.IOException;

import com.crypticbit.javelin.js.convert.JsonVisitor;
import com.crypticbit.javelin.js.convert.JsonVisitorCasAdapter;
import com.crypticbit.javelin.js.convert.JsonVisitorDestination;
import com.crypticbit.javelin.js.convert.JsonVisitorElementAdapter;
import com.crypticbit.javelin.js.convert.JsonVisitorObjectAdapter;
import com.crypticbit.javelin.js.convert.JsonVisitorSource;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonStoreAdapterFactory {

	private final class CasDai<T, F, I, B> extends DataAccessInterface<Object> {
		private JsonVisitorCasAdapter casAdapter = new JsonVisitorCasAdapter(
				cas, jsa.getGson());

		private JsonVisitorSource<Object, B> source;
		private JsonVisitorDestination<T, F, Identity> dest;

		private CasDai(ContentAddressableStorage cas,
				JsonStoreAdapterFactory jsa,
				JsonVisitorSource<Object, B> source,
				JsonVisitorDestination<T, F, Identity> dest) {
			super(cas, jsa);
			this.source = source;
			this.dest = dest;
		}

		@Override
		public Object read(Identity commitId) throws StoreException,
				JsonSyntaxException {
			JsonVisitor<T, F, Identity, JsonElement> sv = new JsonVisitor<>(
					dest, casAdapter);
			return sv.visit(commitId);
		}

		@Override
		public Identity write(Object object) throws StoreException {
			JsonVisitor<Identity, Identity, Object, B> sv = new JsonVisitor<Identity, Identity, Object, B>(
			casAdapter, source);
			return sv.visit(object);
		}
	}

	private DataAccessInterface<JsonElement> jea;
	private DataAccessInterface<Object> joa;

	private ContentAddressableStorage cas;

	/**
	 * The internal gson object we use, which will write out Digest values
	 * properly
	 */
	private static final Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(Identity.class,
					new TypeAdapter<Identity>() {

						@Override
						public Digest read(JsonReader in) throws IOException {
							return new Digest(in.nextString());
						}

						@Override
						public void write(JsonWriter out, Identity value)
								throws IOException {
							if (value != null) {
								out.value(value.getDigestAsString());
							}
						}
					}).create();

	public JsonStoreAdapterFactory(ContentAddressableStorage cas) {
		JsonVisitorObjectAdapter jsonObjectAdapter = new JsonVisitorObjectAdapter(
				this);
		joa = new CasDai(cas, this, jsonObjectAdapter, jsonObjectAdapter);
		JsonVisitorElementAdapter jsonElementAdapter = new JsonVisitorElementAdapter(
				this);
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

	public <T> DataAccessInterface<T> getSimpleObjectAdapter(Class<T> clazz) {
		return new JsonSimpleClassAdapter<T>(cas, clazz, this);
	}

}
