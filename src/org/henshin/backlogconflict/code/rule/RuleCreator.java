package org.henshin.backlogconflict.code.rule;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.henshin.model.compact.CModule;
import org.eclipse.emf.henshin.model.compact.CNode;
import org.eclipse.emf.henshin.model.compact.CRule;
import org.eclipse.emf.henshin.model.compact.CUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.opencsv.exceptions.CsvValidationException;

/*  Delete Annotation for Attributes actions and entities
 *  plus their edges which have target edges or triggers edge
 *  This version is include US's Text
 */
public class RuleCreator {
	private String jsonFile;
	private String henshinFile;
	private String eCoreFile;

	public RuleCreator(String JsonFileName, String henshinFileName, String eCoreFileName)
			throws IOException, CsvValidationException {
		jsonFile = JsonFileName;
		henshinFile = henshinFileName;
		eCoreFile = eCoreFileName;

	}

	public String getJsonFile() {
		return jsonFile;
	}

	public String getJsonFileAbsolutePath() throws EmptyOrNotExistJsonFile {
		Path path = Paths
				.get("C:\\Users\\amirr\\eclipse-workspace_new\\" + "org.henshin.backlogconflict\\" + getJsonFile());
		if (Files.exists(path)) {
			return path.toString();

		} else {
			throw new EmptyOrNotExistJsonFile();
		}

	}

	public String getEcoreFileAbsolutePath() {
		Path path = Paths
				.get("C:\\Users\\amirr\\eclipse-workspace_new\\" + "org.henshin.backlogconflict\\" + getEcoreFile());
		if (Files.exists(path)) {
			return path.toString();

		}
		return null;

	}

	public String getHenshinFile() {
		return henshinFile;
	}

	public String getEcoreFile() {
		return eCoreFile;
	}

	// private static final Logger LOGGER =
	// Logger.getLogger(RuleCreator_v4.class.getName());

	public static void main(String[] args) throws IOException, EcoreFileNotFound, EmptyOrNotExistJsonFile,
			PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound,
			TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound,
			EdgeWithSameSourceAndTarget, CsvValidationException {
		long startTime = System.nanoTime();
		String version = "g03_loudoun";
		createRules(version);
		long endTime = System.nanoTime();
		double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
		System.out.println("Processing time: " + elapsedTimeInSeconds + " seconds");
	}

	public static void createRules(String version) throws IOException, EcoreFileNotFound, EmptyOrNotExistJsonFile,
			PersonaInJsonFileNotFound, UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound,
			TargetsInJsonFileNotFound, ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound,
			EdgeWithSameSourceAndTarget, CsvValidationException {

		RuleCreator ruleCreator = new RuleCreator("00_annotated_datasets\\" + version + "\\" + version + ".json",
				"Henshin_backlog_" + version, "Backlog_v2.4.ecore");
//		ActionsRulesCreator actionsRulesCreator = new ActionsRulesCreator();
//		actionsRulesCreator
//				.addActionsRules("Final_Reports\\Textual_Report_g" + version + "\\g" + version + "_baseline_pos.json");
		ReadJsonFile readJsonFile = new ReadJsonFile();
		JSONArray jsonArray = readJsonFile.readJsonArrayFromFile(ruleCreator.getJsonFileAbsolutePath());
		CModule cModule = ruleCreator.processJsonFile(jsonArray);
		System.out.println("saving");
		cModule.save();
	}

//	This method assign a CModule to a Ecore meta-model. 
//	It creates a new CModule object with the provided Henshin-file name,
//	adds imports from the Ecore file, and returns the module. 
	public CModule assignCmodule() throws EcoreFileNotFound {
		CModule module = new CModule(getHenshinFile());
		if (getEcoreFileAbsolutePath() == null) {
			throw new EcoreFileNotFound();
		}
		module.addImportsFromFile(getEcoreFile());
		return module;

	}

	// It takes parsed JSON array as input and processes their attributes,
//	such as persona, actions/entities, entities, text and their edges, such
//	as targets, triggers. Corresponding elements are created as output in a 
//	the Henshin transformation module (CModule).
	public CModule processJsonFile(JSONArray json) throws EcoreFileNotFound, PersonaInJsonFileNotFound,
			UsNrInJsonFileNotFound, ActionInJsonFileNotFound, EntityInJsonFileNotFound, TargetsInJsonFileNotFound,
			ContainsInJsonFileNotFound, TextInJsonFileNotFound, TriggersInJsonFileNotFound, EdgeWithSameSourceAndTarget,
			EmptyOrNotExistJsonFile, CsvValidationException, IOException {
		CModule cModule = assignCmodule();
		String usNrM = null;

		CRule mainRule = null;
		CRule benefitRule = null;

		JSONObject jsonMain = null;
		JSONObject jsonBenefit = null;

		JSONArray personaM = null;

		JSONArray actionMain = null;
		JSONArray entityMain = null;

		JSONArray actionBenefit = null;
		JSONArray entityBenefit = null;

		JSONArray triggersArrayMain = null;

		JSONArray targetsArrayMain = null;
		JSONArray containsArrayMain = null;

		JSONArray targetsArrayBenefit = null;
		JSONArray containsArrayBenefit = null;

		String textMain = null;
		String textBenefit = null;

		for (int i = 0; i < json.length(); i++) {

			JSONObject jsonObject = json.getJSONObject(i);

			if (jsonObject.has("US_Nr")) {

				usNrM = jsonObject.getString("US_Nr");

			} else {
				throw new UsNrInJsonFileNotFound();
			}

			if (jsonObject.has("Main")) {
				jsonMain = jsonObject.getJSONObject("Main");

				if (jsonMain.length() != 0) {

					JSONObject jsonActionRules = jsonMain.getJSONObject("Action Rules");
					JSONArray targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
					JSONArray containActionRules = jsonActionRules.getJSONArray("Contain Action Rules");
					String actionRule;
					String verb;
					String noun;
					String strActinRules = targetActionRules.toString();
					if (strActinRules.contains(";")) {
						for (int j = 0; j < targetActionRules.length(); j++) {
							JSONArray current = targetActionRules.getJSONArray(j);
							verb = current.getString(0).toLowerCase();
							noun = current.getString(1).toLowerCase();
//						System.out.println("Target verb: " + verb);
							actionRule = current.getString(2).toLowerCase();
//						System.out.println("Target Action-rule: " + actionRule);
							if (actionRule.contains(";")) {
								System.out.println("handling ambiguity " + actionRule);
								String[] items = actionRule.split(";");
								for (String actionRuleAmbiguity : items) {
									mainRule = processRule(usNrM + "_main_" + actionRuleAmbiguity, cModule);
									if (jsonMain.has("Action")) {
										actionMain = jsonMain.getJSONArray("Action");
									} else {
										throw new ActionInJsonFileNotFound();
									}
									if (jsonMain.has("Entity")) {
										entityMain = jsonMain.getJSONArray("Entity");

									} else {
										throw new EntityInJsonFileNotFound();
									}
									if (jsonMain.has("Triggers")) {
										triggersArrayMain = jsonMain.getJSONArray("Triggers");
									} else {
										throw new TriggersInJsonFileNotFound();
									}
									if (jsonMain.has("Targets")) {
										targetsArrayMain = jsonMain.getJSONArray("Targets");
									} else {
										throw new TargetsInJsonFileNotFound();
									}
									if (jsonMain.has("Contains")) {
										containsArrayMain = jsonMain.getJSONArray("Contains");
									} else {
										// it should be at least an empty array like "Contains":[]
										throw new ContainsInJsonFileNotFound();
									}
									if (jsonMain.has("Text")) {
										textMain = jsonMain.getString("Text");
									} else {
										throw new TextInJsonFileNotFound();
									}
									if (jsonMain.has("Persona")) {
										personaM = jsonMain.getJSONArray("Persona");
									} else {
										throw new PersonaInJsonFileNotFound();
									}

									// only applicable for main part
									CNode personaNode = processPersona(personaM, mainRule);

									// store all entities in one map which the string is the name of entities and
									// CNode correspond to their CNode Object
									Map<String, CNode> mapEntityMain = new HashMap<>();

									// store all actions in one map which the string is the name of actions and
									// CNode correspond to their CNode Object
									Map<String, CNode> mapActionMain = new HashMap<>();

									//
									processText(mainRule, textMain);

									processActions(mainRule, jsonMain, actionMain, triggersArrayMain, personaNode,
											mapActionMain, actionRuleAmbiguity, verb, false);
									processEntities(jsonMain, mainRule, entityMain, targetActionRules,
											containActionRules, mapEntityMain, actionRuleAmbiguity, noun, false);
									processTargetsEdges(jsonMain, targetActionRules, mapEntityMain, mapActionMain,
											usNrM, actionRuleAmbiguity, verb, false);
									processContainsEdges(jsonMain, containsArrayMain, targetsArrayMain, mapEntityMain,
											usNrM, cModule, actionRuleAmbiguity, noun, false);
								}
							}
						}

					} else {
						// here rule should create just once, but now a rule will be create
						// for each Target Action Rule
						mainRule = processRule(usNrM + "_main", cModule);
						if (jsonMain.has("Action")) {
							actionMain = jsonMain.getJSONArray("Action");
						} else {
							throw new ActionInJsonFileNotFound();
						}
						if (jsonMain.has("Entity")) {
							entityMain = jsonMain.getJSONArray("Entity");

						} else {
							throw new EntityInJsonFileNotFound();
						}
						if (jsonMain.has("Triggers")) {
							triggersArrayMain = jsonMain.getJSONArray("Triggers");
						} else {
							throw new TriggersInJsonFileNotFound();
						}
						if (jsonMain.has("Targets")) {
							targetsArrayMain = jsonMain.getJSONArray("Targets");
						} else {
							throw new TargetsInJsonFileNotFound();
						}
						if (jsonMain.has("Contains")) {
							containsArrayMain = jsonMain.getJSONArray("Contains");
						} else {
							// it should be at least an empty array like "Contains":[]
							throw new ContainsInJsonFileNotFound();
						}
						if (jsonMain.has("Text")) {
							textMain = jsonMain.getString("Text");
						} else {
							throw new TextInJsonFileNotFound();
						}
						if (jsonMain.has("Persona")) {
							personaM = jsonMain.getJSONArray("Persona");
						} else {
							throw new PersonaInJsonFileNotFound();
						}

						// only applicable for main part
						CNode personaNode = processPersona(personaM, mainRule);

						// store all entities in one map which the string is the name of entities and
						// CNode correspond to their CNode Object
						Map<String, CNode> mapEntityMain = new HashMap<>();

						// store all actions in one map which the string is the name of actions and
						// CNode correspond to their CNode Object
						Map<String, CNode> mapActionMain = new HashMap<>();

						//
						processText(mainRule, textMain);

						processActions(mainRule, jsonMain, actionMain, triggersArrayMain, personaNode, mapActionMain,
								null, null, true);
						processEntities(jsonMain, mainRule, entityMain, targetsArrayMain, containActionRules,
								mapEntityMain, null, null, true);
						processTargetsEdges(jsonMain, targetActionRules, mapEntityMain, mapActionMain, usNrM, null,
								null, true);
						processContainsEdges(jsonMain, containActionRules, targetsArrayMain, mapEntityMain, usNrM,
								cModule, null, null, true);

					}
				}

				// Proceed Benefit part

			}

			if (jsonObject.has("Benefit")) {
				jsonBenefit = jsonObject.getJSONObject("Benefit");
				JSONObject jsonActionRules = jsonBenefit.getJSONObject("Action Rules");
				JSONArray targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
				JSONArray containActionRules = jsonActionRules.getJSONArray("Contain Action Rules");
				String actionRule = "preserve";
				String verb;
				String noun;
				if (jsonBenefit.length() != 0) {
					benefitRule = processRule(usNrM + "_benefit", cModule);

					if (jsonBenefit.has("Action")) {
						actionBenefit = jsonBenefit.getJSONArray("Action");
					} else {
						throw new ActionInJsonFileNotFound();
					}
					if (jsonBenefit.has("Entity")) {
						entityBenefit = jsonBenefit.getJSONArray("Entity");

					} else {
						throw new EntityInJsonFileNotFound();
					}

					if (jsonBenefit.has("Targets")) {
						targetsArrayBenefit = jsonBenefit.getJSONArray("Targets");
					} else {
						throw new TargetsInJsonFileNotFound();
					}
					if (jsonBenefit.has("Contains")) {
						containsArrayBenefit = jsonBenefit.getJSONArray("Contains");
					} else {
						// it should be at least an empty array like "Contains":[]
						throw new ContainsInJsonFileNotFound();
					}
					if (jsonBenefit.has("Text")) {
						textBenefit = jsonBenefit.getString("Text");
					} else {
						throw new TextInJsonFileNotFound();
					}

				} // store all entities in one map which the string is the name of entities
					// and // CNode correspond to their CNode Object
				Map<String, CNode> mapEntityBenefit = new HashMap<>();

				// store all actions in one map which the string is the name of actions and
				// CNode correspond to their CNode Object
				Map<String, CNode> mapActionBenefit = new HashMap<>();
				// System.out.println("Benefit");

				processText(benefitRule, textBenefit);
				processActions(benefitRule, jsonBenefit, actionBenefit, null, null, mapActionBenefit, null, null, true);
				processEntities(jsonBenefit, benefitRule, entityBenefit, targetsArrayBenefit, containActionRules,
						mapEntityBenefit, null, null, true);
				processTargetsEdges(jsonBenefit, targetActionRules, mapEntityBenefit, mapActionBenefit, usNrM, null,
						null, true);
				processContainsEdges(jsonBenefit, containActionRules, targetsArrayBenefit, mapEntityBenefit, usNrM,
						cModule, null, null, true);

			}

		}
		return cModule;
	}

//	It takes the \enquote{US\_Nr} JSON-object as input 
//	and creates a new CRule with the name of unique US 
//	identifier in the CModule.
	private CRule processRule(String usNr, CModule module) {
		CRule userStory = module.createRule(usNr);
		return userStory;
	}

//	It receives as input US text extracted from JSON data and 
//	the associated CRule to create a new CNode representing the 
//	text within the provided CRule and adds the attribute 
//	\enquote{text} with US text as value. Finally, the created 
//	CNode representing the US text is returned.
	private void processText(CRule userStory, String text) {

		CNode nodeText = userStory.createNode("Story");
		text = text.replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
		nodeText.createAttribute("text", "\"" + text + "\"");

	}

	private CNode processPersona(JSONArray persona, CRule userStory) {

		CNode nodePersona = userStory.createNode("Persona");
		String person = persona.getString(0).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
		nodePersona.createAttribute("name", "\"" + person + "\"");
		return nodePersona;

	}

	private void processActions(CRule userStory, JSONObject jsonObject, JSONArray actions, JSONArray triggersArray,
			CNode nodePersona, Map<String, CNode> actionMap, String actionRule, String verb, boolean atomicRule)
			throws ActionInJsonFileNotFound, EdgeWithSameSourceAndTarget, EmptyOrNotExistJsonFile {

		// Creating Nodes for Action/s
		for (int i = 0; i < actions.length(); i++) {
			String action = actions.getString(i).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
			CNode cNode = null;
			if (("preserve".equals(actionRule) || actionRule == null) && (atomicRule == false)
					&& actionMap.get(action) == null) {
				cNode = userStory.createNode("Action");
				cNode.createAttribute("name", "\"" + action + "\"");
				if (triggersArray != null) {
					for (int j = 0; j < triggersArray.length(); j++) {
						JSONArray trigger = triggersArray.getJSONArray(j);
						String actionTrigger = trigger.getString(1);
						if (nodePersona != null && actionTrigger.equalsIgnoreCase(action)) {
							nodePersona.createEdge(cNode, "triggers");
							break;
						}
					}
				}
				actionMap.put(action, cNode);
			} else if (atomicRule == true && actionMap.get(action) == null) {
				JSONObject jsonActionRules = jsonObject.getJSONObject("Action Rules");
				JSONArray targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
				String actionRuleCurrent;
				String verbCurrent;
				String noun;

				for (int j = 0; j < targetActionRules.length(); j++) {
					JSONArray current = targetActionRules.getJSONArray(j);
					verbCurrent = current.getString(0).toLowerCase();
					noun = current.getString(1).toLowerCase();
//					System.out.println("Target verb: " + verb);
					actionRuleCurrent = current.getString(2).toLowerCase();
					if (verbCurrent.equals(action) && actionMap.get(action) == null) {
						cNode = userStory.createNode("Action", actionRuleCurrent);
						cNode.createAttribute("name", "\"" + action + "\"", actionRuleCurrent);
						if (triggersArray != null) {
							for (int k = 0; k < triggersArray.length(); k++) {
								JSONArray trigger = triggersArray.getJSONArray(k);
								String actionTrigger = trigger.getString(1);
								if (nodePersona != null && actionTrigger.equalsIgnoreCase(action)) {
									nodePersona.createEdge(cNode, "triggers", actionRuleCurrent);
									break;
								}
							}
						}
						actionMap.put(action, cNode);
					}
				}
			} else if (actionMap.get(action) == null) {
				cNode = userStory.createNode("Action", actionRule);
				cNode.createAttribute("name", "\"" + action + "\"", actionRule);
				if (nodePersona != null) {
					nodePersona.createEdge(cNode, "triggers", actionRule);
				}
				actionMap.put(action, cNode);
			}

		}

	}

//	It receives as parameters the JSON-object with information about the entities,
//	the CRule object representing the US to which the entities belong and the
//	JSON-array with information about the targets associated with the entities. 
//	The method checks whether primary/secondary entities are present, then creates 
//	a CNode for each primary/secondary entity and checks whether the entity is
//	present in the target array. If this is the case, its attribute \enquote{name} 
//	is annotated for deletion.

	private void processEntities(JSONObject jsonObject, CRule userStory, JSONArray entities, JSONArray targetsArray,
			JSONArray containActionRules, Map<String, CNode> entityMap, String actionRule, String noun,
			boolean atomicRule) throws EntityInJsonFileNotFound {
		// Creating Nodes for Primary Entity/s
		for (int i = 0; i < entities.length(); i++) {
			CNode cNode = null;
			String entity = entities.getString(i).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
			// check if entity exist in entityMap

			if (entity.equalsIgnoreCase(noun) && entityMap.get(entity) == null && atomicRule == false) {
				System.out.println("noun is:" + noun);
				System.out.println("action rule is :" + actionRule);
				cNode = userStory.createNode("Entity", actionRule);
				cNode.createAttribute("name", "\"" + entity + "\"", actionRule);
				entityMap.put(entity, cNode);

			} else if (atomicRule == true && entityMap.get(entity) == null) {
				JSONObject jsonActionRules = jsonObject.getJSONObject("Action Rules");
				JSONArray targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");

				for (int j = 0; j < targetActionRules.length(); j++) {
					JSONArray current = targetActionRules.getJSONArray(j);
//					noun1Current = current.getString(0).toLowerCase();
					String entityCurrent = current.getString(1).toLowerCase();
//					System.out.println("Target verb: " + verb);
					String actionRuleCurrent = current.getString(2).toLowerCase();
					if (entity.equalsIgnoreCase(entityCurrent) && entityMap.get(entity) == null) {
						System.out
								.println("Targets: anotate entity is " + entityCurrent + " with " + actionRuleCurrent);
						cNode = userStory.createNode("Entity", actionRuleCurrent);
						cNode.createAttribute("name", "\"" + entity + "\"", actionRuleCurrent);
						entityMap.put(entity, cNode);
						break;

					}
				}
				for (int j = 0; j < containActionRules.length(); j++) {
					JSONArray current = containActionRules.getJSONArray(j);
					String noun1Current = current.getString(0).toLowerCase();
					String noun2Current = current.getString(1).toLowerCase();
//					System.out.println("Target verb: " + verb);
					String actionRuleCurrent = current.getString(2).toLowerCase();
					if (entity.equalsIgnoreCase(noun2Current) && entityMap.get(noun2Current) == null) {
						System.out
								.println("Contains: anotate entity is " + noun2Current + " with " + actionRuleCurrent);
						cNode = userStory.createNode("Entity", actionRuleCurrent);
						cNode.createAttribute("name", "\"" + noun2Current + "\"", actionRuleCurrent);
						entityMap.put(noun2Current, cNode);

					} else if (entity.equalsIgnoreCase(noun1Current) && entityMap.get(noun1Current) == null) {
						System.out
								.println("Contains: anotate entity is " + noun1Current + " with " + actionRuleCurrent);
						cNode = userStory.createNode("Entity", actionRuleCurrent);
						cNode.createAttribute("name", "\"" + noun1Current + "\"", actionRuleCurrent);
						entityMap.put(noun1Current, cNode);
						break;

					}
				}
			}

			if (entityMap.get(entity) == null && entity != null) {
				cNode = userStory.createNode("Entity");
				cNode.createAttribute("name", "\"" + entity + "\"");
				entityMap.put(entity, cNode);
			}

//			else {
//				throw new EntityInJsonFileNotFound("Entity in JSON file not found!");
//			}
		}

	}

//	It receives the JSON object to be processed, the JSON array with information 
//	about the target edges and the US identifier as parameters. The method checks
//	whether the action and entity in the target array exists in the JSON file.
//	If the action and the entity exist, an edge with the name \enquote{targets} 
//	is created between them in the Henshin files and annotated for deletion.

	// processTargetsEdges(jsonBenefit, targetsArrayBenefit, mapEntityBenefit,
	// mapActionBenefit, usNrM, null, null,false);
	private void processTargetsEdges(JSONObject jsonObject, JSONArray targetsArray, Map<String, CNode> entityMap,
			Map<String, CNode> actionMap, String usNrM, String actionRule, String verb, boolean atomicRule)
			throws EntityInJsonFileNotFound, EdgeWithSameSourceAndTarget, ActionInJsonFileNotFound {

		for (int i = 0; i < targetsArray.length(); i++) {
			// replace space at the end of text if any
//			String action = currentArray.getString(0).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
//			String entity = currentArray.getString(1).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
			CNode nodeEntity = null;
			CNode nodeAction = null;
			JSONArray current = targetsArray.getJSONArray(i);
			String action = current.getString(0).toLowerCase().replaceAll(" $", "").replaceAll("^ ", "");
			String entity = current.getString(1).toLowerCase().replaceAll(" $", "").replaceAll("^ ", "");

			System.out.println("Target verb: " + action);
			System.out.println("Target noun: " + entity);
			String actionRuleCurrent;
			if (actionRule == null) {
				actionRuleCurrent = current.getString(2).toLowerCase();
				System.out.println("Action Rule : " + actionRuleCurrent);
			} else {
				actionRuleCurrent = actionRule;
			}
			// CNode nodeAction = actionMap.get(action);
			if ((actionMap.get(action.toLowerCase()) != null)) {
				nodeAction = actionMap.get(action.toLowerCase());
//				System.out.println("node Action is found and not added to map: " + action);
			} else {
				throw new ActionInJsonFileNotFound(
						"In \"Targets\" of " + usNrM + ", Action: \"" + action.toString() + "\" is not found!");
			}
			if ((entityMap.get(entity) != null)) {

				nodeEntity = entityMap.get(entity);
			} else {

				throw new EntityInJsonFileNotFound(
						"In \"Targets\" of " + usNrM + ", Entity: \"" + entity.toString() + "\" is not found!");
			}
			nodeAction.createEdge(nodeEntity, "targets", actionRuleCurrent);

		}
	}

//	It receives the JSON-object to be processed, the JSON array 
//	with information about contains/target edges and the US identifier
//	as parameters. It first checks whether both entities belong to 
//	contains edges. If both entities exist, an edge is created between 
//	them in CRule with the name \enquote{contains}. If one of the entities 
//	is a target of another entity (as specified in the targets array), the
//	edge is annotated for deletion. If none of the entities is a target,
//	the edge is annotated as \enquote{preserve}.
	private void processContainsEdgess(JSONObject jsonObject, JSONArray containsArray, JSONArray targetsArray,
			Map<String, CNode> entityMap, String usNrM, CModule cModule, String actionRule, String noun,
			boolean atomicRule) throws EntityInJsonFileNotFound, EdgeWithSameSourceAndTarget {

		// iterate through contains JSONArray
		for (int i = 0; i < containsArray.length(); i++) {
			JSONArray currentArray = containsArray.getJSONArray(i);

			// consider the first element of array as firstEnttiy
			// replace space at the end of text if any
			String firstEntity = currentArray.getString(0).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
			// consider the first element of array as secondEnttiy
			// replace space at the end of text if any
			String secondEntity = currentArray.getString(1).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();

			// make sure that both entity is already listed in entityMap
//			System.out.println(firstEntity +" and " + secondEntity);
			if ((entityMap.get(firstEntity) != null) && (entityMap.get(secondEntity) != null)) {
//				System.out.println("cntains entity map first: " + firstEntity + " second : " + secondEntity);
				CNode nodefirstEntity = entityMap.get(firstEntity);
				CNode nodeSecondEntity = entityMap.get(secondEntity);

				// Check if any Entity in Contains is already exist in Targets
				// if so annotate the contains edge as <delete>
				// otherwise annotate the contains edge as <preserve>
				if (checkEntityIsTarget(firstEntity, targetsArray) || checkEntityIsTarget(secondEntity, targetsArray)) {

//					try {
					// add an edge from first Entity to second Entity and annotated it as<delete>
//					System.out.println("contains is in targets: " + firstEntity);	

					if (firstEntity.equalsIgnoreCase(noun)
							|| secondEntity.equalsIgnoreCase(noun) && actionRule != null) {
						System.out.println("contains found:" + noun);
						nodefirstEntity.createEdge(nodeSecondEntity, "contains", actionRule);

					} else if (atomicRule == true) {
						JSONObject jsonActionRules = jsonObject.getJSONObject("Action Rules");
						JSONArray targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
						String actionRuleCurrent;
						String verbCurrent;
						String nounCurrent;

						for (int j = 0; j < targetActionRules.length(); j++) {
							JSONArray current = targetActionRules.getJSONArray(j);
							verbCurrent = current.getString(0).toLowerCase();
							nounCurrent = current.getString(1).toLowerCase();
//							System.out.println("Target verb: " + verb);
							actionRuleCurrent = current.getString(2).toLowerCase();
							if (firstEntity.equalsIgnoreCase(nounCurrent)
									|| secondEntity.equalsIgnoreCase(nounCurrent)) {
								nodefirstEntity.createEdge(nodeSecondEntity, "contains", actionRuleCurrent);

							}
						}
					}

					// } catch (RuntimeException e) {

//						throw new EdgeWithSameSourceAndTarget("In \"Contains\" of " + usNrM + ", Edge with Entity: \""
//								+ firstEntity.toLowerCase().toString() + "\" and Entity \"" + secondEntity.toString()
//								+ "\" is already created!");
//					}
//					for(CUnit unit: cModule.getAllCUnits()) {
//						if(unit instanceof CRule) {
//						CRule rule = (CRule) unit;
//						for(CNode node : rule.ge) {
//							
//						}
//						}		
//					}

				} else {
//					try {
					// add an edge from first Entity to second Entity and annotated it as<preserve>
					System.out.println("here " + firstEntity + " and " + secondEntity);
					nodefirstEntity.createEdge(nodeSecondEntity, "contains");
//					} catch (RuntimeException e) {

//						throw new EdgeWithSameSourceAndTarget("In \"Contains\" of " + usNrM + ", Edge with Entity: \""
//								+ firstEntity.toLowerCase().toString() + "\" and Entity" + secondEntity.toString()
//								+ " is already created!");
//					}

				}
			} else {
				throw new EntityInJsonFileNotFound("In " + usNrM + ", following entties are missing: "
						+ firstEntity.toString() + " and " + secondEntity.toString());
			}
		}

	}

	private void processContainsEdges(JSONObject jsonObject, JSONArray containsArray, JSONArray targetsArray,
			Map<String, CNode> entityMap, String usNrM, CModule cModule, String actionRules, String noun,
			boolean atomicRule) throws EntityInJsonFileNotFound, EdgeWithSameSourceAndTarget {

		// iterate through contains JSONArray
		for (int i = 0; i < containsArray.length(); i++) {
			JSONArray currentArray = containsArray.getJSONArray(i);

			String firstEntity = currentArray.getString(0).toLowerCase();
			String secondEntity = currentArray.getString(1).toLowerCase();
			String actionRule;
			if (actionRules == null) {
				actionRule = currentArray.getString(2).toLowerCase();
			} else {
				actionRule = actionRules;
			}
			// make sure that both entity is already listed in entityMap
//			System.out.println(firstEntity +" and " + secondEntity);
			if ((entityMap.get(firstEntity) != null) && (entityMap.get(secondEntity) != null)) {
				System.out.println("contains entity map first: " + firstEntity + " second : " + secondEntity);
				CNode nodefirstEntity = entityMap.get(firstEntity);
				CNode nodeSecondEntity = entityMap.get(secondEntity);
				nodefirstEntity.createEdge(nodeSecondEntity, "contains", actionRule);
			} else {
				throw new EntityInJsonFileNotFound("In " + usNrM + ", following entties are missing: "
						+ firstEntity.toString() + " and " + secondEntity.toString());
			}
		}

	}

//	It receives the name of the entity and the JSON-array with 
//	information about target edges. The method iterates through
//	the JSON-array targets, which contains arrays that represent
//	targets edges between actions and entities. It compares the 
//	targets entity with the specified entity. If there is a match,
//	it returns true to indicate that the entity is a target.
	private static boolean checkEntityIsTarget(String entity, JSONArray targets) {
		for (int j = 0; j < targets.length(); j++) {
			// iterate through each element in array
			JSONArray currentArray = targets.getJSONArray(j);
			String targetEntity = currentArray.getString(1);
			if (targetEntity.equalsIgnoreCase(entity)) {
				return true;
			}
		}
		return false;
	}

}
