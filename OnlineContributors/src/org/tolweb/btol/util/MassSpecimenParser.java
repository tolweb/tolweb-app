package org.tolweb.btol.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.btol.Specimen;


public class MassSpecimenParser extends BaseMassParser {
	public List<Specimen> getSpecimens() throws MassParseException {
		String regexString = getTabSeparatedRegexString(28, 1);
		Pattern specimenPattern = Pattern.compile(regexString);
		List<Specimen> specimens = new ArrayList<Specimen>();
		while (newlineTokenizer.hasMoreTokens()) {
			String nextLine = newlineTokenizer.nextToken();
			Matcher specimenMatcher = specimenPattern.matcher(nextLine);
			if (!specimenMatcher.find()) {
				setErrorMessage("Input does not match the specimen format.  Please consult the example file for more information.");
			}
			String code = specimenMatcher.group(1);
			if (code.equalsIgnoreCase("Family")) {
				// in this case it's the first line of the template, so just ignore
				continue;
			}			
			int groupCount = specimenMatcher.groupCount();			
			Specimen specimen = new Specimen();
			int groupNum = 1;
			specimen.setFamily(specimenMatcher.group(groupNum++));
			specimen.setSubfamily(specimenMatcher.group(groupNum++));
			specimen.setTribe(specimenMatcher.group(groupNum++));
			specimen.setGenus(specimenMatcher.group(groupNum++));
			specimen.setSpecies(specimenMatcher.group(groupNum++));
			specimen.setIdentificationStatus(specimenMatcher.group(groupNum++));
			specimen.setIdentificationNotes(specimenMatcher.group(groupNum++));
			// don't choke on a bogus node id
			try {
				specimen.setNodeId(Long.valueOf(specimenMatcher.group(groupNum++)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			setRequiredField(specimenMatcher, specimen, groupNum++, "Collection data source", "collectionDataSource", nextLine);
			setRequiredField(specimenMatcher, specimen, groupNum++, "Collection data source ID", "collectionDataSourceId", nextLine);
			parseAndSetDates(specimen, specimenMatcher.group(groupNum++));
			specimen.setCollectionTime(specimenMatcher.group(groupNum++));
			setRequiredField(specimenMatcher, specimen, groupNum++, "Country", "country", nextLine);
			setRequiredField(specimenMatcher, specimen, groupNum++, "First administrative division", "adminDivision1", nextLine);
			specimen.setAdminDivision2(specimenMatcher.group(groupNum++));
			setRequiredField(specimenMatcher, specimen, groupNum++, "General location", "location1", nextLine);
			specimen.setLocation2(specimenMatcher.group(groupNum++));
			specimen.setLatitude(specimenMatcher.group(groupNum++));
			specimen.setLongitude(specimenMatcher.group(groupNum++));
			specimen.setElevation(specimenMatcher.group(groupNum++));
			specimen.setHabitat(specimenMatcher.group(groupNum++));
			specimen.setCollectionMethod(specimenMatcher.group(groupNum++));
			specimen.setPreservationMethod(specimenMatcher.group(groupNum++));
			specimen.setCurrentCondition(specimenMatcher.group(groupNum++));
			setRequiredField(specimenMatcher, specimen, groupNum++, "Life stage", "lifeStage", nextLine);
			setRequiredField(specimenMatcher, specimen, groupNum++, "Sex", "sex", nextLine);
			specimen.setCollector(specimenMatcher.group(groupNum++));
			specimen.setCredit(specimenMatcher.group(groupNum++));
			setRequiredField(specimenMatcher, specimen, groupNum++, "Collection", "collection", nextLine);
			// last one is optional so check before setting
			if (groupCount > groupNum) {
				specimen.setNotes(specimenMatcher.group(groupNum++));
			}
			specimens.add(specimen);
		}
		return specimens;
	}
}
