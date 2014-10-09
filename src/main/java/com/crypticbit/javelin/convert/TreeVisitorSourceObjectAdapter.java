package com.crypticbit.javelin.convert;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class TreeVisitorSourceObjectAdapter implements TreeCopySource<Object, Object> {


    @Override
    public Function<Object, Object> getDestTransform() {
	return Functions.identity();
    }

    @Override
    public Object pack(Object element) {
	return element;
    }

    @Override
    public Function<Object, Object> getSourceTransform() {
	return Functions.identity();
    }

    @Override
    public Object unpack(Object element) {
	return element;
    }

}
