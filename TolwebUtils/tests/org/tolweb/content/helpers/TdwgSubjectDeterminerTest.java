package org.tolweb.content.helpers;

import org.tolweb.hibernate.MappedTextSection;

import junit.framework.TestCase;

public class TdwgSubjectDeterminerTest extends TestCase {
	public void test_subjects_mapped_proper_like() {
		TdwgSubjectDeterminer tsd = new TdwgSubjectDeterminer("introduction");
		assertEquals(TdwgSubjectDeterminer.GENERAL_DESC, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Discussion of Phylogenetic Relationships");
		assertEquals(TdwgSubjectDeterminer.EVOLUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Phylogenetic Notes");
		assertEquals(TdwgSubjectDeterminer.EVOLUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Phylogenetics");
		assertEquals(TdwgSubjectDeterminer.EVOLUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Distribution");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Geographic Distribution");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Geographical Distribution");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Type Locality");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Range");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Distribution and Range");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Distribution Map");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		assertTrue("Key to the city".toLowerCase().startsWith("key to"));
		
		tsd = new TdwgSubjectDeterminer("Key to geographic locations");
		assertEquals(TdwgSubjectDeterminer.KEY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Key to possible phylogenies");
		assertEquals(TdwgSubjectDeterminer.KEY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Key to the city");
		assertEquals(TdwgSubjectDeterminer.KEY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Ecology");
		assertEquals(TdwgSubjectDeterminer.ECOLOGY, tsd.getTdwgUri());
		tsd = new TdwgSubjectDeterminer("Ecological Notes");
		assertEquals(TdwgSubjectDeterminer.ECOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Characteristics");
		assertEquals(TdwgSubjectDeterminer.DESCRIPTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Diagnosis");
		assertEquals(TdwgSubjectDeterminer.DIAGNOSTIC_DESC, tsd.getTdwgUri());
		
		MappedTextSection mtxt = new MappedTextSection();
		mtxt.setHeading("Habitat");
		tsd = new TdwgSubjectDeterminer(mtxt);
		assertEquals(TdwgSubjectDeterminer.HABITAT, tsd.getTdwgUri());
		
		mtxt.setHeading("Habitat and distribution");
		tsd = new TdwgSubjectDeterminer(mtxt);
		assertEquals(TdwgSubjectDeterminer.HABITAT, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Bionomics");
		assertEquals(TdwgSubjectDeterminer.BIOLOGY, tsd.getTdwgUri());
		tsd = new TdwgSubjectDeterminer("Habits");
		assertEquals(TdwgSubjectDeterminer.BIOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Behavior");
		assertEquals(TdwgSubjectDeterminer.BEHAVIOR, tsd.getTdwgUri());
		tsd = new TdwgSubjectDeterminer("Behaviour");
		assertEquals(TdwgSubjectDeterminer.GENERAL_DESC, tsd.getTdwgUri());
		tsd = new TdwgSubjectDeterminer("Courtship Behavior");
		assertEquals(TdwgSubjectDeterminer.BEHAVIOR, tsd.getTdwgUri());
		tsd = new TdwgSubjectDeterminer("Courtship Behaviour");
		assertEquals(TdwgSubjectDeterminer.GENERAL_DESC, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Conservation");
		assertEquals(TdwgSubjectDeterminer.CONSERVATION, tsd.getTdwgUri());
		tsd = new TdwgSubjectDeterminer("Conservation Status");
		assertEquals(TdwgSubjectDeterminer.CONSERV_STATUS, tsd.getTdwgUri());

		tsd = new TdwgSubjectDeterminer("Distribution");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Geographic Distribution");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Geographical Distribution");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Type Locality");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Range");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Distribution and Range");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Distribution Map");
		assertEquals(TdwgSubjectDeterminer.DISTRIBUTION, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Life History");
		assertEquals(TdwgSubjectDeterminer.LIFECYCLE, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Host, Oviposition, and Larval Feeding Habits");
		assertEquals(TdwgSubjectDeterminer.LIFECYCLE, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Reproduction and Early Life History");
		assertEquals(TdwgSubjectDeterminer.LIFECYCLE, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Host Relationships and Larval Biology");
		assertEquals(TdwgSubjectDeterminer.LIFECYCLE, tsd.getTdwgUri());

		tsd = new TdwgSubjectDeterminer("Male Genitalia");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Adult External Characteristics");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Adult Characteristics");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("List of synapomorphies");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Female Characters");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Generic Portrait");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
		
		tsd = new TdwgSubjectDeterminer("Male Characters");
		assertEquals(TdwgSubjectDeterminer.MORPHOLOGY, tsd.getTdwgUri());
	}
}
