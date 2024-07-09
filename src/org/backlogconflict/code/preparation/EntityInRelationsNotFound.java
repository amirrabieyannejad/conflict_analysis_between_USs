package org.backlogconflict.code.preparation;

public class EntityInRelationsNotFound extends Exception{

	public EntityInRelationsNotFound() {
		super("Enity in Relations not found!");
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
