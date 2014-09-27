package com.crypticbit.javelin.convert;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

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
public interface JsonVisitorDestination<T, F, I> {

	public Function<I, F> getTransform(VisitorContext<I, T> context)
			throws VisitorException;

	public T writeList(I source, List<F> list) throws VisitorException;

	public T writeMap(I source, Map<String, F> map) throws VisitorException;

	public T writeNull(I source) throws VisitorException;

	public T writeValue(I source, Object value) throws VisitorException;

}
