package com.crypticbit.javelin.js.convert;

import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public interface DataAccessInterface<T> {

    public T read(Key commitId) throws StoreException, JsonSyntaxException, VisitorException;

    public Key write(T commit) throws StoreException, VisitorException;

}
