package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ScientificManagerNodesSearchResults extends BasePage implements NodeInjectable, 
		BaseInjectable {
	@SuppressWarnings("unchecked")
    public abstract void setNodes(List value);
	@SuppressWarnings("unchecked")
    public abstract List getNodes();
    public abstract MappedNode getCurrentNode();
    
    public String getCurrentNodeLink() {
        return getUrlBuilder().getWorkingURLForObject(getCurrentNode().getName());
    }
}
