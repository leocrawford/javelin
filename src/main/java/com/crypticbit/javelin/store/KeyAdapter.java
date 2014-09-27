package com.crypticbit.javelin.store;

/* Adapter to convert Key to and from byte array */

public class KeyAdapter extends JsonAdapter<Key> {

	public KeyAdapter() {
		super(Key.class);
	}

}
