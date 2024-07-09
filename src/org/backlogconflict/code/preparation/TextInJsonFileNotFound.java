package org.backlogconflict.code.preparation;

public class TextInJsonFileNotFound extends Exception{

	public TextInJsonFileNotFound() {
		super("Text in JOSNObject not found!");
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
