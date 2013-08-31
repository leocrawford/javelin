package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.StoreException;
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
 * 
 * @param <T>
 *            The type returned from a visit
 * @param <F>
 *            the type of the collections contents after transformation
 * @param <I>
 *            the type of the collections contents before transformation
 * 
 */
public interface JsonVisitorDestinationCallback<T, F, I> {

	public T arriveList(List<F> list) throws StoreException;

	public T arriveMap(Map<String, F> map) throws StoreException;

	public T arriveValue(Object value) throws StoreException;

	public Function<I, F> getTransform(VisitorContext<I, T> context);
	

}
