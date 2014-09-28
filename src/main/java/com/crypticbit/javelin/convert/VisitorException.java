package com.crypticbit.javelin.convert;

@SuppressWarnings("serial")
public class VisitorException extends Exception {

	public VisitorException(String message) {
		super(message);
	}

	public VisitorException(String message, Throwable cause) {
		super(message, cause);
	}

}
