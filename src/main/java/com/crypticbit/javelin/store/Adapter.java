package com.crypticbit.javelin.store;

/**
 * Turns a given class into a byte array and back, and (optionally) creates a
 * repeatable Key from the content to enable content addressable storage.
 */

public interface Adapter<T> {

	/* Converts element to byte[] */
	public byte[] toByteArray(T element);

	/* Converts byte[] to element of class T */
	public T fromByteArray(byte[] bytes);

	/*
	 * Creates a repeatable Key for element. <p>Motivation for having here not
	 * using byte array is that different serialisation methods might want to
	 * retain same key
	 */
	public Key getContentDigest(T element);

}
