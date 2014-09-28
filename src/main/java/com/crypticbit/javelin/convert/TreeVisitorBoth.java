package com.crypticbit.javelin.convert;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

public interface TreeVisitorBoth<T, F> {
	// write

//	public Function<I, F> getTransform(VisitorContext<I, T> context)
//			throws VisitorException;

	public T writeList(List<F> list) throws VisitorException;

	public T writeMap(Map<String, F> map) throws VisitorException;

	public T writeNull() throws VisitorException;

	public T writeValue(Object value) throws VisitorException;

	// read

	public abstract ElementType getType(T in);

	public abstract F parse(T in) throws VisitorException;

	public abstract List<T> parseList(F in);

	public abstract Map<String, T> parseMap(F in);

	public abstract Object parsePrimitive(F in);

	public enum ElementType {
		PRIMITIVE, OBJECT, ARRAY, NULL
	}

}

/**
 * The set of callbacks that are called for every source node visited. Methods
 * are distinguished by type.
 * <p>
 * Expectation is that a tree being visited has a node of type
 * <code>Collection<I></code> or <code>I</code>. These are transformed to
 * <code>Collection<F></code> or <code>F</code> by the function returned from
 * <code>getTranform</code>. The
 * <code>arriveType<code> method is then called on the result.
 * 
 * @author Leo
 * @param <T>
 *            The type returned from a visit
 * @param <F>
 *            the type of the collections contents after transformation
 * @param <I>
 *            the type of the collections contents before transformation
 */
interface TreeVisitorDestination<T, F, I> {

}

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
 * @param <I>
 *            the identifier for the root of the tree. Could be the object
 *            itself or a address for it.
 * @param <B>
 *            A representation of the object after it has been fetched or
 *            processed
 */
