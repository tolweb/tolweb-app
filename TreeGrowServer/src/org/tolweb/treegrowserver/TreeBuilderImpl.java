/*
 * Created on Nov 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.w3c.dom.Document;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeBuilderImpl implements TreeBuilder {
    private NodeDAO nodeDao;
    private PageDAO pageDao;
    
    public Document buildTree(Long rootNodeId, boolean isVerbose, int depth) {
        MappedNode root = nodeDao.getNodeWithId(rootNodeId);
        return null;
    }
}
