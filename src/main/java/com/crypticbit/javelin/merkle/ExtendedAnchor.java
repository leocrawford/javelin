package com.crypticbit.javelin.merkle;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

/**
 * An anchor relates a address (in non addressable storage, i.e. one that can be overwritten) to one in content
 * addressable storage. That address in turn relates to a value, which this class helps you find.
 * 
 * @author leo
 * @param <T>
 */
public class ExtendedAnchor<T> extends Anchor {

    private final Class<T> clazz;

    public ExtendedAnchor(AddressableStorage store, Key address, Class<T> clazz) {
	super(store, address);
	this.clazz = clazz;
    }

    public ExtendedAnchor(AddressableStorage store, Class<T> clazz) {
	super(store);
	this.clazz = clazz;

    }

    public ExtendedAnchor(AddressableStorage store, Anchor clone, Class<T> clazz) throws StoreException {

	super(store, clone);
	// FIXME - write called in super. why is this a problem?
	this.clazz = clazz;
    }

    public T readEndPoint() throws JsonSyntaxException, StoreException {
	return getStore().getCas(getValue(), clazz);
    }

    public T writeEndPoint(T value) throws StoreException {
	setValue(getStore().store(value, clazz));
	return value;
    }

    public String toString() {
	try {
	    return super.getAddress()+"->"+super.getValue()+"("+readEndPoint()+")";
	}
	catch (StoreException | JsonSyntaxException e) {
	    return super.getAddress()+"<error>";
	}
    }

    
}
