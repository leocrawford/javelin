package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.gson.JsonElement;

/**
 * A factory that returns converters between the store and either java objects or JsonElements
 * 
 * @author leo
 */
public class JsonStoreAdapterFactory {

    private TreeMapper<Key, JsonElement> jea;
    private TreeMapper<Key, Object> joa;

    public JsonStoreAdapterFactory(AddressableStorage store) {

	StoreTreeNodeConverter sa = new StoreTreeNodeConverter(store);
	JsonElementTreeNodeConverter ea = new JsonElementTreeNodeConverter();
	ObjectTreeNodeConverter oa = new ObjectTreeNodeConverter();

	jea = new TreeMapper<>(sa, ea);
	joa = new TreeMapper<>(sa, oa);

    }

    /**
     * Return a converter that converts to and from JsonElement to Keys in the store
     * 
     * @return converter
     */
    public TreeMapper<Key, JsonElement> getJsonElementAdapter() {
	return jea;
    }

    /**
     * Return a converter that converts to and from Object to Keys in the store
     * 
     * @return
     */
    public TreeMapper<Key, Object> getJavaObjectAdapter() {
	return joa;
    }

}
