package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

/**
 * Parse a thing of type I and return a thing of type B, which can then be
 * further parsed based on type to a list, map, primitive, etc. The lifecycle
 * is:
 * <ol>
 * <li>Start with the root of a tree, referenced by a thing of type I
 * <li>call <code>parse</code> on it, to get something of type B (which could be
 * the same as I). In many cases this will do the remote or db call
 * <li>call <code>getType</code> to determine the type and then..
 * <li>call <code>parseType</code> depending on return of <code>getType</code>
 * </ol>
 * 
 * @author Leo
 * 
 * @param <I>
 *            the identifier for the root of the tree. Could be the object
 *            itself or a address for it.
 * @param <B>
 *            A representation of the object after it has been fetched or
 *            processed
 * 
 */
interface JsonVisitorSource<I, B> {

	public enum ElementType {
		PRIMITIVE, OBJECT, ARRAY, NULL
	}

	public abstract ElementType getType(B in);

	public abstract B parse(I in) throws JsonSyntaxException, StoreException;

	public abstract Object parsePrimitive(B in);

	public abstract List<I> parseList(B in);

	public abstract Map<String, I> parseMap(B in);

}