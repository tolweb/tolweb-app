package org.tolweb.content.helpers;

import java.util.HashMap;
import java.util.Map;

import org.tolweb.hibernate.MappedTextSection;

public class TdwgSubjectDeterminer {
	public static final String GENERAL_DESC = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#GeneralDescription";
	public static final String EVOLUTION = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Evolution";
	public static final String DISTRIBUTION = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Distribution";
	public static final String DIAGNOSTIC_DESC = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#DiagnosticDescription";
	public static final String HABITAT = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Habitat";
	public static final String LIFECYCLE = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#LifeCycle";
	public static final String CONSERVATION = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Conservation";
	public static final String CONSERV_STATUS = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#ConservationStatus";
	public static final String BEHAVIOR = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Behaviour";
	public static final String BIOLOGY = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Biology";
	public static final String DESCRIPTION = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Description";
	public static final String ECOLOGY = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Ecology";
	public static final String KEY = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Key";
	public static final String MORPHOLOGY = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#Morphology";
	public static final String TAXON_BIO = "http://rs.tdwg.org/ontology/voc/SPMInfoItems#TaxonBiology";
	
	public static final String KEY_PREFIX = "key to";
	
	public static final Map<String, String> SUBJECT_MAP = new HashMap<String, String>();
	
	static {
		/* 	"introduction": http://rs.tdwg.org/ontology/voc/SPMInfoItems#GeneralDescription */
		/* EOL Requested Change: May 2, 2010 Guilty-Party: lenards
		/* Katja asked that General Description (GENERAL_DESC) be changed to TAXON_BIO */
		SUBJECT_MAP.put("introduction", TAXON_BIO);
		/* 	"Discussion of Phylogenetic Relationships", 
			"Phylogenetic Notes",
			"Phylogenetics": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Evolution */
		SUBJECT_MAP.put("discussion of phylogenetic relationships", EVOLUTION);
		SUBJECT_MAP.put("phylogenetic notes", EVOLUTION);
		SUBJECT_MAP.put("phylogenetics", EVOLUTION);
		/*	"Distribution",
			"Geographic Distribution",
			"Geographical Distribution",
			"Type Locality",
			"Range", "Distribution and Range",
			"Distribution Map": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Distribution */
		SUBJECT_MAP.put("distribution", DISTRIBUTION);
		SUBJECT_MAP.put("geographic distribution", DISTRIBUTION);
		SUBJECT_MAP.put("geographical distribution", DISTRIBUTION);
		SUBJECT_MAP.put("type locality", DISTRIBUTION);
		SUBJECT_MAP.put("range", DISTRIBUTION);
		SUBJECT_MAP.put("distribution and range", DISTRIBUTION);
		SUBJECT_MAP.put("distribution map", DISTRIBUTION);
		/*   "Diagnosis": http://rs.tdwg.org/ontology/voc/SPMInfoItems#DiagnosticDescription */ 
		SUBJECT_MAP.put("diagnosis", DIAGNOSTIC_DESC);
		/* 	"habitat",
		 * 	"habitat and distribution": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Habitat */
		SUBJECT_MAP.put("habitat", HABITAT);
		SUBJECT_MAP.put("habitat and distribution", HABITAT);
		/* 	"Life History", "Host, Oviposition, and Larval Feeding Habits", "Reproduction and Early Life History", 
		 * 	"Host Relationships and Larval Biology": http://rs.tdwg.org/ontology/voc/SPMInfoItems#LifeCycle  */
		SUBJECT_MAP.put("life history", LIFECYCLE); 
		SUBJECT_MAP.put("host, oviposition, and larval feeding habits", LIFECYCLE); 
		SUBJECT_MAP.put("reproduction and early life history", LIFECYCLE);
		SUBJECT_MAP.put("host relationships and larval biology", LIFECYCLE);
		/* "Conservation": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Conservation */
		SUBJECT_MAP.put("conservation", CONSERVATION);
		/* "Conservation Status": http://rs.tdwg.org/ontology/voc/SPMInfoItems#ConservationStatus */
		SUBJECT_MAP.put("conservation status", CONSERV_STATUS);
		/*  "Behavior", "Courtship Behavior": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Behaviour */
		SUBJECT_MAP.put("behavior", BEHAVIOR); 
		SUBJECT_MAP.put("courtship behavior", BEHAVIOR);
		/*  "Bionomics", "Habits": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Biology */
		SUBJECT_MAP.put("bionomics", BIOLOGY); 
		SUBJECT_MAP.put("habits", BIOLOGY);
		/* "Characteristics": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Description */
		SUBJECT_MAP.put("characteristics", DESCRIPTION);
		/* "Ecology", "Ecological Notes": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Ecology */
		SUBJECT_MAP.put("ecology", ECOLOGY);
		SUBJECT_MAP.put("ecological notes", ECOLOGY);
		/* "Key to .*": http://rs.tdwg.org/ontology/voc/SPMInfoItems#Key */
		SUBJECT_MAP.put("key to", KEY); 
		/*  "Male Genitalia",
		 * 	"Adult External Characteristics", 
		 * 	"Adult Characteristics", 
		 * 	"List of synapomorphies", 
		 * 	"Female Characters", "Male Characters" 
		 * 	"Generic Portrait" : http://rs.tdwg.org/ontology/voc/SPMInfoItems#Morphology */ 
		SUBJECT_MAP.put("male genitalia", MORPHOLOGY);
		SUBJECT_MAP.put("adult external characteristics", MORPHOLOGY);
		SUBJECT_MAP.put("adult characteristics", MORPHOLOGY);
		SUBJECT_MAP.put("list of synapomorphies", MORPHOLOGY);
		SUBJECT_MAP.put("female characters", MORPHOLOGY);
		SUBJECT_MAP.put("male characters", MORPHOLOGY);
		SUBJECT_MAP.put("generic portrait", MORPHOLOGY);  		
	}

	private String tdwgUri;

	public TdwgSubjectDeterminer(MappedTextSection mtxt) {
		this(mtxt.getHeading());
	}
	
	public TdwgSubjectDeterminer(String sectionHeading) {
		sectionHeading = sectionHeading.trim();
		sectionHeading = sectionHeading.toLowerCase();
		if (sectionHeading.startsWith(KEY_PREFIX)) {
			tdwgUri = KEY;
		} else if (SUBJECT_MAP.containsKey(sectionHeading)) {
			tdwgUri = SUBJECT_MAP.get(sectionHeading);
		} else {
			tdwgUri = GENERAL_DESC;
		}
	}
	
	/**
	 * @return the twdgUri
	 */
	public String getTdwgUri() {
		return tdwgUri;
	}

	/**
	 * @param twdgUri the twdgUri to set
	 */
	public void setTdwgUri(String twdgUri) {
		this.tdwgUri = twdgUri;
	}
}
