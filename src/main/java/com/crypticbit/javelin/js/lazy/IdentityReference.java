package com.crypticbit.javelin.js.lazy;

import com.crypticbit.javelin.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class IdentityReference implements Reference {

    private Identity identity;
    private JsonStoreAdapterFactory dereferencedCasAccessInterface;
    private Object value;
    private boolean loaded = false;

    public IdentityReference(JsonStoreAdapterFactory dereferencedCasAccessInterface, Identity identity) {
	this.dereferencedCasAccessInterface = dereferencedCasAccessInterface;
	this.identity = identity;
    }

    @Override
    public Object getValue() {
	if (!loaded) {
	    try {
		value = dereferencedCasAccessInterface.getJsonObjectAdapter().read(identity);
		loaded = true;
	    }
	    catch (JsonSyntaxException | StoreException e) {
		e.printStackTrace();
		throw new Error();
		// FIXME
	    }
	}
	return value;

    }

}
