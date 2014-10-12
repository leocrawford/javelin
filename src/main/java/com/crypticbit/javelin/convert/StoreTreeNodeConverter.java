package com.crypticbit.javelin.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.convert.lazy.LazyArray;
import com.crypticbit.javelin.convert.lazy.LazyMap;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.*;

public class StoreTreeNodeConverter implements TreeNodeAdapter<Key> {

    private AddressableStorage store;
    private static Gson gson = new Gson();

    public StoreTreeNodeConverter(AddressableStorage store) {
	this.store = store;
    }

    @Override
    public Key write(Object unpackedElement) throws TreeMapperException {
	try {
	    if (unpackedElement == null)
		return save(JsonNull.INSTANCE);
	    if (unpackedElement instanceof Map) {
		JsonObject result = new JsonObject();
		for (Map.Entry<String, Object> entry : ((Map<String, Object>) unpackedElement).entrySet())
		    result.add(entry.getKey(), convertObjectToKey(entry.getValue()));
		return save(result);
	    }
	    if (unpackedElement instanceof List) {
		JsonArray result = new JsonArray();
		for (Object entry : (List<Object>) unpackedElement) {
		    result.add(convertObjectToKey(entry));
		}
		return save(result);
	    }
	    return save(gson.toJsonTree(unpackedElement));
	}
	catch (StoreException se) {
	    throw new TreeMapperException("Can't write to store", se);
	}
    }

    private Key save(JsonElement toSave) throws StoreException {
	return store.store(toSave, JsonElement.class);
    }

    private JsonElement convertObjectToKey(Object entry) throws TreeMapperException {
	return gson.toJsonTree(write(entry).getKeyAsString());
    }

    private final Function<JsonElement, Reference> jsonToIdentityReferenceFunction = new Function<JsonElement, Reference>() {
	@Override
	public Reference apply(JsonElement input) {
	    return new IdentityReference(new Key(input.getAsString()));
	}
    };

    private Map<String, JsonElement> asMap(JsonObject jo) {
	Map<String, JsonElement> result = new HashMap<>();
	for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
	    result.put(entry.getKey(), entry.getValue());
	}
	return result;

    }

    private List<JsonElement> asArray(JsonArray ja) {
	List<JsonElement> result = new ArrayList<>();
	for (JsonElement entry : ja) {
	    result.add(entry);
	}
	return result;
    }

    @Override
    public Object read(Key element) throws TreeMapperException {
	try {
	    JsonElement input = store.get(element, JsonElement.class);

	    if (input.isJsonObject()) {
		return new LazyMap(Maps
			.transformValues(asMap(input.getAsJsonObject()), jsonToIdentityReferenceFunction));
	    }
	    else if (input.isJsonArray()) {
		return new LazyArray(com.google.common.collect.Lists.transform(asArray(input.getAsJsonArray()),
			jsonToIdentityReferenceFunction));
	    }
	    else if (input.isJsonPrimitive()) {
		return gson.fromJson(input, Object.class);
	    }
	    else
		return null;
	}
	catch (StoreException e) {
	    throw new TreeMapperException("unable to copy the tree referenced by key " + element, e);
	}
    }

    class IdentityReference implements Reference {

	private Key key;

	public IdentityReference(Key key) {
	    this.key = key;
	}

	@Override
	public Object getValue() {
	    try {
		return read(key);
	    }
	    catch (TreeMapperException e) {
		// FIXME
		throw new Error(e);
	    }
	}

    }

}