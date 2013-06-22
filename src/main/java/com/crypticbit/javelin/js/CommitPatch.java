package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.diff.ExtendedPatch;
import com.crypticbit.javelin.diff.SpecialPatch;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class CommitPatch {

    private Object lca;
    private SpecialPatch patch;

    public CommitPatch(Object lca, SpecialPatch patch) {
	this.lca = lca;
	this.patch = patch;
    }

    // FIXME - crap code
    public Object apply() throws JsonSyntaxException, UnsupportedEncodingException, StoreException,
	    PatchFailedException {
	if (lca instanceof LazyJsonArray)
	    patch.apply(((LazyJsonArray) lca)) ; // .patch(patch);
	else if (lca instanceof LazyJsonMap)
	    ; // ((LazyJsonMap) element).patch(patch);
	else
	    // FIXME - do nothing
	    ;
	return lca;
    }


}
