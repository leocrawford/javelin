package com.crypticbit.javelin.js;

import java.io.Serializable;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;

/**
 * The "anchor" represents any branch, including head. After every write the anchor is updated with the reference to the
 * head of the write. It's expected that one node will be hard-coded (set manually) which will represent the head or
 * root node, and all others will be referenced from within that data structure.
 */

public class Anchor implements Serializable {

    private Key address;
    private transient Key destination;

    public Anchor(Key address) {
	this.address = address;
    }

    Anchor() {
	address = new Key();
    }

    Anchor(AddressableStorage kas, Anchor clone) throws StoreException {
	this();
	write(kas, clone.read(kas));
    }

    /** Same as read() but returns cached/last value */
    public Key getDestination() {
	return destination;
    }

    public Key getAddress() {
	return address;
    }

    public Key read(AddressableStorage kas) throws StoreException {
	if (kas.check(address)) {
	    destination = kas.get(address, Key.class);
	    return destination;
	}
	else {
	   return null;
	}
    }

    public void write(AddressableStorage kas, Key newReference) throws StoreException {
	kas.store(address, destination, newReference, Key.class);
	destination = newReference;
    }

}
