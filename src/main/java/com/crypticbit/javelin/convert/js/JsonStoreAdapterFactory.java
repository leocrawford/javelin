package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.TreeMapper;
import com.crypticbit.javelin.convert.TreeVisitorSourceObjectAdapter;
import com.crypticbit.javelin.store.AddressableStorage;
import com.google.gson.JsonElement;

public class JsonStoreAdapterFactory {

	private TreeMapper<JsonElement> jea;
	private TreeMapper<Object> joa;

	public JsonStoreAdapterFactory(AddressableStorage store) {
		TreeVisitorSourceObjectAdapter jsonObjectAdapter = new TreeVisitorSourceObjectAdapter(
				this);
		joa = new AddressableStorageMapper(store, jsonObjectAdapter,
				jsonObjectAdapter);
		TreeVisitorBothElementAdapter jsonElementAdapter = new TreeVisitorBothElementAdapter();
		jea = new AddressableStorageMapper(store, jsonElementAdapter,
				jsonElementAdapter);
	}

	public TreeMapper<JsonElement> getJsonElementAdapter() {
		return jea;
	}

	public TreeMapper<Object> getJsonObjectAdapter() {
		return joa;
	}


}
