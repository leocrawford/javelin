package com.crypticbit.javelin.js;


import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class StoreVisitor<T, F, I, B> {

	public final Function<I, T> RECURSE_FUNCTION = new Function<I, T>() {
		public T apply(I identity) {
			try {
				return visit(identity);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Error();
			}
		}
	};
	public final Function<Identity, Identity> HALT_FUNCTION = new Function<Identity, Identity>() {
		public Identity apply(Identity identity) {
			return identity;
		}
	};

	private JsonVisitorDestinationCallback<T, F, I> destination;
	private JsonVisitorSource<I, B> source;

	StoreVisitor(ContentAddressableStorage cas,
			JsonVisitorDestinationCallback<T, F, I> destination, JsonVisitorSource<I, B> source, Gson gson) {
		this.destination = destination;
		this.source = source;
	}

	public T visit(I digest) throws JsonSyntaxException, StoreException {
		B in = source.parse(digest);
		switch (source.getType(in)) {
		case ARRAY:
			return destination.arriveList(Lists.transform(source.parseList(in),
					destination.getTransform()));
		case OBJECT:
			return destination.arriveMap(Maps.transformValues(source.parseMap(in),
					destination.getTransform()));
		case PRIMITIVE:
			return destination.arriveValue(source.parsePrimitive(in));
		default:
			return destination.arriveValue(null);
		}
	}

}
