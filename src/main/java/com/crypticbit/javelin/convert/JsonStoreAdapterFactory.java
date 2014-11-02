package com.crypticbit.javelin.convert;

import java.util.*;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A factory that returns converters between the store and either java objects or JsonElements
 *
 * @author leo
 */
public class JsonStoreAdapterFactory {

    protected AddressableStorage store;
    protected static Gson gson = new Gson();

    public JsonStoreAdapterFactory(AddressableStorage store) {
	this.store = store;
    }

    /**
     * Return a converter that converts to and from Object to Keys in the store
     *
     * @return
     */
    public TreeMapper<Key, Object> getJavaObjectAdapter() {
	return new ObjectStoreAdapter(store);
    }

    /**
     * Return a converter that converts to and from JsonElement to Keys in the store
     *
     * @return converter
     */
    public TreeMapper<Key, JsonElement> getJsonElementAdapter() {
	return new JsonElementStoreAdapter(store);
    }

    protected List<JsonElement> asArray(JsonArray ja) {
	List<JsonElement> result = new ArrayList<>();
	for (JsonElement entry : ja) {
	    result.add(entry);
	}
	return result;
    }

    protected Map<String, JsonElement> asMap(JsonObject jo) {
	Map<String, JsonElement> result = new HashMap<>();
	for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
	    result.put(entry.getKey(), entry.getValue());
	}
	return result;
    }

    protected <S> JsonArray createJsonArray(List<S> entries, Function<S, JsonElement> elementTransformer) {
	JsonArray result = new JsonArray();
	for (S entry : entries) {
	    result.add(elementTransformer.apply(entry));
	}
	return result;
    }

    protected <S> JsonObject createJsonObject(Set<Map.Entry<String, S>> entries,
	    Function<S, JsonElement> elementTransformer) {
	JsonObject result = new JsonObject();
	for (Map.Entry<String, S> entry : entries) {
	    result.add(entry.getKey(), elementTransformer.apply(entry.getValue()));
	}
	return result;
    }

    protected Key keyFromJsonElement(JsonElement element) {
	return new Key(element.getAsString());
    }

    protected Key save(JsonElement toSave) {
	return store.store(toSave, JsonElement.class);
    }

}
