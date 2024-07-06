package org.henshin.backlogconflict.code.preparation;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class VerbFinder {
	String actionsRulesPath;
	private Map<String, String> verbMap;

	public VerbFinder(String csvFilePath) throws IOException, CsvValidationException {

		this.verbMap = new HashMap<>();
//        System.out.println("Initializing VerbFinder with CSV file path: " + csvFilePath);
		loadCSV(csvFilePath);
//        System.out.println("VerbFinder initialization complete. Loaded " + verbMap.size() + " entries.");
	}

	private void loadCSV(String csvFilePath) throws IOException, CsvValidationException {
//		System.out.println("loadCSV");
		try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
				.withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length >= 2) {
					String verb = nextLine[0];
//					System.out.println("loadCSV- verb: " + verb);
					String actionRule = nextLine[1];
//					System.out.println("loadCSV- action_rule: " + actionRule);
					verbMap.put(verb.toLowerCase(), actionRule);
				}
			}
		}

	}

	// receive the corresponding rule action of verb
	public String getActionAnnotations(String verb) {
		return verbMap.get(verb);

	}

}
