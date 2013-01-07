package com.crypticbit.javelin;


public class IllegalJsonException extends Exception {

    public IllegalJsonException(String message, Throwable throwable) {
	super(message,throwable);
    }

    public IllegalJsonException(String message) {
	super(message);
    }

}
