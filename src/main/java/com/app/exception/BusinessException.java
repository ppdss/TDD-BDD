package com.app.exception;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = -999999L;

	public BusinessException(String s) {
		super(s);
	}
}
