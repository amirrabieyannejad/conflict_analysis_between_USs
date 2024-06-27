package org.henshin.backlogconflict.code.rule;

public class TriggersInJsonFileNotFound extends Exception{

	public TriggersInJsonFileNotFound() {
		super("Triggers in JOSNObject not found!");
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
