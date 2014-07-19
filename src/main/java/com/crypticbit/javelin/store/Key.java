package com.crypticbit.javelin.store;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

import com.google.common.io.BaseEncoding;

/**
 * Subclases must implement Comparable
 * 
 * @author leo
 */
public class Key implements Comparable<Key>, Serializable {

    private byte[] digest;

    /** Random digest */
    public Key() {
	digest = createRandomData(64);
    }

    public Key(byte[] digestAsByte) {
	this.digest = digestAsByte;
    }

    public Key(MessageDigest messageDigest) {
	digest = messageDigest.digest();
    }

    public Key(String string) {
	this.digest = BaseEncoding.base32Hex().decode(string);
    }

    @Override
    public int compareTo(Key o) {
	if (o instanceof Key) {
	    return ByteBuffer.wrap(digest).compareTo(ByteBuffer.wrap(((Key) o).digest));
	}
	else {
	    return this.getClass().getName().compareTo(o.getClass().getName());
	}
    }

    @Override
    public boolean equals(Object compare) {
	if (compare instanceof Key) {
	    return Arrays.equals(digest, ((Key) compare).digest);
	}
	else {
	    return false;
	}
    }

    public byte[] getDigestAsByte() {
	return digest;
    }

    public String getDigestAsString() {
	return BaseEncoding.base32Hex().encode(getDigestAsByte());
    }

    @Override
    public int hashCode() {
	return ByteBuffer.wrap(digest).hashCode();
    }

    @Override
    public String toString() {
	return getDigestAsString().substring(0, 6);
    }

    private static byte[] createRandomData(int length) {
	byte[] b = new byte[length];
	new Random().nextBytes(b);
	return b;
    }

}
