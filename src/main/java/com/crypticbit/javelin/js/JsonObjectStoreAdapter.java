package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

import com.crypticbit.javelin.js.lazy.IdentityReference;
import com.crypticbit.javelin.js.lazy.LazyJsonArray;
import com.crypticbit.javelin.js.lazy.LazyJsonMap;
import com.crypticbit.javelin.js.lazy.Reference;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonObjectStoreAdapter extends DataAccessInterface<Object> {

    JsonObjectStoreAdapter(ContentAddressableStorage cas, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
    }

    @Override
    public Object read(Identity digest) throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	JsonElement in = new JsonParser().parse(cas.get(digest).getAsString());
	if (in.isJsonArray()) {
	    List<Reference> r = new LinkedList<>();
	    for (JsonElement e : in.getAsJsonArray()) {
		r.add(new IdentityReference(jsa, getGson().fromJson(e, Identity.class)));
	    }
	    return new LazyJsonArray(r);
	}
	else if (in.isJsonObject()) {
	    Map<String, Reference> o = new HashMap<>();
	    for (Entry<String, JsonElement> e : in.getAsJsonObject().entrySet()) {
		o.put(e.getKey(), new IdentityReference(jsa, getGson().fromJson(e.getValue(), Identity.class)));
	    }
	    return new LazyJsonMap(o);
	}
	else if (in.isJsonPrimitive()) {
	    JsonPrimitive primative = in.getAsJsonPrimitive();
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
	    throw new Error("Unknown Json Type: " + in);
	}
	else if (in.isJsonNull()) {
	    return null;
	}
	else {
	    throw new Error("Unknown Json Type: " + in);
	}

    }

    // FIXME if already exists
    @Override
    public Identity write(Object object) throws StoreException, IOException {
	if (object instanceof List) {
	    List<Identity> r = new LinkedList<>();
	    for (Object o : (List<Object>) object) {
		r.add(write(o));
	    }
	    return cas.store(new GeneralPersistableResource(getGson().toJson(r)));
	}
	else if (object instanceof Map) {
	    Map<String, Identity> r = new HashMap<>();
	    for (Map.Entry<String, Object> o : ((Map<String, Object>) object).entrySet()) {
		r.put(o.getKey(), write(o.getValue()));
	    }
	    return cas.store(new GeneralPersistableResource(getGson().toJson(r)));
	}
	else {
	    return cas.store(new GeneralPersistableResource(getGson().toJson(object)));
	}
    }
}
