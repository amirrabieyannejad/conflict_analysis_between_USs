package org.henshin.backlogconflict.code.rule;

public class EntityInJsonFileNotFound extends Exception{

	public EntityInJsonFileNotFound() {
		super("Entity in JOSNObject not found!");
	}
	public EntityInJsonFileNotFound(String message) {
		super(message);
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
