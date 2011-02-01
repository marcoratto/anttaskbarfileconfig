package uk.co.marcoratto.util;

public class XMLException extends Exception {

	public XMLException(Throwable t) {
		super(t);
	}

	public XMLException(Exception e) {
		super(e);
	}
}
