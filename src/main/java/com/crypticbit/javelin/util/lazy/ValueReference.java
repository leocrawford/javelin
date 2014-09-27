package com.crypticbit.javelin.util.lazy;

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
