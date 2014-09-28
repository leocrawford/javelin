package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.Key;

public interface TreeMapper<T> {

	public T read(Key key) throws VisitorException;

	public Key write(T value) throws VisitorException;

}
