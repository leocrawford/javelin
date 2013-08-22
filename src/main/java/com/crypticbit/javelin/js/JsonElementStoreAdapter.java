package com.crypticbit.javelin.js;

import java.util.*;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.gson.*;

public class JsonElementStoreAdapter extends DataAccessInterface<JsonElement> implements StoreVisitorCallback<JsonElement, JsonElement> {

    private StoreVisitor<JsonElement,JsonElement> sv;
    
    JsonElementStoreAdapter(ContentAddressableStorage cas, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
	sv =  new StoreVisitor<>(cas, this, jsa.getGson());
    }

    @Override
    public JsonElement read(Identity digest) throws JsonSyntaxException, StoreException {
	return sv.visit(digest);
    }
    

    @Override
    public JsonElement arriveList(List<JsonElement> list) {
	JsonArray r = new JsonArray();
	for(JsonElement e : list) {
	    r.add(e);
	}
	return r;
    }

    @Override
    public JsonElement arriveMap(Map<String, JsonElement> map) {
	 JsonObject o = new JsonObject();
	    for (Entry<String, JsonElement> e : map.entrySet()) {
		o.add(e.getKey(), e.getValue());
	    }
	    return o;
    }

    @Override
    public JsonElement arriveValue(Object value) {
	System.out.println(value +"-> "+jsa.getGson().toJsonTree(value));
	return jsa.getGson().toJsonTree(value);
    }

    @Override
    public Function<Identity, JsonElement> getTransform() {
	return sv.RECURSE_FUNCTION;
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
