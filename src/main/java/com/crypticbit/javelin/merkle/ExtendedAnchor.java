package com.crypticbit.javelin.merkle;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class ExtendedAnchor<T> extends Anchor {

	private AddressableStorage store;
	private Class<T> clazz;
	private T cachedValue;

	public ExtendedAnchor(Key address, AddressableStorage store, Class<T> clazz) {
		super(address);
		this.store = store;
		this.clazz = clazz;
	}

	public ExtendedAnchor(AddressableStorage store, Class<T> clazz) {
		super();
		this.store = store;
		this.clazz = clazz;

	}

	public ExtendedAnchor(AddressableStorage store, Anchor clone, Class<T> clazz)
			throws StoreException {
		super(store, clone);
		// FIXME - write called in super
		this.store = store;
		this.clazz = clazz;

	}

	public T getEndPoint() {
		return cachedValue;
	}

	public T readEndPoint(AddressableStorage store) throws JsonSyntaxException,
			StoreException {
		cachedValue = store.getCas(getValue(store), clazz);
		return cachedValue;
	}

	public T writeEndPoint(AddressableStorage store, T value)
			throws StoreException {
		cachedValue = value;
		setValue(store, store.store(value, clazz));
		return value;
	}

}
