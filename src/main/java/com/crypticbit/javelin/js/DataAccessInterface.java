package com.crypticbit.javelin.js;

import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class DataAccessInterface<T> {

    protected ContentAddressableStorage cas;
    protected JsonStoreAdapterFactory jsa;

    protected DataAccessInterface(ContentAddressableStorage cas, JsonStoreAdapterFactory jsa) {
	this.cas = cas;
	this.jsa = jsa;
    }

    public Gson getGson() {
	return jsa.getGson();
    }

    public abstract T read(Identity commitId) throws StoreException, JsonSyntaxException;

    public abstract Identity write(T commit) throws StoreException;

}
