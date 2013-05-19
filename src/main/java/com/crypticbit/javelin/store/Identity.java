package com.crypticbit.javelin.store;

public interface Identity extends Comparable<Identity> {

    public byte[] getDigestAsByte();

    public String getDigestAsString();

}
