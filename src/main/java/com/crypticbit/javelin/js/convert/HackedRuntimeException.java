package com.crypticbit.javelin.js.convert;

public class HackedRuntimeException extends RuntimeException {

    public HackedRuntimeException(String message, VisitorException e) {
	super(message, e);
    }
}
