package org.henshin.backlogconflict.code.preparation;

import org.henshin.backlogconflict.code.report.ReportMaker;

public class RunnerActionsRulesCreator {

	public RunnerActionsRulesCreator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {

		 String[] dataSets = { "g03_loudoun", "g04_recycling", "g05_open_spending",
		 "g08_frictionless",
				"g10_scrum_alliance", "g11_nsf", "g12_camperplus", "g14_datahub", "g16_mis", "g18_neurohub",
				"g19_alfred", "g21_badcamp", "g22_rdadmp", "g23_archives_space", "g24_unibath", "g25_duraspace",
				"g26_racdam", "g27_culrepo", "g28_zooniverse" };
//		String[] dataSets = { "g03_loudoun" }; 
		
		 
			String filePath = "00_annotated_datasets\\" ;
			String actionsAnnotationsFile = "actions_annotations.csv";
			JSONTransformer.runJsonTransformer(dataSets, filePath);
			ActionsAnnotationsCreator actionsAnnotationsCreator = new ActionsAnnotationsCreator();
			actionsAnnotationsCreator.addActionsAnnotations(dataSets, filePath,actionsAnnotationsFile);
			
		
//		String filePath = "00_annotated_datasets\\";
		ReportMaker.runReportMaker(dataSets, filePath);
	}

}
