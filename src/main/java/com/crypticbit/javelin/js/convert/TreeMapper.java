package com.crypticbit.javelin.js.convert;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.gson.JsonElement;

/** Copies one tree structure to another, potentially with different implementations */

class TreeMapper<T, F, I, B> implements DataAccessInterface<Object> {

    private JsonVisitorCasAdapter casAdapter;
    private JsonVisitorSource<Object, B> source;
    private JsonVisitorDestination<T, F, Key> dest;

    TreeMapper(AddressableStorage store, JsonVisitorSource<Object, B> source,
	    JsonVisitorDestination<T, F, Key> dest) {
	this.source = source;
	this.dest = dest;
	casAdapter = new JsonVisitorCasAdapter(store);
    }

    @Override
    public Object read(Key commitId) throws VisitorException {
System.out.println("read: "+commitId);
	JsonVisitor<T, F, Key, JsonElement> sv = new JsonVisitor<>(casAdapter, dest);
	return sv.visit(commitId);
    }

    @Override
    public Key write(Object object) throws VisitorException {
	System.out.println("write: "+object);
	JsonVisitor<Key, Key, Object, B> sv = new JsonVisitor<Key, Key, Object, B>(source, casAdapter);
	return sv.visit(object);
    }
}