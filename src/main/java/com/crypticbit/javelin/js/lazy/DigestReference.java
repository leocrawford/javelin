package com.crypticbit.javelin.js.lazy;

import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.js.DereferencedCasAccessInterface;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class DigestReference implements Reference {

    private Digest digest;
    private DereferencedCasAccessInterface dereferencedCasAccessInterface;

    public DigestReference(DereferencedCasAccessInterface dereferencedCasAccessInterface, Digest digest) {
	this.dereferencedCasAccessInterface = dereferencedCasAccessInterface;
	this.digest = digest;
    }

    @Override
    public Object getValue() {
	try {
	    return dereferencedCasAccessInterface.readAsObjects(digest);
	}
	catch (JsonSyntaxException | UnsupportedEncodingException | StoreException e) {
	    e.printStackTrace();
	    throw new Error();
	    // FIXME
	}
    }

}
