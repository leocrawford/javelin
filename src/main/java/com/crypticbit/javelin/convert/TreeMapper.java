package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.StoreException;

/* Chains together two TreeNodeAdapters to allow conversion between two trees */
public interface TreeMapper<S, P> {

    public P read(S element);

    public S write(P value) throws StoreException;

}
