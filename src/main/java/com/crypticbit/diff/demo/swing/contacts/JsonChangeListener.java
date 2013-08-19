package com.crypticbit.diff.demo.swing.contacts;

import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public interface JsonChangeListener {

    void notify(String json) ;

}
