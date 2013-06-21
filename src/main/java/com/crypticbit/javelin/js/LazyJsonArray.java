package com.crypticbit.javelin.js;

import java.util.AbstractList;
import java.util.List;

import com.crypticbit.javelin.diff.SpecialPatch;
import com.crypticbit.javelin.store.Digest;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

public class LazyJsonArray extends AbstractList<Object> {

    private List<Digest> backingList;
    private DereferencedCasAccessInterface dereferencedCasAccessInterface;

    public LazyJsonArray(DereferencedCasAccessInterface dereferencedCasAccessInterface, List<Digest> backingList) {
	this.dereferencedCasAccessInterface = dereferencedCasAccessInterface;
	this.backingList = backingList;
    }

    @Override
    public Object get(int index) {
	try {
	    return dereferencedCasAccessInterface.readAsObjects(backingList.get(index));
	}
	catch (Exception e) {
	    // FIXME
	    throw new Error(e);
	}
    }

    @Override
    public int size() {
	return backingList.size();
    }

    public Patch diff(LazyJsonArray them) {
	return DiffUtils.diff(backingList, them.backingList);
    }

    public void patch(SpecialPatch patch) throws PatchFailedException {
	System.out.println("Old: " + backingList);
	patch.apply(backingList);
	System.out.println("New: " + backingList);
    }

}
