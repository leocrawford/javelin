package com.crypticbit.javelin.store;

import java.util.Collection;

public interface CasKasStore {

	public KeyValueStore getCas();
	public KeyValueStore getKas();
	
	public interface KeyValueStore {

		public byte[] get(Key key);
		public byte[] put(Key key, byte[] value);
		public boolean containsKey(Key key);
		public Collection<Key> keySet();
		
	}
	
}
