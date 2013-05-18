package com.crypticbit.javelin.store.cas;

@SuppressWarnings("serial")
public class CasException extends Exception {

    public CasException(String message) {
	super(message);
    }

    public CasException(String message, Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }
}
