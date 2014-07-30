package com.crypticbit.javelin.store;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import com.google.common.io.BaseEncoding;

/**
 * Used to uniquely identify resources. Can expose itself as String or byte[]
 * 
 */
public class Key implements Comparable<Key> {

    private byte[] keyAsBytes;

    /** Random digest */
    public Key() {
	keyAsBytes = createRandomData(64);
    }

    public Key(byte[] keyAsBytes) {
	this.keyAsBytes = keyAsBytes;
    }

    public Key(String keyAsString) {
	this.keyAsBytes = BaseEncoding.base32Hex().decode(keyAsString);
    }

    @Override
    public int compareTo(Key key) {
	if (key instanceof Key) {
	    return ByteBuffer.wrap(keyAsBytes).compareTo(ByteBuffer.wrap(key.keyAsBytes));
	}
	else {
	    return this.getClass().getName().compareTo(key.getClass().getName());
	}
    }

    @Override
    public boolean equals(Object compare) {
	if (compare instanceof Key) {
	    return Arrays.equals(keyAsBytes, ((Key) compare).keyAsBytes);
	}
	else {
	    return false;
	}
    }

    public byte[] getKeyAsBytes() {
	return keyAsBytes;
    }

    public String getKeyAsString() {
	return BaseEncoding.base32Hex().encode(getKeyAsBytes());
    }

    @Override
    public int hashCode() {
	return ByteBuffer.wrap(keyAsBytes).hashCode();
    }

    @Override
    public String toString() {
	return getKeyAsString().substring(0, 6);
    }

    private static byte[] createRandomData(int length) {
	byte[] b = new byte[length];
	new Random().nextBytes(b);
	return b;
    }

}
