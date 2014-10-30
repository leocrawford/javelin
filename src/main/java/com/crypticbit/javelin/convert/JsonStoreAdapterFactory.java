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

    private StoreTreeNodeConverter sa;

    public JsonStoreAdapterFactory(AddressableStorage store) {

	sa = new StoreTreeNodeConverter(store);
    }

    /**
     * Return a converter that converts to and from JsonElement to Keys in the store
     * 
     * @return converter
     */
    public TreeMapper<Key, JsonElement> getJsonElementAdapter() {
	return new TreeMapper<Key, JsonElement>() {

	    @Override
	    public Key write(JsonElement value) throws TreeMapperException {
		return sa.writeAsJsonElement(value);
	    }

	    @Override
	    public JsonElement read(Key element) throws TreeMapperException {
		return sa.readAsJsonElement(element);
	    }
	};
    }

    /**
     * Return a converter that converts to and from Object to Keys in the store
     * 
     * @return
     */
    public TreeMapper<Key, Object> getJavaObjectAdapter() {
	return new TreeMapper<Key, Object>() {

	    @Override
	    public Key write(Object value) throws TreeMapperException {
		return sa.writeAsObject(value);
	    }

	    @Override
	    public Object read(Key element) throws TreeMapperException {
		return sa.readAsObject(element);
	    }
	};
    }

}
