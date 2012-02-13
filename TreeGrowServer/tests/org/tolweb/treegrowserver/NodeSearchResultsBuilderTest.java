/*
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.XMLConstants;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NodeSearchResultsBuilderTest extends ApplicationContextTestAbstract {
    private NodeSearchResultsBuilder builder;
    
    public NodeSearchResultsBuilderTest(String name) {
        super(name);
        builder = (NodeSearchResultsBuilder) context.getBean("nodeSearchResultsBuilder");
    }
    
    public void testBuildSearchResultsDocument() {
    	Document doc = builder.buildSearchResultsDocument("Carnivora", "dmandel@tolweb.org", "dmandel", true);
    	Element matchElement = doc.getRootElement().getChild(XMLConstants.MATCH);
    	Element lockElement = matchElement.getChild(XMLConstants.LOCK_INFO);
        assertNotNull(lockElement);
        assertNull(lockElement.getAttributeValue(XMLConstants.BATCHID));
        assertEquals(lockElement.getAttributeValue(XMLConstants.TYPE), XMLConstants.SUBMITTED);
        
        // Now do the same test with katja, and there should be the batchid since she is an editor
        // for this batch
        doc = builder.buildSearchResultsDocument("Carnivora", "treegrow@ag.arizona.edu", "e8bcc959103aae7f06e199a59bd956cf", true);
        matchElement = doc.getRootElement().getChild(XMLConstants.MATCH);
    	lockElement = matchElement.getChild(XMLConstants.LOCK_INFO);
        assertNotNull(lockElement.getAttributeValue(XMLConstants.BATCHID));
    	
        doc = builder.buildSearchResultsDocument("Life", "dmandel@tolweb.org", "dmandel", true);
        assertNotNull(doc);
        assertEquals(doc.getRootElement().getName(), XMLConstants.MATCHES);
        matchElement = doc.getRootElement().getChild(XMLConstants.MATCH);
        assertNotNull(matchElement);
        assertEquals(matchElement.getAttributeValue(XMLConstants.ID), "1");
        assertEquals(matchElement.getAttributeValue(XMLConstants.NAME), "Life on Earth");
        assertEquals(matchElement.getAttributeValue(XMLConstants.PERMISSIONS), "1");
        
        doc = builder.buildSearchResultsDocument("a", "dmandel@tolweb.org", "dmandel", false);
        assertEquals(doc.getRootElement().getName(), XMLConstants.ERROR);
        Element errorElement = doc.getRootElement();
        assertEquals(errorElement.getChildText(XMLConstants.ERRORNUM), "200200");
        assertEquals(errorElement.getChildText(XMLConstants.ERRORTEXT), "Too many matches");
        
        doc = builder.buildSearchResultsDocument("DFJERDWedWRWFCXZDSJFNWDJNSF", "dmandel@tolweb.org", "dmandel", true);
        assertEquals(doc.getRootElement().getName(), XMLConstants.ERROR);
        errorElement = doc.getRootElement();
        assertEquals(errorElement.getChildText(XMLConstants.ERRORNUM), "404");
        assertEquals(errorElement.getChildText(XMLConstants.ERRORTEXT), "No matches");
        
        doc = builder.buildSearchResultsDocument("Habronattus", "wmaddisn@interchange.ubc.ca", "b86f1c43a39da8b5c0ed15c1f41d8431", true);
        matchElement = doc.getRootElement().getChild(XMLConstants.MATCH);
        lockElement = matchElement.getChild(XMLConstants.LOCK_INFO);
        assertEquals(lockElement.getAttributeValue(XMLConstants.TYPE), XMLConstants.DOWNLOADED);
        assertEquals(lockElement.getAttributeValue(XMLConstants.USER), "wmaddisn@interchange.ubc.ca");
        assertEquals(lockElement.getAttributeValue(XMLConstants.DOWNLOAD_ID), "993");        
		
    }
}
