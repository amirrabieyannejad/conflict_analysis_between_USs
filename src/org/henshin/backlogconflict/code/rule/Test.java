package org.henshin.backlogconflict.code.rule;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws CsvValidationException, EmptyOrNotExistJsonFile, IOException {
		ActionsRulesCreator actionsRulesCreator = new ActionsRulesCreator();
		actionsRulesCreator.addActionsRules( "Final_Reports\\Textual_Report_g03\\g03_baseline_pos.json");
	}

}
