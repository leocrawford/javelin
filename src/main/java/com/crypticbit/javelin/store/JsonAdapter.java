package com.crypticbit.javelin.store;

import java.io.IOException;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/*
 * Converts an arbitrary class (S) to byte array and back so it can be
 * persisted. This uses gson under the covers so the points raised here apply:
 * https://sites.google.com/site/gson/gson-user-guide#TOC-Object-Examples. In
 * addition Key is registered as a type that can be  - which otherwise it couldn't be, 
 */

public class JsonAdapter<S> implements Adapter<S> {

	static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(
			Key.class, new TypeAdapter<Key>() {

				@Override
				public Key read(JsonReader in) throws IOException {
					return new Key(in.nextString());
				}

				@Override
				public void write(JsonWriter out, Key value) throws IOException {
					if (value != null) {
						out.value(value.getKeyAsString());
					}
				}
			}).create();
	private Class<S> clazz;

	public JsonAdapter(Class<S> clazz) {
		this.clazz = clazz;
	}

	@Override
	public byte[] toByteArray(S element) {
		return gson.toJson(element).getBytes(Charset.forName("UTF-8"));
	}

	@Override
	public S fromByteArray(byte[] bytes) {
		return gson
				.fromJson(new String(bytes, Charset.forName("UTF-8")), clazz);
	}

	@Override
	public Key getContentDigest(S element) {
		return new Key(toByteArray(element));
	}

}
