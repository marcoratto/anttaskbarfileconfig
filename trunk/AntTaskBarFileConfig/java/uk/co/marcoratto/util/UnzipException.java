package uk.co.marcoratto.util;

public class UnzipException extends Exception {

	public UnzipException(String s) {
		super(s);
	}

	public UnzipException(Throwable t) {
		super(t);
	}

	public UnzipException(Exception e) {
		super(e);
	}
}
