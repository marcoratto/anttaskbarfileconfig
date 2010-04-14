package uk.co.marcoratto.util;

public class ZipException extends Exception {

	public ZipException(Throwable t) {
		super(t);
	}

	public ZipException(Exception e) {
		super(e);
	}
}
