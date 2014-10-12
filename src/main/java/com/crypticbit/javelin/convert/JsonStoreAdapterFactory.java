package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.gson.JsonElement;

public class JsonStoreAdapterFactory {

    private TreeCopy<Key, JsonElement> jea;
    private TreeCopy<Key, Object> joa;

    public JsonStoreAdapterFactory(AddressableStorage store) {

	TreeVisitorBothStoreAdapter sa = new TreeVisitorBothStoreAdapter(store);
	TreeVisitorBothElementAdapter ea = new TreeVisitorBothElementAdapter();
	TreeVisitorSourceObjectAdapter oa = new TreeVisitorSourceObjectAdapter();

	jea = new TreeCopy<>(sa, ea);
	joa = new TreeCopy<>(sa, oa);

    }

    public TreeCopy<Key,JsonElement> getJsonElementAdapter() {
	return jea;
    }

    public TreeCopy<Key, Object> getJsonObjectAdapter() {
	return joa;
    }

}
