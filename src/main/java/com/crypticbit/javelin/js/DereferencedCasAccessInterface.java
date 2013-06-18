package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.*;

public class DereferencedCasAccessInterface extends DataAccessInterface<JsonElement> {

    protected DereferencedCasAccessInterface(ContentAddressableStorage cas, Gson gson) {
	super(cas, gson);
    }

    @Override
    public JsonElement read(Identity digest) throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	JsonElement in = new JsonParser().parse(cas.get(digest).getAsString());
	if (in.isJsonArray()) {
	    JsonArray r = new JsonArray();
	    for (JsonElement e : in.getAsJsonArray()) {
		r.add(read(gson.fromJson(e, Digest.class)));
	    }
	    return r;
	}
	else if (in.isJsonObject()) {
	    JsonObject o = new JsonObject();
	    for (Entry<String, JsonElement> e : in.getAsJsonObject().entrySet()) {
		o.add(e.getKey(), read(gson.fromJson(e.getValue(), Digest.class)));
	    }
	    return o;
	}
	else {
	    return in;
	}
    }

    public Object readAsObjects(Identity digest) throws JsonSyntaxException, UnsupportedEncodingException,
	    StoreException {
	JsonElement in = new JsonParser().parse(cas.get(digest).getAsString());
	if (in.isJsonArray()) {
	    List<Digest> r = new LinkedList<>();
	    for (JsonElement e : in.getAsJsonArray()) {
		r.add(gson.fromJson(e, Digest.class));
	    }
	    return new LazyJsonArray(this, r);
	}
	else if (in.isJsonObject()) {
	    Map<String, Digest> o = new HashMap<>();
	    for (Entry<String, JsonElement> e : in.getAsJsonObject().entrySet()) {
		o.put(e.getKey(), gson.fromJson(e.getValue(), Digest.class));
	    }
	    return new LazyJsonMap(this, o);
	}
	else if (in.isJsonPrimitive()) {
	    JsonPrimitive primative = in.getAsJsonPrimitive();
	    if (primative.isBoolean()) {
		return primative.getAsBoolean();
	    }
	    if (primative.isNumber()) {
		return primative.getAsNumber();
	    }
	    if (primative.isString()) {
		return primative.getAsString();
	    }
	    throw new Error("Unknown Json Type: " + in);
	}
	else if (in.isJsonNull()) {
	    return null;
	}
	else {
	    throw new Error("Unknown Json Type: " + in);
	}

    }

    @Override
    public Identity write(JsonElement element) throws StoreException, IOException {

	if (element.isJsonArray()) {
	    LinkedList<Identity> array = new LinkedList<>();
	    for (JsonElement e : element.getAsJsonArray()) {
		array.add(write(e));
	    }
	    return cas.store(new GeneralPersistableResource(gson.toJson(array)));
	}
	else if (element.isJsonObject()) {
	    Map<String, Identity> map = new HashMap<>();
	    for (Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
		map.put(e.getKey(), write(e.getValue()));
	    }
	    return cas.store(new GeneralPersistableResource(gson.toJson(map)));
	}
	else {
	    return cas.store(new GeneralPersistableResource(gson.toJson(element)));
	}

    }

}