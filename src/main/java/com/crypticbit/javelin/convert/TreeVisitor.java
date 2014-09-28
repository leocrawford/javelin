package com.crypticbit.javelin.convert;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
 * Map<String,I> or I
 * <li>The destination visitor then gets <code>getTransform</code> applied to
 * convert the Collection<I> to Collection<F> (I use collection loosely)
 * <li>Finally the relevant visitType method is called and an element of
 * <code><T> returned</code>
 * </ol>
 * 
 * @author Leo
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
 */
public class TreeVisitor<T, F, I, B> implements VisitorContext<I, T> {

	private TreeVisitorBoth<T, F, I, B> tv;

	public TreeVisitor(TreeVisitorBoth<T,F,I, B> tv){
		this.tv = tv;
	}

	@Override
	public Function<I, T> getRecurseFunction() {
		return new Function<I, T>() {
			@Override
			public T apply(I input) {
				try {
					return visit(input);
				} catch (VisitorException e) {
					throw new HackedRuntimeException("Failed on element "
							+ input, e);
				}
			}
		};
	}

	public T visit(I input) throws VisitorException {
		B in = tv.parse(input);
		try {
			switch (tv.getType(in)) {
			case ARRAY:
				return tv.writeList(
						
						Lists.transform(tv.parseList(in),
								tv.getTransform(this)));
			case OBJECT:
				return tv.writeMap(
						
						Maps.transformValues(tv.parseMap(in),
								tv.getTransform(this)));
			case PRIMITIVE:
				return tv.writeValue(
						tv.parsePrimitive(in));
			default:
				return tv.writeNull();
			}
			// because guava Function doesn't allows us to throw non run-time
			// exceptions we do this
		} catch (HackedRuntimeException e) {
			throw new VisitorException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("serial")
	static class HackedRuntimeException extends RuntimeException {

		HackedRuntimeException(String message, VisitorException e) {
			super(message, e);
		}
	}

	
}
