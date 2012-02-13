/*
 * Created on Nov 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;


/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PermissionCheckerTest extends ApplicationContextTestAbstract {
    private ContributorDAO contributorDAO;
    private NodeDAO nodeDAO;
    private PermissionChecker permissionChecker;
    
    public PermissionCheckerTest(String name) {
        super(name);
        contributorDAO = (ContributorDAO) context.getBean("contributorDAO");
        nodeDAO = (NodeDAO) context.getBean("nodeDAO");
        permissionChecker = (PermissionChecker) context.getBean("permissionChecker");
    }
    
    public void testGetContributorsWithPermissionsForAncestorNodes() {
        List contrs = permissionChecker.getContributorIdsWithPermissionsForAncestorNodes(new Long(1));
        assertEquals(contrs.size(), 5);
        Iterator it = contrs.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
        }
        contrs = permissionChecker.getContributorIdsWithPermissionsForAncestorNodes(new Long(3));
        assertEquals(contrs.size(), 7);
    }
    
    public void testCheckPermissionForNode() {
        Contributor danny = contributorDAO.getContributorWithEmail("dmandel@tolweb.org");
        // Check that I have permission to work on life
        assertNull(permissionChecker.checkPermissionForNode(danny, "dmandel", new Long(1)));
        // Also check for homo (since I have permission on life I should have permission on homo)
        assertNull(permissionChecker.checkPermissionForNode(danny, "dmandel", new Long(16418)));
        
        // Try a wrong password now
        Document wrongPasswordDoc = permissionChecker.checkPermissionForNode(danny, "xxx", new Long(1));
        assertNotNull(wrongPasswordDoc);
        assertEquals(wrongPasswordDoc.getRootElement().getName(), XMLConstants.FAILURE);
        assertEquals(wrongPasswordDoc.getRootElement().getAttributeValue(XMLConstants.WRONG_PASSWORD), XMLConstants.ONE);
        
        Contributor wayne = contributorDAO.getContributorWithId("369");
        // Verify that wayne can't work on eukaryotes
        Document noPermissionsDoc = permissionChecker.checkPermissionForNode(wayne, "b86f1c43a39da8b5c0ed15c1f41d8431", new Long(3));
        assertNotNull(noPermissionsDoc);        
        assertEquals(noPermissionsDoc.getRootElement().getName(), XMLConstants.FAILURE);
        assertEquals(noPermissionsDoc.getRootElement().getAttributeValue(XMLConstants.PERMISSIONS), XMLConstants.ZERO);
        // Verify that he can work on salticidae
        assertNull(permissionChecker.checkPermissionForNode(wayne, "b86f1c43a39da8b5c0ed15c1f41d8431", new Long(2677)));
    }    
}
