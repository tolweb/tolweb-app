/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.jdom.CDATA;
import org.jdom.Element;
import org.tolweb.treegrow.page.AccessoryPage;
import org.tolweb.treegrow.page.Page;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.OtherName;
import org.tolweb.treegrow.tree.Tree;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreeGrowXMLWriter extends XMLWriter {
	public TreeGrowXMLWriter(Tree t) {
		super(t);
	}
	
	public TreeGrowXMLWriter() {
		super();
	}
	
	public boolean writeXML(Node root) {
	    initTree();
	    return super.writeXML(root);
	}
	
    protected void addAdditionalRootElements(Element mainElmt) {
    	mainElmt.setAttribute(XMLConstants.VMVERSION, System.getProperty("java.vm.version"));
	}	
	
	protected int getNodeIdForNode(Node node) {
	    return node.getId();
	}
	
	protected int getDateForOtherName(OtherName otherName) {
	    return otherName.getDate();
	}
	
	protected boolean getNodeHasPage(Node node) {
		return node.hasPage() || node.getHasPageOnServer();
	}
	
	public void fleshOutNode(Node node, Element nodeElmt, boolean isComplete, Set imgs, Set contributors, Contributor contr) {
		super.fleshOutNode(node, nodeElmt, isComplete, imgs, contributors, contr, true);
		nodeElmt.setAttribute(XMLConstants.CHECKED_OUT_FILE, node.getCheckedOut() ? XMLConstants.ONE : XMLConstants.ZERO);
	}
	
	protected void writeOutCopyrightInfo(Page page, Element pageElmt) {
        Element copyright = new Element(XMLConstants.COPYRIGHT);
        pageElmt.addContent(copyright);
        
            if(   ( page.getCopyrightDate() != null && !page.getCopyrightDate().equals("") ) ||
            ( page.getCopyrightHolder() != null && !page.getCopyrightHolder().equals("") )  ) {
                
                if (saveToFile() || page.changedFromServer() ) {
                    Element date = new Element(XMLConstants.DATE);
                    copyright.addContent(date);
                    if(page.getCopyrightDate() != null && !page.getCopyrightDate().equals("")) {
                        date.setText(page.getCopyrightDate());
                    }
                    copyright.setAttribute(XMLConstants.DATECHANGED, page.getCopyrightDateChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
                }
                
                if (saveToFile() || page.changedFromServer() ) {
                    Element holder = new Element(XMLConstants.HOLDER);
                    copyright.addContent(holder);
                    if(page.getCopyrightHolder() != null  &&   !page.getCopyrightHolder().equals("")) {
                        holder.addContent(new CDATA(page.getCopyrightHolder()));
                    }
                    copyright.setAttribute(XMLConstants.HOLDERCHANGED, page.getCopyrightHolderChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
                }
            } 
	}
	
	protected int getNodeChildcount(Node node) {
        if (node.getChildren().size() == 0) {
            // If there are no children locally, store the child count on the server
        	return node.getChildCountOnServer();
        } else {
        	return node.getChildren().size();        
        }		
	}
	
	protected Collection getOtherNamesForNode(Node node) {
		return node.getOtherNames();
	}
	
	protected Page getPageForNode(Node node) {
		return node.getPageObject();
	}
	
	protected Collection getTextSectionsForPage(Page page) {
		return page.getTextList();
	}
	
	protected Collection getAccessoryPagesForPage(Page page, Node node) {
		return page.getAccessoryPages();
	}
	
	protected Collection getContributorsForPage(Page page) {
		return page.getContributorList();
	}
	
	protected Collection getContributorsForAccessoryPage(AccessoryPage accPage) {
		return accPage.getContributorList();
	}
	
	protected int getNameDateForNode(Node node) {
		return node.getNameDate();
	}
	
	protected int getNodeRankForNode(Node node) {
		return node.getNodeRank();
	}
	
	protected void writeOutNodesForImage(NodeImage img, Element nodesElmt) {
        Iterator it = img.getNodes().iterator();
        while (it.hasNext()) {
            ImageNode in = (ImageNode) it.next();
            nodesElmt.addContent(encodeImageNode(in, false));
        }
        it = img.getDeletedNodes().iterator();
        while (it.hasNext()) {
            ImageNode in = (ImageNode) it.next();
            nodesElmt.addContent(encodeImageNode(in, true));        
        }        
    }
	
	protected void writeOutVersionsForImage(NodeImage img, Element versionsElement) {
	    Iterator it = img.getVersionsSet().iterator();
	    while (it.hasNext()) {
	        ImageVersion version = (ImageVersion) it.next();
	        versionsElement.addContent(encodeImageVersion(version));
	    }
	}
	
	protected void writeOutPermissionsForContributor(Contributor contr, Element contributor) {
        Iterator it = contr.getPermissions().iterator();
        while (it.hasNext()) {
            Permission p = (Permission) it.next();
            Element permission = new Element(XMLConstants.PERMISSION);
            permission.setAttribute(XMLConstants.ID, "" + p.getNodeId());
            addCDATAElement(p.getNodeName(), XMLConstants.NODE, permission);
            contributor.addContent(permission);
        }	    
	}
	
    
    private Element encodeImageNode(ImageNode in, boolean removed) {
        Element nodeElmt = constructNodeElement();
        nodeElmt.setAttribute(XMLConstants.ID, in.getNodeId() + "");
        String removedStr = removed ? XMLConstants.ONE : XMLConstants.ZERO;
        nodeElmt.setAttribute(XMLConstants.REMOVED, removedStr);
        addCDATAElement(in.getNodeName(), XMLConstants.NAME, nodeElmt);
        return nodeElmt;
    }	
}
