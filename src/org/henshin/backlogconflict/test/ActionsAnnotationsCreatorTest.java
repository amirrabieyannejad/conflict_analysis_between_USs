package org.henshin.backlogconflict.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.henshin.backlogconflict.code.preparation.EmptyOrNotExistJsonFile;

import org.henshin.backlogconflict.code.preparation.ReadJsonFile;
import org.henshin.backlogconflict.code.preparation.ActionAnnotationNotFound;
import org.henshin.backlogconflict.code.preparation.ActionsAnnotationsCreator;
import org.json.JSONArray;

import org.json.JSONObject;

import org.junit.Test;

import com.opencsv.exceptions.CsvValidationException;

public class ActionsAnnotationsCreatorTest {
	@Test
	public void testAddActionsAnnotations() throws EmptyOrNotExistJsonFile, CsvValidationException, IOException, ActionAnnotationNotFound {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\ActionsAnnotationsCreator\\" + "g03_loudoun_healthy";
		String actionsAnnotationsFile = filePath + "\\actions_annotations.csv";
		ActionsAnnotationsCreator actionsAnnotationsCreator = new ActionsAnnotationsCreator();
		actionsAnnotationsCreator.addActionsAnnotations(dataSets, filePath, actionsAnnotationsFile);
		String outputFilePath = filePath + "\\" + dataSets[0] + ".json";
		ReadJsonFile file = new ReadJsonFile();
		JSONArray jsonArray = file.readJsonArrayFromFile(outputFilePath);

		JSONObject jsonObject = jsonArray.getJSONObject(0);
		JSONObject main = jsonObject.getJSONObject("Main");
		JSONObject jsonAcitonAnnotation = main.getJSONObject("Action Annotations");
		String jsonActionAnnotation = jsonAcitonAnnotation.getString("Target Action Annotations");
		String targetActionAnnotation = "create";
		assertTrue(jsonActionAnnotation.contains(targetActionAnnotation));
	}
}
