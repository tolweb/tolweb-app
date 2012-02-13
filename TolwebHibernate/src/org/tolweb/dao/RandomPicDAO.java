/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.MappedPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface RandomPicDAO {
    public List getRandomPicsForPage(MappedPage pg, boolean includeCurrentPage);
    public List getRandomPicsForPage(MappedPage pg);
}
