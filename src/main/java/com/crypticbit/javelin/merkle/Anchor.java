package com.crypticbit.javelin.merkle;

import java.io.Serializable;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;

/**
 * The "anchor" represents an address and its associated value (which is also as address). This is used when we have
 * content addressable storage but we need to preserve a permanent link to it.
 */

public class Anchor  {

    private final Key address;
    private final AddressableStorage store;
    private Key value;

    public Anchor(AddressableStorage store, Key address) {
	this.store = store;
	this.address = address;
    }

    Anchor(AddressableStorage store) {
	this(store, new Key());
    }

    Anchor(AddressableStorage store, Anchor clone) throws StoreException {
	this(store);
	setDestinationAddress(clone.getDestinationAddress());
    }

    public Key getSourceAddress() {
	return address;
    }

    public Key getDestinationAddress() throws StoreException {
	if (store.checkKas(address)) {
	    return (value = store.getKas(address, Key.class));
	}
	else {
	    return null;
	}
    }

    /** write a value to the anchor, but if the value has changed from the last read then throw Exception */
    public void setDestinationAddress(Key newValue) throws StoreException {
	store.store(address, value, newValue, Key.class);
	value = newValue;
    }
    
    protected AddressableStorage getStore() {
	return store;
    }

}
