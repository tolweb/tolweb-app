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
import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.btol.injections.GeneFragmentInjectable;
import org.tolweb.btol.injections.GeneFragmentNodeStatusInjectable;
import org.tolweb.btol.tapestry.selection.SequenceStatusModel;
import org.tolweb.btol.util.GeneFragmentNodeStatusesSource;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.selectionmodels.StringToIntWithOffsetPropertySelectionModel;

public abstract class EditGeneFragmentNodeStatus extends AbstractXTileEditPage implements
		IExternalPage, GeneFragmentNodeStatusInjectable,
		GeneFragmentInjectable, NodeInjectable {
	public static final int EDIT_SOURCE_CALLBACK = 0;
	public static final int EDIT_SOURCE_ID_CALLBACK = 1;
	public static final int EDIT_STATUS_CALLBACK = 2;
	public static final int EDIT_STATUS_NOTES_CALLBACK = 3;
	
	private StringToIntWithOffsetPropertySelectionModel statusSourceModel;
	
	@Persist("session")
	public abstract GeneFragmentNodeStatus getGeneFragmentNodeStatus();
	public abstract void setGeneFragmentNodeStatus(GeneFragmentNodeStatus value);
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
		GeneFragment gene = getGeneFragmentDAO().getGeneFragmentWithId(geneId, getProject().getId());
		if (gene == null) {
			// do some error handling here
		}
		
		GeneFragmentNodeStatus status = getGeneFragmentNodeStatusDAO().getStatusForGeneFragmentAndNodeIdInProject(gene, nodeId, getProject().getId());
		if (status == null) {
			status = getGeneFragmentNodeStatusDAO().createStatusForGeneFragmentAndNodeIdInProject(gene, nodeId, getProject().getId());
		}
		// make sure it's accessible for the base page
		getTaxaIndexPage().getStatusesSource().putStatus(status);		
		setGeneFragmentNodeStatus(status);
	}
	public String getTitle() {
		return "Edit Sequence Status for Gene Fragment " + getGeneFragmentNodeStatus().getGeneFragment().getName() + " and Taxon " + 
			getWorkingNodeDAO().getNameForNodeWithId(getGeneFragmentNodeStatus().getNodeId());
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
			case EDIT_STATUS_NOTES_CALLBACK:
				updateStatusNotes(id, newValue, cycle);
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
	public String getStatusNotesOnChangeString() {
		return getOnChangeString(EDIT_STATUS_NOTES_CALLBACK);
	}	
	private void updateStatus(Long id, Integer integer, IRequestCycle cycle) {
		getGeneFragmentNodeStatusDAO().updateStatusForStatusWithId(id, integer);
		getGeneFragmentNodeStatus().setStatus(integer);
		finishCallback(cycle);		
	}
	private void updateSourceDbId(Long id, String newValue, IRequestCycle cycle) {
		getGeneFragmentNodeStatusDAO().updateSourceDbIdForStatusWithId(id, newValue);
		getGeneFragmentNodeStatus().setSourceDbId(newValue);
		finishCallback(cycle);		
	}
	private void updateSource(Long id, Integer integer, IRequestCycle cycle) {
		getGeneFragmentNodeStatusDAO().updateSourceDbForStatusWithId(id, integer);
		getGeneFragmentNodeStatus().setSource(integer);
		finishCallback(cycle);
	}
	
	private void updateStatusNotes(Long id, String newNotes, IRequestCycle cycle) {
		getGeneFragmentNodeStatusDAO().updateStatusNotesForStatusWithId(id, newNotes);
		getGeneFragmentNodeStatus().setStatusNotes(newNotes);
		finishCallback(cycle);
	}
	
	private void finishCallback(IRequestCycle cycle) {
		ArrayList<Object> listenerParameters = new ArrayList<Object>();
		GeneFragmentNodeStatusesSource source = getTaxaIndexPage().getStatusesSource();
		// parameters should have 3 things: 
		// (1) edit link id
		// (2) new class for edit link
		// (3) new link text for edit link
		String editLinkId = source.getEditLinkIdForStatus(getGeneFragmentNodeStatus());
		if (editLinkId == null) {
			editLinkId = "";
		}
		listenerParameters.add(editLinkId);
		// TODO dna gene coloring change
		//Long nodeId = getGeneNodeStatus().getNodeId();
		//MappedNode node = getTaxaIndexPage().getNodeFromId(nodeId);
		String linkClass = source.getEditLinkClassForStatus(getGeneFragmentNodeStatus());
		//String linkClass = source.getEditLinkClassForStatusAndTier(getGeneNodeStatus(), node.getAdditionalFields().getTier());
		if (linkClass == null) {
			linkClass = "";
		}
		listenerParameters.add(linkClass);
		String editLinkText = source.getEditLinkTextForStatus(getGeneFragmentNodeStatus());
		if (editLinkText == null) {
			editLinkText = "";
		}
		listenerParameters.add(editLinkText);
		cycle.setListenerParameters(listenerParameters.toArray());
	}
	public Map<String, Object> getScriptSymbols() {
		Map<String, Object> symbolsMap = new HashMap<String, Object>();
		symbolsMap.put("id", getGeneFragmentNodeStatus().getId());
		return symbolsMap;
	}
/*
	public void clearSamplingInformation() {
		AdditionalFields fields = getAdditionalFields();
		fields.clearAllSamplingInformation();
		getAdditionalFieldsDAO().saveAdditionalFields(fields);
		MappedNode sourceNode = getTaxaIndexPage().getNodeWithName(getNodeName());
		// make sure memory snapshot is consistent 
		sourceNode.setAdditionalFields(fields);
		getTaxaIndexPage().updateAdditionalFields(fields);
		setClearedSamplingInfo(true);
		finishClearOrMove(sourceNode, null, true, true, true, true, true);
	}
 */	
	public void removeGeneFragmentInformation() {
		GeneFragmentNodeStatus nodeStat = getGeneFragmentNodeStatus();
		getGeneFragmentNodeStatusDAO().removeStatus(nodeStat);
		// the below 'reload' doesn't seem to work... need to look at doing a 'finishClear' javascript / classIDetc to update 
		//TaxaIndex index = getTaxaIndexPage();
		//index.reloadGeneNodeStatuses(index.getProjectId());
	}
}
