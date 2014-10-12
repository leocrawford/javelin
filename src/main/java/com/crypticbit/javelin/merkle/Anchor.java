package com.crypticbit.javelin.merkle;

import java.io.Serializable;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;

/**
 * The "anchor" represents an address and its associated value (which is also as
 * address). This is used when we have content addressable storage but we need
 * to preserve a permanent link to it.
 */

@SuppressWarnings("serial")
public class Anchor implements Serializable {

	private Key address;
	private transient Key value;

	public Anchor(Key address) {
		this.address = address;
	}

	Anchor() {
		address = new Key();
	}

	Anchor(AddressableStorage store, Anchor clone) throws StoreException {
		this();
		setValue(store, clone.getValue(store));
	}

	/** Same as read() but returns cached/last value */
	public Key getValue() {
		return value;
	}

	public Key getAddress() {
		return address;
	}

	public Key getValue(AddressableStorage store) throws StoreException {
		if (store.checkKas(address)) {
			value = store.getKas(address, Key.class);
			return value;
		} else {
			return null;
		}
	}

	public void setValue(AddressableStorage store, Key newValue)
			throws StoreException {
		store.store(address, value, newValue, Key.class);
		value = newValue;
	}

}
