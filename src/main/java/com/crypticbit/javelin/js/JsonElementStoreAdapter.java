package com.crypticbit.javelin.js;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

public class JsonElementStoreAdapter extends DataAccessInterface<JsonElement> implements JsonVisitorDestination<JsonElement, JsonElement, Identity> {

    private StoreVisitor<JsonElement,JsonElement, Identity, JsonElement> sv;
    
    JsonElementStoreAdapter(ContentAddressableStorage cas, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
	sv =  new StoreVisitor<>(cas, this, new JsonVisitorCasAdapter(cas,
			jsa.getGson()), jsa.getGson());
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
	return jsa.getGson().toJsonTree(value);
    }

    @Override
    public Function<Identity, JsonElement> getTransform(VisitorContext<Identity, JsonElement> context) {
	return context.getRecurseFunction();
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
	    Map<String, Identity> map = new LinkedTreeMap<>();
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
