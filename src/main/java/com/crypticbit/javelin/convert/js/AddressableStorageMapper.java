package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.TreeMapper;
import com.crypticbit.javelin.convert.TreeVisitor;
import com.crypticbit.javelin.convert.TreeVisitorBoth;
import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.store.Key;
// import com.google.gson.JsonElement;

/**
 * Simple API to use visitor as a way of copying one tree like data structure to
 * another. This is just the front door that give a usable TreeMapper
 * interface.
 */

class AddressableStorageMapper<T, F, I, B> implements
		TreeMapper<Object> {

	private TreeVisitorBoth<T,F,Key,B> in;
	private TreeVisitorBoth<T,F,B,Key> out;

	AddressableStorageMapper(TreeVisitorBoth<T,F,Key,B> in,
			TreeVisitorBoth<T,F,B,Key> out)
{
		this.in = in;
		this.out = out;
	}

	@Override
	public Object read(Key commitId) throws VisitorException {
		return new TreeVisitor<T,F,Key,B>(in,
				out).visit(commitId);
	}

	@Override
	public Key write(Object object) throws VisitorException {
		return new TreeVisitor<Key, Key, Object, B>(
				out, in).visit(object);
	}
}