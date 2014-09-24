package com.crypticbit.javelin.convert;

@SuppressWarnings("serial")
class HackedRuntimeException extends RuntimeException {

	HackedRuntimeException(String message, VisitorException e) {
		super(message, e);
	}
}
