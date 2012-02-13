/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.Iterator;
import java.util.List;

import org.tolweb.hibernate.GlossaryEntry;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.GlossaryLookup;
import org.tolweb.treegrow.page.TextSection;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlossaryDAOTest extends ApplicationContextTestAbstract {
    private GlossaryDAO dao;
    private PageDAO pageDao;
    
    public GlossaryDAOTest(String name) {
        super(name);
        dao = (GlossaryDAO) context.getBean("glossaryDAO");
        pageDao = (PageDAO) context.getBean("publicPageDAO");
    }
    
    @SuppressWarnings("unchecked")
    public void testGetGlossaryEntries() {
    	String expected_Def = "A type of pseudopod, needle-shaped, with internal skeleton";
    	String expected_Word = "actinopod";
        List list = dao.getGlossaryEntries();
        assertTrue(list.size() >= 197);
        
        System.out.println(list);
        GlossaryEntry number3 = findWord(expected_Word, list);
        assertNotNull(number3);
        assertEquals(number3.getGlossaryId(), new Long(3));
        assertEquals(expected_Word, number3.getWord());
        System.out.println(number3.getDefinition() + "|");
        assertTrue(number3.getDefinition().startsWith(expected_Def));
        assertEquals(number3.getPlurals().size(), 1);
        assertEquals(number3.getPlurals().iterator().next(), "actinopods");
        assertEquals(number3.getSynonyms().size(), 0);
    }
    
    @SuppressWarnings("unchecked")
    private GlossaryEntry findWord(String expectedWord, List list) {
    	for (Iterator itr = list.iterator(); itr.hasNext(); ) {
    		GlossaryEntry entry = (GlossaryEntry)itr.next();
    		if (expectedWord.equals(entry.getWord())) {
    			return entry;
    		}
    	}
    	return null;
	}

	@SuppressWarnings("unchecked")
    public void testGlossaryLookup() {
        GlossaryLookup lookup = (GlossaryLookup) context.getBean("glossaryLookup");
        lookup.getGlossWordsRegex();
        MappedPage eukaryotes = pageDao.getPageWithId(new Long(276));
        String mainContent = "";
        Iterator it = eukaryotes.getTextSections().iterator();
        while (it.hasNext()) {
            mainContent += ((TextSection) it.next()).getText();
        }
        lookup.replaceGlossaryWords(mainContent);
    }
    
    @SuppressWarnings("unchecked")
    public void testGlossaryCompleteness() {
    	String expected_Word = "abyssopelagic";
        List list = dao.getGlossaryEntries();    	
        GlossaryEntry entry = findWord(expected_Word, list);
        assertNotNull(entry);
    }
}
