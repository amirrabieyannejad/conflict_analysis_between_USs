package org.henshin.backlogconflict.code.rule;

public class ActionInJsonFileNotFound extends Exception{

	public ActionInJsonFileNotFound() {
		super("Action in JOSNObject not found!");
	}
	public ActionInJsonFileNotFound(String message) {
		super(message);
	}

}
