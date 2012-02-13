package org.tolweb.tapestry.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;

public class OtherGroupsHelper {
	@SuppressWarnings("unchecked")
	private List childPages = new ArrayList();
	@SuppressWarnings("unchecked")
	private List siblingPages = new ArrayList();
	@SuppressWarnings("unchecked")
	private List ancestorPages = new ArrayList();
	private String parentGroupName;
	private String nodeName;
	private boolean isLeaf;
	private boolean isRoot;
	private Long rootNodeId;
	
	@SuppressWarnings("unchecked")
	public OtherGroupsHelper constructHelperForNode(MappedNode node, PageDAO pageDao, Long rootNodeId) {
		OtherGroupsHelper helper = new OtherGroupsHelper();
        Long pageIdNodeIsOn = pageDao.getPageIdNodeIsOn(node);
        Long pageIdForNode = pageDao.getPageIdForNode(node);		
		helper.setChildPages(pageDao.getChildPageNamesAndIds(pageIdForNode));
		helper.setSiblingPages(pageDao.getChildPageNamesAndIds(pageIdNodeIsOn));
		// find where the root is, then trim the results of the ancestors to stop
		// there so we don't get groups not in the project in the list of pages
		List ancestorPages = pageDao.getAncestorPageNames(pageIdForNode);
		if (ancestorPages != null) {
			int index = 0;
			for (Iterator iter = ancestorPages.iterator(); iter.hasNext();) {
				index++;			
				Object[] nextArr = (Object[]) iter.next();
				// array index one is the node id
				if (nextArr[2].equals(rootNodeId)) {
					break;
				}
			}
			ancestorPages = ancestorPages.subList(0, index);
			helper.setAncestorPages(ancestorPages);
		}
		helper.setParentGroupName(pageDao.getGroupNameForPage(pageIdNodeIsOn));
		helper.setIsRoot(node.getNodeId().equals(rootNodeId));
		helper.setNodeName(node.getName());
		helper.setIsLeaf(node.getIsLeaf());
		helper.setRootNodeId(rootNodeId);
		return helper;
	}
	public String getPreviousSiblingClassName() {
        if (getIsRoot()) {
        	return "disabled";
        } else {
        	return null;
        }
    }
    public String getNextSiblingClassName() {
        if (getIsRoot()) {
        	return "disabled";
        } else {
        	return null;
        }
    }	
    public boolean getHasPreviousSibling() {
        return getNotRoot() && getSiblingIndexOfPage() > 0;
    }
    public boolean getHasNextSibling() {
        return getNotRoot() && getSiblingIndexOfPage() < getSiblingPages().size() - 1;
    }
    public String getPreviousSiblingName() {
        return getSiblingNameAtIndex((getSiblingIndexOfPage() - 1) % getSiblingPages().size());
    }
    public Long getPreviousSiblingId() {
    	return getSiblingIdAtIndex((getSiblingIndexOfPage() + 1) % getSiblingPages().size());
    }
    public String getNextSiblingName() {
        return getSiblingNameAtIndex((getSiblingIndexOfPage() + 1) % getSiblingPages().size());
    }
    public Long getNextSiblingId() {
    	return getSiblingIdAtIndex((getSiblingIndexOfPage() + 1) % getSiblingPages().size());
    }    
    private String getSiblingNameAtIndex(int index) {
        return (String) ((Object[]) getSiblingPages().get(index))[0];
    }
    private Long getSiblingIdAtIndex(int index) {
    	return (Long) ((Object[]) getSiblingPages().get(index))[1];
    }
    /**
     * Returns the order that this page occurs in the sibling list
     * @return
     */
    @SuppressWarnings("unchecked")
    public int getSiblingIndexOfPage() {
        int index = 0;
        String nodeName = getNodeName();
        for (Iterator iter = getSiblingPages().iterator(); iter.hasNext();) {
            Object[] element = (Object[]) iter.next();
            // Check to see if the node name matches the page name
            if (((String) element[0]).equals(nodeName)) {
                return index;
            }
            index++;
        }
        return -1;
    }
    
    @SuppressWarnings("unchecked")
    public boolean getHasSubgroups() {
    	boolean isLeaf = getIsLeaf();
    	List subgroups = getChildPages();
        return !isLeaf && subgroups.size() > 0;
    }
    
    @SuppressWarnings("unchecked")
	public List getAncestorPages() {
		return ancestorPages;
	}

    @SuppressWarnings("unchecked")
	public void setAncestorPages(List ancestorPages) {
		this.ancestorPages = ancestorPages;
	}

    @SuppressWarnings("unchecked")
	public List getChildPages() {
		return childPages;
	}

    @SuppressWarnings("unchecked")
	public void setChildPages(List childPages) {
		this.childPages = childPages;
	}

	public String getParentGroupName() {
		return parentGroupName;
	}

	public void setParentGroupName(String parentGroupName) {
		this.parentGroupName = parentGroupName;
	}

	@SuppressWarnings("unchecked")
	public List getSiblingPages() {
		return siblingPages;
	}

	@SuppressWarnings("unchecked")
	public void setSiblingPages(List siblingPages) {
		this.siblingPages = siblingPages;
	}
	public boolean getIsRoot() {
		return isRoot;
	}
	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public boolean getNotRoot() {
		return !getIsRoot();
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public boolean getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Long getRootNodeId() {
		return rootNodeId;
	}
	public void setRootNodeId(Long rootNodeId) {
		this.rootNodeId = rootNodeId;
	}
}
