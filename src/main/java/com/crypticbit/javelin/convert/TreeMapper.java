package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.Key;

public interface TreeMapper<P,S> {

	public P read(S key) throws VisitorException;

	public S write(P value) throws VisitorException;

}
