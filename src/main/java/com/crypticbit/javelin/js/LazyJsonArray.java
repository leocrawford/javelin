package com.crypticbit.javelin.js;

import java.util.*;

import com.crypticbit.javelin.store.Digest;

import difflib.Delta;
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

    public void patch(List<ExtendedPatch> patches) throws PatchFailedException {
	Set<Object> branches = new HashSet<>();
	for (ExtendedPatch ep : patches) {
	    branches.add(ep.getBranch());
	}
	UnorderedIndexedWritesListDecorator<Object> list = new UnorderedIndexedWritesListDecorator<Object>(
		new LinkedList<Object>(backingList), branches);
	for (ExtendedPatch ep : patches) {
	    list.chooseMode(ep.getBranch());
	    for (Delta d : ep.getPatch().getDeltas())
		d.applyTo(list);
	}

	System.out.println("Old: " + backingList);
	backingList.clear();
	for (Object o : list) {
	    backingList.add((Digest) o);
	}
	System.out.println("New: " + backingList);
    }

}
