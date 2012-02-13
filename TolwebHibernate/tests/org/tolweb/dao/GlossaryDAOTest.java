/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.GlossaryEntry;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlossaryDAOTest extends ApplicationContextTestAbstract {
    private GlossaryDAO dao;
    
    public GlossaryDAOTest(String name) {
        super(name);
        dao = (GlossaryDAO) context.getBean("glossaryDAO");
    }
    
    public void testGetGlossaryEntries() {
        List list = dao.getGlossaryEntries();
        assertEquals(list.size(), 197);
        GlossaryEntry number3 = (GlossaryEntry) list.get(1);
        assertEquals(number3.getGlossaryId(), new Long(3));
        assertEquals(number3.getDefinition(), "A type of pseudopod, needle-shaped, with internal skeleton formed from arrays of microtubules. ");
        assertEquals(number3.getPlurals().size(), 1);
        assertEquals(number3.getPlurals().iterator().next(), "actinopods");
        assertEquals(number3.getSynonyms().size(), 0);
    }
    
    /*public void testGlossaryLookup() {
        GlossaryLookup lookup = (GlossaryLookup) context.getBean("glossaryLookup");
        lookup.getGlossWordsRegex();
        MappedPage eukaryotes = pageDao.getPageWithId(new Long(276));
        String mainContent = "";
        Iterator it = eukaryotes.getTextSections().iterator();
        while (it.hasNext()) {
            mainContent += ((TextSection) it.next()).getText();
        }
        lookup.replaceGlossaryWords(mainContent);
    }*/
}
