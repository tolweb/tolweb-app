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
public interface TreeGifDAO {
    public TreeGif getTreeGifForPage(MappedPage pg, boolean regenerate, boolean isWorking);
    public TreeGif getTreeGifForPage(MappedPage pg);
    public boolean getTreeGifExistsForPage(MappedPage pd);
    public void clearCacheForTreeGif(TreeGif treeGif);
    public TreeGif getNewTreeGifForPage(MappedPage pg);
    public void saveTreeGif(TreeGif treeGif);
}
