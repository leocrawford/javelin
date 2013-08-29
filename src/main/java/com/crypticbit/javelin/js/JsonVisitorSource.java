package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

/**
 * Parse a thing of type S and return a thing of type T, which can then be
 * further parsed based on type to a list, map, primitive, etc
 * 
 * @author Leo
 * 
 * @param <S>
 * @param <T>
 */
interface JsonVisitorSource<S, T> {

	public enum ElementType {
		PRIMITIVE, OBJECT, ARRAY, NULL
	}

	public abstract ElementType getType(T in);

	public abstract T parse(S in) throws JsonSyntaxException, StoreException;

	public abstract Object parsePrimitive(T in);

	public abstract List<S> parseList(T in);

	public abstract Map<String, S> parseMap(T in);

}