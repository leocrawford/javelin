package com.crypticbit.javelin.store;

import java.io.Serializable;

public interface Identity extends Comparable<Identity>, Serializable {

    public byte[] getDigestAsByte();

    public String getDigestAsString();

}
