package com.crypticbit.javelin.merkle;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;

/**
 * The "anchor" represents an address and it's associated value (which is also as address). This is used when we have
 * content addressable storage but we need to preserve a permanent link to it.
 */

public abstract class Anchor {

    protected static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.merkle");

    private final Key address;
    private final AddressableStorage store;
    // used only to make sure we have the freshest value on write
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

    public Key getDestinationAddress() throws StoreException {
	if (store.checkKas(address)) {
	    return (value = store.getKas(address, Key.class));
	}
	else {
	    return null;
	}
    }

    public Key getSourceAddress() {
	return address;
    }

    /** write a value to the anchor, but if the value has changed from the last read then throw Exception */
    public void setDestinationAddress(Key newValue) throws StoreException {
	if (LOG.isLoggable(Level.FINE)) {
	    LOG.log(Level.FINE, "Updating Anchor " + this + " to value " + newValue);
	}

	store.store(address, value, newValue, Key.class);
	value = newValue;
    }

    protected AddressableStorage getStore() {
	return store;
    }

}
