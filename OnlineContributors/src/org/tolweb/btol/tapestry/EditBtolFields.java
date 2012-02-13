package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Lifecycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.Project;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.btol.tapestry.selection.MicroCTSelectionModel;
import org.tolweb.btol.tapestry.selection.MtGenomeSelectionModel;
import org.tolweb.btol.tapestry.selection.SpecimensStateModel;
import org.tolweb.btol.tapestry.selection.TierExtensionSelectionModel;
import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.tapestry.ContributorSelectionModel;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.tapestry.annotations.MolecularPermissionNotRequired;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

@MolecularPermissionNotRequired
public abstract class EditBtolFields extends AbstractXTileEditPage implements ProjectInjectable, PageBeginRenderListener,
	NodeInjectable {
	public static final int TIER_CALLBACK = 0;
	public static final int LARVAL_POSSESSION_CALLBACK = 1;
	public static final int LARVAL_DESTINATION_CALLBACK = 2;
    public static final int LARVAL_SPECIMENS_CALLBACK = 3;
    public static final int TIER_NOTES_CALLBACK = 4;
    public static final int LARVAL_NOTES_CALLBACK = 5;
    public static final int ADULT_POSSESSION_CALLBACK = 6;
    public static final int ADULT_DESTINATION_CALLBACK = 7;
    public static final int ADULT_NOTES_CALLBACK = 8;
    public static final int DNA_POSSESSION_CALLBACK = 9;
    public static final int DNA_DESTINATION_CALLBACK = 10;
    public static final int DNA_NOTES_CALLBACK = 11;
    public static final int ADULT_SPECIMENS_CALLBACK = 12;
    public static final int DNA_SPECIMENS_CALLBACK = 13;
    public static final int MT_GENOME_CALLBACK = 14;
    public static final int GEOGRAPHIC_DISTRIBUTION_CALLBACK = 15;
    public static final int MICRO_CT_CALLBACK = 16;
    public static final int MICRO_CT_POSSESSION_CALLBACK = 17;
    public static final int MICRO_CT_NOTES_CALLBACK = 18;
    
    // textual-id values used in the .html and here to indicate the element to update
    public static final String DNA_SUPPLIER_BOX_ID = "dnaSupplierBox";
    public static final String MT_GENOME_STATUS_BOX_ID = "mtGenomeStatusBox";
    public static final String MICRO_CT_STATUS_BOX_ID = "microCTStatusBox";
    
	@Persist("client")
	public abstract String getNodeName();
	public abstract void setNodeName(String value);
	@Persist("session")
	public abstract AdditionalFields getAdditionalFields();
	public abstract void setAdditionalFields(AdditionalFields value);
	@Persist("session")
	public abstract Long getProjectId();
	public abstract void setProjectId(Long value);
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract TierExtensionSelectionModel getTierModel();
	@Bean(lifecycle = Lifecycle.PAGE)	
    public abstract SpecimensStateModel getSpecimensModel();
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract DNASpecimensStateModel getDnaSpecimensModel();
	@Bean(lifecycle = Lifecycle.PAGE)	
	public abstract PIDNASpecimensStateModel getPIDNASpecimensStateModel();
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract MtGenomeSelectionModel getMtGenomeModel();
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract MicroCTSelectionModel getMicroCTModel();
	@InjectPage("TaxaIndex")
	public abstract TaxaIndex getTaxaIndexPage();
	@Asset("img/plus.gif")
	public abstract IAsset getPlusImage();
	@Persist("session")
	public abstract boolean getUseAutocompleter();
	public abstract void setToggledAutocompleter(boolean value);
	public abstract boolean getToggledAutocompleter();
	
	public abstract void setWrongNodeName(String value);
	public abstract void setDestinationNotDescendant(boolean value);
	@SuppressWarnings("unchecked")
	public List getNodeNames() {
		return getTaxaIndexPage().getNodeNames();
	}
	
	public String getDnaSupplierBoxId() {
		return DNA_SUPPLIER_BOX_ID;
	}
	
	public String getMtGenomeStatusBoxId() {
		return MT_GENOME_STATUS_BOX_ID;
	}
	
	public String getMicroCTStatusBoxId() {
		return MICRO_CT_STATUS_BOX_ID;
	}
	
	public IPropertySelectionModel getDnaSpecimensModelForContributor() {
		if (getProject().getContributorCanEditDna(getContributor())) {
			return getPIDNASpecimensStateModel();
		} else {
			return getDnaSpecimensModel();
		}
	}
	/**
	 * String showing the state when contributors don't have permission
	 * to edit
	 * @return
	 */
	public String getDnaSpecimensState() {
		// use the PI model to get the label in order to ensure that all possible
		// values are handled
		return getPIDNASpecimensStateModel().getLabel(getAdditionalFields().getDnaSpecimensState());
	}
	
	public EditBtolFields editAdditionalFields(MappedNode node, Long projectId) {
		AdditionalFields fields = node.getAdditionalFields();
		// make sure we have the latest version from the db
		fields = getProjectDAO().getAdditionalFieldsForNodeInProject(node, projectId);
        setAdditionalFields(fields);
		setNodeName(node.getName());
		setProjectId(projectId);
		return this;		
	}

	protected void doCallback(int callbackType, Long id, String newValue, IRequestCycle cycle) {
		Contributor newContributor = (Contributor) getContributorSelectionModel().translateValue(newValue);
		Integer newSpecimensState = (Integer) getSpecimensModel().translateValue(newValue);
		System.out.println("callback firing with type: " + callbackType);
		if (callbackType == TIER_CALLBACK) {
			updateTier(id, newValue, cycle);
		} else if (callbackType == LARVAL_POSSESSION_CALLBACK) {
			updateLarvalPossessionPerson(getAdditionalFields(), newContributor, cycle);
		} else if (callbackType == LARVAL_DESTINATION_CALLBACK) {
			updateLarvalDestinationPerson(getAdditionalFields(), newContributor, cycle);
		} else if (callbackType == LARVAL_SPECIMENS_CALLBACK) {
            updateLarvalSpecimensState(getAdditionalFields(), newSpecimensState, cycle);
        } else if (callbackType == TIER_NOTES_CALLBACK) {
        	updateTierNotes(getAdditionalFields(), newValue, cycle);
        } else if (callbackType == LARVAL_NOTES_CALLBACK) {
        	updateLarvalNotes(getAdditionalFields(), newValue, cycle);
        } else if (callbackType == ADULT_POSSESSION_CALLBACK) {
        	updateAdultPossessionPerson(getAdditionalFields(), newContributor, cycle);
        } else if (callbackType == ADULT_DESTINATION_CALLBACK) {
        	updateAdultDestinationPerson(getAdditionalFields(), newContributor, cycle);
        } else if (callbackType == ADULT_NOTES_CALLBACK) {
        	updateAdultNotes(getAdditionalFields(), newValue, cycle);
        } else if (callbackType == ADULT_SPECIMENS_CALLBACK) {
        	updateAdultSpecimensState(getAdditionalFields(), newSpecimensState, cycle);
        } else if (callbackType == DNA_POSSESSION_CALLBACK) {
        	updateDnaPossessionPerson(getAdditionalFields(), newContributor, cycle);
        } else if (callbackType == DNA_DESTINATION_CALLBACK) {
        	updateDnaDestinationPerson(getAdditionalFields(), newContributor, cycle);
        } else if (callbackType == DNA_NOTES_CALLBACK) {
        	updateDnaNotes(getAdditionalFields(), newValue, cycle);
        } else if (callbackType == DNA_SPECIMENS_CALLBACK) {
        	updateDnaSpecimensState(getAdditionalFields(), newSpecimensState, cycle);
        } else if (callbackType == MT_GENOME_CALLBACK) {
        	Integer mtGenomeState = (Integer) getMtGenomeModel().translateValue(newValue);
        	// mtGenome has a 'not-needed' status added, this was implemented by using a negative-offset
        	updateMtGenomeState(getAdditionalFields(), mtGenomeState - 1, cycle);
        } else if (callbackType == GEOGRAPHIC_DISTRIBUTION_CALLBACK) {
        	updateGeographicDistributionNotes(getAdditionalFields(), newValue, cycle);
        } else if (callbackType == MICRO_CT_CALLBACK) {
        	Integer microCTState = (Integer) getMicroCTModel().translateValue(newValue);
        	updateMicroCTState(getAdditionalFields(), microCTState, cycle);
        } else if (callbackType == MICRO_CT_NOTES_CALLBACK) {
        	updateMicroCTNotes(getAdditionalFields(), newValue, cycle);
        } else if (callbackType == MICRO_CT_POSSESSION_CALLBACK) {
        	updateMicroCTPossessionPerson(getAdditionalFields(), newContributor, cycle);
        }
	}

	private void updateMtGenomeState(AdditionalFields additionalFields, Integer mtGenomeState, IRequestCycle cycle) {
		additionalFields.setMtGenomeState(mtGenomeState);
		getAdditionalFieldsDAO().updateMtGenomeStateForAdditionalFields(mtGenomeState, additionalFields.getId());
		finishCollectionCallback(MT_GENOME_CALLBACK, additionalFields, cycle, null);
	}
	private void updateDnaSpecimensState(AdditionalFields additionalFields, Integer newSpecimensState, IRequestCycle cycle) {
		additionalFields.setDnaSpecimensState(newSpecimensState);
		getAdditionalFieldsDAO().updateDnaSpecimensStateForAdditionalFields(newSpecimensState, additionalFields.getId());
		String elementIdToUpdate = null;
		if (additionalFields.getDnaPossessionPersonId() == null) {
			// if there is no dna possession person, we want to set the person editing to be 
			// the dna possession person
			updateDnaPossessionPerson(getAdditionalFields(), getContributor(), cycle);
			elementIdToUpdate = getDnaSupplierBoxId();
		}
		finishCollectionCallback(DNA_SPECIMENS_CALLBACK, additionalFields, cycle, elementIdToUpdate);
	}
	private void updateGeographicDistributionNotes(AdditionalFields additionalFields, String newValue, IRequestCycle cycle) {
		additionalFields.setGeographicDistribution(newValue);
		getAdditionalFieldsDAO().updateGeographicDistributionForAdditionalFields(newValue, additionalFields.getId());
	}	
	private void updateDnaNotes(AdditionalFields additionalFields, String newValue, IRequestCycle cycle) {
		additionalFields.setDnaNotes(newValue);
		getAdditionalFieldsDAO().updateDnaNotesForAdditionalFields(newValue, additionalFields.getId());
		finishCollectionCallback(DNA_NOTES_CALLBACK, additionalFields, cycle, null);
	}
	private void updateDnaDestinationPerson(AdditionalFields additionalFields, Contributor newContributor, IRequestCycle cycle) {
		additionalFields.setDnaDestinationPerson(newContributor);
		getAdditionalFieldsDAO().updateDnaDestinationPersonForAdditionalFields(newContributor, additionalFields.getId());
		finishCollectionCallback(DNA_DESTINATION_CALLBACK, additionalFields, cycle, null);
	}
	private void updateDnaPossessionPerson(AdditionalFields additionalFields, Contributor newContributor, IRequestCycle cycle) {
		additionalFields.setDnaPossessionPerson(newContributor);
		getAdditionalFieldsDAO().updateDnaPossessionPersonForAdditionalFields(newContributor, additionalFields.getId());
		finishCollectionCallback(DNA_POSSESSION_CALLBACK, additionalFields, cycle, null);
	}
	private void updateAdultSpecimensState(AdditionalFields additionalFields, Integer newSpecimensState, IRequestCycle cycle) {
		additionalFields.setAdultSpecimensState(newSpecimensState);
		getAdditionalFieldsDAO().updateAdultSpecimensStateForAdditionalFields(newSpecimensState, additionalFields.getId());
		finishCollectionCallback(ADULT_SPECIMENS_CALLBACK, additionalFields, cycle, null);
	}
	private void updateAdultNotes(AdditionalFields additionalFields, String newValue, IRequestCycle cycle) {
		additionalFields.setAdultNotes(newValue);
		getAdditionalFieldsDAO().updateAdultNotesForAdditionalFields(newValue, additionalFields.getId());
		finishCollectionCallback(ADULT_NOTES_CALLBACK, additionalFields, cycle, null);	
	}
	private void updateAdultDestinationPerson(AdditionalFields additionalFields, Contributor newContributor, IRequestCycle cycle) {
		additionalFields.setAdultDestinationPerson(newContributor);
		getAdditionalFieldsDAO().updateAdultDestinationPersonForAdditionalFields(newContributor, additionalFields.getId());
		finishCollectionCallback(ADULT_DESTINATION_CALLBACK, additionalFields, cycle, null);
	}
	private void updateAdultPossessionPerson(AdditionalFields additionalFields, Contributor newContributor, IRequestCycle cycle) {
		additionalFields.setAdultPossessionPerson(newContributor);
		getAdditionalFieldsDAO().updateAdultPossessionPersonForAdditionalFields(newContributor, additionalFields.getId());
		finishCollectionCallback(ADULT_POSSESSION_CALLBACK, additionalFields, cycle, null);
	}
	@SuppressWarnings("unchecked")
	private void updateTierNotes(AdditionalFields fields, String newValue, IRequestCycle cycle) {
		fields.setTierNotes(newValue);
		getAdditionalFieldsDAO().updateTierNotesForAdditionalFields(newValue, fields.getId());
		MappedNode node = getTaxaIndexPage().updateAdditionalFields(fields);
		ArrayList fieldsToUpdate = new ArrayList();
		setNodeOnIndexPage(node);
		addTierUpdateFieldsForNode(node, fieldsToUpdate);
		fieldsToUpdate.add(0, TIER_NOTES_CALLBACK);
		cycle.setListenerParameters(fieldsToUpdate.toArray());
	}
	private void updateLarvalNotes(AdditionalFields fields, String newValue, IRequestCycle cycle) {
		fields.setLarvalNotes(newValue);
		getAdditionalFieldsDAO().updateLarvalNotesForAdditionalFields(newValue, fields.getId());
		finishCollectionCallback(LARVAL_NOTES_CALLBACK, fields, cycle, null);
	}
	private void updateLarvalSpecimensState(AdditionalFields additionalFields, Integer newSpecimensState, IRequestCycle cycle) {
	    getAdditionalFieldsDAO().updateLarvalSpecimensStateForAdditionalFields(newSpecimensState, additionalFields.getId());
        additionalFields.setLarvalSpecimensState(newSpecimensState);
        finishCollectionCallback(LARVAL_SPECIMENS_CALLBACK, additionalFields, cycle, null);
    }
    private void updateLarvalDestinationPerson(AdditionalFields fields, Contributor contr, IRequestCycle cycle) {
		getAdditionalFieldsDAO().updateLarvalDestinationPersonForAdditionalFields(contr, fields.getId());
		fields.setLarvalDestinationPerson(contr);
		finishCollectionCallback(LARVAL_DESTINATION_CALLBACK, fields, cycle, null);
	}
	private void updateLarvalPossessionPerson(AdditionalFields fields, Contributor contr, IRequestCycle cycle) {
		getAdditionalFieldsDAO().updateLarvalPossessionPersonForAdditionalFields(contr, fields.getId());
		fields.setLarvalPossessionPerson(contr);
		finishCollectionCallback(LARVAL_POSSESSION_CALLBACK, fields, cycle, null);
	}
	private void updateMicroCTPossessionPerson(AdditionalFields fields, Contributor contr, IRequestCycle cycle) {
		getAdditionalFieldsDAO().updateMicroCTPossessionPersonForAdditionalFields(contr, fields.getId());
		fields.setMicroCTPossessionPerson(contr);
		finishCollectionCallback(MICRO_CT_POSSESSION_CALLBACK, fields, cycle, null);
	}
	private void updateMicroCTNotes(AdditionalFields fields, String newValue, IRequestCycle cycle) {
		fields.setMicroCTNotes(newValue);
		getAdditionalFieldsDAO().updateMicroCTNotesForAdditionalFields(newValue, fields.getId());
		finishCollectionCallback(MICRO_CT_NOTES_CALLBACK, fields, cycle, null);
	}
	private void updateMicroCTState(AdditionalFields fields, Integer microCTState, IRequestCycle cycle) {
		fields.setMicroCTState(microCTState);
		getAdditionalFieldsDAO().updateMicroCTStateForAdditionalFields(microCTState, fields.getId());
		finishCollectionCallback(MICRO_CT_CALLBACK, fields, cycle, null);
	}	
	@SuppressWarnings("unchecked")
	private void finishCollectionCallback(int callbackType, AdditionalFields fields, IRequestCycle cycle, String elementIdToUpdate) {
		TaxaIndex indexPage = getTaxaIndexPage();
		MappedNode node = indexPage.updateAdditionalFields(fields);
		setNodeOnIndexPage(node);		
		List params = getIdsNamesAndTextToUpdate(node, callbackType);
		if (StringUtils.notEmpty(elementIdToUpdate)) {
			if (elementIdToUpdate.equals(DNA_SUPPLIER_BOX_ID)) {
				// assume this is a combobox (the dna supplier, specifically), 
				// so send the arg in the form elementId:index
				// where index is the value to set as the selected
				params.add(0, elementIdToUpdate + ":" + getContributorSelectionModel().getMeIndex());
			} else if (elementIdToUpdate.equals(MT_GENOME_STATUS_BOX_ID)){
				// assume that a tier-change has happened and has trigger the mt genome stats to be switched to 'needed'
				params.add(0, elementIdToUpdate + ":" + AdditionalFields.NO_MT_GENOME + 1);
			} else {
				// a tier-change has happened and has trigger the microCT status to be switched to 'needed'
				if (fields.getMicroCTState() == AdditionalFields.MICRO_CT_NOT_NEEDED) {
					params.add(0, elementIdToUpdate + ":" + AdditionalFields.MICRO_CT_NEED_SPECIMEN);
				} // if the microCT state of the specimen is has-specimen or complete - we don't want to muck with it
			}
		} else {
			params.add(0, callbackType);
		}
		cycle.setListenerParameters(params.toArray());
	}
	@SuppressWarnings("unchecked")
	private void updateTier(Long additionalFieldsId, String tierValue, IRequestCycle cycle) {
		Integer newTier = (Integer) getTierModel().translateValue(tierValue);
		getAdditionalFieldsDAO().updateTierForAdditionalFields(newTier, additionalFieldsId);
		// want to update this in memory as well
		AdditionalFields fields = getAdditionalFields();
		fields.setTier(newTier);
		// now adjust values on the main page
		TaxaIndex indexPage = getTaxaIndexPage();		
		MappedNode nodeToUpdate = indexPage.updateAdditionalFields(fields);
		if (nodeToUpdate != null) {
			setNodeOnIndexPage(nodeToUpdate);
			// now make a list of all parents (to page root, that need to have their classes updated)
			List classIdsNamesAndText = getIdsNamesAndTextToUpdate(nodeToUpdate, TIER_CALLBACK);
			// since the tier can change the color of all the other fields, need to 
			// add all of them to the update list as well
			addDnaCollectionUpdateFieldsForNode(nodeToUpdate, classIdsNamesAndText);
			addHiddenAttributesForNode(nodeToUpdate, classIdsNamesAndText);		
			addAdultCollectionUpdateFieldsForNode(nodeToUpdate, classIdsNamesAndText);
			addHiddenAttributesForNode(nodeToUpdate, classIdsNamesAndText);		
			addLarvalCollectionUpdateFieldsForNode(nodeToUpdate, classIdsNamesAndText);
			addHiddenAttributesForNode(nodeToUpdate, classIdsNamesAndText);	
			addMicroCTStateUpdateFieldsForNode(nodeToUpdate, classIdsNamesAndText);
			addHiddenAttributesForNode(nodeToUpdate, classIdsNamesAndText);	
			classIdsNamesAndText.add(0, TIER_CALLBACK);
			Object[] listenerParams = classIdsNamesAndText.toArray();
			cycle.setListenerParameters(listenerParams);			
		}
		
		if (newTier == 1) {
			updateMtGenomeState(getAdditionalFields(), AdditionalFields.NO_MT_GENOME, cycle);
			String elementIdToUpdate = getMtGenomeStatusBoxId();
			finishCollectionCallback(TIER_CALLBACK, getAdditionalFields(), cycle, elementIdToUpdate);
		}
		if (newTier == 0) {
			// if the tier is changed to Tier 0 - it needs a microCT
			updateMicroCTState(getAdditionalFields(), AdditionalFields.MICRO_CT_NEED_SPECIMEN, cycle);
			String elementIdToUpdate = getMicroCTStatusBoxId();
			finishCollectionCallback(TIER_CALLBACK, getAdditionalFields(), cycle, elementIdToUpdate);
		}
	}
	
	/**
	 * Builds a list of html element to update by walking up the tree
	 * @param startingNode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List getIdsNamesAndTextToUpdate(MappedNode nodeToUpdate, int callbackType) {
		List classIdsNamesAndText = new ArrayList();
		MappedNode currentNode = nodeToUpdate;
		while (currentNode != null) {
			if (callbackType == TIER_CALLBACK || callbackType == MT_GENOME_CALLBACK) {
				addTierUpdateFieldsForNode(currentNode, classIdsNamesAndText);
			} else if (callbackType == LARVAL_SPECIMENS_CALLBACK || callbackType == LARVAL_DESTINATION_CALLBACK
					|| callbackType == LARVAL_POSSESSION_CALLBACK || callbackType == LARVAL_NOTES_CALLBACK){
				addLarvalCollectionUpdateFieldsForNode(currentNode, classIdsNamesAndText);
			} else if (callbackType == ADULT_SPECIMENS_CALLBACK || callbackType == ADULT_DESTINATION_CALLBACK
					|| callbackType == ADULT_POSSESSION_CALLBACK || callbackType == ADULT_NOTES_CALLBACK) {
				addAdultCollectionUpdateFieldsForNode(currentNode, classIdsNamesAndText);
			} else if (callbackType == DNA_SPECIMENS_CALLBACK || callbackType == DNA_DESTINATION_CALLBACK
					|| callbackType == DNA_POSSESSION_CALLBACK || callbackType == DNA_NOTES_CALLBACK) {
				addDnaCollectionUpdateFieldsForNode(currentNode, classIdsNamesAndText);
			} else if (callbackType == MICRO_CT_CALLBACK || callbackType == MICRO_CT_POSSESSION_CALLBACK
					|| callbackType == MICRO_CT_NOTES_CALLBACK) {
				addMicroCTStateUpdateFieldsForNode(currentNode, classIdsNamesAndText);
			}
			addHiddenAttributesForNode(currentNode, classIdsNamesAndText);
			currentNode = (MappedNode) currentNode.getParent();
		}
		return classIdsNamesAndText;
	}
	@SuppressWarnings("unchecked")
	private void addHiddenAttributesForNode(MappedNode currentNode, List classIdsNamesAndText) {
		// all of these need to have the special attribute that indicates
		// whether these should be shown internally (whether closed or not)
		String showAttribute = getTaxaIndexPage().getAlwaysShowAttribute(currentNode);
		String attributeVal = StringUtils.notEmpty(showAttribute) ? "true" : "false"; 
		classIdsNamesAndText.add(attributeVal);
		String ulId = getTaxaIndexPage().getTaxonList().getUlIdForNode(currentNode);
		classIdsNamesAndText.add(ulId);
		String openManyAttribute = getTaxaIndexPage().getExpandMostAttribute(currentNode);
		attributeVal = StringUtils.notEmpty(openManyAttribute) ? "true" : "false";
		classIdsNamesAndText.add(attributeVal);		
	}
	
	private void setNodeOnIndexPage(MappedNode nodeToUpdate) {
		TaxaIndex indexPage = getTaxaIndexPage();
		MappedNode currentNode = nodeToUpdate;	
		boolean foundRoot = false;
		while (true && currentNode != null) {
			if (currentNode.getParent() != null) {
				currentNode = (MappedNode) currentNode.getParent();
			} else {
				indexPage.setNode(currentNode);
				foundRoot = true;
				break;
			}
		}
		if (!foundRoot) {
			// in the case where memory contents don't match the editor, just
			// set things to be the node being updated
			indexPage.setNode(nodeToUpdate);
		}
		// now that this has been updated, need to update the tiers, since ancestors
		// might also have had their tiers changed
		indexPage.setupCalculatedValues();		
	}
	/**
	 * Adds the required fields for a node that need to be sent in the 
	 * request cycle listener parameters when something related to the
	 * tier changes
	 * @param node
	 * @param fieldsList
	 */
	@SuppressWarnings("unchecked")
	private void addTierUpdateFieldsForNode(MappedNode node, List classIdsNamesAndText) {
		TaxaIndex indexPage = getTaxaIndexPage();
		// get id, className, and text for the nodes to update.  these are sent to 
		// javascript in order to do dynamic refresh
		int tierForNode = indexPage.getCalculatedTierForNode(node);
		String linkId = indexPage.getEditTierLinkIdForNode(node);
        String newClassname = indexPage.getTierClassForNode(node);
        boolean hasNotes = node.getAdditionalFields().getHasTierNotes();
        String newText = indexPage.getTierTextForTier(tierForNode, hasNotes);
        String actualClassname = indexPage.getActualTierClassForNode(node);        
		classIdsNamesAndText.add(linkId);        
		classIdsNamesAndText.add(newClassname);
		classIdsNamesAndText.add(newText);
        classIdsNamesAndText.add(actualClassname);
	}
	@SuppressWarnings("unchecked")
	private void addLarvalCollectionUpdateFieldsForNode(MappedNode node, List classIdsNamesAndText) {
		TaxaIndex indexPage = getTaxaIndexPage();
		classIdsNamesAndText.add(indexPage.getEditLarvaeLinkIdForNode(node));
		classIdsNamesAndText.add(indexPage.getLarvaeClassForNode(node));		
		classIdsNamesAndText.add(indexPage.getLarvaeTextForNode(node));
        classIdsNamesAndText.add(indexPage.getActualLarvaeClassForNode(node));
	}
	@SuppressWarnings("unchecked")
	private void addAdultCollectionUpdateFieldsForNode(MappedNode node, List elements) {
		TaxaIndex indexPage = getTaxaIndexPage();
		elements.add(indexPage.getEditAdultLinkIdForNode(node));
		elements.add(indexPage.getAdultClassForNode(node));
		elements.add(indexPage.getAdultTextForNode(node));
        elements.add(indexPage.getActualAdultClassForNode(node));		
	}
	@SuppressWarnings("unchecked")
	private void addDnaCollectionUpdateFieldsForNode(MappedNode node, List elements) {
		TaxaIndex indexPage = getTaxaIndexPage();
		elements.add(indexPage.getEditDnaLinkIdForNode(node));
		elements.add(indexPage.getDnaClassForNode(node));
		elements.add(indexPage.getDnaTextForNode(node));
        elements.add(indexPage.getActualDnaClassForNode(node));		
	}
	@SuppressWarnings("unchecked")
	private void addMicroCTStateUpdateFieldsForNode(MappedNode node, List elements) {
		TaxaIndex indexPage = getTaxaIndexPage();
		elements.add(indexPage.getEditMicroCTLinkIdForNode(node));
		elements.add(indexPage.getMicroCTClassForNode(node));
		elements.add("&nbsp;"); // don't want anything to display
		elements.add(indexPage.getActualMicroCTClassForNode(node));
	}
	public Map<String, Object> getScriptSymbols() {
		Map<String, Object> symbolsMap = new HashMap<String, Object>();
		symbolsMap.put("additionalFieldsId", getAdditionalFields().getId());
		symbolsMap.put("tierCallback", TIER_CALLBACK);
		symbolsMap.put("larvalCallback", LARVAL_POSSESSION_CALLBACK);
		symbolsMap.put("movedFields", getMovedSamplingInfo() || getClearedSamplingInfo());
		symbolsMap.put("arrayVals", getArrayString());
		return symbolsMap;
	}
	@SuppressWarnings("unchecked")
	public ContributorSelectionModel getContributorSelectionModel() {
		Project project = getProject();
		List members = project.getAllProjectMembers();
		Collections.sort(members, new ContributorNameComparator());
		ContributorSelectionModel model = new ContributorSelectionModel(members, true, true, getContributor());
		return model;
	}
	public String getTierOnChangeString() {
		return getOnChangeString(TIER_CALLBACK);
	}
	public String getLarvalPossessionOnChangeString() {
		return getOnChangeString(LARVAL_POSSESSION_CALLBACK);
	}
	public String getLarvalDestinationOnChangeString() {
		return getOnChangeString(LARVAL_DESTINATION_CALLBACK);
	}
	public String getAdultPossessionOnChangeString() {
		return getOnChangeString(ADULT_POSSESSION_CALLBACK);
	}
	public String getAdultDestinationOnChangeString() {
		return getOnChangeString(ADULT_DESTINATION_CALLBACK);
	}	
	public String getDnaPossessionOnChangeString() {
		return getOnChangeString(DNA_POSSESSION_CALLBACK);
	}
	public String getDnaDestinationOnChangeString() {
		return getOnChangeString(DNA_DESTINATION_CALLBACK);
	}	
    public String getLarvalSpecimensOnChangeString() {
        return getOnChangeString(LARVAL_SPECIMENS_CALLBACK);
    }
    public String getAdultSpecimensOnChangeString() {
    	return getOnChangeString(ADULT_SPECIMENS_CALLBACK);
    }
    public String getDnaSpecimensOnChangeString() {
    	return getOnChangeString(DNA_SPECIMENS_CALLBACK);
    }
    public String getTierNotesOnChangeString() {
    	return getOnChangeString(TIER_NOTES_CALLBACK);
    }
    public String getLarvalNotesOnChangeString() {
    	return getOnChangeString(LARVAL_NOTES_CALLBACK);
    }
    public String getAdultNotesOnChangeString() {
    	return getOnChangeString(ADULT_NOTES_CALLBACK);
    }
    public String getDnaNotesOnChangeString() {
    	return getOnChangeString(DNA_NOTES_CALLBACK);
    }
    public String getGeographicDistributionOnChangeString() {
    	return getOnChangeString(GEOGRAPHIC_DISTRIBUTION_CALLBACK);
    }
    public String getMtGenomeOnChangeString() {
    	return getOnChangeString(MT_GENOME_CALLBACK);
    }
    public String getMicroCTOnChangeString() {
    	return getOnChangeString(MICRO_CT_CALLBACK);
    }
    public String getMicroCTPossessionOnChangeString() {
    	return getOnChangeString(MICRO_CT_POSSESSION_CALLBACK);
    }
    public String getMicroCTNotesOnChangeString() {
    	return getOnChangeString(MICRO_CT_NOTES_CALLBACK);
    }
	/*
	 * ------------------------------------------------------------------------
	 */
	public abstract void setMovedSamplingInfo(boolean value);
	public abstract boolean getMovedSamplingInfo();
	public abstract void setClearedSamplingInfo(boolean value);
	public abstract boolean getClearedSamplingInfo();
	public abstract void setSourceNodeId(Long value);
	public abstract void setDestinationNodeId(Long value);
	public abstract String getDestinationNodeName();
	public abstract boolean getMoveTier();
	public abstract void setMoveTier(boolean value);	
	public abstract boolean getMoveDNA();
	public abstract void setMoveDNA(boolean value);	
	public abstract boolean getMoveAdult();
	public abstract void setMoveAdult(boolean value);
	public abstract boolean getMoveLarvae();
	public abstract void setMoveLarvae(boolean value);
	public abstract boolean getMoveMtGenome();
	public abstract void setMoveMtGenome(boolean value);
	public abstract boolean getMoveMicroCT();
	public abstract void setMoveMicroCT(boolean value);	
	public abstract void setArrayString(String value);
	public abstract String getArrayString();
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			setMoveTier(true);
			setMoveDNA(true);
			setMoveAdult(true);
			setMoveLarvae(true);
			setMoveMtGenome(true);
			setMoveMicroCT(true);
		}
		if (StringUtils.isEmpty(getArrayString())) {
			setArrayString("[]");
		}
	}
	
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
	@SuppressWarnings("unchecked")
	public void moveSamplingInformation() {
		String destNodeName = getDestinationNodeName();
		MappedNode destNode = getTaxaIndexPage().getNodeWithName(destNodeName);
		if (destNode == null) {
			// check to see if this is an existing taxon, but just not
			// on the page
			List nodes = getWorkingNodeDAO().findNodesExactlyNamed(destNodeName);
			if (nodes.size() > 0) {
				destNode = (MappedNode) nodes.get(0);
				// check to make sure it's a descendant of the project root
				Long projectRootNodeId = getProject().getRootNodeId();
				boolean isDescendant = getMiscNodeDAO().getNodeIsAncestor(destNode.getNodeId(), projectRootNodeId);
				if (!isDescendant) {
					setDestinationNotDescendant(true);
					return;
				} else {
					AdditionalFields fields = getProjectDAO().getAdditionalFieldsForNodeInProject(destNode, getProjectId());
					if (fields == null) {
						List nodeIdList = new ArrayList();
						nodeIdList.add(destNode.getNodeId());
						getProjectDAO().createFieldsForNodeIdsInProject(nodes, projectRootNodeId);
						fields = getProjectDAO().getAdditionalFieldsForNodeInProject(destNode, getProjectId());
					}
					destNode.setAdditionalFields(fields);
				}
			} else {
				setWrongNodeName(getDestinationNodeName());
				return;
			}
		}
		MappedNode sourceNode = getTaxaIndexPage().getNodeWithName(getNodeName());
		AdditionalFields sourceFields = getAdditionalFields();
		AdditionalFields destFields = destNode.getAdditionalFields();
		getAdditionalFieldsDAO().moveAdditionalFieldsToOther(sourceFields, destFields, 
				getMoveTier(), getMoveMtGenome(), getMoveDNA(), getMoveAdult(), getMoveLarvae());
		setMovedSamplingInfo(true);
		getTaxaIndexPage().updateAdditionalFields(destFields);
		getTaxaIndexPage().updateAdditionalFields(sourceFields);
		finishClearOrMove(sourceNode, destNode, getMoveTier(), getMoveMtGenome(), getMoveAdult(),
				getMoveLarvae(), getMoveDNA());
	}
	public void toggleAutocompleter() {
		setToggledAutocompleter(true);
	}
	public String getClearedOrMoved() {
		if (getMovedSamplingInfo()) {
			return "Moved";
		} else {
			return "Cleared";
		}
	}
	@SuppressWarnings("unchecked")
	private void finishClearOrMove(MappedNode sourceNode, MappedNode destNode, 
			boolean moveTier, boolean moveMtGenome, boolean moveAdult, boolean moveLarvae, 
			boolean moveDNA) {
		List classIdsNamesAndText = new ArrayList();
		setNodeOnIndexPage(sourceNode);	
		if (moveTier || moveMtGenome) {
			if (destNode != null) {
				classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(destNode, TIER_CALLBACK));
			}
			classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(sourceNode, TIER_CALLBACK));
		}
		// do one each of adult, larvae, and dna -- doesn't matter which one
		if (moveAdult || moveTier) {
			if (destNode != null) {
				classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(destNode, ADULT_DESTINATION_CALLBACK));
			}
			classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(sourceNode, ADULT_DESTINATION_CALLBACK));
		}
		if (moveLarvae || moveTier) {
			if (destNode != null) {
				classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(destNode, LARVAL_DESTINATION_CALLBACK));
			}
			classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(sourceNode, LARVAL_DESTINATION_CALLBACK));
		}
		if (moveDNA || moveTier) {
			if (destNode != null) {
				classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(destNode, DNA_DESTINATION_CALLBACK));
			}
			classIdsNamesAndText.addAll(getIdsNamesAndTextToUpdate(sourceNode, DNA_DESTINATION_CALLBACK));
		}
		// the response is expecting a number as the first param
		classIdsNamesAndText.add(0, TIER_CALLBACK);
		setArrayString(getJavascriptArrayString(classIdsNamesAndText));		
	}
	public String getProjectRootName() {
		return getWorkingNodeDAO().getNameForNodeWithId(getProject().getRootNodeId());
	}
	public BaseDAO getDAO() {
		return getAdditionalFieldsDAO();
	}
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return AdditionalFields.class;
	}
	public void setObjectToEdit(Object value) {
		setAdditionalFields((AdditionalFields) value);
	}
	public AbstractEditPage editNewObject(IPage page) {
		return null;
	}
	public boolean getCanEditDnaSpecimensState() {
		int specimensState = getAdditionalFields().getDnaSpecimensState();
		if (specimensState == AdditionalFields.HAVE_DNA) {
			return getProject().getContributorCanEditDna(getContributor());
		} else {
			return true;
		}
	}
	/**
	 * create a string that is a javascript array
	 * @param strings
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getJavascriptArrayString(List strings) {
		StringBuilder returnString = new StringBuilder();
		returnString.append("[");
		for (Iterator iter = strings.iterator(); iter.hasNext();) {
			Object nextObj = iter.next();
			returnString.append("'");
			returnString.append(nextObj.toString());
			returnString.append("'");
			if (iter.hasNext()) {
				returnString.append(", ");
			}
		}
		returnString.append("]");
		return returnString.toString();
	}
}
