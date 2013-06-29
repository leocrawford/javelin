package com.crypticbit.javelin.js.lazy;

public class ValueReference implements Reference {

    private Object value;

    ValueReference(Object value) {
	this.value = value;
    }

    @Override
    public Object getValue() {
	return value;
    }

}
