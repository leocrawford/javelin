package com.crypticbit.javelin.cas;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Subclases must implement Comparable
 * 
 * @author leo
 */
public class Digest implements Comparable<Digest> {

    private MessageDigest delegate;
    private ByteBuffer digest;

    public Digest(MessageDigest messageDigest) {
	// ideally clone so that we know it's not being changed in the background
	try {
	    this.delegate = (MessageDigest) messageDigest.clone();
	}
	catch (CloneNotSupportedException e) {
	    this.delegate = messageDigest;
	}
	digest = ByteBuffer.wrap(delegate.digest());
    }

    @Override
    public int compareTo(Digest o) {
	return digest.compareTo(o.digest);
    }

    public byte[] getDigestAsByte() {
	return digest.array();
    }

    public String getDigestAsString() {
	return new BigInteger(1, getDigestAsByte()).toString(16);
    }

    @Override
    public String toString() {
	return getDigestAsString().substring(0,6)+"...";
    }


}
