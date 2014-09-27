package com.crypticbit.javelin.convert.js;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.store.Key;

public class IdentityReference implements Reference {

	private Key identity;
	private JsonStoreAdapterFactory dereferencedCasAccessInterface;
	private Object value;
	private boolean loaded = false;

	public IdentityReference(
			JsonStoreAdapterFactory dereferencedCasAccessInterface, Key identity) {
		this.dereferencedCasAccessInterface = dereferencedCasAccessInterface;
		this.identity = identity;
	}

	@Override
	public Object getValue() {
		if (!loaded) {
			try {
				value = dereferencedCasAccessInterface.getJsonObjectAdapter()
						.read(identity);
				loaded = true;
			} catch (VisitorException e) {
				e.printStackTrace();
				throw new Error();
				// FIXME - imporve exception handling
			}
		}
		return value;

	}

}
