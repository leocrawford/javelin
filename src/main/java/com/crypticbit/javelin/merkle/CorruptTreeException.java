package com.crypticbit.javelin.merkle;

public class CorruptTreeException extends Exception {

    public CorruptTreeException(String message) {
	super(message);
    }

    public CorruptTreeException(String message, Throwable cause) {
	super(message, cause);
    }

}
