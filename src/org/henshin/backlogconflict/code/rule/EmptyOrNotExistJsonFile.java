package org.henshin.backlogconflict.code.rule;

public class EmptyOrNotExistJsonFile extends Exception{

	public EmptyOrNotExistJsonFile() {
		super("The JOSN File is empty or not exist!");
	}

}
