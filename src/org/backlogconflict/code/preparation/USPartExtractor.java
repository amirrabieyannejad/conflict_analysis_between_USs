package org.backlogconflict.code.preparation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class USPartExtractor {

//	public static void main(String[] args) {
////		String[] dataSets = { "g03_loudoun", "g04_recycling", "g05_open_spending", "g08_frictionless",
////				"g10_scrum_alliance", "g11_nsf", "g12_camperplus", "g14_datahub", "g16_mis", "g18_neurohub",
////				"g19_alfred", "g21_badcamp", "g22_rdadmp", "g23_archives_space", "g24_unibath", "g25_duraspace",
////				"g26_racdam", "g27_culrepo", "g28_zooniverse" };
////		
////		String filePath = "00_annotated_datasets\\";
////		jsonTransformer(dataSets,filePath);
//	}

	public static void runUSPartExtractor(String[] dataSets, String filePath)
			throws JSONException, IOException, EntityInRelationsNotFound {
		for (int j = 0; j < dataSets.length; j++) {
			long startTime = System.nanoTime();
			System.out.println("[USPartExtractor]running dataset: " + dataSets[j]);
			// TODO: Uncomment this for test
//			String inputFilePath = filePath + "\\admin.jsonl";
//			String outputFilePath = filePath + "\\" + dataSets[j] + ".json";
			
			// TODO: Uncomment this for datasets
			String inputFilePath = filePath + dataSets[j] + "\\admin.jsonl";
			String outputFilePath = filePath + dataSets[j] + "\\" + dataSets[j] + ".json";

			int i = 1;

			List<String> jsonLines = Files.readAllLines(Paths.get(inputFilePath));
			JSONArray outputArray = new JSONArray();

			for (String jsonString : jsonLines) {

				JSONObject input = new JSONObject(jsonString);
				JSONObject transformed = transformJson(input, i, dataSets[j]);
				outputArray.put(transformed);
				i++;
			}

			Files.write(Paths.get(outputFilePath), outputArray.toString(4).getBytes());
			long endTime = System.nanoTime();
			double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
			System.out.println("Processing time: " + elapsedTimeInSeconds + " seconds");
		}
	}

	private static JSONObject transformJson(JSONObject input, int identifier, String dataSets)
			throws EntityInRelationsNotFound {

		// Body
		JSONObject result = new JSONObject();
//		JSONObject pId = new JSONObject();
//		JSONObject usNr = new JSONObject();
//		JSONObject usText = new JSONObject();
		JSONArray entities = input.getJSONArray("entities");
		JSONArray relations = input.getJSONArray("relations");
		int usId = input.getInt("id");

		Map<Integer, JSONObject> mapReference = new HashMap<>();
		// Main
		JSONObject main = new JSONObject();
		// JSONObject mainText = new JSONObject();
		JSONArray mainActionArray = new JSONArray();
		JSONArray mainEntityArray = new JSONArray();

		JSONArray personArray = new JSONArray();

		JSONArray mainTargetsArray = new JSONArray();

		JSONArray mainContainsArray = new JSONArray();

		// Benefit
		JSONObject benefit = new JSONObject();
		JSONArray benefitTargetsArray = new JSONArray();
		JSONArray benefitContainsArray = new JSONArray();

		JSONArray triggersArray = new JSONArray();
		JSONArray benefitActionArray = new JSONArray();
		JSONArray benefitEntityArray = new JSONArray();

		// Mix
		JSONObject mix = new JSONObject();

		// Add text
		String usText = input.getString("text");

		result.put("Text", usText);

		// Add user story identifier
		String usIdentifier = String.format("user_story_%02d", identifier);
		String usIdentifierDefault = String.format("user_story_%02d", usId);
		result.put("US_Nr", usIdentifier);
		result.put("US_ID", usIdentifierDefault);
		int benefitStartOffset = -1;
		int benefitEndOffset = -1;

		// determine the offset for the "Benefit" section
		for (int i = 0; i < entities.length(); i++) {
			JSONObject entity = entities.getJSONObject(i);
			if (entity.get("label").equals("Benefit")) {
				benefitStartOffset = entity.getInt("start_offset");
				benefitEndOffset = entity.getInt("end_offset");
				break;
			}

		}

		// Split entities
		for (int i = 0; i < entities.length(); i++) {
			JSONObject entity = entities.getJSONObject(i);
			int startOffset = entity.getInt("start_offset");
			int endOffset = entity.getInt("end_offset");
			String value = getStringFromOffset(usText, startOffset, endOffset);
			int id = entity.getInt("id");
			entity.put("name", value);

			mapReference.put(id, entity);

			switch (entity.getString("label")) {
			case "PID":
				result.put("PID", value);
				break;
			case "Benefit":
				benefit.put("Text", value);

				String mainText = usText.substring(0, startOffset - 1);
				mainText = mainText.replace("so that", "");
				mainText = mainText.replace("So that", "");
				main.put("Text", mainText);

				break;
			case "Persona":
				personArray.put(value);
				break;
			case "Entity":
				if (startOffset >= benefitStartOffset && endOffset <= benefitEndOffset) {
					benefitEntityArray.put(value);
				} else if (endOffset < benefitEndOffset || benefitEndOffset == -1) {
					mainEntityArray.put(value);
				}
//				else {
//					System.out.println(
//							"Error: dataset: " + dataSets + " - US: " + usIdentifier + "  Entity not found: " + value);
//				}
				break;
			case "Action":
				if (startOffset >= benefitStartOffset && endOffset <= benefitEndOffset) {
					benefitActionArray.put(value);
				} else if (endOffset < benefitEndOffset || benefitEndOffset == -1) {
					mainActionArray.put(value);
				}
//				else {
//					System.out.println(
//							"Error: dataset: " + dataSets + " - US: " + usIdentifier + " Action not found: " + value);
//				}
				break;
//			default:
//				System.out.println("Unknown label: " + entity.getString("label"));
//				break;
			}
			// make sure that benefit part is not exist, if so, whole user story belongs
			// consist of main part
			if (!main.has("Text") && !usText.contains("so that")) {
				main.put("Text", usText);
			}
		}
		// PROCEED RELATIONS
		for (int i = 0; i < relations.length(); i++) {
			JSONObject relation = relations.getJSONObject(i);
			int fromId = relation.getInt("from_id");
			int toId = relation.getInt("to_id");
			String type = relation.getString("type");
//			System.out.println("type is: " + type);
			JSONObject jsonFromEntry = mapReference.get(fromId);
			JSONObject jsonToEntry = mapReference.get(toId);
			String nameFromEntry;
			String nameToEntry;
			if (jsonFromEntry != null) {

				nameFromEntry = jsonFromEntry.getString("name");
			} else {
				throw new NullPointerException();
			}
			if (jsonToEntry != null) {
				nameToEntry = jsonToEntry.getString("name");
			} else {
				throw new NullPointerException();
			}
//			System.out.println("From: " + nameFromEntry);

//			System.out.println("To: " + nameToEntry);
			int startOffsetFromEntry = jsonFromEntry.getInt("start_offset");
			int endOffsetFromEntry = jsonFromEntry.getInt("start_offset");
			int startOffsetToEntry = jsonToEntry.getInt("start_offset");
			int endOffsetToEntry = jsonToEntry.getInt("start_offset");
			switch (type) {
			case "triggers": {
				JSONArray triggerPair = new JSONArray();
				triggerPair.put(nameFromEntry);
				triggerPair.put(nameToEntry);
				triggersArray.put(triggerPair);
				break;
			}
			case "targets": {
				JSONArray targetPair = new JSONArray();
				targetPair.put(nameFromEntry);
				targetPair.put(nameToEntry);
				if ((startOffsetFromEntry >= benefitStartOffset && endOffsetFromEntry <= benefitEndOffset)
						&& (startOffsetToEntry >= benefitStartOffset && endOffsetToEntry <= benefitEndOffset)) {
					benefitTargetsArray.put(targetPair);
				} else if (((endOffsetFromEntry < benefitStartOffset) && ((endOffsetToEntry < benefitStartOffset)))
						|| benefitStartOffset == -1) {
					mainTargetsArray.put(targetPair);

				} else {
					// Otherwise add all mixed relations into benefit
					mix.put("Targets", targetPair);
					// benefitTargetsArray.put(targetPair);
				}

				break;
			}
			case "contains": {
				JSONArray containPair = new JSONArray();
				containPair.put(nameFromEntry);
				containPair.put(nameToEntry);
				if ((startOffsetFromEntry >= benefitStartOffset && endOffsetFromEntry <= benefitEndOffset)
						&& (startOffsetToEntry >= benefitStartOffset && endOffsetToEntry <= benefitEndOffset)) {
					benefitContainsArray.put(containPair);
				} else if (((endOffsetFromEntry < benefitStartOffset) && ((endOffsetToEntry < benefitStartOffset))
						|| benefitStartOffset == -1)) {
					mainContainsArray.put(containPair);

				} else {
					// Otherwise add all mixed relations into benefit
					mix.put("Contains", containPair);
					// benefitContainsArray.put(containPair);
				}
				break;
			}
			}
		}

		main.put("Persona", personArray);
		main.put("Entity", mainEntityArray);
		main.put("Action", mainActionArray);
		main.put("Triggers", triggersArray);
		main.put("Targets", mainTargetsArray);
		main.put("Contains", mainContainsArray);

		benefit.put("Entity", benefitEntityArray);
		benefit.put("Action", benefitActionArray);
		benefit.put("Targets", benefitTargetsArray);
		benefit.put("Contains", benefitContainsArray);

		result.put("Main", main);
		result.put("Benefit", benefit);
		result.put("Mix", mix);
		return result;
	}

	// receive an start and end offset and return the string from main text
	private static String getStringFromOffset(String text, int startOffset, int endOffset) {
		return text.substring(startOffset, endOffset);
	}

}
