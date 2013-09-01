package com.crypticbit.javelin.js;

import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * walk a tree performing operations on another (or multiple) trees as we
 * travel. Used for copying one tree to another, e.g. in memory Json structure
 * to content addressable storage.
 * <p>
 * The usual journey is:
 * <ol>
 * <li>A node referenced by I is the start of the search
 * <li>The source visitor navigates to the start and "loads" it, returning an
 * element of type B
 * <li>The source visitor then parses element of type <B> to return a List<I>,
 * Map<String,I> or <I>f
 * <li>The destination visitor then gets <code>getTransform</code> applied to
 * convert the Collection<I> to Collection<F> (I use collection loosely)
 * <li>Finally the relevant visitType method is called and an element of
 * <code><T> returned</code>
 * </ol>
 * 
 * @author Leo
 * 
 * @param <T>
 *            The type returned from a visit (destination)
 * @param <F>
 *            the type of the collections contents after transformation
 *            (destination)
 * @param <I>
 *            the type of the collections contents before transformation
 *            (destination) and the identifier for the root of the tree. Could
 *            be the object itself or a address for it. (source)
 * @param <B>
 *            A representation of the object after it has been fetched or
 *            processed (source)
 * 
 */
public class JsonVisitor<T, F, I, B> implements VisitorContext<I, T> {

	private JsonVisitorDestination<T, F, I> destination;
	private JsonVisitorSource<I, B> source;

	JsonVisitor(
			JsonVisitorDestination<T, F, I> destination,
			JsonVisitorSource<I, B> source) {
		this.destination = destination;
		this.source = source;
	}

	public T visit(I input) throws JsonSyntaxException, StoreException {
		B in = source.parse(input);
		switch (source.getType(in)) {
		case ARRAY:
			return destination.arriveList(Lists.transform(source.parseList(in),
					destination.getTransform(this)));
		case OBJECT:
			return destination.arriveMap(Maps.transformValues(
					source.parseMap(in), destination.getTransform(this)));
		case PRIMITIVE:
			return destination.arriveValue(source.parsePrimitive(in));
		default:
			return destination.arriveValue(null);
		}
	}

	@Override
	public Function<I, T> getRecurseFunction() {
		return new Function<I, T>() {
			public T apply(I input) {
				try {
					return visit(input);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new Error();
				}
			}
		};
	}

	@Override
	public Function<T, T> getHaltFunction() {
		return new Function<T, T>() {
			public T apply(T input) {
				return input;
			}
		};
	}

}
