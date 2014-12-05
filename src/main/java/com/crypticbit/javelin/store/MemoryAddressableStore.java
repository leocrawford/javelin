package com.crypticbit.javelin.store;

import java.util.TreeMap;

public class MemoryAddressableStore implements CasKasStore {

	static class MemoryKeyValueStore extends TreeMap<Key, byte[]> implements KeyValueStore {

		@Override
		public byte[] get(Key key) {
			return super.get(key);
		}

		@Override
		public boolean containsKey(Key key) {
			return super.containsKey(key);
		}
	}

	private MemoryKeyValueStore cas = new MemoryKeyValueStore();
	private MemoryKeyValueStore kas = new MemoryKeyValueStore();

	@Override
	public KeyValueStore getCas() {
		return cas;
	}

	@Override
	public KeyValueStore getKas() {
		return kas;
	}

}
