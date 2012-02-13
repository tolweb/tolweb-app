package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class AddImageToNode extends ArticleNoteImageSearch implements IExternalPage, NodeInjectable {
	public abstract void setNodeId(Long value);
	@Persist("client")
	public abstract Long getNodeId();
	public abstract void setJustAddedNode(boolean value);
	public abstract boolean getJustAddedNode();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Long nodeId = (Long) parameters[0];
		setNodeId(nodeId);
	}
	
	public String getGroupNameFromNodeId() {
		return getWorkingNodeDAO().getNameForNodeWithId(getNodeId());
	}
	public byte getCallbackType() {
		return ImageSearchResults.ADD_NODE_TO_IMAGE_CALLBACK;
	}
	public String getInstructionsString() {
		return "Click the 'select' string to attach that image to " + getGroupNameFromNodeId();
	}
	public void finishNodeAdding(Long editedObjectId, IRequestCycle cycle) {
		setNodeId(editedObjectId);
		setJustAddedNode(true);
		cycle.activate(this);
	}
	public String getAnOrAnother() {
		if (getJustAddedNode()) {
			return "another";
		} else {
			return "an";
		}
	}
	public String getTitle() {
		return "Add an Image to " + getGroupNameFromNodeId();
	}
	public String getUploadedAndString() {
		return getJustAddedNode() ? "" : "Uploaded and ";
	}
	protected boolean showTopBorderForInstructions() {
		return super.showTopBorderForInstructions() || getJustAddedNode();
	}
}
