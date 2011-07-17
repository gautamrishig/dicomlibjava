package com.sidewinder.dicomreader.exception;

public class MalformedDicomException extends Exception {

	private static final long serialVersionUID = -1402275358654193998L;

	public MalformedDicomException() {
		super();
	}
	
	public MalformedDicomException(String message) {
		super(message);
	}
}
