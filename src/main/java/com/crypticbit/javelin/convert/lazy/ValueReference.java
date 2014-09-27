package com.crypticbit.javelin.convert.lazy;

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
