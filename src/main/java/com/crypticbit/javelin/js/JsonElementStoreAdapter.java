package com.crypticbit.javelin.js;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.*;

public class JsonElementStoreAdapter extends DataAccessInterface<JsonElement> {

    JsonElementStoreAdapter(ContentAddressableStorage cas, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
    }

    @Override
    public JsonElement read(Identity digest) throws JsonSyntaxException, StoreException {
	JsonElement in = new JsonParser().parse(cas.get(digest).getAsString());
	if (in.isJsonArray()) {
	    JsonArray r = new JsonArray();
	    for (JsonElement e : in.getAsJsonArray()) {
		r.add(read(getGson().fromJson(e, Identity.class)));
	    }
	    return r;
	}
	else if (in.isJsonObject()) {
	    JsonObject o = new JsonObject();
	    for (Entry<String, JsonElement> e : in.getAsJsonObject().entrySet()) {
		o.add(e.getKey(), read(getGson().fromJson(e.getValue(), Identity.class)));
	    }
	    return o;
	}
	else {
	    return in;
	}
    }

    @Override
    public Identity write(JsonElement element) throws StoreException {
	if (element.isJsonArray()) {
	    LinkedList<Identity> array = new LinkedList<>();
	    for (JsonElement e : element.getAsJsonArray()) {
		array.add(write(e));
	    }
	    return cas.store(new GeneralPersistableResource(getGson().toJson(array)));
	}
	else if (element.isJsonObject()) {
	    Map<String, Identity> map = new HashMap<>();
	    for (Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
		map.put(e.getKey(), write(e.getValue()));
	    }
	    return cas.store(new GeneralPersistableResource(getGson().toJson(map)));
	}
	else {
	    return cas.store(new GeneralPersistableResource(getGson().toJson(element)));
	}

    }

}
