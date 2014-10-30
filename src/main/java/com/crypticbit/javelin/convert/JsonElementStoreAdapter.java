package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.gson.AddressToJsonElementWrapper;
import com.google.gson.JsonElement;
import com.google.gson.LazyJsonElement;

public class JsonElementStoreAdapter extends JsonStoreAdapterFactory implements TreeMapper<Key, JsonElement>,
	AddressToJsonElementWrapper {

    public JsonElementStoreAdapter(AddressableStorage store) {
	super(store);
    }

    @Override
    public Key write(JsonElement element) throws TreeMapperException {

	try {
	    if (element.isJsonNull() || element.isJsonPrimitive())
		return save(element);
	    if (element.isJsonObject())
		return save(createJsonObject(element.getAsJsonObject().entrySet(),
			jsonElementToJsonKeyReferencesFunction));
	    if (element.isJsonArray())
		return save(createJsonArray(asArray(element.getAsJsonArray()), jsonElementToJsonKeyReferencesFunction));
	    throw new TreeMapperException("JsonElement(" + element.getClass() + ") was not a recognised type",
		    new IllegalStateException());
	}
	catch (StoreException se) {
	    throw new TreeMapperException("Can't write to store", se);
	}

    }

    @Override
    public JsonElement read(Key element) throws TreeMapperException {
	try {
	    JsonElement input = store.getCas(element, JsonElement.class);
	    if (input.isJsonObject() || input.isJsonArray())
		return new LazyJsonElement(input, this);
	    else
		return input;
	}
	catch (StoreException e) {
	    throw new TreeMapperException("unable to copy the tree referenced by key " + element, e);
	}
    }

    @Override
    public JsonElement wrap(JsonElement input) {
	try {
	    return read(new Key(input.getAsString()));
	}
	catch (TreeMapperException e) {

	    e.printStackTrace();
	    // FIXME
	    throw new Error();
	}
    }

    private final Function<JsonElement, JsonElement> jsonElementToJsonKeyReferencesFunction = new Function<JsonElement, JsonElement>() {
	@Override
	public JsonElement apply(JsonElement input) {
	    try {
		return JsonStoreAdapterFactory.gson.toJsonTree(write(input).getKeyAsString());
	    }
	    catch (TreeMapperException e) {
		// FIXME
		throw new Error();
	    }
	}
    };

}