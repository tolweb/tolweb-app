package org.tolweb.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.tolweb.treegrow.main.StringUtils;

import junit.framework.TestCase;

/**
 * 
 * @author lenards
 *
 */
public class MappedTextSectionTest extends TestCase {
	private MappedTextSection mtxt; 
	
	private static final String TEXT1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris convallis urna sed lacus. Proin diam. Phasellus a ipsum placerat arcu fringilla ullamcorper. Fusce posuere ligula porttitor diam. Donec non mauris. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc sem est, ullamcorper quis, vulputate quis, iaculis sagittis, velit. Aenean dolor nibh, bibendum eu, lobortis sit amet, euismod eget, est. Vivamus ultrices neque ac purus. Vestibulum vestibulum magna et eros. Donec quis purus vitae ipsum gravida vehicula. Proin accumsan. Aliquam vel lorem non urna porttitor consequat.";
	private static final String TEXT2 = "Integer blandit ligula eget eros. Sed ac magna. Maecenas consectetur elit id orci. Vivamus nunc orci, vulputate sed, mollis id, suscipit vel, felis. Pellentesque eros. Donec leo felis, pulvinar a, imperdiet quis, pretium ac, tortor. Duis molestie massa ac mi. In ut dui. Nulla porttitor neque in risus blandit euismod. Sed urna pede, semper sed, interdum et, lobortis semper, dolor. Suspendisse potenti. Nulla facilisi. Sed lacinia semper odio. Morbi vestibulum pede quis arcu pellentesque pretium.";
	private static final String TEXT3 = "Nulla sapien ipsum, egestas id, sollicitudin at, suscipit a, lorem. Vestibulum magna lectus, venenatis sed, ullamcorper quis, euismod vel, sem. Mauris porttitor leo eu quam. Phasellus eu odio vitae orci congue lacinia. Fusce libero lacus, molestie at, tincidunt ut, aliquet id, odio. Fusce id neque ut libero eleifend aliquet. Aliquam quis lacus vitae magna eleifend mattis. Donec egestas lorem ut est adipiscing tristique. Cras tristique lectus eget augue. Nam neque.	";
	
	protected void setUp() throws Exception {
		super.setUp();
		mtxt = new MappedTextSection();
	}

	/** 
	 * A test to verify that all mutators are functioning
	 *  
	 * Fields defined by MappedTextSection
	 * ========================================
	 * textSectionId - identity field, primary key
	 * notes - text field for author notes 
	 * 
	 * Fields defined by superclass TextSection
	 * ========================================
	 * m_Heading - Holds the heading of this text section 
	 * m_Text - Holds the text of this text section
	 */
	public void test_mutators_are_correct() {
		assertNull(mtxt.getTextSectionId());
		mtxt.setTextSectionId(Long.valueOf(650));
		assertTrue(mtxt.getTextSectionId().equals(Long.valueOf(650)));
		mtxt.setTextSectionId(null);
		assertNull(mtxt.getTextSectionId());
		mtxt.setTextSectionId(Long.valueOf(1980));
		assertTrue(mtxt.getTextSectionId().equals(Long.valueOf(1980)));
		
		assertNull(mtxt.getNotes());
		mtxt.setNotes("");
		assertTrue(StringUtils.isEmpty(mtxt.getNotes()));
		mtxt.setNotes("Lorem ipsum dolor sit amet....");
		assertEquals("Lorem ipsum dolor sit amet....", mtxt.getNotes());
		mtxt.setNotes(null);
		assertNull(mtxt.getNotes());
		
		assertEquals("<<< New Text Section >>>", mtxt.getHeading());
		mtxt.setHeading("Phasellus elementum ultricies nibh");
		assertEquals("Phasellus elementum ultricies nibh", mtxt.getHeading());
		mtxt.setHeading("");
		assertTrue(StringUtils.isEmpty(mtxt.getHeading()));
		mtxt.setHeading(null);
		assertNull(mtxt.getHeading());
		
		assertNull(mtxt.getText());
		mtxt.setText("");
		assertTrue(StringUtils.isEmpty(mtxt.getText()));
		mtxt.setText(TEXT1);
		assertEquals(TEXT1, mtxt.getText());
		mtxt.setText(null);
		assertNull(mtxt.getText());
		
		assertTrue(mtxt.getOrder() == 0);
		mtxt.setOrder(650);
		assertTrue(mtxt.getOrder() == 650);
		mtxt.setOrder(1980);
		assertTrue(mtxt.getOrder() == 1980);
	}

	/**
	 * Various places need to have the heading without space, the 
	 * getHeadingNoSpaces() so exactly that w/ a regex.  The places 
	 * of note there this is used are Web Services (ids for text 
	 * sections) and the HTML anchor links. 
	 */
	public void test_heading_space_removal() {
		// verify the heading is what we expect 
		// (the default value from a paren-paren constructor call)
		assertEquals("<<< New Text Section >>>", mtxt.getHeading());
		assertEquals("<<<NewTextSection>>>", mtxt.getHeadingNoSpaces());
		
		// set the heading to some latin gibberish.
		mtxt.setHeading("Phasellus elementum ultricies nibh");
		assertEquals("Phaselluselementumultriciesnibh", mtxt.getHeadingNoSpaces());
		
		mtxt.setHeading("     ");
		assertEquals("", mtxt.getHeadingNoSpaces());
		
		mtxt.setHeading("ultriciesnibh");
		assertEquals("ultriciesnibh", mtxt.getHeadingNoSpaces());

		// not testing how it handles null
	}

	/**
	 * Equality is defined by the superclass of TextSection, 
	 * OrderedObject.  TextSections are edited in an ordered 
	 * manner in the UI - this is the technical detail that 
	 * was allowed to seep into the lower level.  Thus, the 
	 * oddness or complexity behind the manner of defining 
	 * equality has nothing to do w/ the business logic of 
	 * the domain object. 
	 */
	public void test_equality_comparable() {
		mtxt.setTextSectionId(Long.valueOf(55));
		mtxt.setHeading("Vivamus eu dolor lobortis elit semper lacinia");
		mtxt.setText(TEXT1);
		mtxt.setNotes("Proin id elit. Nullam scelerisque euismod nunc. ");
	
		MappedTextSection mtxtAlpha = new MappedTextSection();
		mtxtAlpha.setTextSectionId(Long.valueOf(59));
		mtxtAlpha.setHeading("");
		mtxtAlpha.setText(TEXT2);
		mtxtAlpha.setNotes("Suspendisse varius pretium pede.");
		
		MappedTextSection mtxtBeta = new MappedTextSection();
		mtxtBeta.setTextSectionId(Long.valueOf(65));
		mtxtBeta.setHeading("Quisque porttitor lacinia enim.");
		mtxtBeta.setNotes("Praesent mollis pellentesque eros");
		mtxtBeta.setText(TEXT3);
		
		// do we handle null elegantly? 
		assertFalse(mtxt.equals(null));
		
		// x==x 
		assertEquals(mtxt, mtxt);
		
		// hashcodes should be equal too 
		assertTrue(mtxt.hashCode() == mtxt.hashCode());
	
		assertTrue(mtxt.compareTo(mtxt) == 0);
		
		// if x==y, then y==x
		assertEquals(mtxt, mtxtAlpha);
		assertEquals(mtxtAlpha, mtxt);

		// if two objects are equal, their compareTo() should be 0 
		assertTrue(mtxt.compareTo(mtxtAlpha) == 0);
		assertTrue(mtxtAlpha.compareTo(mtxt) == 0);
		
		// sanity check
		assertTrue(mtxt.hashCode() == mtxt.getHashCode());

		// verify that the hashCode() and getHashCode() are same
		assertTrue(mtxt.hashCode() == mtxtAlpha.hashCode());
		assertTrue(mtxt.getHashCode() == mtxtAlpha.getHashCode());
		assertTrue(mtxt.getHashCode() == mtxtAlpha.hashCode());
		assertTrue(mtxt.hashCode() == mtxtAlpha.getHashCode());
		
		
		// if x==y, y==z then x==z
		assertEquals(mtxt, mtxtAlpha);
		assertEquals(mtxtAlpha, mtxtBeta);
		assertEquals(mtxt, mtxtBeta);

		assertTrue(mtxt.compareTo(mtxtAlpha) == 0);
		assertTrue(mtxtAlpha.compareTo(mtxtBeta) == 0);
		assertTrue(mtxt.compareTo(mtxtBeta) == 0);
		
		mtxtAlpha.setOrder(9);
		mtxtBeta.setOrder(-9);
		
		assertNotSame(mtxt, mtxtAlpha);
		assertNotSame(mtxtAlpha, mtxtBeta);
		assertNotSame(mtxt, mtxtBeta);
		
		assertNotSame(mtxt.hashCode(), mtxtAlpha.hashCode());
		assertNotSame(mtxtAlpha.hashCode(), mtxtBeta.hashCode());
		assertNotSame(mtxt.hashCode(), mtxtBeta.hashCode());
		
		assertTrue(mtxt.compareTo(mtxtAlpha) != 0);
		assertTrue(mtxtAlpha.compareTo(mtxtBeta) != 0);
		assertTrue(mtxt.compareTo(mtxtBeta) != 0);
	}
	
	@SuppressWarnings("unchecked")
	public void test_static_values() {
		assertNotNull(MappedTextSection.getImmutableNames());
		assertTrue(!MappedTextSection.getImmutableNames().isEmpty());
		Set s = new HashSet();
		s.add(MappedTextSection.CHARACTERISTICS);
		s.add(MappedTextSection.DISCUSSION);
		s.add(MappedTextSection.INTRODUCTION);
		
		Set t = new HashSet(MappedTextSection.getImmutableNames());
		assertTrue(s.equals(t));
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		mtxt = null;
	}
}
