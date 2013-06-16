package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.Patch;
import difflib.PatchFailedException;

public class CommitPatch {

	private Commit lca;
	private Patch result;

	public CommitPatch(Commit lca, Patch result) {
		this.lca = lca;
		this.result = result;
	}

	public Object apply() throws JsonSyntaxException,
			UnsupportedEncodingException, StoreException, PatchFailedException {
		Object element = lca.getObject();
		if (element instanceof LazyJsonArray)
			((LazyJsonArray) element).patch(result);
		else if (element instanceof LazyJsonMap)
			((LazyJsonMap) element).patch(result);
		else
			// FIXME - do nothing
			;
		return element;
	}
}
