package com.crypticbit.javelin.convert;

/* Chains together two TreeNodeAdapters to allow conversion between two trees */
public interface TreeMapper<S, P> {




    public P read(S element) throws TreeMapperException;
  

    public S write(P value) throws TreeMapperException;

}
