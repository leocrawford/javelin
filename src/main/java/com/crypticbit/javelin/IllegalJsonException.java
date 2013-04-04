package com.crypticbit.javelin;

public class IllegalJsonException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IllegalJsonException(String message) {
	super(message);
    }

    public IllegalJsonException(String message, Throwable throwable) {
	super(message, throwable);
    }

}
