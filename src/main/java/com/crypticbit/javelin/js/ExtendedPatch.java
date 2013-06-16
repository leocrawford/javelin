package com.crypticbit.javelin.js;

import difflib.Patch;

public class ExtendedPatch  {

    private final Patch patch;
    private final Object branch;
    
    ExtendedPatch(Patch patch, Object branch) {
	this.patch = patch;
	this.branch = branch;
    }
    
    Patch getPatch() {
	return patch;
    }
    
    Object getBranch() {
	return branch;
    }
    
    
}
