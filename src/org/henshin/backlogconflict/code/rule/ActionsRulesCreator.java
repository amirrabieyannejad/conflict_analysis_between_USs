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
		JSONArray targets = jsonObject.getJSONArray("Targets");
		JSONArray contains = jsonObject.getJSONArray("Contains");
		JSONArray targetActionRules = new JSONArray();
		JSONArray containActionRules = new JSONArray();
		Set<String> setTargets = new HashSet<>();
		Set<String> setContains = new HashSet<>();
		JSONArray targetPair;
		JSONArray containPair;
		for (int i = 0; i < targets.length(); i++) {
			JSONArray target = targets.getJSONArray(i);
			String verb = target.getString(0);
			String entity = target.getString(1);
			String actionRule = findActionRule(verb.toLowerCase());
//			System.out.println("action Rule is: " + actionRule + " verb is: " + verb);
			targetPair = new JSONArray();
			if (actionRule == null) {
				System.out.println("ERROR: Verb not found: " + verb);
				
			
			} else {
				if(!setTargets.contains(verb.toLowerCase())) {
				targetPair.put(verb.toLowerCase());
				targetPair.put(entity.toLowerCase());
				targetPair.put(actionRule);
				setTargets.add(verb.toLowerCase()+entity.toLowerCase());
				targetActionRules.put(targetPair);
				}
				for (int j = 0; j < contains.length(); j++) {
					JSONArray contain = contains.getJSONArray(j);
					String entity1 = contain.getString(0);
					String entity2 = contain.getString(1);
					
					if (entity1.equalsIgnoreCase(entity)) {
//						System.out.println("entity is: " + entity);
						containPair = new JSONArray();
						if(!setContains.contains(entity2.toLowerCase())) {
						containPair.put(entity2);
						containPair.put(actionRule);
						containActionRules.put(containPair);
						setContains.add(entity2.toLowerCase()+entity.toLowerCase());
						}
					}
					if (entity2.equalsIgnoreCase(entity)) {
						containPair = new JSONArray();
//						System.out.println("entity is if2: " + entity);
						if(!setContains.contains(entity1.toLowerCase())) {
						containPair.put(entity1);
						containPair.put(actionRule);
						containActionRules.put(containPair);
						setContains.add(entity1.toLowerCase()+entity.toLowerCase());
						}
					}
				}
			}

			
			
		}
		JSONObject actionRules = new JSONObject();
		actionRules.put("Target Action Rules", targetActionRules);
		actionRules.put("Contain Action Rules", containActionRules);
		jsonObject.put("Action Rules", actionRules);

	}

}
