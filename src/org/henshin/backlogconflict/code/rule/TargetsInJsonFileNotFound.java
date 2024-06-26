package org.henshin.backlogconflict.code.rule;

public class TargetsInJsonFileNotFound extends Exception{

	public TargetsInJsonFileNotFound() {
		super("Targets in JOSNObject not found!");
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
