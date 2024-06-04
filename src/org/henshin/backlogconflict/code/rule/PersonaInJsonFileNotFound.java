package org.henshin.backlogconflict.code.rule;

public class PersonaInJsonFileNotFound extends Exception{

	public PersonaInJsonFileNotFound() {
		super("Persona in JOSNObject not found!");
	}
	public PersonaInJsonFileNotFound(String message) {
		super(message);
	}

}
