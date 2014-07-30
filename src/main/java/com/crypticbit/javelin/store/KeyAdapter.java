package com.crypticbit.javelin.store;

import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class KeyAdapter implements Adapter<Key> {

    static KeyFactory keyFactory = new KeyFactory();

    public KeyAdapter() {
    }

    @Override
    public byte[] toByteArray(Key key) {
	return key.getDigestAsByte();
    }

    @Override
    public Key fromByteArray(byte[] bytes) {
	return new Key(bytes);
    }

    @Override
    public Key getContentDigest(Key element) {
	// only should be called for content addressable values
	throw new UnsupportedOperationException();
    }


}
