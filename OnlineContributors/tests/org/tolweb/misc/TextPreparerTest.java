/*
 * Created on Jul 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.misc;

import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.hibernate.MappedAccessoryPage;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TextPreparerTest  extends ApplicationContextTestAbstract {
	private TextPreparer preparer;
	
	public TextPreparerTest(String name) {
		super(name);
		preparer = (TextPreparer) context.getBean("textPreparer");
	}
	public void testPrepareImages() {
		String testString = "<ToLimg id=\"1\">";
		String preparedString = preparer.prepareImages(testString);
		System.out.println("returned string is: " + preparedString);
		assertEquals("<img src=\"/tree/ToLimages/VampStellate17.jpg\" >", preparedString);
		testString = "<ToLimg id=\"1\"> <ToLimg id=\"2\">";
		preparedString = preparer.prepareImages(testString);
		assertEquals("<img src=\"/tree/ToLimages/VampStellate17.jpg\" > <img src=\"/tree/ToLimages/OingensBeak.jpg\" >", preparedString);
	}
	
	public void testTranslateImagesToTreehouseFormat() {
	    String testString = "<ToLimg id=\"1\">";
	    String translatedString = preparer.translateImagesToTreehouseFormat(testString);
	    assertEquals("TOLIMG1", translatedString);
	    
	    testString = "xxxxx   <ToLimg id=\"1\">  <ToLimg id=\"2\">   xx";
	    translatedString = preparer.translateImagesToTreehouseFormat(testString);
	    assertEquals("xxxxx   TOLIMG1  TOLIMG2   xx", translatedString);
	}
	
	public void testTranslateImagesFromTreehouseFormat() {
	    String testString = "xxxxx TOLIMG1 yyyyy TOLIMG1234";
	    String translatedString = preparer.translateImagesFromTreehouseFormat(testString);
	    assertEquals("xxxxx <ToLimg id=\"1\"> yyyyy <ToLimg id=\"1234\">", translatedString);
	}
	
	public void testPerformance() {
	    AccessoryPageDAO dao = (AccessoryPageDAO) context.getBean("accessoryPageDAO");
	    MappedAccessoryPage bracteonKey = dao.getAccessoryPageWithId(14);
	    String text = bracteonKey.getText();
	    long currentTime = System.currentTimeMillis();
	    for (int i = 0; i < 100; i++) {
	        preparer.prepareImages(text);
	    }
	    System.out.println("new way: " + (System.currentTimeMillis() - currentTime));
	    currentTime = System.currentTimeMillis();	    
	    for (int i = 0; i < 100; i++) {
	        preparer.oldPrepareImages(text);
	    }	    
	    System.out.println("old way: " + (System.currentTimeMillis() - currentTime));
	    currentTime = System.currentTimeMillis();	    
	    for (int i = 0; i < 100; i++) {
	        preparer.prepareImages(text);
	    }
	    System.out.println("new way again: " + (System.currentTimeMillis() - currentTime));
	    currentTime = System.currentTimeMillis();	    
	    for (int i = 0; i < 100; i++) {
	        preparer.oldPrepareImages(text);
	    }
	    MappedAccessoryPage carabidTribes = dao.getAccessoryPageWithId(1);
	    text = carabidTribes.getText();	    
	    currentTime = System.currentTimeMillis();
	    for (int i = 0; i < 100; i++) {
	        preparer.prepareImages(text);
	    }
	    System.out.println("new way: " + (System.currentTimeMillis() - currentTime));
	    currentTime = System.currentTimeMillis();	    
	    for (int i = 0; i < 100; i++) {
	        preparer.oldPrepareImages(text);
	    }	    
	    System.out.println("old way: " + (System.currentTimeMillis() - currentTime));
	    currentTime = System.currentTimeMillis();	    
	    for (int i = 0; i < 100; i++) {
	        preparer.prepareImages(text);
	    }
	    System.out.println("new way again: " + (System.currentTimeMillis() - currentTime));
	    currentTime = System.currentTimeMillis();	    
	    for (int i = 0; i < 100; i++) {
	        preparer.oldPrepareImages(text);
	    }	    
	}
}
