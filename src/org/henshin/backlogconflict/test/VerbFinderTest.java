package org.henshin.backlogconflict.test;
import java.io.IOException;


import org.henshin.backlogconflict.code.preparation.VerbFinder;

import org.junit.Test;

import com.opencsv.exceptions.CsvValidationException;

import static org.junit.Assert.*;
public class VerbFinderTest {

	@Test
	public void testLoadCSV_actionAnnotation_Case1() throws IOException, CsvValidationException{
		String path = "Tests\\VerbFinder\\actions_annotations.csv";
		VerbFinder verbFinder = new VerbFinder(path);	
		assertTrue(verbFinder.getActionAnnotations("check").contains("preserve"));
	}
	@Test
	public void testLoadCSV_actionAnnotation_Case2() throws IOException, CsvValidationException{
		String path = "Tests\\VerbFinder\\actions_annotations_less_column.csv";
		VerbFinder verbFinder = new VerbFinder(path);	
		assertTrue(verbFinder.getActionAnnotations("check")== null);
	}

}
