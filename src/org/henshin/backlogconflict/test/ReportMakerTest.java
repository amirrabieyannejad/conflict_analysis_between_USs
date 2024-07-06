package org.henshin.backlogconflict.test;

import org.junit.Test;

import static org.junit.Assert.*;
import org.henshin.backlogconflict.code.report.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.henshin.backlogconflict.code.preparation.ActionAnnotationInJsonFileNotFound;
import org.henshin.backlogconflict.code.preparation.TextInJsonFileNotFound;
import org.henshin.backlogconflict.code.preparation.UsNrInJsonFileNotFound;
public class ReportMakerTest {

	@Test
	public void runReportMaker_Main() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "Main\\";
		String us1Main = "user_story_13: #G03# As a Staff member, i want to #apply# a #hold#,";
		String us2Main = "user_story_14: #G03# As a Staff member, i want to #remove# a #hold#,";

		ReportMaker.runReportMaker(dataSets, filePath);
		File fileTextReport = new File(filePath + dataSets[0] + "\\" + dataSets[0] + ".txt");
//		File fileJsonReport = new File(filePath + dataSets[0] + ".json");
		BufferedReader reader = new BufferedReader(new FileReader(fileTextReport));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		reader.close();
		assertTrue(result.toString().contains(us1Main) && result.toString().contains(us2Main));
	}
	@Test(expected = MainPartInJsonFileNotFound.class)
	public void runReportMaker_MainNotExist() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "MainNotExist\\";
		ReportMaker.runReportMaker(dataSets, filePath);
		
		
	}
	@Test(expected = MainPartInJsonFileNotFound.class)
	public void runReportMaker_MainIsEmpty() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "MainIsEmpty\\";
		ReportMaker.runReportMaker(dataSets, filePath);
			
	}
	@Test
	public void runReportMaker_MainNew() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "Main\\";
		String us1Main = "user_story_13: #G03# As a Staff member, i want to #apply# a #hold#,";
		String us2Main = "user_story_14: #G03# As a Staff member, i want to #remove# a #hold#,";
		File fileTextReport = new File(filePath + dataSets[0] + "\\" + dataSets[0] + ".txt");
		fileTextReport.delete();
		ReportMaker.runReportMaker(dataSets, filePath);
		fileTextReport = new File(filePath + dataSets[0] + "\\" + dataSets[0] + ".txt");
//		File fileJsonReport = new File(filePath + dataSets[0] + ".json");
		BufferedReader reader = new BufferedReader(new FileReader(fileTextReport));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		reader.close();
		assertTrue(result.toString().contains(us1Main) && result.toString().contains(us2Main));
	}
	@Test
	public void runReportMaker_NoConflict() throws Exception {
		String[] dataSets = { "g04_recycling" };
		String filePath = "Tests\\ReportMaker\\" + "NoConflict\\";
		

		ReportMaker.runReportMaker(dataSets, filePath);
		File fileTextReport = new File(filePath + dataSets[0] + "\\" + dataSets[0] + ".txt");
//		File fileJsonReport = new File(filePath + dataSets[0] + ".json");
		BufferedReader reader = new BufferedReader(new FileReader(fileTextReport));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		reader.close();
		assertTrue(result.toString()=="");
	}
	@Test
	public void runReportMaker_EntityContainExist() throws Exception {
		String[] dataSets = { "g28_zooniverse" };
		String filePath = "Tests\\ReportMaker\\" + "EntityContainExist\\";
		String entityContainUs2 = "Affected Contain Entity US2 is: << segmentation >>";
		String us2Main = "user_story_14: #G03# As a Staff member, i want to #remove# a #hold#,";

		ReportMaker.runReportMaker(dataSets, filePath);
		File fileTextReport = new File(filePath + dataSets[0] + "\\" + dataSets[0] + ".txt");
//		File fileJsonReport = new File(filePath + dataSets[0] + ".json");
		BufferedReader reader = new BufferedReader(new FileReader(fileTextReport));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		reader.close();
		assertTrue(result.toString().contains(entityContainUs2));
	}
	@Test
	public void runReportMaker_writeTable() throws Exception {
		String[] dataSets = { "consolidated_conflict_report_total" };
		String filePath = "Tests\\ReportMaker\\" + "Total\\";
		
		ReportMaker.runReportMaker(dataSets, filePath);
		File fileTextReport = new File(filePath + dataSets[0] + "\\" + dataSets[0] + ".txt");
//		File fileJsonReport = new File(filePath + dataSets[0] + ".json");
		BufferedReader reader = new BufferedReader(new FileReader(fileTextReport));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		reader.close();
		assertTrue(result.toString().contains("us_07")
				&&result.toString().contains("us_91"));
	} 
	
	@Test(expected = ActionAnnotationInJsonFileNotFound.class)
	public void runReportMaker_ActionAnnotationInJsonFileNotFound() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "ActionAnnotationInJsonFileNotFound\\";
		ReportMaker.runReportMaker(dataSets, filePath);
			
	}
	@Test(expected = ActionAnnotationInJsonFileNotFound.class)
	public void runReportMaker_TargetActionAnnotationInJsonFileNotFound() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "TargetActionAnnotationInJsonFileNotFound\\";
		ReportMaker.runReportMaker(dataSets, filePath);
			
	}
	@Test(expected = ActionAnnotationInJsonFileNotFound.class)
	public void runReportMaker_ContainActionAnnotationInJsonFileNotFound() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "ContainActionAnnotationInJsonFileNotFound\\";
		ReportMaker.runReportMaker(dataSets, filePath);
			
	}
	@Test
	public void runReportMaker_JsonArrayNotFound() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "JsonArrayNotFound\\";
		
		ReportMaker.runReportMaker(dataSets, filePath);
		assertTrue(true);
	}
	@Test(expected = UsNrInJsonFileNotFound.class)
	public void runReportMaker_UsNrInJsonFileNotFound() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "UsNrInJsonFileNotFound\\";
		ReportMaker.runReportMaker(dataSets, filePath);
			
	}
	@Test
	public void runReportMaker_JsonObjectNotFound() throws Exception {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ReportMaker\\" + "JsonObjectNotFound\\";
		
		ReportMaker.runReportMaker(dataSets, filePath);
		assertTrue(true);
	}
	
}
