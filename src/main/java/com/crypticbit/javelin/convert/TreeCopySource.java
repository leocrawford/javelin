package com.crypticbit.javelin.convert;

import com.google.common.base.Function;


public interface TreeCopySource<S> {

    // source
    
	public Object unpack(S element) throws VisitorException;
	
	// dest
	
	
	public S pack(Object source) throws VisitorException;

}

