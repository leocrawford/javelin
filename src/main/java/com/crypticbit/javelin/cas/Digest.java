package com.crypticbit.javelin.cas;


/**
 * Subclases must implement Comparable
 * @author leo
 *
 */
public interface Digest {

    public byte[] getDigestAsByte();

    public String getDigestAsString();

}
