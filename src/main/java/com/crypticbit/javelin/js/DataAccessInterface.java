package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class DataAccessInterface<T> {

    protected ContentAddressableStorage cas;
    protected Gson gson;

    protected DataAccessInterface(ContentAddressableStorage cas, Gson gson) {
	this.cas = cas;
	this.gson = gson;
    }

    public abstract T read(Identity commitId) throws StoreException, JsonSyntaxException, UnsupportedEncodingException;

    public abstract Identity write(T commit) throws StoreException, IOException;

}
