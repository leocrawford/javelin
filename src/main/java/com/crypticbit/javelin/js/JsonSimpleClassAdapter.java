package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.JsonSyntaxException;

public class JsonSimpleClassAdapter<T> extends DataAccessInterface<T> {

    private Class<T> clazz;

    JsonSimpleClassAdapter(ContentAddressableStorage cas, Class<T> clazz, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
	this.clazz = clazz;
    }

    @Override
    public T read(Identity commitId) throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	System.out.println(cas.get(commitId).getAsString()+","+clazz);
	return getGson().fromJson(cas.get(commitId).getAsString(), clazz);
    }

    @Override
    public Identity write(T commit) throws StoreException, IOException {
	return cas.store(new GeneralPersistableResource(getGson().toJson(commit)));
    }

}
