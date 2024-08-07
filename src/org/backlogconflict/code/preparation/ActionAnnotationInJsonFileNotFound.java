package org.backlogconflict.code.preparation;

public class ActionAnnotationInJsonFileNotFound extends Exception{

	public ActionAnnotationInJsonFileNotFound() {
		super("Action-Annotation in JOSNObject not found!");
	}
	public ActionAnnotationInJsonFileNotFound(String message) {
		super(message);
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
