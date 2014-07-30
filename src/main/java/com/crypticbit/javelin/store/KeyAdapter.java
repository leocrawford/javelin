package com.crypticbit.javelin.store;

/* Adapter to convert Key to and from byte array */

public class KeyAdapter implements Adapter<Key> {

    @Override
    public byte[] toByteArray(Key key) {
	return key.getKeyAsBytes();
    }

    @Override
    public Key fromByteArray(byte[] bytes) {
	return new Key(bytes);
    }

    @Override
    public Key getContentDigest(Key element) {
	// only should be called for content addressable values
	throw new UnsupportedOperationException();
    }

}
