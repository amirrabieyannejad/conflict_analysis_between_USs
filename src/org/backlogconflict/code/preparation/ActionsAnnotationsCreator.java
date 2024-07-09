package org.backlogconflict.code.preparation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.opencsv.exceptions.CsvValidationException;
import java.io.FileWriter;

public class ActionsAnnotationsCreator {
	// String datasetNum;
	private VerbFinder verbFinder;

	public ActionsAnnotationsCreator() {
//		this.datasetNum = dataset;
	}

	public void addActionsAnnotations(String[] dataSets, String filePath,String actionAnnotation) throws EmptyOrNotExistJsonFile, CsvValidationException, IOException, ActionAnnotationNotFound {
		
		for (int j = 0; j < dataSets.length; j++) {
		
			// TODO: Uncomment this for Test
//		String path = filePath + "\\" + dataSets[j] + ".json";
		
		// TODO: Uncomment this for Datasets
			String path = filePath +dataSets[j]+ "\\" + dataSets[j] + ".json";
		ReadJsonFile file = new ReadJsonFile();
		JSONArray jsonArray = file.readJsonArrayFromFile(path);
		this.verbFinder = new VerbFinder(actionAnnotation);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			addActionAnnotations(jsonObject);  
		}
		try (FileWriter fileWriter = new FileWriter(path)) {
			fileWriter.write(jsonArray.toString(4)); // Indent with 4 spaces for readability
		}
		}
	} 

	public String findActionsAnnotations(String verb) {
		return verbFinder.getActionAnnotations(verb);
	}

	private void addActionAnnotations(JSONObject jsonObject) {
		JSONObject main = jsonObject.getJSONObject("Main");
//		JSONObject benefit = jsonObject.getJSONObject("Benefit");

		JSONArray mainTargets = main.getJSONArray("Targets");
		JSONArray mainContains = main.getJSONArray("Contains");

//		JSONArray benefitTargets = benefit.getJSONArray("Targets");
//		JSONArray benefitContains = benefit.getJSONArray("Contains");

		JSONArray mainTargetActionAnnoAnnotations = new JSONArray();
		JSONArray mainContainActionAnnotations = new JSONArray();

//		JSONArray benefitTargetActionRules = new JSONArray();
//		JSONArray benefitContainActionRules = new JSONArray();

		Set<String> setMainTargets = new HashSet<>();
		Set<String> setMainContains = new HashSet<>();

//		Set<String> setBenefitTargets = new HashSet<>();
//		Set<String> setBenefitContains = new HashSet<>();

		JSONArray targetPair;
		JSONArray containPair;

		// Proceed Targets in Main part
		for (int i = 0; i < mainTargets.length(); i++) {
			JSONArray target = mainTargets.getJSONArray(i);
			String verb = target.getString(0);
			String entity = target.getString(1);
			String key = verb.toLowerCase() + entity.toLowerCase();
			String actionAnnotation = findActionsAnnotations(verb.toLowerCase());

			// System.out.println("action Rule is: " + actionRule + " verb is: " + verb);
			targetPair = new JSONArray();
			
			if (actionAnnotation == "") {
				System.out.println("ERROR: " + jsonObject.getString("US_Nr") + "Verb in Main part not found: " + verb);
			} else {
				if (!setMainTargets.contains(key)) {
					targetPair.put(verb.toLowerCase());
					targetPair.put(entity.toLowerCase());
					targetPair.put(actionAnnotation);
					setMainTargets.add(key);
					mainTargetActionAnnoAnnotations.put(targetPair);
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
							containPair.put(actionAnnotation);
							containPair.put(verb);
							mainContainActionAnnotations.put(containPair);
							setMainContains.add(keyContain);
						}
					}
					if (entity2.equalsIgnoreCase(entity)) {
						containPair = new JSONArray();
//						System.out.println("entity is if2: " + entity);
						if (!setMainContains.contains(keyContain)) {
							containPair.put(entity1);
							containPair.put(entity2);
							containPair.put(actionAnnotation);
							containPair.put(verb);
							mainContainActionAnnotations.put(containPair);
							setMainContains.add(keyContain);
						}
					}
				}
			}

		}
/*
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
		*/

		// Add Action Annotations into Main and return the root jsonobject
		JSONObject mainActionAnnotations = new JSONObject();
		mainActionAnnotations.put("Target Action Annotations", mainTargetActionAnnoAnnotations);
		mainActionAnnotations.put("Contain Action Annotations", mainContainActionAnnotations);
		main.put("Action Annotations", mainActionAnnotations);

		// Add Action Rules into Main and return the root jsonobject
//		JSONObject benefitActionRules = new JSONObject();
//		benefitActionRules.put("Target Action Rules", benefitTargetActionRules);
//		benefitActionRules.put("Contain Action Rules", benefitContainActionRules);
//		benefit.put("Action Rules", benefitActionRules);

	}

}
