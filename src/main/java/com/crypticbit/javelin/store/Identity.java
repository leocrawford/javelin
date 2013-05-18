package com.crypticbit.javelin.store;

public interface Identity<T> extends Comparable<T> {

    public byte[] getDigestAsByte();

    public String getDigestAsString();

}
