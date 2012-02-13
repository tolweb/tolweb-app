package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class FindNodeIds extends BasePage implements NodeInjectable, BaseInjectable, PageInjectable {
    
    public static final int DEFAULT_PAGE_SIZE = 35;
    
	public abstract String getGroupName();

	public abstract List<MappedNode> getNodes();
	public abstract void setNodes(List<MappedNode> nodes);

	public abstract MappedNode getNode();
	public abstract void setNode(MappedNode node);
	
	public abstract boolean getHasPage();
	public abstract void setHasPage(boolean value);
	
    public abstract Integer getPageSize();
    public abstract void setPageSize(Integer size);

	public abstract boolean getMatchExactly();
	public abstract void setMatchExactly(boolean value);
	
	@SuppressWarnings("unchecked")
    public void doSearch(IRequestCycle cycle) {
		List nodes = null;
		if (getMatchExactly()) {
			nodes = getMiscNodeDAO().findNodesExactlyNamed(getGroupName());
		} else {
			nodes = getMiscNodeDAO().findNodesNamed(getGroupName());
		}
        setNodes(nodes);
	}
	
    public String getPreviewPageUrl() {
    	return getUrlBuilder().getWorkingURLForObject(getNode());
    }

    public boolean getNodeHasPage() {
        boolean result = getWorkingPageDAO().getNodeHasPage(getNode());
        setHasPage(result);
        return result;
    }    
    
    public int getCurrentPageSize() {
    	if (getPageSize() != null) {
    		return getPageSize().intValue();
    	}
    	return DEFAULT_PAGE_SIZE; 
    }
        
    @SuppressWarnings("unchecked")
    public IPrimaryKeyConvertor getConvertor() {
	    return new IPrimaryKeyConvertor() {
			public Object getPrimaryKey(Object objValue) {
				return ((MappedNode) objValue).getNodeId();
			}

			/**
			 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
			 */
			public Object getValue(Object objPrimaryKey) {
			    Long nodeId = (Long) objPrimaryKey;
			    for (Iterator iter = getNodes().iterator(); iter.hasNext();) {
		            MappedNode node = (MappedNode) iter.next();
		            Long nextId = node.getNodeId();
		            if (nextId.equals(nodeId)) {
		                return node;
		            }
		        }
			    return null;
			} 

	    };
	}    
}
