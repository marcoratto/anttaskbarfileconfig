package uk.co.marcoratto.util;

public class UtilityException extends Exception {

	public UtilityException(Throwable t) {
		super(t);
	}

	public UtilityException(Exception e) {
		super(e);
	}
}
