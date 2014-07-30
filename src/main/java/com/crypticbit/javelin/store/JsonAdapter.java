package com.crypticbit.javelin.store;

import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/* Converts JsonElement to byte array and back so it can be persisted */

public class JsonAdapter implements Adapter<JsonElement> {

    static Gson gson = new Gson();
    static KeyFactory keyFactory = new KeyFactory();

    @Override
    public byte[] toByteArray(JsonElement element) {
	return gson.toJson(element).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public JsonElement fromByteArray(byte[] bytes) {
	return gson.fromJson(new String(bytes, Charset.forName("UTF-8")), JsonElement.class);
    }

    @Override
    public Key getContentDigest(JsonElement element) {
	return keyFactory.getDefaultDigest(toByteArray(element));
    }

}
