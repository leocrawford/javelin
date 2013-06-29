package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class CommitPatch {

    private Object lca;
    private SequenceDiff patch;

    public CommitPatch(Object lca, SequenceDiff patch) {
	this.lca = lca;
	this.patch = patch;
    }

    // FIXME - crap code
    public Object apply() throws JsonSyntaxException, UnsupportedEncodingException, StoreException,
	    PatchFailedException {
	patch.apply(lca);
	return lca;
    }

}
