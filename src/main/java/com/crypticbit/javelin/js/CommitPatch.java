package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.Patch;
import difflib.PatchFailedException;

public class CommitPatch {

    private Commit lca;
    private List<ExtendedPatch> patches = new LinkedList<>();

    public CommitPatch(Commit lca) {
	this.lca = lca;
    }

    public Object apply() throws JsonSyntaxException, UnsupportedEncodingException, StoreException,
	    PatchFailedException {
	Object element = lca.getObject();
	if (element instanceof LazyJsonArray)
	    ((LazyJsonArray) element).patch(patches);
	else if (element instanceof LazyJsonMap)
	    ((LazyJsonMap) element).patch(patches);
	else
	    // FIXME - do nothing
	    ;
	return element;
    }

    public void add(ExtendedPatch p) {
	patches.add(p);
    }

}
