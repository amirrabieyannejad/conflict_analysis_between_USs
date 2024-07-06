package org.henshin.backlogconflict.code.preparation;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONTokener;

public class ReadJsonFile {
	

	public ReadJsonFile() {
		
	}
	// This method receives a JSON file as input and reads the JSON file,
	// tokenises the JSON content and parses the JSON content into a JSON
	// array and returns the parsed JSON array.
	public JSONArray readJsonArrayFromFile(String path) throws EmptyOrNotExistJsonFile, FileNotFoundException {
		JSONArray jsonArray;
		// System.out.println("getJsonFileAbsolutePath(): " +
		// getJsonFileAbsolutePath());
		FileReader reader = new FileReader(path);

		JSONTokener tokener = new JSONTokener(reader);
		if (!tokener.more()) {
			throw new EmptyOrNotExistJsonFile();

		}
		// Read JSON file
		jsonArray = new JSONArray(tokener);

		return jsonArray;

	}
}
