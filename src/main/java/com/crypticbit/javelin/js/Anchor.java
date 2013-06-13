package com.crypticbit.javelin.js;

import java.io.IOException;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.kas.KeyAddressableStorage;

/**
 * The "anchor" represents any branch, including head. After every write the anchor is updated with the reference to the
 * head of the write. It's expected that one node will be hard-coded (set manually) which will represent the head or
 * root node, and all others will be referenced from within that data structure.
 */

public class Anchor {

    private Identity anchor;
    private KeyAddressableStorage kas;
    private Identity reference;

    public Anchor(KeyAddressableStorage kas, Digest anchor) {
	this.kas = kas;
	this.anchor = anchor;

    }

    Anchor(KeyAddressableStorage kas) {
	anchor = new Digest();
	this.kas = kas;
    }

    Anchor(KeyAddressableStorage kas, Anchor clone) throws StoreException, IOException {
	anchor = new Digest();
	this.kas = kas;
	write(clone.read());
    }

    /** Same as read() but returns cached/last value */
    public Identity get() {
	return reference;
    }

    public Identity getDigest() {
	return anchor;
    }

    public Identity read() throws StoreException {
	if (kas.check(anchor)) {
	    reference = new Digest(kas.get(anchor).getBytes());
	    return reference;
	}
	else {
	    // FIXME
	    throw new Error("Id not found: " + anchor);
	}
    }

    public void write(Identity newReference) throws StoreException, IOException {
	kas.store(anchor, reference, newReference);
	reference = newReference;
    }

}
