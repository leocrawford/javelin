package com.crypticbit.javelin.js;

import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.Gson;

public class FactoryImpl {

    protected ContentAddressableStorage cas;
    protected Gson gson;
    protected FactoryImpl(ContentAddressableStorage cas, Gson gson) {
	this.cas = cas;
	this.gson = gson;
    }
    
}
