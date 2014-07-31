package com.crypticbit.javelin.store;

import java.nio.charset.Charset;

import com.google.gson.Gson;

/* Converts JsonElement to byte array and back so it can be persisted */

public class JsonAdapter<S> implements Adapter<S> {

    static final Gson gson = new Gson();
    static final KeyFactory keyFactory = new KeyFactory();
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
	return gson.fromJson(new String(bytes, Charset.forName("UTF-8")), clazz);
    }

    @Override
    public Key getContentDigest(S element) {
	return keyFactory.getDefaultDigest(toByteArray(element));
    }

}
