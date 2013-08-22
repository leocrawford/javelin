package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class StoreVisitor<T, F> {

    public final Function<Identity, T> RECURSE_FUNCTION = new Function<Identity, T>() {
	public T apply(Identity identity) {
	    try {
		return visit(identity);
	    }
	    catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new Error();
	    }
	}
    };
    public final Function<Identity, Identity> HALT_FUNCTION = new Function<Identity, Identity>() {
	public Identity apply(Identity identity) {
	    return identity;
	}
    };

    private ContentAddressableStorage cas;
    private StoreVisitorCallback<T, F> callback;
    private Gson gson;

    StoreVisitor(ContentAddressableStorage cas, StoreVisitorCallback<T, F> callback, Gson gson) {
	this.cas = cas;
	this.callback = callback;
	this.gson = gson;
    }

    public T visit(Identity digest) throws JsonSyntaxException, StoreException {
	JsonElement in = new JsonParser().parse(cas.get(digest).getAsString());
	if (in.isJsonArray()) {
	    List<Identity> list = gson.fromJson(in, new TypeToken<List<Identity>>() {
	    }.getType());
	    return callback.arriveList(Lists.transform(list, callback.getTransform()));
	}
	else if (in.isJsonObject()) {
	    Map<String, Identity> map = gson.fromJson(in, new TypeToken<Map<String, Identity>>() {
	    }.getType());
	    return callback.arriveMap(Maps.transformValues(map, callback.getTransform()));
	}
	else if (in.isJsonPrimitive()) {
	    return callback.arriveValue(convertJavaPrimivativeToObject((JsonPrimitive) in));
	}
	else
	    return callback.arriveValue(null);
    }

    private Object convertJavaPrimivativeToObject(JsonPrimitive primative) {
	if (primative.isBoolean()) {
	    return primative.getAsBoolean();
	}
	if (primative.isNumber()) {
	    if (!primative.getAsString().contains(".")) {
		return primative.getAsInt();
	    }
	    else {
		return primative.getAsFloat();
	    }
	}
	if (primative.isString()) {
	    return primative.getAsString();
	}
	throw new InternalError("illegal Json Type found: " + primative);
    }

}
