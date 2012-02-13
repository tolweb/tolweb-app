package org.tolweb.tapestry;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class EditMoveBranch extends AbstractNodeEditingPage implements PageBeginRenderListener, NodeInjectable {
	public abstract String getNewParentName();
	public abstract void setNewParentName(String value);
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding() && StringUtils.isEmpty(getNewParentName())) {
			checkError();
		}
	}
	
	private void checkError() {
		Long parentNodeId = getNode().getParentNodeId();
		if (parentNodeId != null && parentNodeId.intValue() > 0) {
			int numChildren = getNodeDAO().getNumChildrenForNodeId(parentNodeId, true);
			if (numChildren <= 2) {
				setError("This node's parent has only two children and cannot be moved.");
			}
		} else {
			setError("This node has no valid parent and cannot be moved.");
		}
	}
}
