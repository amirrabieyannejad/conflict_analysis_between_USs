package org.backlogconflict.code.preparation;

public class ActionInJsonFileNotFound extends Exception{

	public ActionInJsonFileNotFound() {
		super("Action in JOSNObject not found!");
	}
	public ActionInJsonFileNotFound(String message) {
		super(message);
	}
	@FunctionalInterface
	public
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
