package org.tolweb.tapestry.admin;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IFieldTracking;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

/**
 * Allows the user to perform the correction of Node Ancestors for a given node.  
 * 
 * Background:
 * Node Ancestors is a table in the Misc database that provides a de-normalized 
 * representation of the ancestoral relations of nodes.  A given node knows its' 
 * parent and its' children, but knowing about it's grandchildren or it's, say, 
 * great-grandparents is something that must be done in code and not query.  This 
 * table allows queries to be performed to determine such relations.  
 * 
 * At times, actions such as deletes & moves cause the Node Ancestors table to 
 * be out of synch with the actual NODES table representation.  It is not common, 
 * but it happens.  Finding each case which causes these synch problems appears 
 * (at the time of authoring this) to be outside of the scope of the time 
 * (currently) available.  So this page exists as a quick fix to the consistency 
 * problem. 
 * 
 * @author lenards
 *
 */
public abstract class NodeAncestorsCorrect extends BasePage implements BaseInjectable, NodeInjectable {
	public static final String ERROR_MSG = "The node-id entered does not correspond with an active node.";
	
	/**
	 * Returns the node-id entered by the user into the text field. 
	 * @return a long representing the node-id for the desired node to correct
	 */
	public abstract Long getNodeId();
	/**
	 * Sets the node-id entered by the user. 
	 * @param id the node-id for the desired node to correct.
	 */
	public abstract void setNodeId(Long id);
	
	/**
	 * Indicates if the correction process has been done.
	 * @return true if the correction process run, otherwise false. 
	 */
	public abstract boolean getCorrectionDone();
	public abstract void setCorrectionDone(boolean done);
	
	/**
	 * Returns the validation delegate instance for this page. 
	 * @return a reference to the validation delegate for this page. 
	 */
	@Bean
	public abstract ValidationDelegate getValidationDelegate();	
	
	/**
	 * Returns the current field tracking instance. 
	 * 
	 * Used by the template (.html) to page the validation errors 
	 * determined by the ValidationDelegate.  Each error is represented 
	 * as an instance implementing IFieldTracking. 
	 * @return the current field tracking instance. 
	 */
	public abstract IFieldTracking getCurrentFieldTracking();
	/**
	 * Sets the current field tracking instance. 
	 * @param ift the current instance
	 */
	public abstract void setCurrentFieldTracking(IFieldTracking ift);

	/**
	 * Performs the node ancestor correction for the specified node id. 
	 * @param cycle
	 */
	public void doCorrection(IRequestCycle cycle) {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getNodeId()); 
		if (mnode != null) {
			List<Long> ancestorIds = NodeHelper.getAncestorIdsForNode(mnode, getWorkingNodeDAO());
			getMiscNodeDAO().resetAncestorsForNode(mnode.getNodeId(), ancestorIds);
			setCorrectionDone(true);
			cycle.activate(this);
		} else {
			getValidationDelegate().record(ERROR_MSG, ValidationConstraint.CONSISTENCY);
		}
	}
	
	public IPage correctAnotherNode() {
		return this;
	}
	
	/**
	 * Returns a renderer for popup links. 
	 * 
	 * Used by the Node Ids Search page. 
	 * @return a reference to a popup link renderer. 
	 */
    public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 350;
    	return getRendererFactory().getLinkRenderer("Find Node Id", width, height);
    }	
}