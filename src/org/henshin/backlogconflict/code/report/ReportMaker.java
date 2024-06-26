package org.henshin.backlogconflict.code.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.henshin.backlogconflict.code.rule.ActionInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.ActionRuleInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.ContainsInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.EdgeWithSameSourceAndTarget;
import org.henshin.backlogconflict.code.rule.EmptyOrNotExistJsonFile;
import org.henshin.backlogconflict.code.rule.EntityInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.PersonaInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.ReadJsonFile;
import org.henshin.backlogconflict.code.rule.TargetsInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.TextInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.TriggersInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.UsNrInJsonFileNotFound;
import org.henshin.backlogconflict.code.rule.ActionInJsonFileNotFound.ExceptionSupplier;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openjdk.nashorn.internal.ir.debug.JSONWriter;

import java.util.HashSet;
import java.util.Set;

public class ReportMaker {
	private String jsonFileName;

	public ReportMaker(String jsonFileName) {
		this.jsonFileName = jsonFileName;
	}

	public String getJsonFile() {
		return jsonFileName;
	}

	private String getJsonFileAbsolutePath() throws EmptyOrNotExistJsonFile {
		Path path = Paths.get(
				"C:\\Users\\amirr\\eclipse-workspace_new\\" + "org.henshin.backlogconflict\\00_annotated_datasets\\"
						+ getJsonFile() + "\\" + getJsonFile() + ".json");
		if (Files.exists(path)) {
			return path.toString();

		} else {
			throw new EmptyOrNotExistJsonFile();
		}

	}

	public static void main(String[] args) throws Exception {
		String[] version = { "g03_loudoun", "g04_recycling", "g05_open_spending", "g08_frictionless",
				"g10_scrum_alliance", "g11_nsf", "g12_camperplus", "g14_datahub", "g16_mis", "g18_neurohub",
				"g19_alfred", "g21_badcamp", "g22_rdadmp", "g23_archives_space", "g24_unibath", "g25_duraspace",
				"g26_racdam", "g27_culrepo", "g28_zooniverse" };
//		String[] version = { "g10_scrum_alliance" };
//		 make a report for the whole
		File consolidatedConflictReport = new File("00_annotated_datasets\\consolidated_conflict_report.txt");
		FileWriter consolidatedFileWriter = new FileWriter(consolidatedConflictReport);

		// create jsonReport
		File fileJsonReport = new File("00_annotated_datasets\\consolidated_conflict_report.json");
		FileWriter jsonFileWriter = new FileWriter(fileJsonReport);
		JSONArray jsonReport = new JSONArray();
		for (int i = 0; i < version.length; i++) {

			long startTime = System.nanoTime();
			System.out.println("------------------- Proceed Dataset " + version[i]);
//			consolidatedFileWriter.write("\n\n\n------------------- Proceed Dataset " + version[i]+ " -------------------");
			makeReport(version[i], consolidatedFileWriter, jsonFileWriter, jsonReport);
			long endTime = System.nanoTime();
			double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
			System.out.println("Processing time: " + elapsedTimeInSeconds + " seconds");
		}
		consolidatedFileWriter.close();
		jsonFileWriter.write(jsonReport.toString(4));
		jsonFileWriter.close();

	}

	public static void makeReport(String fileName, FileWriter consolidatedFileWriter, FileWriter jsonFileWriter,
			JSONArray jsonReport) throws Exception {
//		System.out.println("Received fileName: " + fileName);
		ReportMaker reportMaker = new ReportMaker(fileName);
		ReadJsonFile readJsonFile = new ReadJsonFile();
//		System.out.println("make sure path is correct: " + reportMaker.getJsonFileAbsolutePath());
		JSONArray jsonMainArray = readJsonFile.readJsonArrayFromFile(reportMaker.getJsonFileAbsolutePath());

		// Creeate Conflict Report for single dataset
		File conflictReport = new File(
				"00_annotated_datasets\\" + reportMaker.getJsonFile() + "\\" + reportMaker.getJsonFile() + ".txt");
		FileWriter fileWriter = reportMaker.createOrOverwriteReportFile(conflictReport);

		reportMaker.findConflict(jsonMainArray, fileWriter, consolidatedFileWriter, jsonFileWriter, jsonReport,
				fileName);
		fileWriter.close();

	}

	private void findConflict(JSONArray jsonMainArray, FileWriter fileWriter, FileWriter consolidatedFileWriter,
			FileWriter jsonFileWriter, JSONArray jsonReport, String fileName) throws Exception {
//		System.out.println("iterate through jsonArray the main Structure");
//		List<ConflictPair> conflictPairs = new ArrayList<>();
		Set<ConflictPair> conflictPairs = new HashSet<>();
		Set<String> conflicts = new HashSet<>();
		for (int i = 0; i < jsonMainArray.length(); i++) {
//			System.out.println("get the current JSONObject");
			JSONObject jsonObject = jsonMainArray.getJSONObject(i);
//			System.out.println("get the US_Nr form jsonObject");
			String usNrM = getUs(jsonObject, "US_Nr");
//			System.out.println("proceed " + usNrM);
			if (jsonObject.has("Main")) {
				JSONObject jsonMain = jsonObject.getJSONObject("Main");
//				System.out.println("current jsonObject has Main Part. ");
				if (jsonMain.length() != 0) {
//					System.out.println("jsonMain is not empty. try to process main part..");
					processMainPart(conflictPairs, jsonObject, jsonMain, usNrM, jsonMainArray, conflicts);

				}
			}

		}
		printReport(conflictPairs, fileWriter, consolidatedFileWriter, jsonFileWriter, jsonReport, fileName);

	}

	// Create or overwrite report file which return/pass the FileWriter object
	public FileWriter createOrOverwriteReportFile(File fileConflict) throws IOException {
		FileWriter cdaWriter = null;
		if (fileConflict.createNewFile()) {
			// System.out.println("File created successfully: " + totalCda.getName());
			cdaWriter = new FileWriter(fileConflict);
			return cdaWriter;

		} else {
			cdaWriter = new FileWriter(fileConflict);
			// System.out.println("File already exists. Try to overwrite..!");
			return cdaWriter;
		}
	}

	private void printReport(Set<ConflictPair> conflictPairs, FileWriter fileWriter, FileWriter consolidatedFileWriter,
			FileWriter jsonFileWriter, JSONArray jsonReport, String fileName)
			throws EmptyOrNotExistJsonFile, IOException {
//		File main = new File(getJsonFileAbsolutePath());

		if (conflictPairs.size() > 0) {
			fileWriter.write("\n\n********************* << Dataset: " + fileName + " >> ***************\n");
			consolidatedFileWriter.write("\n\n********************* << Dataset: " + fileName + " >> ***************\n");
			writeTable(fileWriter, conflictPairs, consolidatedFileWriter);
		}
		for (ConflictPair conflictPair : conflictPairs) {
			JSONObject jsonObject = new JSONObject();
			String us1 = conflictPair.getConflictPair1();
			String us2 = conflictPair.getConflictPair2();
			System.out.println("reporting US1: " + us1 + " and US2: " + us2);
			JSONObject jsonUs1 = conflictPair.getJsonConflict1();
			String textUs1 = jsonUs1.getString("Text");
			JSONObject jsonUs1Main = jsonUs1.getJSONObject("Main");
			String textUs1Main = jsonUs1Main.getString("Text");

			JSONObject jsonUs2 = conflictPair.getJsonConflict2();
			String textUs2 = jsonUs2.getString("Text");
			JSONObject jsonUs2Main = jsonUs2.getJSONObject("Main");
			String textUs2Main = jsonUs2Main.getString("Text");
			int firstCommaUs1Main = textUs1Main.indexOf(',');
			int firstCommaUs2Main = textUs2Main.indexOf(',');
			String subStringFirstUs1 = textUs1Main.substring(0, firstCommaUs1Main);
			String subStringFirstUs2 = textUs2Main.substring(0, firstCommaUs2Main);
			
			String subStringSecondUs1 = textUs1Main.substring(firstCommaUs1Main + 1);
			String subStringSecondUs2 = textUs2Main.substring(firstCommaUs2Main + 1);
			
			if (conflictPair.getNounContainUs1().equals("") || conflictPair.getNounContainUs2().equals("")) {
				String[] matchesUs1 = { conflictPair.getActionUs1(), conflictPair.getNounMainUs1() };
				String[] matchesUs2 = { conflictPair.getActionUs2(), conflictPair.getNounMainUs2() };
				subStringSecondUs1 = applyHashSymbols(subStringSecondUs1.toLowerCase(), matchesUs1);
				subStringSecondUs2 = applyHashSymbols(subStringSecondUs2.toLowerCase(), matchesUs2);
				
			} else {

				String[] matchesUs1 = { conflictPair.getActionUs1(), conflictPair.getNounMainUs1(),
						conflictPair.getNounContainUs1() };
				String[] matchesUs2 = { conflictPair.getActionUs2(), conflictPair.getNounMainUs2(),
						conflictPair.getNounContainUs2() };
				subStringSecondUs1 = applyHashSymbols(subStringSecondUs1.toLowerCase(), matchesUs1);
				subStringSecondUs2 = applyHashSymbols(subStringSecondUs2.toLowerCase(), matchesUs2);

			}
			textUs1Main = subStringFirstUs1 + "," + subStringSecondUs1;
			textUs2Main = subStringFirstUs2 + "," + subStringSecondUs2;
			
			String usPair = us1 + "_AND_" + us2;

			// First of all create a table

			fileWriter.write("\n\n------------------[Potential Conflict between following User"
					+ " Stories found]--------------------------\n{" + usPair + "}\n  ");
			consolidatedFileWriter.write("\n------------------[Potential Conflict between following User"
					+ " Stories found]--------------------------\n{" + usPair + "}\n  ");
			jsonObject.put("Conflict Pair", usPair);

			fileWriter.write("\nAffected Resource of US1 is: " + "<< " + conflictPair.getNounMainUs1() + " >>");
			consolidatedFileWriter
					.write("\nAffected Resource of US1 is: " + "<< " + conflictPair.getNounMainUs1() + " >>");
			jsonObject.put("Entity of US1", conflictPair.getNounMainUs1());

			fileWriter.write("\nAffected Resource of US2 is: " + "<< " + conflictPair.getNounMainUs2() + " >>");
			consolidatedFileWriter
					.write("\nAffected Resource of US2 is: " + "<< " + conflictPair.getNounMainUs2() + " >>");
			jsonObject.put("Entity of US2", conflictPair.getNounMainUs2());

			fileWriter.write("\nAffected Contain Resource US1 is: " + "<< " + conflictPair.getNounContainUs1() + " >>");
			consolidatedFileWriter
					.write("\nAffected Contain Entity US1 is: " + "<< " + conflictPair.getNounContainUs1() + " >>");
			jsonObject.put("Contain US1 Entity", conflictPair.getNounContainUs1());

			fileWriter.write("\nAffected Resource US2 is: " + "<< " + conflictPair.getNounContainUs2() + " >>");
			consolidatedFileWriter
					.write("\nAffected Contain Entity US2 is: " + "<< " + conflictPair.getNounContainUs2() + " >>");
			jsonObject.put("Contain US2 Entity", conflictPair.getNounContainUs2());

			JSONObject jsonUs1ActionAnnotation = jsonUs1Main.getJSONObject("Action Rules");
			JSONObject jsonUs2ActionAnnotation = jsonUs2Main.getJSONObject("Action Rules");
			// test
			fileWriter.write("\nContain Resources for: " + us1 + ": << "
					+ jsonUs1ActionAnnotation.getJSONArray("Contain Action Rules").toString() + " >>");
			consolidatedFileWriter.write("\nAffected Resources for: " + us1 + ": << "
					+ jsonUs1ActionAnnotation.getJSONArray("Contain Action Rules").toString() + " >>");
			jsonObject.put("Contain Action Rules US1", jsonUs1ActionAnnotation.getJSONArray("Contain Action Rules"));

			// test
			fileWriter.write("\nContain Resources for: " + us2 + ": << "
					+ jsonUs2ActionAnnotation.getJSONArray("Contain Action Rules").toString() + " >>");
			consolidatedFileWriter.write("\nAffected Resources for: " + us2 + ": << "
					+ jsonUs2ActionAnnotation.getJSONArray("Contain Action Rules").toString() + " >>");
			jsonObject.put("Contain Action Rules US2", jsonUs2ActionAnnotation.getJSONArray("Contain Action Rules"));

			// TODO:
			fileWriter.write(
					"\n\n Action of " + conflictPair.getConflictPair1() + " is: " + "<< " + conflictPair.getActionUs1()
							+ " >> " + " which is annotated with: " + "<< " + conflictPair.getActionRuleUs1() + " >>");
			consolidatedFileWriter.write(
					"\n\n Action of " + conflictPair.getConflictPair1() + " is: " + "<< " + conflictPair.getActionUs1()
							+ " >> " + " which is annotated with: " + "<< " + conflictPair.getActionRuleUs1() + " >>");
			JSONObject jsonActionRule = new JSONObject();
			jsonActionRule.put("US1", conflictPair.getActionRuleUs1());
			jsonActionRule.put("US2", conflictPair.getActionRuleUs2());
			jsonObject.put("Action Rules", jsonActionRule);

			fileWriter.write(
					"\n Action of " + conflictPair.getConflictPair2() + " is: " + "<< " + conflictPair.getActionUs2()
							+ " >> " + " which is annotated with: " + "<< " + conflictPair.getActionRuleUs2() + " >>");
			consolidatedFileWriter.write(
					"\n Action of " + conflictPair.getConflictPair2() + " is: " + "<< " + conflictPair.getActionUs2()
							+ " >> " + " which is annotated with: " + "<< " + conflictPair.getActionRuleUs2() + " >>");
			JSONObject jsonAction = new JSONObject();
			jsonAction.put("Action US1", conflictPair.getActionUs1());
			jsonAction.put("Action US2", conflictPair.getActionUs2());
			jsonObject.put("Actions", jsonAction);

			fileWriter.write("\n\nConflict Reason is: " + "<< " + conflictPair.getConflictReason() + " >>");
			consolidatedFileWriter.write("\n\nConflict Reason is: " + "<< " + conflictPair.getConflictReason() + " >>");
			jsonObject.put("Conflict-Reason", conflictPair.getConflictReason());

			fileWriter.write("\n\nHighlighted elements in main parts of user stories: ");
			consolidatedFileWriter.write("\n\nHighlighted elements in main parts of user stories: ");

			fileWriter.write("\n   " + us1 + ": " + textUs1Main + "\n\n   " + us2 + ": " + textUs2Main);
			consolidatedFileWriter.write("\n   " + us1 + ": " + textUs1Main + "\n\n   " + us2 + ": " + textUs2Main);
			JSONObject jsonTextsMain = new JSONObject();
			jsonTextsMain.put("US1", textUs1Main);
			jsonTextsMain.put("US2", textUs2Main);
			jsonObject.put("Texts of Main Parts", jsonTextsMain);

			fileWriter.write("\n\nOriginal texts of user stories are: ");
			consolidatedFileWriter.write("\n\nOriginal texts of user stories are: ");

			fileWriter.write("\n   " + us1 + ": " + textUs1);
			consolidatedFileWriter.write("\n   " + us1 + ": " + textUs1);

			fileWriter.write("\n\n   " + us2 + ": " + textUs2);
			consolidatedFileWriter.write("\n\n   " + us2 + ": " + textUs2 + "\n");

			JSONObject jsonTexts = new JSONObject();
			jsonTexts.put("US1", textUs1);
			jsonTexts.put("US2", textUs2);
			jsonObject.put("Texts of USs", jsonTexts);

			jsonObject.put("PID", conflictPair.getpId());

			jsonReport.put(jsonObject);

		}

	}

	// Create a table at the top of the reports
	private void writeTable(FileWriter fileWriter, Set<ConflictPair> conflictPairs, FileWriter consolidatedFileWriter)
			throws IOException {

		List<String> pairListSeperate = new ArrayList<>();

		StringBuilder table = new StringBuilder();
		table.append("* Table of potential conflict between user stories" + "\n\n");
		for (ConflictPair conflictPair : conflictPairs) {
			String us1 = conflictPair.getConflictPair1();
			String us2 = conflictPair.getConflictPair2();
			System.out.println("reporting US1: " + us1 + " and US2: " + us2);
			if (!pairListSeperate.contains(conflictPair.getConflictPair1())) {
				pairListSeperate.add(conflictPair.getConflictPair1());
			}
			if (!pairListSeperate.contains(conflictPair.getConflictPair2())) {
				pairListSeperate.add(conflictPair.getConflictPair2());
			}
		}
		String[][] stringTable = createTable(pairListSeperate, conflictPairs);
		int numCols = stringTable[0].length;

		// find the maximum width for each column
		int[] maxWidths = new int[numCols];
		for (String[] row : stringTable) {
			for (int j = 0; j < numCols; j++) {
				maxWidths[j] = Math.max(maxWidths[j], row[j].length());
			}
		}
		for (String[] row : stringTable) {
			for (int j = 0; j < numCols; j++) {

				table.append(String.format("%-" + (maxWidths[j] + 2) + "s", row[j]));
			}
			table.append("\n");
		}
		fileWriter.write(table.toString());
		consolidatedFileWriter.write(table.toString());

	}

	private String[][] createTable(List<String> pairListSeperate, Set<ConflictPair> conflictPairs) {
		int size = pairListSeperate.size();
		String[][] table = new String[size + 1][size + 1];

		table[0][0] = "";
		for (int i = 0; i < size; i++) {
			table[0][i + 1] = pairListSeperate.get(i).replaceAll("user_story", "us"); // header row
			table[i + 1][0] = pairListSeperate.get(i).replaceAll("user_story", "us"); // first column
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				String pair1 = pairListSeperate.get(i);
				String pair2 = pairListSeperate.get(j);
				String conflict = getTotalRedundanciesFromPair(conflictPairs, pair1, pair2);
				table[i + 1][j + 1] = conflict;
			}

		}
		return table;
	}

	private String getTotalRedundanciesFromPair(Set<ConflictPair> conflictPairs, String pair1, String pair2) {
		for (ConflictPair conflictPair : conflictPairs) {
			if ((conflictPair.getConflictPair1().equals(pair1) && conflictPair.getConflictPair2().equals(pair2))
					|| (conflictPair.getConflictPair1().equals(pair2)
							&& conflictPair.getConflictPair2().equals(pair1))) {
				return "x";
			}
		}

		return " ";
	}

	private void processMainPart(Set<ConflictPair> conflictPairs, JSONObject jsonObject, JSONObject jsonMain,
			String usNrM, JSONArray jsonMainArray, Set<String> conflicts) throws Exception {
		JSONArray actionMain = getArray(jsonMain, "Action", ActionInJsonFileNotFound::new);
//		System.out.println("[processMainPart] Action from current jsonObejct readed.");

		JSONArray entityArray = getArray(jsonMain, "Entity", EntityInJsonFileNotFound::new);
//		System.out.println("Entity from current jsonObejct readed.");

		JSONArray triggersArrayMain = getArray(jsonMain, "Triggers", TriggersInJsonFileNotFound::new);
//		System.out.println("Triggers from current jsonObejct readed.");

		JSONArray targetsArrayMain = getArray(jsonMain, "Targets", TargetsInJsonFileNotFound::new);
//		System.out.println("Targets from current jsonObejct readed.");

		JSONArray containsArrayMain = getArray(jsonMain, "Contains", ContainsInJsonFileNotFound::new);
//		System.out.println("Contains from current jsonObejct readed.");

		JSONArray persona = getArray(jsonMain, "Persona", PersonaInJsonFileNotFound::new);
//		System.out.println("Persona from current jsonObejct readed.");

		String textMain = getString(jsonMain, "Text", TextInJsonFileNotFound::new);
//		System.out.println("Text from current jsonObejct readed.");

		JSONArray targetActionRules;
		JSONArray containActionRules;
		if (jsonMain.has("Action Rules")) {
			JSONObject jsonActionRules = jsonMain.getJSONObject("Action Rules");
			if (jsonActionRules.has("Target Action Rules")) {
				targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
//				System.out.println("Target Action Rules from current jsonObejct readed.");
			} else {
				throw new ActionRuleInJsonFileNotFound("Target Action Rules in Aciton-Rule not found");
			}
			if (jsonActionRules.has("Contain Action Rules")) {
				containActionRules = jsonActionRules.getJSONArray("Contain Action Rules");
//				System.out.println("Contains Action Rules from current jsonObejct readed.");
			} else {
				throw new ActionRuleInJsonFileNotFound("Contain Action Rules in Aciton-Rule not found");
			}
		} else {
			throw new ActionRuleInJsonFileNotFound();
		}
//		System.out.println("[processMainPart]try to process Action Rules...");
		processActionRules(conflictPairs, jsonMainArray, jsonObject, targetActionRules, containActionRules, usNrM,
				jsonMain, actionMain, entityArray, triggersArrayMain, targetsArrayMain, containsArrayMain, textMain,
				persona, conflicts);

	}

	// handle ambiguity in action-rules and atomic rules
	private void processActionRules(Set<ConflictPair> conflictPairs, JSONArray jsonMainArray,
			JSONObject jsonObjectCurrent, JSONArray targetActionRules, JSONArray containActionRules, String usNrM,
			JSONObject jsonObject, JSONArray actionArray, JSONArray entityArray, JSONArray triggersArray,
			JSONArray targetsArray, JSONArray containsArray, String text, JSONArray persona, Set<String> conflicts)
			throws ActionInJsonFileNotFound, EdgeWithSameSourceAndTarget, EmptyOrNotExistJsonFile,
			EntityInJsonFileNotFound {
		for (int j = 0; j < targetActionRules.length(); j++) {
			JSONArray current = targetActionRules.getJSONArray(j);

//			System.out.println("[processAcitonRules] reading targetAction Rule.. " + (j + 1) + " of US: " + usNrM);
			String verb = current.getString(0).toLowerCase();
			String noun = current.getString(1).toLowerCase();
//			System.out.println("targetActionRule verb is: " + verb);
//			System.out.println("targetActionRule noun is: " + noun);
			String actionRule = current.getString(2).toLowerCase();

			// Check if there is ambiguity by action annotation
			if (actionRule.contains(";")) {
//				System.out.println("found ambiguity in ActionRule.. Try to handle");
				String[] items = actionRule.split(";");
				for (String actionRuleAmbiguity : items) {
//					System.out.println("[processAcitonRule] ");

					executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject, actionArray,
							targetActionRules, containActionRules, entityArray, triggersArray, targetsArray,
							containsArray, text, persona, usNrM, verb, noun, actionRuleAmbiguity, false, conflicts,
							noun);

					// proceed entities in contain
					for (int i = 0; i < containActionRules.length(); i++) {
						JSONArray jsonContain = containActionRules.getJSONArray(i);

						String noun1 = jsonContain.getString(0);
						String noun2 = jsonContain.getString(1);
						String containActionAnnotation = jsonContain.getString(2);
						String containVerb = jsonContain.getString(3);

						// find the contain entity and iterate through targets and contains of other USs
						if ((noun1.equalsIgnoreCase(noun)
								|| noun2.equalsIgnoreCase(noun) && containVerb.equalsIgnoreCase(verb))
								&& actionRule.equalsIgnoreCase(containActionAnnotation)) {
							if (noun1.equalsIgnoreCase(noun)) {
								System.out.println("Contain in Proccess action Rule noun1 is " + noun1 + " noun2 is: "
										+ noun2 + " of " + usNrM);
								executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject,
										actionArray, targetActionRules, containActionRules, entityArray, triggersArray,
										targetsArray, containsArray, text, persona, usNrM, verb, noun2,
										actionRuleAmbiguity, true, conflicts, noun);
							}
							if (noun2.equalsIgnoreCase(noun)) {
								executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject,
										actionArray, targetActionRules, containActionRules, entityArray, triggersArray,
										targetsArray, containsArray, text, persona, usNrM, verb, noun1,
										actionRuleAmbiguity, true, conflicts, noun);
							}

						}

					}

//					for(int i=0;i<containActionRules.length();i++) {
//						JSONArray jsonContain = containActionRules.getJSONArray(i);
//						String noun1= jsonContain.getString(0);
//						String noun2= jsonContain.getString(1);
//						if(noun.equalsIgnoreCase(noun1)) {
//							executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject, actionArray,
//									targetActionRules, containActionRules, entityArray, triggersArray, targetsArray,
//									containsArray, text, persona, usNrM, verb, noun2, actionRuleAmbiguity, false, conflicts);
//						}
//						if(noun.equalsIgnoreCase(noun2)) {
//							executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject, actionArray,
//									targetActionRules, containActionRules, entityArray, triggersArray, targetsArray,
//									containsArray, text, persona, usNrM, verb, noun1, actionRuleAmbiguity, false, conflicts);
//						}
//					}
				}

			} else {

//				System.out.println("process atomic action Rule");

				executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject, actionArray,
						targetActionRules, containActionRules, entityArray, triggersArray, targetsArray, containsArray,
						text, persona, usNrM, verb, noun, actionRule, true, conflicts, noun);

				for (int i = 0; i < containActionRules.length(); i++) {
					JSONArray jsonContain = containActionRules.getJSONArray(i);

					String noun1 = jsonContain.getString(0);
					String noun2 = jsonContain.getString(1);
					String containActionAnnotation = jsonContain.getString(2);
					String containVerb = jsonContain.getString(3);

					// find the contain entity and iterate through targets and contains of other USs
					if ((noun1.equalsIgnoreCase(noun)
							|| noun2.equalsIgnoreCase(noun) && containVerb.equalsIgnoreCase(verb))
							&& actionRule.equalsIgnoreCase(containActionAnnotation)) {
						if (noun1.equalsIgnoreCase(noun)) {
							System.out.println("Contain in Proccess action Rule noun1 is" + noun1 + " noun2 is: "
									+ noun2 + " of " + usNrM);
							executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject,
									actionArray, targetActionRules, containActionRules, entityArray, triggersArray,
									targetsArray, containsArray, text, persona, usNrM, verb, noun2, actionRule, true,
									conflicts, noun);
						}
						if (noun2.equalsIgnoreCase(noun)) {
							executeProcessActions(conflictPairs, jsonMainArray, jsonObjectCurrent, jsonObject,
									actionArray, targetActionRules, containActionRules, entityArray, triggersArray,
									targetsArray, containsArray, text, persona, usNrM, verb, noun1, actionRule, true,
									conflicts, noun);
						}

					}

				}
			}
		}
		// proceed contain entities

	}

	private void executeProcessActions(Set<ConflictPair> conflictPairs, JSONArray jsonMainArray,
			JSONObject jsonObjectCurrent, JSONObject jsonObject, JSONArray actionArray, JSONArray targetActionRules,
			JSONArray containActionRules, JSONArray entityArray, JSONArray triggersArray, JSONArray targetsArray,
			JSONArray containsArray, String text, JSONArray persona, String usNrM, String verb, String noun,
			String actionRule, boolean containEntity, Set<String> conflicts, String nounMainUs1) {
//		System.out.println("Action Rule is: " + actionRule);
		switch (actionRule) {
		case "preserve":
			findConflictForActionAnnotation(conflictPairs, jsonMainArray, jsonObjectCurrent, actionRule, "delete", noun,
					verb, conflicts, nounMainUs1,containEntity);
			break;
		case "delete":
			findConflictForActionAnnotation(conflictPairs, jsonMainArray, jsonObjectCurrent, actionRule, "preserve",
					noun, verb, conflicts, nounMainUs1,containEntity);
			findConflictForActionAnnotation(conflictPairs, jsonMainArray, jsonObjectCurrent, actionRule, "delete", noun,
					verb, conflicts, nounMainUs1,containEntity);
		case "create":
			findConflictForActionAnnotation(conflictPairs, jsonMainArray, jsonObjectCurrent, actionRule, "forbid", noun,
					verb, conflicts, nounMainUs1,containEntity);
			break;
		case "forbid":
			findConflictForActionAnnotation(conflictPairs, jsonMainArray, jsonObjectCurrent, actionRule, "create", noun,
					verb, conflicts, nounMainUs1,containEntity);
			break;

		default:
			break;

		}

	}

	private void findConflictForActionAnnotation(Set<ConflictPair> conflictPairs, JSONArray jsonMainArray,
			JSONObject jsonObjectCurrent, String actionRuleUs1, String criticalActionRule, String entityUs1,
			String actionUs1, Set<String> conflicts, String nounMainUs1,boolean containEntity ) {
		String usNr1 = jsonObjectCurrent.getString("US_Nr");
		String pId = jsonObjectCurrent.getString("PID");

		// iterate through WHOLE Dataset
		for (int i = 0; i < jsonMainArray.length(); i++) {
			JSONObject current = jsonMainArray.getJSONObject(i);
			String usNr2 = current.getString("US_Nr");

			// Check if the US are different and are not yet proceeded
//			if (!usNr1.equals(usNr2) && (!conflicts.contains(usNr1 + usNr2 + entityUs1)
//					|| !conflicts.contains(usNr2 + usNr1 + entityUs1))) {
			if (!usNr1.equals(usNr2) && (!conflicts.contains(usNr1 + usNr2) 
					|| !conflicts.contains(usNr2 + usNr1))) {
				JSONObject main = current.getJSONObject("Main");
				JSONObject jsonObjectActionRules = main.getJSONObject("Action Rules");
				JSONArray targetActionRulesArray = jsonObjectActionRules.getJSONArray("Target Action Rules");
				JSONArray containActionRulesArray = jsonObjectActionRules.getJSONArray("Contain Action Rules");
				for (int j = 0; j < targetActionRulesArray.length(); j++) {
					JSONArray innerArray = targetActionRulesArray.getJSONArray(j);
					String actionUs2 = innerArray.getString(0);
					String entityUs2 = innerArray.getString(1);
					String actionRuleUs2 = innerArray.getString(2);

					// check if actionRule of this US contains any critical action rule
					if (entityUs2.equalsIgnoreCase(entityUs1) && actionRuleUs2.contains(criticalActionRule)) {

						System.out.println("----Target: Conflict found  between: " + usNr1 + " and " + usNr2);
						System.out.println("Target: Noun is " + entityUs1 + " there is  " + actionRuleUs1 + "-"
								+ criticalActionRule + " Conflict");
						System.out.println("entity of US1: " + entityUs1 + " entity of US2: " + entityUs2);

						
						// due to the fact that actionRuleUs2 can be more than one actionRule, we
						// passing
						// "criticalActionRule" (which is founded in actionRuleUs2) for saving
						// actionRules
						ConflictPair conflictPair = setConflictPair(usNr1, usNr2, pId, jsonObjectCurrent, current,
								actionUs1, actionUs2, actionRuleUs1, criticalActionRule,
								actionRuleUs1 + "-" + criticalActionRule + "-Conflict", nounMainUs1, "", entityUs1,
								entityUs2);

						conflictPairs.add(conflictPair);
//						conflicts.add(usNr1 + usNr2 + entityUs1);
//						conflicts.add(usNr2 + usNr1 + entityUs1);
						conflicts.add(usNr1 + usNr2);
						conflicts.add(usNr2 + usNr1);
					}

				}
					
				
				// iterate through CONTAINS of main US
				for (int k = 0; k < containActionRulesArray.length(); k++) {
					JSONArray jsonArrayContains = containActionRulesArray.getJSONArray(k);
					String containEntity1Us2 = jsonArrayContains.getString(0);
					String containEntity2Us2 = jsonArrayContains.getString(1);
					String containActionAnnotationUs2 = jsonArrayContains.getString(2);
					String containActionUs2 = jsonArrayContains.getString(3);

					// check in contains if the entity1Us1 has container, if so, then check the
					// entity of US2
					// is the entity2Us? If so, then there is a also conflict but with different
					// entity

					// check if entity belongs to one of their entities
					if ((containEntity1Us2.equalsIgnoreCase(entityUs1) || containEntity2Us2.equalsIgnoreCase(entityUs1))
							&& containActionAnnotationUs2.contains(criticalActionRule)) {

						if (containEntity1Us2.equalsIgnoreCase(entityUs1)) {
							System.out.println("\n\nContains: Conflict found  between: " + usNr1 + " and " + usNr2);
//							System.out.println("Contains: Noun is " + containEntity2Us2 + " there is  " + actionRuleUs1
//									+ "-" + criticalActionRule + " Conflict");
							// due to the fact that actionRuleUs2 can be more than one actionRule, we
							// passing
							// "criticalActionRule" (which is founded in actionRuleUs2) for saving
							// actionRules
							ConflictPair conflictPair = setConflictPair(usNr1, usNr2, pId, jsonObjectCurrent, current,
									actionUs1, containActionUs2, actionRuleUs1, criticalActionRule,
									actionRuleUs1 + "-" + criticalActionRule + "-Conflict", entityUs1,
									containEntity1Us2, nounMainUs1, containEntity2Us2);
							System.out.println("entity US1 is : "+ nounMainUs1 +" Contains: Noun US1 is " + entityUs1 + 
									" entity US2 is : "+ containEntity2Us2 +" Contains: Noun US2 is: " + containEntity1Us2 );

							conflictPairs.add(conflictPair);
//							conflicts.add(usNr1 + usNr2 + containEntity2Us2);
//							conflicts.add(usNr2 + usNr1 + containEntity2Us2);
							conflicts.add(usNr1 + usNr2);
							conflicts.add(usNr2 + usNr1);

						}
						if (containEntity2Us2.equalsIgnoreCase(entityUs1)) {
							System.out.println("\n\nContains: Conflict found  between: " + usNr1 + " and " + usNr2);
							System.out.println("Contains: Noun is " + containEntity1Us2 + " there is  " + actionRuleUs1
									+ "-" + criticalActionRule + " Conflict");
							// due to the fact that actionRuleUs2 can be more than one actionRule, we
							// passing
							// "criticalActionRule" (which is founded in actionRuleUs2) for saving
							// actionRules
							ConflictPair conflictPair = setConflictPair(usNr1, usNr2, pId, jsonObjectCurrent, current,
									actionUs1, containActionUs2, actionRuleUs1, criticalActionRule,
									actionRuleUs1 + "-" + criticalActionRule + "-Conflict", entityUs1,
									containEntity2Us2, nounMainUs1, containEntity1Us2);
							System.out.println("entity US1 is : " + nounMainUs1 + " Contains: Noun US1 is " + entityUs1
									+ " entity US2 is : " + containEntity1Us2 + " Contains: Noun US2 is: "
									+ containEntity2Us2);
							conflictPairs.add(conflictPair);
//							conflicts.add(usNr1 + usNr2 + containEntity1Us2);
//							conflicts.add(usNr2 + usNr1 + containEntity1Us2);
							conflicts.add(usNr1 + usNr2);
							conflicts.add(usNr2 + usNr1);

						}

					}

				}
			}
		}
	}

	// Store founded conflicted pair into ConflictPair class
	private ConflictPair setConflictPair(String us1, String us2, String pId, JSONObject jsonUs1, JSONObject jsonUs2,
			String actionUs1, String actionUs2, String actionRuleUs1, String actionRuleUs2, String conflictReason,
			String nounContainUs1, String nounContainUs2, String nounMainUs1, String nounMainUs2) {
		ConflictPair cp = new ConflictPair();
		cp.setConflictPair1(us1);
		cp.setConflictPair2(us2);
		cp.setJsonConflict1(jsonUs1);
		cp.setJsonConflict2(jsonUs2);
		cp.setpId(pId);
		cp.setConflictReason(conflictReason);

		cp.setNounMainUs1(nounMainUs1);
		cp.setNounMainUs2(nounMainUs2);

		cp.setNounContainUs1(nounContainUs1);
		cp.setNounContainUs2(nounContainUs2);

		cp.setActionRuleUs1(actionRuleUs1);
		cp.setActionRuleUs2(actionRuleUs2);

		cp.setActionUs1(actionUs1);
		cp.setActionUs2(actionUs2);

		return cp;
	}

	// return jsonArray from specific jsonObject
	private JSONArray getArray(JSONObject jsonObject, String key,
			ExceptionSupplier<? extends Exception> exceptionSupplier) throws Exception {
		if (jsonObject.has(key)) {
			return jsonObject.getJSONArray(key);
		}
		return null;
	}

	// Get UsNr from jsonObject and return back
	private String getUs(JSONObject jsonObject, String key) throws UsNrInJsonFileNotFound {
		if (jsonObject.has(key)) {
			return jsonObject.getString(key);

		} else {
			throw new UsNrInJsonFileNotFound();
		}

	}

	// return String from specific jsonObject
	private String getString(JSONObject jsonObject, String key,
			ExceptionSupplier<? extends Exception> exceptionSupplier) throws Exception {
		if (jsonObject.has(key)) {
			return jsonObject.getString(key);
		}
		return null;
	}

	// replace hash symbol at beginning and ending of founded element
	private String applyHashSymbols(String subString, String[] matches) {
		Arrays.sort(matches, Comparator.comparing(String::length).reversed());
		for (String match : matches) {
			subString = subString.replaceAll("\\b" + match + "\\b", "#" + match + "#").replaceAll("#+", "#");
			;

		}
		return subString;
	}
}
