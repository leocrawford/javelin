package com.crypticbit.javelin.diff;

import java.util.List;

import com.crypticbit.javelin.js.UnorderedIndexedWritesListDecorator;

import difflib.Delta;
import difflib.Patch;
import difflib.PatchFailedException;

public class ListDelta {

    private Patch patch;
    private Object branch;

    public ListDelta(Patch patch, Object branch) {
	this.patch = patch;
	this.branch = branch;
    }

    List wrap(List list) {
	return new UnorderedIndexedWritesListDecorator(list);
    }

    void preprocess(List list) {
	((UnorderedIndexedWritesListDecorator) list).addMode(branch);
    }

    public void apply(List list) {
	try {
	    for (Delta d : patch.getDeltas())
		d.applyTo(((UnorderedIndexedWritesListDecorator) list).chooseMode(branch));
	}
	catch (PatchFailedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new Error();
	}
    }

}
