package com.crypticbit.javelin.convert;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class TreeVisitorBothElementAdapter implements TreeNodeAdapter<JsonElement> {
    private static final Gson gson = new Gson();


    @Override
    public JsonElement write(Object input) {
	return gson.toJsonTree(input);
    }

    @Override
    public Object read(JsonElement input) {
	return gson.fromJson(input, Object.class);
    }

}
