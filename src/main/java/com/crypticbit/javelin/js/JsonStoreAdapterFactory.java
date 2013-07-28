package com.crypticbit.javelin.js;

import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonStoreAdapterFactory {

    private DataAccessInterface<JsonElement> jea;
    private DataAccessInterface<Object> joa;

    private ContentAddressableStorage cas;
    private Gson gson;
    
    JsonStoreAdapterFactory(ContentAddressableStorage cas,  Gson gson) {
	jea = new JsonElementStoreAdapter(cas, gson, this);
	joa = new JsonObjectStoreAdapter(cas, gson, this);
	this.cas = cas;
	this.gson = gson;
	
    }

    public DataAccessInterface<JsonElement> getJsonElementAdapter() {
	return jea;
    }

    public DataAccessInterface<Object> getJsonObjectAdapter() {
	return joa;
    }

    public <T> DataAccessInterface<T> getSimpleObjectAdapter(Class<T> clazz ) {
	return new JsonSimpleClassAdapter<T>(cas, gson, clazz, this);
    }
    
}
