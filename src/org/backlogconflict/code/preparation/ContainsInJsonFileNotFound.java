package org.backlogconflict.code.preparation;

public class ContainsInJsonFileNotFound extends Exception{

	public ContainsInJsonFileNotFound() {
		super("Contains in JOSNObject not found!");
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
