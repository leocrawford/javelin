package com.crypticbit.javelin.store;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Random;

import com.google.common.io.BaseEncoding;

/**
 * Subclases must implement Comparable
 * 
 * @author leo
 */
public class Digest implements Identity {

    private ByteBuffer digest;

    /** Random digest */
    public Digest() {
	digest = ByteBuffer.wrap(createRandomData(64));
    }

    public Digest(byte[] digestAsByte) {
	this.digest = ByteBuffer.wrap(digestAsByte);
    }

    public Digest(MessageDigest messageDigest) {
	digest = ByteBuffer.wrap(messageDigest.digest());
    }

    public Digest(String string) {
	this.digest = ByteBuffer.wrap(BaseEncoding.base32Hex().decode(string));
    }

    @Override
    public int compareTo(Identity o) {
	if (o instanceof Digest) {
	    return digest.compareTo(((Digest) o).digest);
	}
	else {
	    return this.getClass().getName().compareTo(o.getClass().getName());
	}
    }

    @Override
    public boolean equals(Object compare) {
	if (compare instanceof Digest) {
	    return digest.equals(((Digest) compare).digest);
	}
	else {
	    return false;
	}
    }

    @Override
    public int hashCode() {
	return digest.hashCode();
    }

    @Override
    public byte[] getDigestAsByte() {
	return digest.array();
    }

    @Override
    public String getDigestAsString() {
	return BaseEncoding.base32Hex().encode(getDigestAsByte());
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
