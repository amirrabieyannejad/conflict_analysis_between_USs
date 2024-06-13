package org.henshin.backlogconflict.code.rule;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.opencsv.exceptions.CsvValidationException;
import java.io.FileWriter;

public class ActionsRulesCreator {
	// String datasetNum;
	private VerbFinder verbFinder;

	public ActionsRulesCreator() {
//		this.datasetNum = dataset;
	}

	public void addActionsRules(String path) throws EmptyOrNotExistJsonFile, CsvValidationException, IOException {
		ReadJsonFile file = new ReadJsonFile();
		JSONArray jsonArray = file.readJsonArrayFromFile(path);
		this.verbFinder = new VerbFinder("actions_rules.csv");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			addActionRule(jsonObject);
		}
		try (FileWriter fileWriter = new FileWriter(path)) {
			fileWriter.write(jsonArray.toString(4)); // Indent with 4 spaces for readability
		}
	}

	public String findActionRule(String verb) {
		return verbFinder.getActionRule(verb);
	}

	private void addActionRule(JSONObject jsonObject) {
		JSONObject main = jsonObject.getJSONObject("Main");
		JSONObject benefit = jsonObject.getJSONObject("Benefit");

		JSONArray mainTargets = main.getJSONArray("Targets");
		JSONArray mainContains = main.getJSONArray("Contains");

		JSONArray benefitTargets = benefit.getJSONArray("Targets");
		JSONArray benefitContains = benefit.getJSONArray("Contains");

		JSONArray mainTargetActionRules = new JSONArray();
		JSONArray mainContainActionRules = new JSONArray();

		JSONArray benefitTargetActionRules = new JSONArray();
		JSONArray benefitContainActionRules = new JSONArray();

		Set<String> setMainTargets = new HashSet<>();
		Set<String> setMainContains = new HashSet<>();

		Set<String> setBenefitTargets = new HashSet<>();
		Set<String> setBenefitContains = new HashSet<>();

		JSONArray targetPair;
		JSONArray containPair;

		// Proceed Targets in Main part
		for (int i = 0; i < mainTargets.length(); i++) {
			JSONArray target = mainTargets.getJSONArray(i);
			String verb = target.getString(0);
			String entity = target.getString(1);
			String key = verb.toLowerCase() + entity.toLowerCase();
			String actionRule = findActionRule(verb.toLowerCase());

			// System.out.println("action Rule is: " + actionRule + " verb is: " + verb);
			targetPair = new JSONArray();
			if (actionRule == null) {
				System.out.println("ERROR: " + jsonObject.getString("US_Nr") + "Verb in Main part not found: " + verb);
			} else {
				if (!setMainTargets.contains(key)) {
					targetPair.put(verb.toLowerCase());
					targetPair.put(entity.toLowerCase());
					targetPair.put(actionRule);
					setMainTargets.add(key);
					mainTargetActionRules.put(targetPair);
				}
				// Proceed Contains in Main part
				for (int j = 0; j < mainContains.length(); j++) {
					JSONArray contain = mainContains.getJSONArray(j);
					String entity1 = contain.getString(0).toLowerCase();
					String entity2 = contain.getString(1).toLowerCase();
					String keyContain = entity1 + entity2;
					if (entity1.equalsIgnoreCase(entity)) {
//						System.out.println("entity is: " + entity);
						containPair = new JSONArray();
						if (!setMainContains.contains(keyContain)) {
							containPair.put(entity1);
							containPair.put(entity2);
							containPair.put(actionRule);
							mainContainActionRules.put(containPair);
							setMainContains.add(keyContain);
						}
					}
					if (entity2.equalsIgnoreCase(entity)) {
						containPair = new JSONArray();
//						System.out.println("entity is if2: " + entity);
						if (!setMainContains.contains(keyContain)) {
							containPair.put(entity1);
							containPair.put(entity2);
							containPair.put(actionRule);
							mainContainActionRules.put(containPair);
							setMainContains.add(keyContain);
						}
					}
				}
			}

		}

		// Proceed Targets in Benefit part
		for (int i = 0; i < benefitTargets.length(); i++) {
			JSONArray target = benefitTargets.getJSONArray(i);
			String verb = target.getString(0);
			String entity = target.getString(1);
			String key = verb.toLowerCase()+entity.toLowerCase();
			
			String actionRule = findActionRule(verb.toLowerCase());

			// System.out.println("action Rule is: " + actionRule + " verb is: " + verb);
			targetPair = new JSONArray();
			if (actionRule == null) {
				System.out
						.println("ERROR: " + jsonObject.getString("US_Nr") + "Verb in Benefit part not found: " + verb);
			} else {
				if (!setBenefitTargets.contains(key)) {
					targetPair.put(verb.toLowerCase());
					targetPair.put(entity.toLowerCase());
					targetPair.put(actionRule);
					setBenefitTargets.add(key);
					benefitTargetActionRules.put(targetPair);
				}
				// Proceed Contains in Benefit part
				for (int j = 0; j < benefitContains.length(); j++) {
					JSONArray contain = benefitContains.getJSONArray(j);
					String entity1 = contain.getString(0);
					String entity2 = contain.getString(1);
					String keyContain = entity1.toLowerCase()+ entity2.toLowerCase();
					if (entity1.equalsIgnoreCase(entity)) {
//								System.out.println("entity is: " + entity);
						containPair = new JSONArray();
						if (!setBenefitContains.contains(keyContain)) {
							containPair.put(entity1);
							containPair.put(entity2);
							containPair.put(actionRule);
							benefitContainActionRules.put(containPair);
							setBenefitContains.add(keyContain);
						}
					}
					if (entity2.equalsIgnoreCase(entity)) {
						containPair = new JSONArray();
//								System.out.println("entity is if2: " + entity);
						if (!setBenefitContains.contains(keyContain)) {
							containPair.put(entity1);
							containPair.put(entity2);
							containPair.put(actionRule);
							benefitContainActionRules.put(containPair);
							setBenefitContains.add(keyContain);
						}
					}
				}
			}

		}

		// Add Action Rules into Main and return the root jsonobject
		JSONObject mainActionRules = new JSONObject();
		mainActionRules.put("Target Action Rules", mainTargetActionRules);
		mainActionRules.put("Contain Action Rules", mainContainActionRules);
		main.put("Action Rules", mainActionRules);

		// Add Action Rules into Main and return the root jsonobject
		JSONObject benefitActionRules = new JSONObject();
		benefitActionRules.put("Target Action Rules", benefitTargetActionRules);
		benefitActionRules.put("Contain Action Rules", benefitContainActionRules);
		benefit.put("Action Rules", benefitActionRules);

	}

}
