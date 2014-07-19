package com.crypticbit.javelin.js;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.KeyAddressableStorage;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class ExtendedAnchor<T> extends Anchor {

    private DataAccessInterface<T> dataInterface;
    private T cachedValue;

    public ExtendedAnchor(Key address, JsonStoreAdapterFactory jsonFactory, Class<T> clazz) {
	super(address);
	dataInterface = jsonFactory.getSimpleObjectAdapter(clazz);
    }

    public ExtendedAnchor(JsonStoreAdapterFactory jsonFactory, Class<T> clazz) {
	super();
	dataInterface = jsonFactory.getSimpleObjectAdapter(clazz);
    }

    public ExtendedAnchor(KeyAddressableStorage kas, Anchor clone, JsonStoreAdapterFactory jsonFactory, Class<T> clazz)
	    throws StoreException {
	super(kas, clone);
	// FIXME - write called in super
	dataInterface = jsonFactory.getSimpleObjectAdapter(clazz);
    }

    public T getEndPoint() {
	return cachedValue;
    }

    public T readEndPoint(KeyAddressableStorage kas) throws JsonSyntaxException, StoreException, VisitorException {
	cachedValue = dataInterface.read(read(kas));
	return cachedValue;
    }

    public T writeEndPoint(KeyAddressableStorage kas, T value) throws StoreException, VisitorException {
	cachedValue = value;
	write(kas, dataInterface.write(value));
	return value;
    }

}
