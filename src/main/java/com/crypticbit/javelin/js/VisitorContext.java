package com.crypticbit.javelin.js;

import com.google.common.base.Function;

public interface VisitorContext<I, T> {

	public Function<I,T> getRecurseFunction();
	public Function<T,T> getHaltFunction();
	
	
}
