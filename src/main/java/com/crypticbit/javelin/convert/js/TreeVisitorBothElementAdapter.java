package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.TreeCopySource;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class TreeVisitorBothElementAdapter implements TreeCopySource<JsonElement> {
    private static final Gson gson = new Gson();


    @Override
    public JsonElement pack(Object input) {
	return gson.toJsonTree(input);
    }

    @Override
    public Object unpack(JsonElement input) {
	return gson.fromJson(input, Object.class);
    }

}
