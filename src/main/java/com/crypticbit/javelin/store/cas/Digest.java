package com.crypticbit.javelin.store.cas;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

import com.crypticbit.javelin.store.Identity;
import com.google.common.io.BaseEncoding;

/**
 * Subclases must implement Comparable
 * 
 * @author leo
 */
public class Digest implements Identity<Digest> {

    private ByteBuffer digest;

    public Digest(MessageDigest messageDigest) {
	digest = ByteBuffer.wrap(messageDigest.digest());
    }

    public Digest(String string) {
	this.digest = ByteBuffer.wrap(BaseEncoding.base32Hex().decode(string));
    }

    public Digest(byte[] digestAsByte) {
	this.digest = ByteBuffer.wrap(digestAsByte);
    }

    @Override
    public int compareTo(Digest o) {
	return digest.compareTo(o.digest);
    }

    public byte[] getDigestAsByte() {
	return digest.array();
    }

    public String getDigestAsString() {
	return  BaseEncoding.base32Hex().encode(getDigestAsByte());
    }

    @Override
    public String toString() {
	return getDigestAsString().substring(0,6)+"...";
    }


}
