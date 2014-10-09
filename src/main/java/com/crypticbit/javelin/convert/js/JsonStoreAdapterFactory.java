package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.TreeCopy;
import com.crypticbit.javelin.convert.TreeMapper;
import com.crypticbit.javelin.convert.TreeVisitorSourceObjectAdapter;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.gson.JsonElement;

public class JsonStoreAdapterFactory {

	private TreeMapper<JsonElement,Key> jea;
	private TreeMapper<Object,Key> joa;

	public JsonStoreAdapterFactory(AddressableStorage store) {
		
	    TreeVisitorBothStoreAdapter sa = new TreeVisitorBothStoreAdapter(store);
	    TreeVisitorBothElementAdapter ea = new TreeVisitorBothElementAdapter();
	    TreeVisitorSourceObjectAdapter oa = new TreeVisitorSourceObjectAdapter();
	    
	    jea = new TreeCopy<>(sa, ea);
	    joa = new TreeCopy<>(sa, oa);
	    
	}

	public TreeMapper<JsonElement,Key> getJsonElementAdapter() {
		return jea;
	}

	public TreeMapper<Object,Key> getJsonObjectAdapter() {
		return joa;
	}


}
