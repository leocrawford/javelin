package com.crypticbit.javelin.convert;

import com.google.common.base.Function;


public interface TreeCopySource<S, R> {

    // source
    
	public Function<R, Object> getSourceTransform() throws VisitorException;
	
	public R unpack(S element) throws VisitorException;
	
	// dest
	
	public Function<Object,R> getDestTransform() throws VisitorException;

	public S pack(R unpackedElement) throws VisitorException;

}

