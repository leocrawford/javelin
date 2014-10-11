package com.crypticbit.javelin.convert;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class TreeVisitorSourceObjectAdapter implements TreeCopySource<Object> {

    @Override
    public Object pack(Object element) {
	return element;
    }


    @Override
    public Object unpack(Object element) {
	return element;
    }

}
