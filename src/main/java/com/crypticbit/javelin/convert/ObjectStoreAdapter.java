package com.crypticbit.javelin.convert;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.convert.lazy.LazyArray;
import com.crypticbit.javelin.convert.lazy.LazyMap;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public final class ObjectStoreAdapter extends JsonStoreAdapterFactory implements TreeMapper<Key, Object> {
    private final Function<Object, JsonElement> objectToJsonKeyFunction = new Function<Object, JsonElement>() {

	@Override
	public JsonElement apply(Object input) {
	    return JsonStoreAdapterFactory.gson.toJsonTree(write(input).getKeyAsString());

	}
    };

    private final Function<JsonElement, Reference> JsonKeyToIdentityFunction = new Function<JsonElement, Reference>() {
	@Override
	public Reference apply(JsonElement input) {
	    return new IdentityReference(keyFromJsonElement(input));
	}

	class IdentityReference implements Reference {

	    private Key key;

	    public IdentityReference(Key key) {
		this.key = key;
	    }

	    @Override
	    public Object getValue() {
		return read(key);
	    }

	}
    };

    public ObjectStoreAdapter(AddressableStorage store) {
	super(store);
    }

    @Override
    public Object read(Key element) {
	try {
	    JsonElement input = store.getCas(element, JsonElement.class);

	    if (input.isJsonObject()) {
		return new LazyMap(Maps.transformValues(asMap(input.getAsJsonObject()), JsonKeyToIdentityFunction));
	    }
	    else if (input.isJsonArray()) {
		return new LazyArray(Lists.transform(asArray(input.getAsJsonArray()), JsonKeyToIdentityFunction));
	    }
	    else if (input.isJsonPrimitive()) {
		return parsePrimitiveStatic(input.getAsJsonPrimitive());
	    }
	    else {
		return null;
	    }
	}
	catch (StoreException e) {
	    throw new IllegalStateException("unable to copy the tree referenced by key " + element, e);
	}
    }

    @Override
    public Key write(Object element) {
	if (element == null) {
	    return save(JsonNull.INSTANCE);
	}
	if (element instanceof Map) {
	    return save(createJsonObject(((Map<String, Object>) element).entrySet(), objectToJsonKeyFunction));
	}
	if (element instanceof List) {
	    return save(createJsonArray(((List<Object>) element), objectToJsonKeyFunction));
	}
	else {
	    return save(JsonStoreAdapterFactory.gson.toJsonTree(element));
	}

    }

    Object parsePrimitiveStatic(JsonPrimitive primitive) {
	// return JsonStoreAdapterFactory.gson.fromJson(primitive, Object.class);
	if (primitive.isBoolean()) {
	    return primitive.getAsBoolean();
	}
	if (primitive.isNumber()) {
	    if (!primitive.getAsString().contains(".")) {
		return primitive.getAsInt();
	    }
	    else {
		return primitive.getAsFloat();
	    }
	}
	if (primitive.isString()) {
	    return primitive.getAsString();
	}
	throw new IllegalStateException("illegal Json Type found: " + primitive);
    }

}