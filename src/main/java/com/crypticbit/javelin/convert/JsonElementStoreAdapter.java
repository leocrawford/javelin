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

    private final Function<JsonElement, JsonElement> jsonElementToJsonKeyReferencesFunction = new Function<JsonElement, JsonElement>() {
	@Override
	public JsonElement apply(JsonElement input) {

	    return JsonStoreAdapterFactory.gson.toJsonTree(write(input).getKeyAsString());
	}
    };

    public JsonElementStoreAdapter(AddressableStorage store) {
	super(store);
    }

    @Override
    public LazyJsonElement read(Key element) {
	return new LazyJsonElement(element, this);
    }

    @Override
    public JsonElement unwrap(JsonElement element) {
	return new LazyJsonElement(keyFromJsonElement(element), this);
    }

    @Override
    public JsonElement wrap(Key key) {
	try {
	    return store.getCas(key, JsonElement.class);
	}
	catch (StoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new Error();
	}

    }

    @Override
    public Key write(JsonElement element) {

	if (element.isJsonNull() || element.isJsonPrimitive()) {
	    return save(element);
	}
	if (element.isJsonObject()) {
	    return save(createJsonObject(element.getAsJsonObject().entrySet(), jsonElementToJsonKeyReferencesFunction));
	}
	if (element.isJsonArray()) {
	    return save(createJsonArray(asArray(element.getAsJsonArray()), jsonElementToJsonKeyReferencesFunction));
	}
	throw new IllegalStateException("JsonElement(" + element.getClass() + ") was not a recognised type");

    }

}