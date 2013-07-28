package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonSimpleClassAdapter<T> extends DataAccessInterface<T> {

    private Class<T> clazz;

    JsonSimpleClassAdapter(ContentAddressableStorage cas, Gson gson, Class<T> clazz, JsonStoreAdapterFactory jsa) {
	super(cas, gson,jsa);
	this.clazz = clazz;
    }

    @Override
    public T read(Identity commitId) throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	return gson.fromJson(cas.get(commitId).getAsString(), clazz);
    }

    @Override
    public Identity write(T commit) throws StoreException, IOException {
	return cas.store(new GeneralPersistableResource(gson.toJson(commit)));
    }

}
