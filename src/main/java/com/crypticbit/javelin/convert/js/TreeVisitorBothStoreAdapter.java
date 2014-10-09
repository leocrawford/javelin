package com.crypticbit.javelin.convert.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.convert.TreeCopySource;
import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.convert.lazy.LazyArray;
import com.crypticbit.javelin.convert.lazy.LazyMap;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.*;

public class TreeVisitorBothStoreAdapter implements TreeCopySource<Key, JsonElement> {

    private AddressableStorage store;
    private static Gson gson = new Gson();

    public TreeVisitorBothStoreAdapter(AddressableStorage store) {
	this.store = store;
    }

    @Override
    public Function<Object, JsonElement> getDestTransform() {
	return new Function<Object, JsonElement>() {

	    @Override
	    public JsonElement apply(Object unpackedElement) {
		if (unpackedElement == null)
		    return JsonNull.INSTANCE;
		if (unpackedElement instanceof Map) {
		    JsonObject result = new JsonObject();
		    for (Map.Entry<String, Object> entry : ((Map<String, Object>) unpackedElement).entrySet())
			result.add(entry.getKey(), convertObjectToKey(entry.getValue()));
		    return result;
		}
		if (unpackedElement instanceof List) {
		    JsonArray result = new JsonArray();
		    for (Object entry : (List<Object>) unpackedElement) {
			result.add(convertObjectToKey(entry));
		    }
		    return result;
		}
		try {
		    return gson.toJsonTree(pack(gson.toJsonTree(unpackedElement)).getKeyAsString());
		}
		catch (VisitorException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    throw new Error();
		}
	    }

	    private JsonElement convertObjectToKey(Object entry) {
		try {
		    return gson.toJsonTree(pack(apply(entry)).getKeyAsString());
		}
		catch (VisitorException e) {
		    // FIXME
		    e.printStackTrace();
		    throw new Error();
		}
	    }
	};

    }

    @Override
    public Key pack(JsonElement unpackedElement) throws VisitorException {
	try {
	    return store.store(unpackedElement, JsonElement.class);
	}
	catch (StoreException e) {
	    throw new VisitorException("unable to copy the tree to the store", e);
	}
    }

    private final Function<JsonElement, Reference> jsonToIdentityReferenceFunction = new Function<JsonElement, Reference>() {
	@Override
	public Reference apply(JsonElement input) {
	    return new IdentityReference(new Key(input.getAsString()));
	}
    };

    @Override
    public Function<JsonElement, Object> getSourceTransform() {
	return new Function<JsonElement, Object>() {

	    @Override
	    public Object apply(JsonElement input) {
		if (input.isJsonObject()) {
		    return new LazyMap(Maps.transformValues(asMap(input.getAsJsonObject()),
			    jsonToIdentityReferenceFunction));
		}
		else if (input.isJsonArray()) {
		    return new LazyArray(com.google.common.collect.Lists.transform(asArray(input.getAsJsonArray()),
			    jsonToIdentityReferenceFunction));
		}
		else if (input.isJsonPrimitive()) {
		    return jsonToIdentityReferenceFunction.apply(input);
		}
		else
		    return null;
	    }

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
	};
    }

    @Override
    public JsonElement unpack(Key element) throws VisitorException {
	try {
	    return store.get(element, JsonElement.class);
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
		return getSourceTransform().apply(unpack(key));
	    }
	    catch (VisitorException e) {
		// FIXME
		throw new Error(e);
	    }
	}

    }

}