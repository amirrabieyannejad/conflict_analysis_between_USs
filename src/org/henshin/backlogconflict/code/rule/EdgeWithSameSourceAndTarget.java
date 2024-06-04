package org.henshin.backlogconflict.code.rule;

public class EdgeWithSameSourceAndTarget extends Exception{

	public EdgeWithSameSourceAndTarget() {
		super("Edge with same entity and action is alredy exist!");
	}
	public EdgeWithSameSourceAndTarget(String message) {
		super(message);
	}

}
