package com.crypticbit.javelin.js.convert;

import java.util.Set;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.gson.JsonElement;

public class JsonStoreAdapterFactory {

    private DataAccessInterface<JsonElement> jea;
    private DataAccessInterface<Object> joa;

    private AddressableStorage cas;

    public JsonStoreAdapterFactory(AddressableStorage cas) {
	JsonVisitorObjectAdapter jsonObjectAdapter = new JsonVisitorObjectAdapter(this);
	joa = new AddressableStorageMapper(cas, jsonObjectAdapter, jsonObjectAdapter);
	JsonVisitorElementAdapter jsonElementAdapter = new JsonVisitorElementAdapter();
	jea = new AddressableStorageMapper(cas, jsonElementAdapter, jsonElementAdapter);
	this.cas = cas;
    }

    public DataAccessInterface<JsonElement> getJsonElementAdapter() {
	return jea;
    }

    public DataAccessInterface<Object> getJsonObjectAdapter() {
	return joa;
    }

    public JsonVisitor<Set<Key>, Set<Key>, Key, JsonElement> getKeyAdapter() {
	JsonVisitorKeyAdapter jsonKeyAdapter = new JsonVisitorKeyAdapter();
	return new JsonVisitor<Set<Key>, Set<Key>, Key, JsonElement>(new JsonVisitorCasAdapter(cas), jsonKeyAdapter);

    }

}
