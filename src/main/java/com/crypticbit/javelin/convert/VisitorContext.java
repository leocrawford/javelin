package com.crypticbit.javelin.convert;

import com.google.common.base.Function;

public interface VisitorContext<I, T> {

    public Function<I, T> getRecurseFunction();

}
