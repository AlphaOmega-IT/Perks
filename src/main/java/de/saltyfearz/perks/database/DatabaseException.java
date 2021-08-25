package de.saltyfearz.perks.database;

import java.io.Serial;

public class DatabaseException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -7084738630488792761L;
	
	public DatabaseException(String message) {
		super(message);
	}
	
	public DatabaseException(Throwable cause) {
		super(cause);
	}
	
	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
