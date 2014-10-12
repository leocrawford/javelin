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

public class TreeVisitorBothStoreAdapter implements TreeCopySource<Key> {

    private AddressableStorage store;
    private static Gson gson = new Gson();

    public TreeVisitorBothStoreAdapter(AddressableStorage store) {
	this.store = store;
    }

    @Override
    public Key pack(Object unpackedElement) throws VisitorException {
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
	    throw new VisitorException("Can't write to store", se);
	}
    }

    private Key save(JsonElement toSave) throws StoreException {
	return store.store(toSave, JsonElement.class);
    }

    private JsonElement convertObjectToKey(Object entry) throws VisitorException {
	return gson.toJsonTree(pack(entry).getKeyAsString());
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
    public Object unpack(Key element) throws VisitorException {
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
//		JsonPrimitive value = input.getAsJsonPrimitive();
//		if (value.isBoolean())
//		    return value.getAsBoolean();
//		if (value.isNumber())
//		    if(value.getAsNumber().)
//		    return value.getAsNumber();
//		if (value.isString())
//		    return value.getAsString();
//		else
//		    throw new IllegalStateException("unknown type: " + value);
	    }
	    else
		return null;
	}
	catch (StoreException e) {
	    throw new VisitorException("unable to copy the tree referenced by key " + element, e);
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
		return unpack(key);
	    }
	    catch (VisitorException e) {
		// FIXME
		throw new Error(e);
	    }
	}

    }

}