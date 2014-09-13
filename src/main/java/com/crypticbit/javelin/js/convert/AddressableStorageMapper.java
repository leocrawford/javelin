package com.crypticbit.javelin.js.convert;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.google.gson.JsonElement;

/**
 * Simple API to use visitor as a way of copying one tree like data structure to another. This is just the front door
 * that give a usable DataAccessInterface interface.
 */

class AddressableStorageMapper<T, F, I, B> implements DataAccessInterface<Object> {

    private JsonVisitorCasAdapter casAdapter;
    private JsonVisitorSource<Object, B> source;
    private JsonVisitorDestination<T, F, Key> dest;

    AddressableStorageMapper(AddressableStorage store, JsonVisitorSource<Object, B> source,
	    JsonVisitorDestination<T, F, Key> dest) {
	this.source = source;
	this.dest = dest;
	casAdapter = new JsonVisitorCasAdapter(store);
    }

    @Override
    public Object read(Key commitId) throws VisitorException {
	System.out.println("read: " + commitId);
	JsonVisitor<T, F, Key, JsonElement> sv = new JsonVisitor<>(casAdapter, dest);
	return sv.visit(commitId);
    }

    @Override
    public Key write(Object object) throws VisitorException {
	System.out.println("write: " + object);
	JsonVisitor<Key, Key, Object, B> sv = new JsonVisitor<Key, Key, Object, B>(source, casAdapter);
	return sv.visit(object);
    }
}