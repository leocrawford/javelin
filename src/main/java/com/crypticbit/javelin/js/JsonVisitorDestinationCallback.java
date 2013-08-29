package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.Identity;
import com.google.common.base.Function;

/**
 * The set of callbacks that are called for every source node visited. Methods
 * are distinguished by type.
 * 
 * @author Leo
 * 
 * @param <T>
 *            The type returned from a visit
 * @param <F>
 *            the type of the collections contents
 */
public interface JsonVisitorDestinationCallback<T, F, I> {

	public T arriveList(List<F> list);

	public T arriveMap(Map<String, F> map);

	public T arriveValue(Object value);

	public Function<I, F> getTransform();

}
