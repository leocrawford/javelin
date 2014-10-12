package com.crypticbit.javelin.convert;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class TreeVisitorSourceObjectAdapter implements TreeNodeAdapter<Object> {

    @Override
    public Object write(Object element) {
	return element;
    }


    @Override
    public Object read(Object element) {
	return element;
    }

}
