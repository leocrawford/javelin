package com.crypticbit.javelin.cas;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class ImmutableComparableMessageDigest implements Digest, Comparable<ImmutableComparableMessageDigest> {

    private MessageDigest delegate;
    private ByteBuffer digest;

    ImmutableComparableMessageDigest(MessageDigest messageDigest) {
	// ideally clone so that we know it's not being changed in the background
	try {
	    this.delegate = (MessageDigest) messageDigest.clone();
	}
	catch (CloneNotSupportedException e) {
	    this.delegate = messageDigest;
	}
	digest = ByteBuffer.wrap(delegate.digest());
    }

    public byte[] getDigestAsByte() {
	return digest.array();
    }

    public String getDigestAsString() {
	return new BigInteger(1, getDigestAsByte()).toString(16);
    }

    public String toString() {
	return delegate.toString();
    }

    @Override
    public int compareTo(ImmutableComparableMessageDigest o) {
	return digest.compareTo(o.digest);
    }

}
