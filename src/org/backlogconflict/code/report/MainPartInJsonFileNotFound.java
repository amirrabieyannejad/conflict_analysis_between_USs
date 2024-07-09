package org.backlogconflict.code.report;

public class MainPartInJsonFileNotFound extends Exception{

	public MainPartInJsonFileNotFound() {
		super("Main part in JOSNObject not found!");
	}
	public MainPartInJsonFileNotFound(String message) {
		super(message);
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
