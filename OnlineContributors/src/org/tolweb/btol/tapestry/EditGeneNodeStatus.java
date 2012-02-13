package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Lifecycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneNodeStatus;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.btol.injections.GeneNodeStatusInjectable;
import org.tolweb.btol.tapestry.selection.SequenceStatusModel;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.selectionmodels.StringToIntWithOffsetPropertySelectionModel;

/**
 * @deprecated As of July 2007, replaced by EditGeneFragmentNodeStatus
 * @author dmandel
 *
 */
public abstract class EditGeneNodeStatus extends AbstractXTileEditPage implements IExternalPage,
		GeneNodeStatusInjectable, GeneInjectable, NodeInjectable {
	public static final int EDIT_SOURCE_CALLBACK = 0;
	public static final int EDIT_SOURCE_ID_CALLBACK = 1;
	public static final int EDIT_STATUS_CALLBACK = 2;
	
	private StringToIntWithOffsetPropertySelectionModel statusSourceModel;
	
	@Persist("session")
	public abstract GeneNodeStatus getGeneNodeStatus();
	public abstract void setGeneNodeStatus(GeneNodeStatus value);
	@InjectPage("TaxaIndex")
	public abstract TaxaIndex getTaxaIndexPage();
	
	public IPropertySelectionModel getStatusSourceModel() {
		if (statusSourceModel == null) {
			List<String> strings = Arrays.asList(new String[] {"None Selected", "BTOL", "DRM", "GenBank"});
			statusSourceModel = new StringToIntWithOffsetPropertySelectionModel(strings, -1);
		}
		return statusSourceModel;
	}
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract SequenceStatusModel getStatusModel();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Long nodeId = (Long) parameters[0];
		Long geneId = (Long) parameters[1];
		Gene gene = getGeneDAO().getGeneWithId(geneId, getProject().getId());
		if (gene == null) {
			// do some error handling here
		}
		GeneNodeStatus status = getGeneNodeStatusDAO().getStatusForGeneAndNodeIdInProject(gene, nodeId, getProject().getId());
		if (status == null) {
			status = getGeneNodeStatusDAO().createStatusForGeneAndNodeIdInProject(gene, nodeId, getProject().getId());
		}
		// make sure it's accessible for the base page
		//getTaxaIndexPage().getStatusesSource().putStatus(status);		
		setGeneNodeStatus(status);
	}
	public String getTitle() {
		return "Edit Sequence Status for Gene " + getGeneNodeStatus().getGene().getName() + " and Taxon " + 
			getWorkingNodeDAO().getNameForNodeWithId(getGeneNodeStatus().getNodeId());
	}
	protected void doCallback(int callbackType, Long id, String newValue, IRequestCycle cycle) {
		switch (callbackType) {
		case EDIT_SOURCE_CALLBACK: 
			updateSource(id, Integer.valueOf(newValue) - 1, cycle);
			break;
		case EDIT_SOURCE_ID_CALLBACK:
			updateSourceDbId(id, newValue, cycle);
			break;
		case EDIT_STATUS_CALLBACK:
			updateStatus(id, Integer.valueOf(newValue), cycle);
			break;
		}
	}
	public String getSourceOnChangeString() {
		return getOnChangeString(EDIT_SOURCE_CALLBACK);
	}
	public String getStatusOnChangeString() {
		return getOnChangeString(EDIT_STATUS_CALLBACK);
	}
	public String getSourceIdOnChangeString() {
		return getOnChangeString(EDIT_SOURCE_ID_CALLBACK);
	}
	private void updateStatus(Long id, Integer integer, IRequestCycle cycle) {
		getGeneNodeStatusDAO().updateStatusForStatusWithId(id, integer);
		getGeneNodeStatus().setStatus(integer);
		finishCallback(cycle);		
	}
	private void updateSourceDbId(Long id, String newValue, IRequestCycle cycle) {
		getGeneNodeStatusDAO().updateSourceDbIdForStatusWithId(id, newValue);
		getGeneNodeStatus().setSourceDbId(newValue);
		finishCallback(cycle);		
	}
	private void updateSource(Long id, Integer integer, IRequestCycle cycle) {
		getGeneNodeStatusDAO().updateSourceDbForStatusWithId(id, integer);
		getGeneNodeStatus().setSource(integer);
		finishCallback(cycle);
	}
	private void finishCallback(IRequestCycle cycle) {
		ArrayList<Object> listenerParameters = new ArrayList<Object>();
		//GeneNodeStatusesSource source = getTaxaIndexPage().getStatusesSource();
		// parameters should have 3 things: 
		// (1) edit link id
		// (2) new class for edit link
		// (3) new link text for edit link
		String editLinkId = null; //source.getEditLinkIdForStatus(getGeneNodeStatus());
		if (editLinkId == null) {
			editLinkId = "";
		}
		listenerParameters.add(editLinkId);
		// TODO dna gene coloring change
		//Long nodeId = getGeneNodeStatus().getNodeId();
		//MappedNode node = getTaxaIndexPage().getNodeFromId(nodeId);
		String linkClass = null; //source.getEditLinkClassForStatus(getGeneNodeStatus());
		//String linkClass = source.getEditLinkClassForStatusAndTier(getGeneNodeStatus(), node.getAdditionalFields().getTier());
		if (linkClass == null) {
			linkClass = "";
		}
		listenerParameters.add(linkClass);
		String editLinkText = ""; //source.getEditLinkTextForStatus(getGeneNodeStatus());
		if (editLinkText == null) {
			editLinkText = "";
		}
		listenerParameters.add(editLinkText);
		cycle.setListenerParameters(listenerParameters.toArray());
	}
	public Map<String, Object> getScriptSymbols() {
		Map<String, Object> symbolsMap = new HashMap<String, Object>();
		symbolsMap.put("id", getGeneNodeStatus().getId());
		return symbolsMap;
	}
}
