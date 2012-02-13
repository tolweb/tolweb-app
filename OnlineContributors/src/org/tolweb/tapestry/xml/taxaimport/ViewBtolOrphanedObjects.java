package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.btol.injections.GeneFragmentNodeStatusInjectable;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.BtolMetaNode;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ViewBtolOrphanedObjects extends BasePage implements PageBeginRenderListener, NodeInjectable, 
	MetaInjectable, BaseInjectable, ProjectInjectable, GeneFragmentNodeStatusInjectable {
	private static final Long BEETLES_NODE_ID = 8221L;
	
	public abstract MappedNode getBasalNode();
	public abstract void setBasalNode(MappedNode mnode);
	
	@Persist("session")
	public abstract List<BtolTuple> getBtolTuples();
	public abstract void setBtolTuples(List<BtolTuple> tuples);
	
	public abstract BtolTuple getCurrentTuple();
	public abstract void setCurrentTuple(BtolTuple tuple);
	
	public abstract Boolean getViewToggle();
	public abstract void setViewToggle(Boolean value);
	
	public abstract String getConfirmMessage();
	public abstract void setConfirmMessage(String msg);
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			MappedNode beetleNode = getWorkingNodeDAO().getNodeWithId(BEETLES_NODE_ID);
			setBasalNode(beetleNode);
			
			List<Long> inactiveBeetleNodeIds = NodeHelper.getInactiveNodeIdsForClade(getBasalNode().getNodeId(), getMiscNodeDAO(), getWorkingNodeDAO());
			Set<Long> nodeIdsWithFields = getProjectDAO().getNodeIdsWithRevelantTierData(inactiveBeetleNodeIds);
			Set<Long> nodeIdsWithStatuses = getGeneFragmentNodeStatusDAO().getNodeIdsWithStatuses(new ArrayList<Long>(nodeIdsWithFields));			
			Set<Long> masterList = new HashSet<Long>();
			masterList.addAll(nodeIdsWithFields);
			masterList.addAll(nodeIdsWithStatuses);
			
			List<BtolTuple> tuples = new ArrayList<BtolTuple>();
			for (Long nodeId : masterList) {
				MappedNode mnode = getMiscNodeDAO().getNodeWithId(nodeId, true);
				BtolTuple tuple = new BtolTuple(mnode);
				if (nodeIdsWithFields.contains(nodeId)) {
					tuple.setHasAdditionalFields(true);
				}
				if (nodeIdsWithStatuses.contains(nodeId)) {
					tuple.setHasStatuses(true);
				}
				
				if (tuple.getHasAdditionalFields()) { // || tuple.getHasStatuses()) {
					tuples.add(tuple);
					List result = getWorkingNodeDAO().findNodesExactlyNamed(tuple.getNode().getName(), true);
					if (result != null && result.size() == 1) {
						tuple.setTargetNodeId((Long)result.get(0));
					}
				}
			}

			setBtolTuples(tuples);
		}
	}
	
	public void processOperations(IRequestCycle cycle) {
		try {
			for (BtolTuple tuple : getBtolTuples()) {
				if (tuple.getTargetNodeId() != null) {
					Long id = tuple.getNode().getNodeId();
					BtolMetaNode meta = getBtolMetaNodeDAO().getMetaNodeForBtol(id);
					executeReattach(tuple, meta);
				}
			}
			setConfirmMessage("BToL data moved successfully....");
		} catch (Exception ex) {
			setConfirmMessage("Failure occurred. BToL data was not moved... " + ex.getMessage());
		}
		setViewToggle(false);
		cycle.activate(this);
	}

    private void executeReattach(BtolTuple tuple, BtolMetaNode meta) {
    	if (tuple.getNode() != null && meta != null) {
			Long oldNodeId = tuple.getNode().getNodeId();
			Long newNodeId = tuple.getTargetNodeId();
			
    		AdditionalFields fields = meta.getNode().getAdditionalFields();
    		if (fields != null) {
    			System.out.println("\treattaching additional fields (" + fields.getId() + ") from " + oldNodeId + " to " + newNodeId);
    			getProjectDAO().reattachAdditionalFields(fields.getId(), newNodeId, oldNodeId);
    		}
    		
    		if (tuple.getHasStatuses()) {
    			for (GeneFragmentNodeStatus status : meta.getStatuses()) {
    				Long statusId = status.getId();
    				System.out.println("\treattaching status data (" + statusId + ") from " + oldNodeId + " to " + newNodeId);
    				getGeneFragmentNodeStatusDAO().reattachStatus(statusId, newNodeId, oldNodeId);
    			}
    		}
    	}
   	
	}
    
	public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 550;
    	return getRendererFactory().getLinkRenderer("", width, height);
    }	
}
