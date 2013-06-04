package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SimpleCasAccessInterface<T>  extends DataAccessInterface<T>  {

    private Class<T> clazz;
    
    protected SimpleCasAccessInterface(ContentAddressableStorage cas, Gson gson, Class<T> clazz) {
	super(cas,gson);
	this.clazz = clazz;
    }

    public T read(Identity commitId) throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	return gson.fromJson(cas.get(commitId).getAsString(), clazz);
    }

    public Identity write(T commit) throws StoreException, IOException {
	return cas.store(new GeneralPersistableResource(gson.toJson(commit)));
    }


}
