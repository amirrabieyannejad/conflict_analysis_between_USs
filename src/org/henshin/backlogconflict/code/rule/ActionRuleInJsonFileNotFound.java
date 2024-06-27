package org.henshin.backlogconflict.code.rule;

public class ActionRuleInJsonFileNotFound extends Exception{

	public ActionRuleInJsonFileNotFound() {
		super("Action-Rule in JOSNObject not found!");
	}
	public ActionRuleInJsonFileNotFound(String message) {
		super(message);
	}
	@FunctionalInterface
	interface ExceptionSupplier<T extends Exception>{
		T get() throws T;
	}

}
