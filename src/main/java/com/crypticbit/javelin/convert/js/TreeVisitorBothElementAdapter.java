package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.TreeCopySource;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class TreeVisitorBothElementAdapter implements TreeCopySource<JsonElement, JsonElement> {
    private static final Gson gson = new Gson();

    @Override
    public Function<Object, JsonElement> getDestTransform() {
	return new Function<Object, JsonElement>() {

	    @Override
	    public JsonElement apply(Object input) {
		return gson.toJsonTree(input);
	    }
	};
    }

    @Override
    public JsonElement pack(JsonElement unpackedElement) {
	return unpackedElement;
    }

    @Override
    public Function<JsonElement, Object> getSourceTransform() {
	return new Function<JsonElement, Object>() {

	    @Override
	    public Object apply(JsonElement input) {
		return gson.fromJson(input, Object.class);
	    }
	};
    }

    @Override
    public JsonElement unpack(JsonElement element) {
	return element;
    }

}
