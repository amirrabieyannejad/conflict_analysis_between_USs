package org.backlogconflict.test;


import java.io.IOException;

import java.nio.file.NoSuchFileException;

import org.backlogconflict.code.preparation.EmptyOrNotExistJsonFile;
import org.backlogconflict.code.preparation.EntityInRelationsNotFound;
import org.backlogconflict.code.preparation.USPartExtractor;
import org.backlogconflict.code.preparation.ReadJsonFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONTransformerTest {

	// check if PID already has been founded and correctly placed into transformed
	// json
	@Test
	public void testPid() throws EmptyOrNotExistJsonFile, JSONException, IOException, EntityInRelationsNotFound {
		String[] dataSets = { "g03_loudoun" };
		
		String filePath = "Tests\\JSONTransformer\\" + "g03_loudoun_healthy";

		USPartExtractor.runUSPartExtractor(dataSets, filePath);
		String outputFilePath = filePath +"\\" + dataSets[0] + ".json";
		ReadJsonFile file = new ReadJsonFile();
		JSONArray jsonArray = file.readJsonArrayFromFile(outputFilePath);
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		String pId = jsonObject.getString("PID");
		assertTrue(pId.equals("#G03#"));

	}

	// check what happen if benefit are not exist
	@Test
	public void testBenefitNotExist()
			throws EmptyOrNotExistJsonFile, JSONException, IOException, EntityInRelationsNotFound {
		String[] dataSets = { "g04_recycling" };
		String filePath = "Tests\\JSONTransformer\\" + dataSets[0];
		USPartExtractor.runUSPartExtractor(dataSets, filePath);
		String outputFilePath = filePath + "\\" + dataSets[0] + ".json";
		ReadJsonFile file = new ReadJsonFile();
		JSONArray jsonArray = file.readJsonArrayFromFile(outputFilePath);
		JSONObject jsonObject = jsonArray.getJSONObject(19);
		JSONObject benefit = jsonObject.getJSONObject("Benefit");
		JSONArray benefitEntity = benefit.getJSONArray("Entity");
		JSONArray benefitAction = benefit.getJSONArray("Action");
		JSONArray benefitContains = benefit.getJSONArray("Contains");
		JSONArray benefitTargets = benefit.getJSONArray("Targets");
		assertTrue(benefitEntity.length() == 0 && benefitAction.length() == 0 && benefitContains.length() == 0
				&& benefitTargets.length() == 0);

	}

	// check what happen if unexpected type has been occurred, as input a wrong type
	// is given
	@Test(expected = JSONException.class)
	public void testUnexpectedType()
			throws EmptyOrNotExistJsonFile, JSONException, IOException, EntityInRelationsNotFound {
		String[] dataSets = { "g03_loudoun" };
		String filePath = "Tests\\JSONTransformer\\" + dataSets[0] + "\\";
		USPartExtractor.runUSPartExtractor(dataSets, filePath);

	}

	// In a case that file not found
	@Test(expected = NoSuchFileException.class)
	public void testFileNotFound()
			throws EmptyOrNotExistJsonFile, JSONException, IOException, EntityInRelationsNotFound {
//		String[] dataSets = { "g03_loudoun_Access_Denided" };
		String[] dataSets = { "Dummy" };
		String filePath = "Dummy\\";
//		String filePath = "Tests\\JSONTransformer\\";
		USPartExtractor.runUSPartExtractor(dataSets, filePath);

	}

	// check what happen if entity are not found in relations
	// one is null
	@Test(expected = NullPointerException.class)
	public void testEntityInRelationNotFound_Case1()
			throws EmptyOrNotExistJsonFile, JSONException, IOException, EntityInRelationsNotFound {
		String[] dataSets = { "g03_loudoun" };

		String filePath = "Tests\\JSONTransformer\\" + "g03_loudoun_Entity_in_Relations_Not_Found_Case1\\";
		USPartExtractor.runUSPartExtractor(dataSets, filePath);

	}

	// check what happen if entity are not found in relations
	// another is null
	@Test(expected = NullPointerException.class)
	public void testEntityInRelationNotFound_Case2()
			throws EmptyOrNotExistJsonFile, JSONException, IOException, EntityInRelationsNotFound {
		String[] dataSets = { "g03_loudoun" };

		String filePath = "Tests\\JSONTransformer\\" + "g03_loudoun_Entity_in_Relations_Not_Found_Case2\\";
		USPartExtractor.runUSPartExtractor(dataSets, filePath);

	}

}
