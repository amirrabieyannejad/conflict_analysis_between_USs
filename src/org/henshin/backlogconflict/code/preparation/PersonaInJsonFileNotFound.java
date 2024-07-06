package org.henshin.backlogconflict.code.preparation;

public class PersonaInJsonFileNotFound extends Exception{

	public PersonaInJsonFileNotFound() {
		super("Persona in JOSNObject not found!");
	}
	public PersonaInJsonFileNotFound(String message) {
		super(message);
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
