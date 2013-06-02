package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.crypticbit.javelin.store.cas.PersistableResource;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class CommitFactory extends FactoryImpl {

    protected CommitFactory(ContentAddressableStorage cas, Gson gson) {
	super(cas, gson);
    }

    public CommitDao read(Identity commitId) throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	PersistableResource commitAsEncodedJson = cas.get(commitId);
	return gson.fromJson(commitAsEncodedJson.getAsString(), CommitDao.class);
    }
    
    public Identity write(CommitDao commit) throws StoreException, IOException {
	String commitAsJson = gson.toJson(commit);
	return cas.store(new GeneralPersistableResource(commitAsJson));
    }

}
