package org.henshin.backlogconflict.code.rule;

public class UsNrInJsonFileNotFound extends Exception{

	public UsNrInJsonFileNotFound() {
		super("US_Nr in JOSNObject not found!");
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
