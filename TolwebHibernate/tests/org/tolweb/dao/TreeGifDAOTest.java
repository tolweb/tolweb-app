/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TreeGif;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeGifDAOTest extends ApplicationContextTestAbstract {
    private TreeGifDAO dao;
    private PageDAO pageDao;
    
    public TreeGifDAOTest(String name) {
        super(name);      
        dao = (TreeGifDAO) context.getBean("treeGifDAO");
        pageDao = (PageDAO) context.getBean("pageDAO");
    }

    
    public void testGetTreeGifForPage() {
        MappedPage life = pageDao.getPageWithId(new Long(1));
        TreeGif lifeGif = dao.getTreeGifForPage(life);
        assertNotNull(lifeGif);
        assertEquals(lifeGif.getId(), new Long(63234));
        assertEquals(lifeGif.getHeight(), new Integer(96));
        assertEquals(lifeGif.getWidth(), new Integer(471));
        assertEquals(lifeGif.getFilenameNoPath(), "Life_on_Earth.gif");
        MappedPage viruses = pageDao.getPageWithId(new Long(2722));
        TreeGif virusGif = dao.getTreeGifForPage(viruses);
        assertNotNull(virusGif);
    }
}
