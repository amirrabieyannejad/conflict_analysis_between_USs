package org.backlogconflict.code.preparation;

import org.backlogconflict.code.report.ReportMaker;

public class RunnerActionsRulesCreator {

	public RunnerActionsRulesCreator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {

//		 String[] dataSets = { "g03_loudoun", "g04_recycling", "g05_open_spending",
//		 "g08_frictionless",
//				"g10_scrum_alliance", "g11_nsf", "g12_camperplus", "g14_datahub", "g16_mis", "g18_neurohub",
//				"g19_alfred", "g21_badcamp", "g22_rdadmp", "g23_archives_space", "g24_unibath", "g25_duraspace",
//				"g26_racdam", "g27_culrepo", "g28_zooniverse" };
		String[] dataSets = { "g04_recycling","g11_nsf","g10_scrum_alliance", "g27_culrepo", "g19_alfred" }; 
		
		 
			String filePath = "00_annotated_datasets\\" ;
			String actionsAnnotationsFile = "actions_annotations.csv";
			USPartExtractor.runUSPartExtractor(dataSets, filePath);
			ActionsAnnotationsCreator actionsAnnotationsCreator = new ActionsAnnotationsCreator();
			actionsAnnotationsCreator.addActionsAnnotations(dataSets, filePath,actionsAnnotationsFile);
			ReportMaker.runReportMaker(dataSets, filePath);
		
//		String filePath = "00_annotated_datasets\\";
		
	}

}
