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
import org.henshin.backlogconflict.code.rule.ActionInJsonFileNotFound.ExceptionSupplier;
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

	public static void main(String[] args) throws Exception {

		String[] version = { "g03_loudoun", "g04_recycling", "g05_open_spending", "g08_frictionless",
				"g10_scrum_alliance", "g11_nsf", "g12_camperplus", "g14_datahub", "g16_mis", "g18_neurohub",
				"g19_alfred", "g21_badcamp", "g22_rdadmp", "g23_archives_space", "g24_unibath", "g25_duraspace",
				"g26_racdam", "g27_culrepo", "g28_zooniverse" };
//		String[] version = { "g03_loudoun" };
		for (int i = 0; i < version.length; i++) {
			long startTime = System.nanoTime();
			System.out.println("-------------Creating Dataset "+version[i]+" --------------");
			createRules(version[i]);
			long endTime = System.nanoTime();
			double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
			System.out.println("Processing time: " + elapsedTimeInSeconds + " seconds");
		}

	}

	public static void createRules(String version) throws Exception {

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
	public CModule processJsonFile(JSONArray json) throws Exception {
		CModule cModule = assignCmodule();
		String usNrM = null;

		System.out.println("iterate through jsonArray the main Structure");
		for (int i = 0; i < json.length(); i++) {
			System.out.println("get the current JSONObject");
			JSONObject jsonObject = json.getJSONObject(i);
			System.out.println("get the US_Nr form jsonObject");
			usNrM = getUs(jsonObject, "US_Nr");
			if (jsonObject.has("Main")) {
				JSONObject jsonMain = jsonObject.getJSONObject("Main");
				System.out.println("current jsonObject has Main Part. ");
				if (jsonMain.length() != 0) {
					System.out.println("jsonMain is not empy. try to process main part..");
					processMainPart(jsonMain, usNrM, cModule);

				}
			}
			if (jsonObject.has("Benefit")) {
				System.out.println("current jsonObject has Main Part. ");
//				JSONObject jsonBenefit = jsonObject.getJSONObject("Benefit");
//				if (jsonBenefit.length() != 0) {
//					System.out.println("jsonBenefit is not empy. try to process benefit part..");
//					processBenefitPart(jsonBenefit, usNrM, cModule);
//				}

			}
		}


		return cModule;
	}

	private void processBenefitPart(JSONObject jsonBenefit, String usNrM, CModule cModule) throws Exception {
		JSONArray actionMain = getArray(jsonBenefit, "Action", ActionInJsonFileNotFound::new);
		System.out.println("[processMainPart] Action from current jsonObejct readed.");

		JSONArray entityArray = getArray(jsonBenefit, "Entity", EntityInJsonFileNotFound::new);
		System.out.println("Entity from current jsonObejct readed.");

		JSONArray targetsArrayBenefit = getArray(jsonBenefit, "Targets", TargetsInJsonFileNotFound::new);
		System.out.println("Targets from current jsonObejct readed.");

		JSONArray containsArrayBenefit = getArray(jsonBenefit, "Contains", ContainsInJsonFileNotFound::new);
		System.out.println("Contains from current jsonObejct readed.");

		String textMain = getString(jsonBenefit, "Text", TextInJsonFileNotFound::new);
		System.out.println("Text from current jsonObejct readed.");

		JSONArray targetActionRules;
		JSONArray containActionRules;
		if (jsonBenefit.has("Action Rules")) {
			JSONObject jsonActionRules = jsonBenefit.getJSONObject("Action Rules");
			if (jsonActionRules.has("Target Action Rules")) {
				targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
				System.out.println("Target Action Rules from current jsonObejct readed.");
			} else {
				throw new ActionRuleInJsonFileNotFound("Target Action Rules in Aciton-Rule not found");
			}
			if (jsonActionRules.has("Contain Action Rules")) {
				containActionRules = jsonActionRules.getJSONArray("Contain Action Rules");
				System.out.println("Contains Action Rules from current jsonObejct readed.");
			} else {
				throw new ActionRuleInJsonFileNotFound("Contain Action Rules in Aciton-Rule not found");
			}
		} else {
			throw new ActionRuleInJsonFileNotFound();
		}
		System.out.println("[processMainPart]try to process Action Rules...");
		processActionRules(targetActionRules, containActionRules, usNrM, cModule, jsonBenefit, actionMain, entityArray,
				null, targetsArrayBenefit, containsArrayBenefit, textMain, null, false);

	}

	private void processMainPart(JSONObject jsonMain, String usNrM, CModule cModule) throws Exception {

		JSONArray actionMain = getArray(jsonMain, "Action", ActionInJsonFileNotFound::new);
		System.out.println("[processMainPart] Action from current jsonObejct readed.");

		JSONArray entityArray = getArray(jsonMain, "Entity", EntityInJsonFileNotFound::new);
		System.out.println("Entity from current jsonObejct readed.");

		JSONArray triggersArrayMain = getArray(jsonMain, "Triggers", TriggersInJsonFileNotFound::new);
		System.out.println("Triggers from current jsonObejct readed.");

		JSONArray targetsArrayMain = getArray(jsonMain, "Targets", TargetsInJsonFileNotFound::new);
		System.out.println("Targets from current jsonObejct readed.");

		JSONArray containsArrayMain = getArray(jsonMain, "Contains", ContainsInJsonFileNotFound::new);
		System.out.println("Contains from current jsonObejct readed.");

		JSONArray persona = getArray(jsonMain, "Persona", PersonaInJsonFileNotFound::new);
		System.out.println("Persona from current jsonObejct readed.");

		String textMain = getString(jsonMain, "Text", TextInJsonFileNotFound::new);
		System.out.println("Text from current jsonObejct readed.");

		JSONArray targetActionRules;
		JSONArray containActionRules;
		if (jsonMain.has("Action Rules")) {
			JSONObject jsonActionRules = jsonMain.getJSONObject("Action Rules");
			if (jsonActionRules.has("Target Action Rules")) {
				targetActionRules = jsonActionRules.getJSONArray("Target Action Rules");
				System.out.println("Target Action Rules from current jsonObejct readed.");
			} else {
				throw new ActionRuleInJsonFileNotFound("Target Action Rules in Aciton-Rule not found");
			}
			if (jsonActionRules.has("Contain Action Rules")) {
				containActionRules = jsonActionRules.getJSONArray("Contain Action Rules");
				System.out.println("Contains Action Rules from current jsonObejct readed.");
			} else {
				throw new ActionRuleInJsonFileNotFound("Contain Action Rules in Aciton-Rule not found");
			}
		} else {
			throw new ActionRuleInJsonFileNotFound();
		}
		System.out.println("[processMainPart]try to process Action Rules...");
		processActionRules(targetActionRules, containActionRules, usNrM, cModule, jsonMain, actionMain, entityArray,
				triggersArrayMain, targetsArrayMain, containsArrayMain, textMain, persona, true);

	}

	// handle ambiguity in action-rules and atomic rules
	private void processActionRules(JSONArray targetActionRules, JSONArray containActionRules, String usNrM,
			CModule cModule, JSONObject jsonObject, JSONArray actionArray, JSONArray entityArray,
			JSONArray triggersArray, JSONArray targetsArray, JSONArray containsArray, String text, JSONArray persona,
			boolean inMainPart) throws ActionInJsonFileNotFound, EdgeWithSameSourceAndTarget, EmptyOrNotExistJsonFile,
			EntityInJsonFileNotFound {
		CRule cRule = null;

		for (int j = 0; j < targetActionRules.length(); j++) {
			JSONArray current = targetActionRules.getJSONArray(j);
			System.out.println("[processAcitonRules] reading targetAction Rule.. " + (j + 1));
			String verb = current.getString(0).toLowerCase();
			String noun = current.getString(1).toLowerCase();
			System.out.println("targetActionRule verb is: " + verb);
			System.out.println("targetActionRule noun is: " + noun);
			String actionRule = current.getString(2).toLowerCase();
			if (actionRule.contains(";")) {
				System.out.println("found ambiguity in ActionRule.. Try to handle");
				String[] items = actionRule.split(";");
				for (String actionRuleAmbiguity : items) {
					System.out.println("proceed action rule: " + actionRuleAmbiguity);
					if (inMainPart) {
						cRule = processRule(usNrM + "_main_" + verb + "_" + actionRuleAmbiguity + "_" + noun, cModule);
						System.out.println("--------------------------------------Rule Created " + usNrM + "_main_"
								+ verb + "_" + actionRuleAmbiguity);
					} else {
						cRule = processRule(usNrM + "_benefit_" + verb + "_" + actionRuleAmbiguity + "_" + noun,
								cModule);
						System.out.println("--------------------------------------Rule Created " + usNrM + "_benefit_"
								+ verb + "_" + actionRuleAmbiguity);
					}
					System.out.println("[processAcitonRule] ");
					executeProcessActions(jsonObject, actionArray, targetActionRules, containActionRules, entityArray,
							triggersArray, targetsArray, containsArray, text, persona, cRule, usNrM, verb, noun,
							actionRuleAmbiguity, false);

				}

			} else {

				System.out.println("process atomic action Rule");
				if (inMainPart) {
					cRule = processRule(usNrM + "_main_" + verb + "_" + actionRule + "_" + noun, cModule);
					System.out
							.println("atomic rule created: " + usNrM + "_main_" + verb + "_" + actionRule + "_" + noun);
				} else {
					cRule = processRule(usNrM + "_benefit_" + verb + "_" + actionRule + "_" + noun, cModule);
					System.out.println(
							"atomic rule created: " + usNrM + "_benefit_" + verb + "_" + actionRule + "_" + noun);
				}
				executeProcessActions(jsonObject, actionArray, targetActionRules, containActionRules, entityArray,
						triggersArray, targetsArray, containsArray, text, persona, cRule, usNrM, null, null, actionRule,
						true);
			}
		}

	}

	private void executeProcessActions(JSONObject jsonObject, JSONArray actionArray, JSONArray targetActionRules,
			JSONArray containActionRules, JSONArray entityArray, JSONArray triggersArray, JSONArray targetsArray,
			JSONArray containsArray, String text, JSONArray persona, CRule cRule, String usNr, String verb, String noun,
			String actionRuleAmbiguity, boolean isActionAtomic) throws ActionInJsonFileNotFound,
			EdgeWithSameSourceAndTarget, EmptyOrNotExistJsonFile, EntityInJsonFileNotFound {

		// only applicable for main part
//		CNode personaNode = processPersona(persona, cRule);
//		if (personaNode == null) {
//			System.out.println("Persona not Found >> handle as Benefit");
//		} else {
//			System.out.println("Persona node as been created");
//		}
		// store all entities in one map which the string is the name of entities and
		// CNode correspond to their CNode Object
		Map<String, CNode> mapEntityMain = new HashMap<>();

		// store all actions in one map which the string is the name of actions and
		// CNode correspond to their CNode Object
		Map<String, CNode> mapActionMain = new HashMap<>();

		processText(cRule, text);
		if (actionRuleAmbiguity != null) {
			System.out.println("actionRuleAmbiguity  " + actionRuleAmbiguity);
		} else {
			System.out.println("proceeding atomic action rule");
		}

//		System.out.println("process Action");
//		processActions(cRule, jsonObject, actionArray, triggersArray, personaNode, mapActionMain, actionRuleAmbiguity,
//				verb, isActionAtomic);

		System.out.println("process Entity");
		processEntities(jsonObject, cRule, entityArray, containActionRules, mapEntityMain, actionRuleAmbiguity, noun,
				isActionAtomic);

//		System.out.println("process Targets");
//		processTargetsEdges(jsonObject, targetActionRules, mapEntityMain, mapActionMain, usNr, actionRuleAmbiguity,
//				verb, noun, isActionAtomic);

		System.out.println("process Contains");
		processContainsEdges(jsonObject, containActionRules, mapEntityMain, usNr, actionRuleAmbiguity, noun,
				isActionAtomic);

	}

	// return jsonArray from specific jsonObject
	private JSONArray getArray(JSONObject jsonObject, String key,
			ExceptionSupplier<? extends Exception> exceptionSupplier) throws Exception {
		if (jsonObject.has(key)) {
			return jsonObject.getJSONArray(key);
		}
		return null;
	}

	// return String from specific jsonObject
	private String getString(JSONObject jsonObject, String key,
			ExceptionSupplier<? extends Exception> exceptionSupplier) throws Exception {
		if (jsonObject.has(key)) {
			return jsonObject.getString(key);
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

//		CNode nodeText = userStory.createNode("Story");
//		text = text.replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
//		nodeText.createAttribute("text", "\"" + text + "\"");

	}

	private CNode processPersona(JSONArray persona, CRule userStory) {

		if (persona == null) {
			return null;
		}
		CNode nodePersona = userStory.createNode("Persona");
		String person = persona.getString(0).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
		nodePersona.createAttribute("name", "\"" + person + "\"");
		return nodePersona;

	}

	private void processActions(CRule userStory, JSONObject jsonObject, JSONArray actions, JSONArray triggersArray,
			CNode nodePersona, Map<String, CNode> actionMap, String actionRule, String verb, boolean atomicRule)
			throws ActionInJsonFileNotFound, EdgeWithSameSourceAndTarget, EmptyOrNotExistJsonFile {
		CNode cNode = null;
		if (atomicRule) {
			// Creating Nodes for Action/s
			for (int i = 0; i < actions.length(); i++) {
				String action = actions.getString(i).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();

				System.out.println("(1)action Rule is: " + actionRule);
				if (("preserve".equals(actionRule) || actionRule == null) && !atomicRule
						&& actionMap.get(action) == null) {
					System.out.println("[Action]: actionRule: " + actionRule + " AtomicRule is false");
					cNode = userStory.createNode("Action");
					cNode.createAttribute("name", "\"" + action + "\"");
					System.out.println("action Node " + action + " has been create!");
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

				} else if (atomicRule && actionMap.get(action) == null) {
					System.out.println("Atomic rule is TRUE" + " Action is: " + action);
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
						if (actionRule == null) {
							actionRuleCurrent = current.getString(2).toLowerCase();
							System.out.println("actionRule is null");
						} else {
							actionRuleCurrent = actionRule;
							System.out.println("actionRule is not null");
						}
						if (verbCurrent.equals(action) && actionMap.get(action) == null) {
							System.out.println("Aciton " + action + " actionRule " + actionRuleCurrent);
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
		} else {
			cNode = userStory.createNode("Action", actionRule);
			cNode.createAttribute("name", "\"" + verb + "\"", actionRule);
			System.out.println("action Node " + verb + " has been create! with action rule: " + actionRule);
			if (triggersArray != null) {
				for (int j = 0; j < triggersArray.length(); j++) {
					JSONArray trigger = triggersArray.getJSONArray(j);
					String actionTrigger = trigger.getString(1);
					if (nodePersona != null && actionTrigger.equalsIgnoreCase(verb)) {
						nodePersona.createEdge(cNode, "triggers", actionRule);
						break;
					}
				}
			}
			actionMap.put(verb, cNode);
		}

	}

	private void processEntities(JSONObject jsonObject, CRule userStory, JSONArray entities,
			JSONArray containActionRules, Map<String, CNode> entityMap, String actionRule, String noun,
			boolean atomicRule) throws EntityInJsonFileNotFound {
		// Creating Nodes for Primary Entity/s
		CNode cNode = null;

		if (atomicRule) {
			for (int i = 0; i < entities.length(); i++) {
				System.out.println("[Entity]------------------all entites are" + entities.toString());
				String entity = entities.getString(i).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
				// check if entity exist in entityMap

				if (entityMap.get(entity) == null) {
//				System.out.println("noun is:" + noun);
//				System.out.println("action rule is :" + actionRule);
					cNode = userStory.createNode("Entity", actionRule);
					cNode.createAttribute("name", "\"" + entity + "\"", actionRule);
					System.out.println("Node for Entity is Created: " + entity + " with action Rule: " + actionRule);
					entityMap.put(entity, cNode);
					for (int j = 0; j < containActionRules.length(); j++) {
						System.out.println("proceed contains in Entity");
						JSONArray current = containActionRules.getJSONArray(j);
						String noun1Current = current.getString(0).toLowerCase();
						String noun2Current = current.getString(1).toLowerCase();
						System.out.println("[ContainActionRule] noun1: " + noun1Current + " noun2: " + noun2Current
								+ " Entity is: " + entity);
						String actionRuleCurrent = actionRule;

						if (entity.equalsIgnoreCase(noun2Current) && entityMap.get(noun1Current) == null) {
							System.out.println(
									"Contains: annotate entity is " + noun1Current + " with " + actionRuleCurrent);
							cNode = userStory.createNode("Entity", actionRuleCurrent);
							cNode.createAttribute("name", "\"" + noun1Current + "\"", actionRuleCurrent);
							entityMap.put(noun1Current, cNode);

						} else if (entity.equalsIgnoreCase(noun1Current) && entityMap.get(noun2Current) == null) {
							System.out.println(
									"Contains: annotate entity is " + noun2Current + " with " + actionRuleCurrent);
							cNode = userStory.createNode("Entity", actionRuleCurrent);
							cNode.createAttribute("name", "\"" + noun2Current + "\"", actionRuleCurrent);
							entityMap.put(noun2Current, cNode);

						} else {
							System.out.println("Entity Contain Else: entity is : " + entity);
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
		} else {
			System.out.println("process multi action rule in Entity");
			cNode = userStory.createNode("Entity", actionRule);
			cNode.createAttribute("name", "\"" + noun + "\"", actionRule);
			System.out.println("Entity: " + noun + " with action: " + actionRule + " is created");
			entityMap.put(noun, cNode);
			for (int j = 0; j < containActionRules.length(); j++) {
				System.out.println("proceed contains in Entity");
				JSONArray current = containActionRules.getJSONArray(j);
				String noun1Current = current.getString(0).toLowerCase();
				String noun2Current = current.getString(1).toLowerCase();
				System.out.println("[ContainActionRule] noun1: " + noun1Current + " noun2: " + noun2Current
						+ " Entity is: " + noun);
				if (noun.equalsIgnoreCase(noun2Current) && entityMap.get(noun1Current) == null) {
					cNode = userStory.createNode("Entity", actionRule);
					cNode.createAttribute("name", "\"" + noun1Current + "\"", actionRule);
					System.out.println("Contains: create entity: " + noun1Current + " with " + actionRule);
					entityMap.put(noun1Current, cNode);

				} else if (noun.equalsIgnoreCase(noun1Current) && entityMap.get(noun2Current) == null) {
					cNode = userStory.createNode("Entity", actionRule);
					cNode.createAttribute("name", "\"" + noun2Current + "\"", actionRule);
					System.out.println("Contains: create entity: " + noun2Current + " with " + actionRule);
					entityMap.put(noun2Current, cNode);

				} else {
					System.out.println(
							"Contains: entities are not belogs to Contains: " + noun1Current + " and: " + noun2Current);
				}

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

//	It receives the JSON object to be processed, the JSON array with information 
//	about the target edges and the US identifier as parameters. The method checks
//	whether the action and entity in the target array exists in the JSON file.
//	If the action and the entity exist, an edge with the name \enquote{targets} 
//	is created between them in the Henshin files and annotated for deletion.

	// processTargetsEdges(jsonBenefit, targetsArrayBenefit, mapEntityBenefit,
	// mapActionBenefit, usNrM, null, null,false);
	private void processTargetsEdges(JSONObject jsonObject, JSONArray targetsActionRuleArray,
			Map<String, CNode> entityMap, Map<String, CNode> actionMap, String usNrM, String actionRule, String verb,
			String noun, boolean atomicRule)
			throws EntityInJsonFileNotFound, EdgeWithSameSourceAndTarget, ActionInJsonFileNotFound {
		CNode nodeEntity = null;
		CNode nodeAction = null;
		if (atomicRule) {
			for (int i = 0; i < targetsActionRuleArray.length(); i++) {
				// replace space at the end of text if any
//			String action = currentArray.getString(0).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();
//			String entity = currentArray.getString(1).replaceAll(" $", "").replaceAll("^ ", "").toLowerCase();

				JSONArray current = targetsActionRuleArray.getJSONArray(i);
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
				System.out.println("targets edge has been created for action: " + action + " and entity: " + entity
						+ " with actionRule " + actionRule);

			}
		} else {
			if ((actionMap.get(verb.toLowerCase()) != null)) {
				nodeAction = actionMap.get(verb.toLowerCase());
//			System.out.println("node Action is found and not added to map: " + action);
			} else {
				throw new ActionInJsonFileNotFound(
						"In \"Targets\" of " + usNrM + ", Action: \"" + verb.toString() + "\" is not found!");
			}
			if ((entityMap.get(noun) != null)) {

				nodeEntity = entityMap.get(noun);
			} else {

				throw new EntityInJsonFileNotFound(
						"In \"Targets\" of " + usNrM + ", Entity: \"" + noun.toString() + "\" is not found!");
			}
			nodeAction.createEdge(nodeEntity, "targets", actionRule);
			System.out.println("targets edge has been created for action: " + verb + " and entity: " + noun
					+ " with actionRule " + actionRule);

		}
	}

	private void processContainsEdges(JSONObject jsonObject, JSONArray containsActionRulesArray,
			Map<String, CNode> entityMap, String usNrM, String actionRules, String noun, boolean atomicRule)
			throws EntityInJsonFileNotFound, EdgeWithSameSourceAndTarget {

		if (atomicRule) {
			// iterate through contains JSONArray
			for (int i = 0; i < containsActionRulesArray.length(); i++) {
				JSONArray currentArray = containsActionRulesArray.getJSONArray(i);

				String firstEntity = currentArray.getString(0).toLowerCase();
				String secondEntity = currentArray.getString(1).toLowerCase();
				String actionRule;
				if (actionRules == null) {
					actionRule = currentArray.getString(2).toLowerCase();
					System.out.println("Action rule is null! get action from containArray");
				} else {
					actionRule = actionRules;
					System.out.println("Action rule is " + actionRule);
				}
				// make sure that both entity is already listed in entityMap
//			System.out.println(firstEntity +" and " + secondEntity);
				if ((entityMap.get(firstEntity) != null) && (entityMap.get(secondEntity) != null)) {
					System.out
							.println("contains entity mapping first: " + firstEntity + " -- second : " + secondEntity);
					CNode nodefirstEntity = entityMap.get(firstEntity);
					CNode nodeSecondEntity = entityMap.get(secondEntity);
					nodefirstEntity.createEdge(nodeSecondEntity, "contains", actionRule);
				} else {
					throw new EntityInJsonFileNotFound("In " + usNrM + ", following entties are missing: "
							+ firstEntity.toString() + " and " + secondEntity.toString());
				}
			}
		} else {
			System.out.println("Contains in mulit action-rule already proceed!");
		}

	}

}
