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
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public final class ObjectStoreAdapter extends JsonStoreAdapterFactory implements TreeMapper<Key, Object> {
    public ObjectStoreAdapter(AddressableStorage store) {
	super(store);
	// TODO Auto-generated constructor stub
    }

    private final Function<Object, JsonElement> objectToJsonKeyReferencesFunction = new ConvertObjectToKeyFunction();
    private final Function<JsonElement, Reference> jsonToIdentityReferenceFunction = new Function<JsonElement, Reference>() {
	@Override
	public Reference apply(JsonElement input) {
	    return new IdentityReference(new Key(input.getAsString()));
	}
    };

    @Override
    public Key write(Object unpackedElement) throws TreeMapperException {
	try {
	    if (unpackedElement == null)
		return save(JsonNull.INSTANCE);
	    if (unpackedElement instanceof Map)
		return save(createJsonObject(((Map) unpackedElement)
			.entrySet(), objectToJsonKeyReferencesFunction));
	    if (unpackedElement instanceof List)
		return save(createJsonArray(((List) unpackedElement),
			objectToJsonKeyReferencesFunction));
	    else
		return save(JsonStoreAdapterFactory.gson.toJsonTree(unpackedElement));
	}
	catch (StoreException se) {
	    throw new TreeMapperException("Can't write to store", se);
	}
    }

    @Override
    public Object read(Key element) throws TreeMapperException {
	try {
	    JsonElement input = store.getCas(element, JsonElement.class);

	    if (input.isJsonObject()) {
		return new LazyMap(Maps.transformValues(asMap(input.getAsJsonObject()),
			jsonToIdentityReferenceFunction));
	    }
	    else if (input.isJsonArray()) {
		return new LazyArray(com.google.common.collect.Lists.transform(asArray(input
			.getAsJsonArray()), jsonToIdentityReferenceFunction));
	    }
	    else if (input.isJsonPrimitive()) {
		return parsePrimitiveStatic(input.getAsJsonPrimitive());
	    }
	    else
		return null;
	}
	catch (StoreException e) {
	    throw new TreeMapperException("unable to copy the tree referenced by key " + element, e);
	}
    }

    Object parsePrimitiveStatic(JsonPrimitive primitive) {
//	return JsonStoreAdapterFactory.gson.fromJson(primitive, Object.class);
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
	throw new InternalError("illegal Json Type found: " + primitive);
    }

    class ConvertObjectToKeyFunction implements Function<Object, JsonElement> {
	@Override
	public JsonElement apply(Object input) {
	    try {
		return JsonStoreAdapterFactory.gson.toJsonTree(write(input).getKeyAsString());
	    }
	    catch (TreeMapperException e) {
		// FIXME
		throw new Error();
	    }
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