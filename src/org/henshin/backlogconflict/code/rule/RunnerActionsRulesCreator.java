package org.henshin.backlogconflict.code.rule;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;

public class RunnerActionsRulesCreator {

	public RunnerActionsRulesCreator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws CsvValidationException, EmptyOrNotExistJsonFile, IOException {
		ActionsRulesCreator actionsRulesCreator = new ActionsRulesCreator();

		String[] dataSets = { "g03_loudoun", "g04_recycling", "g05_open_spending", "g08_frictionless",
				"g10_scrum_alliance", "g11_nsf", "g12_camperplus", "g14_datahub", "g16_mis", "g18_neurohub",
				"g19_alfred", "g21_badcamp", "g22_rdadmp", "g23_archives_space", "g24_unibath", "g25_duraspace",
				"g26_racdam", "g27_culrepo", "g28_zooniverse" };
//		String[] dataSets = {"g05_open_spending"};
		for (int j = 0; j < dataSets.length; j++) {
			System.out.println("running dataset: " + dataSets[j]);
			actionsRulesCreator.addActionsRules("00_annotated_datasets\\" + dataSets[j] + "\\" + dataSets[j] + ".json");
		}

	}

}
