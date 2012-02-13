package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Lifecycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Gene;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.btol.tapestry.selection.NeededFilterSelectionModel;
import org.tolweb.btol.tapestry.selection.SequenceStatusModel;
import org.tolweb.btol.tapestry.selection.TierFilterSelectionModel;
import org.tolweb.btol.util.NodeFilterHelper;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.tapestry.adt.HashtableTree;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class AssignSequenceDefaults extends ProjectPage implements GeneInjectable,	NodeInjectable {
	private TierFilterSelectionModel taxaModel;
	@Bean(ArrayList.class)
	public abstract List<Gene> getGenesToAssign();
	public abstract int getStatusSelection();
	public abstract int getTaxaSelection();
	
	public TierFilterSelectionModel getTaxaModel() {
		if (taxaModel == null) {
			taxaModel = new TierFilterSelectionModel("All");
		}
		return taxaModel;
	}
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract SequenceStatusModel getStatusModel();
	@InjectPage("TaxaIndex")
	public abstract TaxaIndex getTaxaIndexPage();
	@Persist("client")
	public abstract Long getNodeId();
	public abstract void setNodeId(Long value);
	public abstract boolean getOverwriteExisting();
	public abstract void setOverwriteExisting(boolean value);
	public abstract boolean getDefaultsApplied();
	public abstract void setDefaultsApplied(boolean value);
	
	@SuppressWarnings("unchecked")
	public List getAllGenes() {
		return getGeneDAO().getAllGenesInProject(getProject().getId(), true);
	}
	
	@SuppressWarnings("unchecked")
	public void applyDefaults() {
		NodeFilterHelper filterHelper = getTaxaIndexPage().getNodeFilterHelper();
		MappedNode rootNode = null;
		Hashtable<Long, List<MappedNode>> allNodes = null;
		rootNode = getWorkingNodeDAO().getNodeWithId(getNodeId());		
		// do the application stuff here
		if (getNodeId().equals(getTaxaIndexPage().getRootNodeId())) {
			allNodes = getTaxaIndexPage().getNodes();
		} else {
			// can't get the values from the page, we need to fetch them ourselves
			Collection descendantIds = getMiscNodeDAO().getDescendantIdsForNode(getNodeId());
			allNodes = getWorkingNodeDAO().getDescendantNodesForIndexPage(descendantIds);
		}
		Hashtable <Long, List<MappedNode>> filteredNodes = filterHelper.getNodesFromUserSelection
			(getTaxaSelection(), NeededFilterSelectionModel.SHOW_ALL, true, rootNode, allNodes, null);
		if (filteredNodes == null) {
			filteredNodes = allNodes;
		}
		HashtableTree tree = new HashtableTree();
		tree.setTable(filteredNodes);
//		Set<Long> allIds = tree.getAllNodeIds();
//		List<GeneNodeStatus> statuses = getGeneNodeStatusDAO().assignInitialStatusForNodeIdsAndGenesInProject(allIds, getGenesToAssign(), 
//				getStatusSelection(), getOverwriteExisting(), getProject().getId());
		// reload the containing page
		getTaxaIndexPage().reloadGeneNodeStatuses(getProject().getId());
		setDefaultsApplied(true);
	}
	
	public String getTitle() {
		return "Assign Sequence Defaults for " + getWorkingNodeDAO().getNameForNodeWithId(getNodeId()) + " Taxa";
	}
}
