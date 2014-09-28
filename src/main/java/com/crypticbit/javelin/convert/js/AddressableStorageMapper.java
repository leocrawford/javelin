package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.TreeMapper;
import com.crypticbit.javelin.convert.TreeVisitor;
import com.crypticbit.javelin.convert.TreeVisitorDestination;
import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.convert.TreeVisitorSource;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
// import com.google.gson.JsonElement;
import com.google.gson.JsonElement;

/**
 * Simple API to use visitor as a way of copying one tree like data structure to
 * another. This is just the front door that give a usable TreeMapper
 * interface.
 */

class AddressableStorageMapper<T, F, I, B> implements
		TreeMapper<Object> {

	private TreeVisitorBothStoreAdapter casAdapter;
	private TreeVisitorSource<Object, B> source;
	private TreeVisitorDestination<T, F, Key> dest;

	AddressableStorageMapper(AddressableStorage store,
			TreeVisitorSource<Object, B> source,
			TreeVisitorDestination<T, F, Key> dest) {
		this.source = source;
		this.dest = dest;
		casAdapter = new TreeVisitorBothStoreAdapter(store);
	}

	@Override
	public Object read(Key commitId) throws VisitorException {
		TreeVisitor<T, F, Key, JsonElement> sv = new TreeVisitor<>(casAdapter,
				dest);
		return sv.visit(commitId);
	}

	@Override
	public Key write(Object object) throws VisitorException {
		TreeVisitor<Key, Key, Object, B> sv = new TreeVisitor<Key, Key, Object, B>(
				source, casAdapter);
		return sv.visit(object);
	}
}