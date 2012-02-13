package org.tolweb.btol.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.dao.SpecimenDAO;
import org.tolweb.btol.dao.SpecimenExtractionDAO;
import org.tolweb.treegrow.main.StringUtils;

public class MassExtractionParser extends BaseMassParser {
	private SpecimenExtractionDAO specimenExtractionDAO;
	private SpecimenDAO specimenDAO;

	/**
	 * parses the string that was previously set with
	 * setStringToParse
	 * @param projectId 
	 * @param contr
	 * @return
	 * @throws MassParseException
	 */
	public List<SpecimenExtraction> getExtractions(SourceCollection collection, List<String> existingNames, Long projectId) throws MassParseException {
		//String tabString = getTabRegexString();
		String regexString = getTabSeparatedRegexString(6, 2);		

		// in case the upload has codes of the form BT0001 instead of 0001, ignore the code
		Pattern codePattern = Pattern.compile("\\s*[^\\d]+(\\d+)\\s*");		
		Pattern extractionPattern = Pattern.compile(regexString);
		List<SpecimenExtraction> extractions = new ArrayList<SpecimenExtraction>();
		while (newlineTokenizer.hasMoreTokens()) {
			String nextLine = newlineTokenizer.nextToken();
			Matcher extractionMatcher = extractionPattern.matcher(nextLine);
			if (!extractionMatcher.find()) {
				setErrorMessage("Input does not match the extraction format.  Please consult the example file for more information.");
			}

			String code = extractionMatcher.group(1);
			if (code.equalsIgnoreCase("Code")) {
				// in this case it's the first line of the template, so just ignore
				continue;
			}
			Matcher codeMatcher = codePattern.matcher(code);
			if (codeMatcher.find()) {
				// chop off the non-numeric portion of the code
				code = codeMatcher.group(1);
			}
			// check to see if there is an existing extraction with the code
			SpecimenExtraction existingExtraction = getSpecimenExtractionDAO().getExtractionWithCode(code, collection, projectId);
			if (existingExtraction != null) {
				// already exists, so don't save
				existingNames.add(code);
				continue;
			}			
			int groupCount = extractionMatcher.groupCount();
			SpecimenExtraction extraction = new SpecimenExtraction();
			extraction.setSourceCollection(collection);			
			extraction.setCode(code);
			String specimenId = extractionMatcher.group(2);
			if (StringUtils.notEmpty(specimenId)) {
				try {
					Specimen specimen = getSpecimenDAO().getSpecimenWithId(Long.valueOf(specimenId));
					if (specimen == null) {
						throw new IllegalArgumentException();
					} else {
						extraction.setSpecimen(specimen);
					}
				} catch (IllegalArgumentException e) {
					setErrorMessage("There is no specimen with id: " + specimenId + ".  Specimens must exist in the database before uploading extractions.");
				}
			} else {
				setRequiredFieldErrorMessage("Specimen ID", nextLine);
			}
			String date = extractionMatcher.group(3);
			if (StringUtils.notEmpty(date)) {
				parseAndSetDates(extraction, date);
			} else {
				setRequiredFieldErrorMessage("Extraction Date", nextLine);
			}
			setRequiredField(extractionMatcher, extraction, 4, "Who Extracted", "person", nextLine);
			extraction.setMethod(extractionMatcher.group(5));
			setRequiredField(extractionMatcher, extraction, 6, "Extraction Target", "target", nextLine);
			if (groupCount > 6) {
				extraction.setDnaCollections(extractionMatcher.group(7));
			}
			if (groupCount > 7) {
				extraction.setNotes(extractionMatcher.group(8));
			}
			extractions.add(extraction);
		}
		return extractions;
	}

	public SpecimenExtractionDAO getSpecimenExtractionDAO() {
		return specimenExtractionDAO;
	}

	public void setSpecimenExtractionDAO(SpecimenExtractionDAO extractionDAO) {
		this.specimenExtractionDAO = extractionDAO;
	}

	public SpecimenDAO getSpecimenDAO() {
		return specimenDAO;
	}

	public void setSpecimenDAO(SpecimenDAO specimenDAO) {
		this.specimenDAO = specimenDAO;
	}
}
