package com.crypticbit.javelin.js;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public abstract class DataAccessInterface<T> {

    protected AddressableStorage cas;
    protected JsonStoreAdapterFactory jsa;

    protected DataAccessInterface(AddressableStorage cas, JsonStoreAdapterFactory jsa) {
	this.cas = cas;
	this.jsa = jsa;
    }

    public Gson getGson() {
	return jsa.getGson();
    }

    public abstract T read(Key commitId) throws StoreException, JsonSyntaxException, VisitorException;

    public abstract Key write(T commit) throws StoreException, VisitorException;

}
