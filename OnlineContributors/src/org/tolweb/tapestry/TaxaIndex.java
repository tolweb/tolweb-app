package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Lifecycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.event.PageAttachListener;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.link.DefaultLinkRenderer;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.Project;
import org.tolweb.btol.injections.GeneFragmentInjectable;
import org.tolweb.btol.injections.GeneFragmentNodeStatusInjectable;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.btol.tapestry.AssignSequenceDefaults;
import org.tolweb.btol.tapestry.EditBtolFields;
import org.tolweb.btol.tapestry.selection.NeededFilterSelectionModel;
import org.tolweb.btol.tapestry.selection.TierFilterSelectionModel;
import org.tolweb.btol.util.GeneFragmentNodeStatusesSource;
import org.tolweb.btol.util.NodeFilterHelper;
import org.tolweb.btol.util.TaxonSamplingHelper;
import org.tolweb.btol.validation.ProjectValidator;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.adt.HashtableTree;
import org.tolweb.tapestry.annotations.MolecularPermissionNotRequired;
import org.tolweb.tapestry.helpers.OtherGroupsHelper;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

@MolecularPermissionNotRequired
public abstract class TaxaIndex extends AbstractBranchOrLeafPage implements IExternalPage,
		PageBeginRenderListener, ProjectInjectable, NodeInjectable, PageInjectable, 
		UserInjectable, PageAttachListener, PageDetachListener, GeneInjectable, 
		GeneFragmentInjectable, GeneFragmentNodeStatusInjectable, CookieInjectable {
	public static final String NOTIER = "notier";
	public static final String INVISIBLE = "invisible";
	public static final String HASSPECIMENS = "hasspecimens";
	public static final String SPECIMENSNOSUPPLIER = "specimensnosupplier";
	public static final String NEEDSSPECIMENS = "needsspecimens";
	public static final boolean RALLY_TROOPS_MODE = true;
    public static final String HIDE_INTERNAL_HIDDEN_ID = "hideinternalnodes";
    public static final String ALWAYS_SHOW_ATTRIBUTE = "alwaysshow";
    public static final String SHOW_MOST = "showmost";
	private PopupLinkRenderer renderer; 
	private SupplierContributorSelectionModel supplierSelectionModel;
	@Asset("css/taxonsampling.css")
	public abstract IAsset getBtolStylesheetAsset();
    @Asset("img/legend.jpg")
    public abstract IAsset getLegendImage();
    @Asset("img/help.gif")
    public abstract IAsset getHelpImage();
    @Asset("img/printernew.png")
    public abstract IAsset getPrinterImage();

	public abstract String getNodeName();
	
	// error conditions
	public abstract void setNoNode(boolean value);
	public abstract void setMissingNodeName(String value);
	
	@InjectObject("spring:otherGroupsHelper")
	public abstract OtherGroupsHelper getOtherGroupsHelper();
	public abstract OtherGroupsHelper getHelper();
	public abstract void setHelper(OtherGroupsHelper helper);
	
	public void highlightNode() {
		System.out.println("highlighting: " + getNodeName());
	}
	
    /**
     * Hashtable for full node tree structure
     * Keys are node ids, values are lists of their children 
     * @param nodes
     */
	@Persist("session")
	public abstract void setNodes(Hashtable<Long, List<MappedNode>> nodes);
	public abstract Hashtable<Long, List<MappedNode>> getNodes();
    /**
     * Hashtable for tiered node tree structure
     * Keys are node ids, values are lists of their children 
     * @param nodes
     */	
    @Persist("session")	
	public abstract void setDisplayedNodes(Hashtable<Long, List<MappedNode>> nodes);
	public abstract Hashtable<Long, List<MappedNode>> getDisplayedNodes();	
	@Persist("session")
	public abstract Long getProjectId();
	public abstract void setProjectId(Long value);
	@InjectPage("MessagePage")
	public abstract IPage getMessagePage();
	@InjectPage("btol/EditBtolFields")
	public abstract EditBtolFields getEditFieldsPage();
	@InjectComponent("taxonList")
	public abstract TaxonList getTaxonList();
    @InjectObject("spring:urlBuilder")
    public abstract URLBuilder getURLBuilder();
	
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract TaxonSamplingHelper getTaxonSamplingHelper();
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract NodeFilterHelper getNodeFilterHelper();
	@Persist("session")
	public abstract GeneFragmentNodeStatusesSource getStatusesSource();
	public abstract void setStatusesSource(GeneFragmentNodeStatusesSource value);

	@SuppressWarnings("unchecked")
	public abstract Collection getContributors();
	public abstract MappedNode getCurrentNode();
	@Persist("session")
	public abstract Integer getTierSelection();
	public abstract void setTierSelection(Integer value);
	@Persist("session")
	public abstract Integer getNeededSelection();
	public abstract void setNeededSelection(Integer value);
	public abstract void setTierSelectPressed(boolean value);
	public abstract boolean getTierSelectPressed();
    /**
     * Need to have this stored in the session since the edit window will ask the page 
     * this even when a form submit doesn't occur
     * @return
     */
    @Persist("session")
    public abstract Boolean getHideInternalTiers();
    public abstract void setHideInternalTiers(Boolean value);
    @Persist("session")    
    public abstract Boolean getShowAllTaxa();
    public abstract void setShowAllTaxa(Boolean value);
    @Persist("session")    
    public abstract Boolean getShowTaxaWithSequences();
    public abstract void setShowTaxaWithSequences(Boolean value);    
    @Persist("session")
    @InitialValue("false")
    public abstract Boolean getShowExtinctTaxa();
    public abstract void setShowExtinctTaxa(Boolean value);
    
    @Persist("session")
    @InitialValue("true")
    public abstract Boolean getShowDNA();
    public abstract void setShowDNA(Boolean value);
    @Persist("session")
    @InitialValue("true")
    public abstract Boolean getShowAdult();
    public abstract void setShowAdult(Boolean value);
    @Persist("session")
    @InitialValue("false")
    public abstract Boolean getShowMicroCT();
    public abstract void setShowMicroCT(Boolean value);
    @Persist("session")
    @InitialValue("true")
    public abstract Boolean getShowLarvae();
    public abstract void setShowLarvae(Boolean value);
    @Persist("session")
    @InitialValue("false")
    public abstract Boolean getShowGeographicInfo();
    public abstract void setShowGeographicInfo(Boolean value);
    @Persist("session")
    @InitialValue("true")
    public abstract Boolean getShowDnaNotes();
    public abstract void setShowDnaNotes(Boolean value);
    
    @Persist("session")
    public abstract List<Gene> getDisplayedGenes();
    public abstract void setDisplayedGenes(List<Gene> value);
    public abstract Gene getCurrentGene();
    public abstract void setCurrentGene(Gene value);
    
    @Persist("session")
    public abstract List<GeneFragment> getDisplayedGeneFragments();
    public abstract void setDisplayedGeneFragments(List<GeneFragment> value);
    public abstract GeneFragment getCurrentGeneFragment();
    public abstract void setCurrentGeneFragment(GeneFragment value);
    
    @SuppressWarnings("unchecked")
    public List getAllGenes() {
    	return getGeneDAO().getAllGenesInProject(getProjectId(), true);
    }

    @SuppressWarnings("unchecked")
    public List getAllGeneFragments() {
    	return getGeneFragmentDAO().getAllGeneFragmentsInProject(getProjectId(), true);
    }
    
    public boolean getShowGenes() {
    	return getProject().getContributorCanViewAndEditMolecularData(getContributor());
    }
    public String getGeneNodeStatusText() {
    	return getStatusesSource().getEditLinkTextForNodeIdAndGeneFragment(getCurrentNode().getNodeId(), getCurrentGeneFragment());
    }
    public String getMicroCTStatusText() {
    	return ""; // we don't care about showing any text... 
    }
    public String getGeneNodeStatusClass() {
    	//int tier = getCurrentNode().getAdditionalFields().getTier();
    	return getStatusesSource().getEditLinkClassForNodeIdAndGeneFragment(getCurrentNode().getNodeId(), getCurrentGeneFragment());
    	//return getStatusesSource().getEditLinkClassForNodeIdGeneAndTier(getCurrentNode().getNodeId(), getCurrentGene(), tier);
    }
    public String getGeneNodeStatusLinkId() {
    	return getStatusesSource().getEditLinkIdForNodeIdAndGeneFragment(getCurrentNode().getNodeId(), getCurrentGeneFragment());
    }
    @SuppressWarnings("unchecked")
    public List getGeneFragmentNodeStatuses(long nodeId) {
    	ArrayList al = new ArrayList();
    	al.add(nodeId);
    	List lst = getGeneFragmentNodeStatusDAO().getStatusesForNodeIdsInProject(al, getProjectId());
    	return lst;
    }
    
    public String getCurrentGeneName() {
    	if (StringUtils.notEmpty(getCurrentGene().getAbbreviatedName())) {
    		return getCurrentGene().getAbbreviatedName();
    	} else {
    		return getCurrentGene().getName();
    	}
    }
    public String getCurrentGeneFragmentName() {
    	if (StringUtils.notEmpty(getCurrentGeneFragment().getAbbreviatedName())) {
    		return getCurrentGeneFragment().getAbbreviatedName();
    	} else {
    		return getCurrentGeneFragment().getName();
    	}
    }    
    @InjectPage("btol/AssignSequenceDefaults")
    public abstract AssignSequenceDefaults getSequenceDefaultsPage();
    public AssignSequenceDefaults assignSequenceDefaults(Long nodeId) {
    	AssignSequenceDefaults defaultsPage = getSequenceDefaultsPage();
    	defaultsPage.setNodeId(nodeId);
    	return defaultsPage;
    }
    /**
     * need this here because if the root node on a form submission is different,
     * we need to discard the fetched nodes in memory and fetch the nodes for the
     * submitted root node id
     * @return
     */
    @Persist("session")
    public abstract Long getRootNodeId();
    public abstract void setRootNodeId(Long value);
    
    public Boolean getShowInternalTiers() {
        return !getHideInternalTiers();
    }
    public void setShowInternalTiers(Boolean value) {
        setHideInternalTiers(!value);
    }
    public abstract Hashtable<MappedNode, Integer> getNodesToTiers();
    public abstract void setNodesToTiers(Hashtable<MappedNode, Integer> value);
    public abstract Hashtable<MappedNode, Integer> getNodesToMtGenomeState();
    public abstract void setNodesToMtGenomeState(Hashtable<MappedNode, Integer> value);
    
    /**
     * Used to store inferred values for internal nodes' larval class.
     * This is only used when an internal node doesn't have its own tier
     * set
     * @return
     */
    public abstract Hashtable<MappedNode, String> getInternalNodesToLarvalClass();
    public abstract void setInternalNodesToLarvalClass(Hashtable<MappedNode, String> value);
    /**
     * Used to store inferred values for internal nodes' adult class.
     * This is only used when an internal node doesn't have its own tier
     * set
     * @return
     */
    public abstract Hashtable<MappedNode, String> getInternalNodesToAdultClass();
    public abstract void setInternalNodesToAdultClass(Hashtable<MappedNode, String> value);
    /**
     * Used to store inferred values for internal nodes' dna class.
     * This is only used when an internal node doesn't have its own tier
     * set
     * @return
     */
    public abstract Hashtable<MappedNode, String> getInternalNodesToDnaClass();
    public abstract void setInternalNodesToDnaClass(Hashtable<MappedNode, String> value);
    /**
     * Used to store inferred values for internal nodes' microCT class.
     * This is only used when an internal node doesn't have its own tier
     * set
     * @return
     */
    public abstract Hashtable<MappedNode, String> getInternalNodesToMicroCTClass();
    public abstract void setInternalNodesToMicroCTClass(Hashtable<MappedNode, String> value);    
    /**
     * Need to remember if we are doing a full page render or not since
     * the classes for internal nodes will have different values based on
     * whether it's a full or partial render
     * @return
     */
    public abstract boolean getIsFullPageRender();
    public abstract void setIsFullPageRender(boolean value);
    public abstract MappedNode getParentPageNode();
    public abstract void setParentPageNode(MappedNode value);
    public abstract void setIncompleteSubgroups(boolean value);
    public abstract boolean getIncompleteSubgroups();
    /**
     * Transient property for storing which node ids have some info 
     * attached to them
     * @param value
     */
    public abstract void setIdsWithAnyInfo(Set<Long> value);
    public abstract Set<Long> getIdsWithAnyInfo();

	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract TierFilterSelectionModel getTierFilterSelectionModel();
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract NeededFilterSelectionModel getNeededFilterSelectionModel();
	
	@SuppressWarnings("unchecked")
	public ContributorSelectionModel getSupplierFilterSelectionModel() {
		if (supplierSelectionModel == null) {
			Project project = getProject();
			List members = project.getAllProjectMembers();
			Collections.sort(members, new ContributorNameComparator());
			supplierSelectionModel = new SupplierContributorSelectionModel(members, true, true, getContributor());
			supplierSelectionModel.setNoSelectionText("No Supplier");
			supplierSelectionModel.setAllowShowAllSelection(true);
		}
		return supplierSelectionModel;
	}
	
	public void setSupplierFilterSelectionModel(SupplierContributorSelectionModel model) {
		this.supplierSelectionModel = model;
	}
	
	@Persist("session")
	public abstract Contributor getSupplierFilterSelection();
	public abstract void setSupplierFilterSelection(Contributor value);	
	
	public abstract String getSelectedGeoLocation();
	public abstract void setSelectedGeoLocation(String value);
	
	public DefaultLinkRenderer getEditRenderer() {
		if (renderer == null) {
			renderer = getRendererFactory().getLinkRenderer("additionalFields", 900, 550, "scrollbars=yes, resizable=yes");
		}
		return renderer;
	}
	public boolean getShowPageLink() {
		return getCurrentNode().getAdditionalFields().getHasPage();
	}
	public boolean getShowAddNodesLink() {
		return !getCurrentNode().getIsLeaf();
	}
	public boolean getShowCurrentGeoInfo() {
		return getShowGeographicInfo() && getCurrentNode().getAdditionalFields() != null && 
			StringUtils.notEmpty(getCurrentNode().getAdditionalFields().getGeographicDistribution()); 
	}
	public boolean getShowCurrentDnaNotes() {
		return getShowDnaNotes() && getCurrentNode().getAdditionalFields() != null &&
			StringUtils.notEmpty(getCurrentNode().getAdditionalFields().getDnaNotes());
	}
	
	@SuppressWarnings("unchecked")
	public List getCurrentEditParameters() {
		List<Long> params = new ArrayList<Long>();
		params.add(getCurrentNode().getNodeId());
		return params;
	}
	public String getBranchPageName() {
		return getTaxonList().getPageNameForNode(getCurrentNode());
	}
	public String getWorkingUrl() {
		return getURLBuilder().getWorkingURLForBranchPage(getBranchPageName(), getCurrentNode().getNodeId());
	}
	public String getContainingGroupName() {
		return getParentPageNode().getActualPageTitle(false, false, false);
	}
	/**
	 * Listener to setup tier hierarchy in case of tier filter refresh
	 * @param cycle
	 */
	public void updateNode() {
		setupRequestCycleAttributes();
		setIsFullPageRender(true);
		setNode(getTolPage().getMappedNode());
		if (!getNode().getNodeId().equals(getRootNodeId())) {
			doDatabaseFetchingAndInit(getNode(), getProjectId());
		}
		fetchRootAdditionalFields();
		setupCalculatedValues();
	}
	
	@SuppressWarnings("unchecked")
	public void flushCache(IRequestCycle cycle) {
		setupRequestCycleAttributes();
		setIsFullPageRender(true);
		getMiscNodeDAO().flushQueryCache();
		getWorkingNodeDAO().flushQueryCache();
		Collection nodeIds = getNodes().keySet();
		getCacheAccess().evictNodesFromCache(nodeIds);
		
		// call activateExternalPage to re-initialize everything
		Object[] parameters = {getTolPage().getMappedNode().getNodeId(), getProject().getId()};
		activateExternalPage(parameters, cycle);
	}
	public boolean getIsRoot() {
		if (getProject() != null) {
			return getNode().getNodeId().equals(getProject().getRootNodeId());
		} else {
			return false;
		}
	}
	public void pageBeginRender(PageEvent event) {
		super.pageBeginRender(event);
		if (!event.getRequestCycle().isRewinding()) {
			setupCalculatedValues();
			setParentPageNode(getWorkingPageDAO().getNodeForPageNodeIsOn(getNode()));
			setIncompleteSubgroups(getNode().getHasIncompleteSubgroups());
			fetchRootAdditionalFields();	
			if (getDisplayedGenes() == null) {
				setDisplayedGenes(new ArrayList<Gene>());
			}
			if (getDisplayedGeneFragments() == null) {
				setDisplayedGeneFragments(new ArrayList<GeneFragment>());
			}
			//OtherGroupsHelper helper = getOtherGroupsHelper().constructHelperForNode(getNode(), getPageDAO(), getProject().getRootNodeId());
			//setHelper(helper);
		}		
	}
	
	public void setupCalculatedValues() {
		Hashtable<MappedNode, Integer> nodesToTiers = new Hashtable<MappedNode, Integer>();
		Hashtable<MappedNode, Integer> nodesToMtGenomeState = new Hashtable<MappedNode, Integer>();		
		Hashtable<MappedNode, String> nodesToLarvalClass = new Hashtable<MappedNode, String>();
		Hashtable<MappedNode, String> nodesToAdultClass = new Hashtable<MappedNode, String>();
		Hashtable<MappedNode, String> nodesToDnaClass = new Hashtable<MappedNode, String>();
		Hashtable<MappedNode, String> nodesToMicroCTClass = new Hashtable<MappedNode, String>();
		getTaxonSamplingHelper().calculateValuesForNodeAndDescendants(getNode(), nodesToTiers,
				nodesToMtGenomeState, nodesToLarvalClass, nodesToAdultClass, nodesToDnaClass, 
				nodesToMicroCTClass, null, getNodesToDisplay(), getContributor(), getProject());
		setNodesToTiers(nodesToTiers);	
		setNodesToMtGenomeState(nodesToMtGenomeState);
		setInternalNodesToLarvalClass(nodesToLarvalClass);
		setInternalNodesToAdultClass(nodesToAdultClass);
		setInternalNodesToDnaClass(nodesToDnaClass);
		setInternalNodesToMicroCTClass(nodesToMicroCTClass);
	}
	public boolean getNoNodesToDisplay() {
		// if any nodes are shown in the list then show them,
		for (List<MappedNode> nodeList : getNodesToDisplay().values()) {
			if (nodeList.size() > 0) {
				return false;
			}
		}
		// they're all empty, so there is nothing available
		return true;
	}
	public Hashtable<Long, List<MappedNode>> getNodesToDisplay() {
		if (getDisplayedNodes() != null) {
			return getDisplayedNodes();
		} else {
			return getNodes();
		}
	}
	private void fetchRootAdditionalFields() {
		if (getNode().getAdditionalFields() == null) {
			fetchNodeAdditionalFields(getNode());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void doDatabaseFetchingAndInit(MappedNode node, Long projectIdLong) {
		// fetch all of the information from the database
		Collection descendantIds = getMiscNodeDAO().getDescendantIdsForNode(node.getNodeId());
		Hashtable<Long, List<MappedNode>> hash = getWorkingNodeDAO().getDescendantNodesForIndexPage(descendantIds);
		setNodes(hash);		
		setDisplayedNodes(null);
		Hashtable<Long, MappedNode> idsToNodes = new Hashtable<Long, MappedNode>();
		Set<Long> nodeIds = new HashSet<Long>();		
		// loop through all of the nodes and gather their ids
		for (List<MappedNode> nextNodeList : hash.values()) {
			for (MappedNode node2 : nextNodeList) {
				nodeIds.add(node2.getNodeId());
				idsToNodes.put(node2.getNodeId(), node2);
			}
		}
		// make sure root has its additional fields set
		fetchRootAdditionalFields();
		// fetch the additional fields all at once
		List additionalFields = getProjectDAO().getAdditionalFieldsForNodeIdsInProject(nodeIds, projectIdLong);
		// create these objects if they don't already exist
		if (additionalFields.size() < nodeIds.size()) {
			getProjectDAO().createFieldsForNodeIdsInProject(nodeIds, projectIdLong);
			additionalFields = getProjectDAO().getAdditionalFieldsForNodeIdsInProject(nodeIds, projectIdLong);
		}		
		reloadGeneNodeStatuses(projectIdLong);
        Collection nodeIdsWithPages = getWorkingPageDAO().getNodeIdsWithPages(nodeIds);
        // initialize parent relationships and additional fields objects
		for (Iterator iter = additionalFields.iterator(); iter.hasNext();) {
			AdditionalFields nextFields = (AdditionalFields) iter.next();
			MappedNode nextNode = idsToNodes.get(nextFields.getNodeId());
			if (nextNode == null) {
				System.out.println("missing node is: " + nextFields.getNodeId());
			}
            if (nodeIdsWithPages.contains(nextNode.getNodeId())) {
                nextFields.setHasPage(true);
            }
			nextNode.setAdditionalFields(nextFields);
			// also initialize the parent relationship since it will make various filters easier
			MappedNode parentNode = idsToNodes.get(nextNode.getParentNodeId());
			nextNode.setParent(parentNode);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void reloadGeneNodeStatuses(Long projectId) {
		HashtableTree tree = new HashtableTree();
		tree.setTable(getNodes());
		Set nodeIds = tree.getAllNodeIds();
		List statuses = getGeneFragmentNodeStatusDAO().getStatusesForNodeIdsInProject(nodeIds, projectId);
		GeneFragmentNodeStatusesSource source = new GeneFragmentNodeStatusesSource();
		source.setStatuses(statuses);
		// 
		setStatusesSource(source);		
	}
	
	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		getProjectValidator().setExternalParams(parameters);
		getProjectValidator().setExternalPageName(getPageName());
		setIsFullPageRender(true);
		// only initialize these if they haven't been previously set
		if (getHideInternalTiers() == null) {
			setHideInternalTiers(true);
		}
		if (getShowAllTaxa() == null) {
			setShowAllTaxa(false);
		}
		if (getShowTaxaWithSequences() == null) {
			setShowTaxaWithSequences(false);
		}
		if (getTierSelection() == null) {
			setTierSelection(TierFilterSelectionModel.TIER_2);
		}		
		if (getNeededSelection() == null) {
			setNeededSelection(NeededFilterSelectionModel.SHOW_ALL);
		}
		// get all of the services and variables configured
		setupRequestCycleAttributes();
		Object firstArg = parameters[0];
		if (Number.class.isInstance(firstArg)) {
			Number nodeId = (Number) parameters[0];
			Number projectIdNumber = (Number) parameters[1];		
			setupNodeIdAndProjectId(nodeId, projectIdNumber);
		} else if (String.class.isInstance(firstArg)) {
			String groupName = (String) parameters[0];
			List nodes = getWorkingNodeDAO().findNodesNamed(groupName);
			if (nodes != null && nodes.size() >= 1) {
				MappedNode groupNode = (MappedNode) nodes.get(0);
				Number projectId = (Number) parameters[1];
				setupNodeIdAndProjectId(groupNode.getNodeId(), projectId);
			} else {
				setNoNode(true);
				setMissingNodeName(groupName);
			}
		}
		initalizeFilterOptionsCookieSettings();
		doDatabaseFetchingAndInit(getNode(), getProjectId());
		// need to call this since the default is to not show all taxa
		filterNodes();
	}
	
	private void initalizeFilterOptionsCookieSettings() {
		CookieAndContributorSource cookieSrc = getCookieAndContributorSource();
		if (cookieSrc.getShowDnaInformationCookieExists()) {
			setShowDNA(cookieSrc.getShowDnaInformationCookieEnabled());
		}
		if (cookieSrc.getShowAdultInformationCookieExists()) {
			setShowAdult(cookieSrc.getShowAdultInformationCookieEnabled());
		}
		if (cookieSrc.getShowLarvaeInformationCookieExists()) {
			setShowLarvae(cookieSrc.getShowLarvaeInformationCookieEnabled());
		}
		if (cookieSrc.getShowGeoDistInformationCookieExists()) {
			setShowGeographicInfo(cookieSrc.getShowGeoDistInformationCookieEnabled());
		}
		if (cookieSrc.getShowDnaNotesCookieExists()) {
			setShowDnaNotes(cookieSrc.getShowDnaNotesCookieEnabled());
		}
		if (cookieSrc.getTierFilterCriteriaExists()) {
			setTierSelection(cookieSrc.getTierFilterCriteria());
		}
		if (cookieSrc.getNeededFilterCriteriaExists()) {
			setNeededSelection(cookieSrc.getNeededFilterCriteria());
		}
		if (cookieSrc.getDnaSupplierFilterCriteriaExists()) {
			int contributorId = cookieSrc.getDnaSupplierFilterCriteria();
			Contributor contributor = getContributor().getContributorWithId(contributorId);
			setSupplierFilterSelection(contributor);
		}
		if (cookieSrc.getGeoDistFilterCriteriaExists()) {
			setSelectedGeoLocation(cookieSrc.getGeoDistFilterCriteria());
		}
	}
	protected void setupNodeIdAndProjectId(Number nodeId, Number projectIdNumber) {
		MappedNode node = getWorkingNodeDAO().getNodeWithId(Long.valueOf(nodeId.intValue()));
		setNode(node);
		// store this in the session, to prevent problems with the back button
		// and server-side state not matching client views
		setRootNodeId(node.getNodeId());
		setTolPage(getPageDAO().getPage(node));	
		Long projectIdLong = Long.valueOf(projectIdNumber.intValue());
		setProjectId(projectIdLong);
		if (getProject() == null) {
			getCookieAndContributorSource().loginProject(getProjectDAO().getProjectWithId(projectIdLong));
		}
	}

	public String getAlwaysShowAttribute() {
		return getAlwaysShowAttribute(getCurrentNode());		
	}
	/**
	 * Need to have a special attribute that indicates info should always
	 * be shown if it is actually assigned.
	 * @return
	 */
	public String getAlwaysShowAttribute(MappedNode node) {
		AdditionalFields fields = node.getAdditionalFields();
        boolean includeSpecialAttribute = fields.getHasTierAssigned() || fields.getHasAdultSpecimensAssigned()
    	|| fields.getHasDnaSpecimensAssigned() || fields.getHasLarvalSpecimensAssigned();
        if (includeSpecialAttribute) {
        	return "1";
        } else {
        	return null;
        }
	}
	public EditBtolFields editAdditionalFields(Long nodeId) {
		MappedNode node = getNodeFromId(nodeId);
		if (node == null) {
			node = getWorkingNodeDAO().getNodeWithId(nodeId);
			fetchNodeAdditionalFields(node);
		}
		return getEditFieldsPage().editAdditionalFields(node, getProjectId());
	}
	private void fetchNodeAdditionalFields(MappedNode node) {
		node.setAdditionalFields(getProjectDAO().getAdditionalFieldsForNodeInProject(node, getProjectId()));
	}
	// ------------------------- TIERS -----------------------------
	public String getEditTierLinkId() {
		return getEditTierLinkIdForNode(getCurrentNode());
	}
	public String getEditTierLinkIdForNode(MappedNode node) {
		return getEditTierPrefix() + node.getNodeId();
	}
	/**
	 * Needed so the script knows how to figure out the link id
	 */	
	public String getEditTierPrefix() {
		return "edittier"; 
	}
	public String getUlIdPrefix() {
		return getTaxonList().getUlIdPrefix();
	}
    public String getActualTierClassForNode(MappedNode node) {
        return getTierClassForTier(getCalculatedTierForNode(node), getCalculatedMtGenomeStateForNode(node));
    }
	/**
	 * An additional attribute we add to the invisible links to make
	 * the javascript open/close work correctly
	 * @return
	 */
	public String getActualTierClass() {
		return getActualTierClassForNode(getCurrentNode());
	}
	public String getTierClass() {
		return getTierClassForNode(getCurrentNode());
	}
	public String getTierClassForNode(MappedNode node) {
        // if we want to actually show the link, then give it a real class
        if (getShowInternalNodeLinkForNode(node)) {
        	int tier = getCalculatedTierForNode(node);
        	int calcMtState = getCalculatedMtGenomeStateForNode(node);
            return getTierClassForTier(tier, calcMtState);
        } else {
            // here it will be in the markup, but make it invisible so it doesn't appear
            return getInvisibleClass();
        }		
	}
	public String getTierText() {
		if (getCurrentNode() == null) {
			System.out.println("current node null");
		} else if (getCurrentNode().getAdditionalFields() == null) {
			System.out.println("null fields node is: " + getCurrentNode());
		}
		
		return getTierTextForTier(getCurrentTier(), getCurrentNode().getAdditionalFields().getHasTierNotes());
	}	
	public String getTierClassForTier(int tier, int mtGenomeState) {
		String tierClass = getUnassignedTierClass();
		if (mtGenomeState > AdditionalFields.DONT_HAVE_ANY) {
			if (mtGenomeState == AdditionalFields.HAVE_MT_GENOME) {
				tierClass = (tier == 1) ? "tier1havegenome" : "tier0havegenome" ;
			} else {
				tierClass = "tier1genomesoon";
			}
		} else {
			if (tier >= 0) {
				tierClass = "tier" + tier;
			}
		}
		return tierClass;
	}
	public String getTierTextForTier(int tier, boolean hasNotes) {	
		String tierText = "no tier";
		if (tier >= 0) {
			tierText = "" + tier;
		}
		if (hasNotes) {
			tierText += "*";
		}
		return tierText;
	}
	public int getCalculatedTierForNode(MappedNode node) {
		Integer currentTier = getNodesToTiers().get(node); 
		if (currentTier != null) {
			return currentTier;
		} else {
			return 10;
		}		
	}
	public int getCalculatedMtGenomeStateForNode(MappedNode node) {
		Integer calcState = getNodesToMtGenomeState().get(node);
		if (calcState != null) {
			return calcState;
		} else {
			return AdditionalFields.NO_MT_GENOME;
		}
	}
	/**
	 * This is the minimum of:
	 * 1) the tier assigned to a given node, or 
	 * 2) the minimum tier for any descendant node
	 * @return
	 */
	private int getCurrentTier() {
		return getCalculatedTierForNode(getCurrentNode());
	}	
	// -------------------------- LARVAE ----------------------------------
	/**
	 * An additional attribute we add to the invisible links to make
	 * the javascript open/close work correctly
	 * @return
	 */
	public String getActualLarvaeClass() {
		return getActualLarvaeClassForNode(getCurrentNode());
	}
	public String getLarvaeClass() {
		return getLarvaeClassForNode(getCurrentNode());
	}
    /**
     * Returns the larvae class for a node, taking into account whether
     * internal nodes are shown or hidden
     * @param node
     * @return
     */
    public String getLarvaeClassForNode(MappedNode node) {
    	AdditionalFields fields = node.getAdditionalFields();
        if (getShowInternalNodeLinkForNode(node) || fields.getHasLarvalSpecimensAssigned()) {
            return getActualLarvaeClassForNode(node);
        } else {
            return getInvisibleClass();
        }
    }
	public String getEditLarvaeLinkId() {
		return getEditLarvaeLinkIdForNode(getCurrentNode());
	}
	/**
	 * Needed so the script knows how to figure out the link id
	 */	
	public String getEditLarvaePrefix() {
		return "editlarvae";
	}
	public String getEditLarvaeLinkIdForNode(MappedNode currentNode) {
		return getEditLarvaePrefix() + currentNode.getNodeId();
	}
	public String getActualLarvaeClassForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return returnStoredValueOrOtherClass(currentNode, getInternalNodesToLarvalClass(), 
				fields.getLarvalSpecimensState(), fields.getLarvalPossessionPerson(), fields.getLarvalDestinationPerson());
	}
	public String getLarvaeText() {
		return getLarvaeTextForNode(getCurrentNode());
	}
	public String getLarvaeTextForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return getCollectionTextForNode(fields.getLarvalSpecimensState(), fields.getLarvalPossessionPerson(),
				fields.getHasLarvalNotes());
	}	
	// ------------------------------ ADULTS ------------------------------
	public String getActualAdultClassForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return returnStoredValueOrOtherClass(currentNode, getInternalNodesToAdultClass(), 
				fields.getAdultSpecimensState(), fields.getAdultPossessionPerson(), fields.getAdultDestinationPerson());		
	}
	public String getActualAdultClass() {
		return getActualAdultClassForNode(getCurrentNode());
	}
	public String getAdultClass() {
		return getAdultClassForNode(getCurrentNode());
	}
    public String getAdultClassForNode(MappedNode node) {
		AdditionalFields fields = node.getAdditionalFields();    	
        if (getShowInternalNodeLinkForNode(node) || fields.getHasAdultSpecimensAssigned()) {
            return getActualAdultClassForNode(node);
        } else {
            return getInvisibleClass();
        }
    }
	public String getEditAdultLinkId() {
		return getEditAdultLinkIdForNode(getCurrentNode());
	}
	/**
	 * Needed so the script knows how to figure out the link id
	 */	
	public String getEditAdultPrefix() {
		return "editadult";
	}
	public String getEditAdultLinkIdForNode(MappedNode currentNode) {
		return getEditAdultPrefix() + currentNode.getNodeId();
	}
	public String getAdultText() {
		return getAdultTextForNode(getCurrentNode());
	}
	public String getAdultTextForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return getCollectionTextForNode(fields.getAdultSpecimensState(), fields.getAdultPossessionPerson(),
				fields.getHasAdultNotes());
	}		
	// -------------------------- DNA -------------------------------
	public String getActualDnaClass() {
		return getActualDnaClassForNode(getCurrentNode());
	}
	public String getDnaClass() {
		return getDnaClassForNode(getCurrentNode());
	}
    public String getDnaClassForNode(MappedNode node) {
		AdditionalFields fields = node.getAdditionalFields();    
		boolean showInternal = getShowInternalNodeLinkForNode(node);
		boolean hasSpecimensAssigned = fields.getHasDnaSpecimensAssigned(); 
        if (showInternal || hasSpecimensAssigned) {
            return getActualDnaClassForNode(node);
        } else {
            return getInvisibleClass();
        }
    }
	public String getEditDnaLinkId() {
		return getEditDnaLinkIdForNode(getCurrentNode());
	}
	/**
	 * Needed so the script knows how to figure out the link id
	 */
	public String getEditDnaPrefix() {
		return "editdna";
	}
	public String getEditDnaLinkIdForNode(MappedNode currentNode) {
		return getEditDnaPrefix() + currentNode.getNodeId();
	}
	public String getDnaText() {
		return getDnaTextForNode(getCurrentNode());
	}	
	public String getDnaTextForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return getCollectionTextForNode(fields.getDnaSpecimensState(), fields.getDnaPossessionPerson(),
				fields.getHasDnaNotes());
	}			
	public String getActualDnaClassForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return returnStoredValueOrOtherClassForDna(currentNode, getInternalNodesToDnaClass(), 
				fields.getDnaSpecimensState(), fields.getDnaPossessionPerson(), fields.getDnaDestinationPerson());		
	}	
	// ------------------------------ MICRO CT ------------------------------
	public String getActualMicroCTClassForNode(MappedNode currentNode) {
		AdditionalFields fields = currentNode.getAdditionalFields();
		return returnStoredValueOrOtherClassForMicroCT(currentNode, getInternalNodesToMicroCTClass(), 
				fields.getMicroCTState(), fields.getMicroCTPossessionPerson());	
	}
	public String getActualMicroCTClass() {
		return getActualMicroCTClassForNode(getCurrentNode());
	}
	public String getMicroCTClass() {
		return getMicroCTClassForNode(getCurrentNode());
	}
    public String getMicroCTClassForNode(MappedNode node) {
		AdditionalFields fields = node.getAdditionalFields();
		boolean showInternalNode = getShowInternalNodeLinkForNode(node);
		boolean hasMicroCTSpecimens = fields.getHasMicroCTSpecimens();
        if (showInternalNode || hasMicroCTSpecimens) {
            return getActualMicroCTClassForNode(node);
        } else {
            return getInvisibleClass();
        }
    }
	public String getEditMicroCTLinkId() {
		return getEditMicroCTLinkIdForNode(getCurrentNode());
	}
	/**
	 * Needed so the script knows how to figure out the link id
	 */	
	public String getEditMicroCTPrefix() {
		return "editmicroct";
	}
	public String getEditMicroCTLinkIdForNode(MappedNode currentNode) {
		return getEditMicroCTPrefix() + currentNode.getNodeId();
	}
	private String getCollectionTextForNode(int collectionState, Contributor possContr, 
			boolean hasNotes) {
		String returnString = "";
		if (possContr != null) {
			returnString = possContr.getInitials();
		}
		if (StringUtils.isEmpty(returnString)) {
			returnString = "&nbsp;";
		}
		if (hasNotes) {
			returnString += "*";
		}
		return returnString;		
	}
	private String returnStoredValueOrOtherClass(MappedNode currentNode, Hashtable<MappedNode, String> classHash,
			int samplingStatus, Contributor possPerson, Contributor destPerson) {
		// first check to see if there is a stored value for the node.  if there is, then use it
		// stored classes are used for nodes that don't have info associated with them but need
		// classes when their children are not shown
		String storedValue = classHash.get(currentNode);
		if (StringUtils.notEmpty(storedValue)) {
			return storedValue;
		}
		AdditionalFields fields = currentNode.getAdditionalFields(); 
		return getSamplingClassForNode(samplingStatus, possPerson,
				destPerson, fields);		
	}
	private String returnStoredValueOrOtherClassForDna(MappedNode currentNode, Hashtable<MappedNode, String> classHash,
			int samplingStatus, Contributor possPerson, Contributor destPerson) {
		// first check to see if there is a stored value for the node.  if there is, then use it
		// stored classes are used for nodes that don't have info associated with them but need
		// classes when their children are not shown
		String storedValue = classHash.get(currentNode);
		if (StringUtils.notEmpty(storedValue)) {
			return storedValue;
		}
		AdditionalFields fields = currentNode.getAdditionalFields();
		if (RALLY_TROOPS_MODE) {
			return getDnaSamplingClassForNode(samplingStatus, possPerson,
					destPerson, fields);
		} else {
			return getSamplingClassForNode(samplingStatus, possPerson, destPerson, fields);
		}
	}
	/**
	 * Returns the appropriate CSS class for the MicroCT divs - either the stored value or the other class.
	 * @param currentNode
	 * @param classHash
	 * @param samplingStatus
	 * @param possPerson
	 * @return the correct CSS class for the node's MicroCT collection status
	 * @author lenards
	 */
	private String returnStoredValueOrOtherClassForMicroCT(MappedNode currentNode, 
			Hashtable<MappedNode, String> classHash,
			int samplingStatus, Contributor possPerson) {
		String storedValue = classHash.get(currentNode);
		if (StringUtils.notEmpty(storedValue)) {
			return storedValue;
		}
		AdditionalFields fields = currentNode.getAdditionalFields(); 
		return getMicroCTSamplingClassForNode(samplingStatus, possPerson, fields);		
	}
    public String getInvisibleClass() {
		return INVISIBLE;
	}
    public String getUnassignedClass() {
    	return "unassigned";
    }
    public String getUnassignedTierClass() {
    	return NOTIER;
    }
    private String getDnaSamplingClassForNode(int collectionStatus, Contributor possContributor, 
			Contributor workContributor, AdditionalFields fields) {
    	Contributor loggedInContributor = getContributor();
    	boolean loggedInSameAsPoss = loggedInContributor != null && possContributor != null &&
    		loggedInContributor.getId() == possContributor.getId();
    	if (collectionStatus == AdditionalFields.HAVE_DNA) {
    		return getHasEnoughSpecimensClass();
    	} else if (collectionStatus == AdditionalFields.HAVE_ENOUGH || 
    			collectionStatus == AdditionalFields.HAVE_SOME) {
    		// 3 possibilities:
    		//	(1) no one is designated as the supplier -- pale pink
    		//  (2) person logged in is person who said they would supply it: bright green 
    		//  (3) person logged in is not person who said they would supply it, but someone 
    		//		else said they would supply it: pale green
    		if (possContributor == null) {
    			return getSpecimensNoSupplierClass();
    		} else {
    			if (loggedInSameAsPoss) {
    				return getSpecimensYouSupplierClass();
    			} else {
    				return getSpecimensOtherSupplierClass();
    			}
    		}
    	} else if (collectionStatus == AdditionalFields.DONT_HAVE_ANY) {
			if (fields.getHasTierAssigned()) {
				if (loggedInSameAsPoss) {
					return getNoSpecimensYouSupplierClass();
				} else {
					return getNoSpecimensClass();
				}
			} else {
				return getUnassignedClass();
			}
    	} else if (collectionStatus == AdditionalFields.SPECIMENS_TO_LAB) {
    		// if one of the core people who can edit dna status, make it 
    		// bright yellow, else pale gray
    		if (getProject().getContributorCanEditDna(getContributor())) {
    			return getActionNeededClass();
    		} else {
    			return getIgnoreClass();
    		}
    	} else {
    		return getUnassignedClass();
    	}
    }

	private String getSamplingClassForNode(int collectionStatus, Contributor possContributor, 
			Contributor workContributor, AdditionalFields fields) {
		/*boolean personHasThemWhoNeedsThem = possContributor != null &&
		workContributor != null && possContributor.getId() == workContributor.getId();*/
		if (collectionStatus == AdditionalFields.DONT_HAVE_ANY) {
			if (fields.getHasTierAssigned()) {
				return getNoSpecimensClass();
			} else {
				return getUnassignedClass();
			}
		} else if (collectionStatus == AdditionalFields.HAVE_SOME) {
			// DANNY: DON'T WORRY ABOUT WHO HAS THEM RIGHT NOW!			
			//if (personHasThemWhoNeedsThem) {
			//	return getHasSomeSpecimensClass();
			//} else {
				return getHasSomeSpecimensWrongPersonClass();
			//}
		} else if (collectionStatus == AdditionalFields.HAVE_ENOUGH) {
			// DANNY: DON'T WORRY ABOUT WHO HAS THEM RIGHT NOW!
			//if (personHasThemWhoNeedsThem) {
				return getHasEnoughSpecimensClass();
			//} else {
			//	return getHasEnoughSpecimensWrongPersonClass();
			//}
		} else {
			return getUnassignedClass();
		}		
	}
	private String getMicroCTSamplingClassForNode(int collectionStatus, Contributor possContributor, 
			AdditionalFields fields) {
		/*
    * if "microCT completed", then show in peach
    * if "Beutel has specimens for micro CT", AND it is a Tier 0, then color is a darker gray than is now used for no Tier.
    * if "Beutel needs specimens for microCT" AND it is Tier 0, then color is dark pink (as used currently for BTOL has no specimens.
    * all other cases show the pale gray "no tier" color. 
		 */
		if (collectionStatus == AdditionalFields.MICRO_CT_NOT_NEEDED ||
				collectionStatus == AdditionalFields.MICRO_CT_NEED_SPECIMEN) {
			if (fields.getTier() == 0) {
				return getNoSpecimensClass();
			} else {
				return getUnassignedClass();
			}
		} else if (collectionStatus == AdditionalFields.MICRO_CT_HAS_SPECIMEN) {
			if (fields.getTier() == 0) {
				return getMicroCTSpecimensAcquiredClass();
			} else {
				return getMicroCTSpecimensAcquiredClass();
			}
		} else if (collectionStatus == AdditionalFields.MICRO_CT_COMPLETED) {
			return getHasEnoughSpecimensClass();
		} else {
			return getUnassignedClass();
		}
	}
	public static String getSpecimensOtherSupplierClass() {
		return "specimensothersupplier";
	}
	public static String getSpecimensYouSupplierClass() {
		return "specimensyousupplier";
	}
	public static String getNoSpecimensYouSupplierClass() {
		return "nospecimensyousupplier";
	}
	public static String getSpecimensNoSupplierClass() {
		return SPECIMENSNOSUPPLIER;
	}
	public String getNoSpecimensClass() {
		return NEEDSSPECIMENS;
	}
	public String getHasSomeSpecimensClass() {
		return "hasspecimensneedmore";
	}
	public String getHasSomeSpecimensWrongPersonClass() {
		return "hasspecimenswrongpersonneedmore";
	}
	public String getHasEnoughSpecimensClass() {
		return HASSPECIMENS;
	}
	public String getHasEnoughSpecimensWrongPersonClass() {
		return "hasspecimenswrongperson";
	}
	public String getIgnoreClass() {
		return "ignore";
	}	
	public static String getActionNeededClass() {
		return "actionneeded";
	}
	public String getMicroCTSpecimensAcquiredClass() {
		return "microctspecimensacquired";
	}
	public MappedNode getNodeFromId(Long nodeId) {
		return getNodeFromHashtable(nodeId, getNodes());
	}
	public static MappedNode getNodeFromHashtable(Long nodeId, Hashtable<Long, List<MappedNode>> nodes) {
		for (List<MappedNode> currentNodes : nodes.values()) {
			for (MappedNode node : currentNodes) {
				if (node.getNodeId().equals(nodeId)) {
					return node;
				}
			}
		}
		return null;		
	}
	public List<String> getNodeNames() {
		List<String> nodeNames = new ArrayList<String>();
		for (List<MappedNode> nodes : getNodes().values()) {
			for (MappedNode node : nodes) {
				nodeNames.add(node.getName());
			}
		}
		Collections.sort(nodeNames);
		return nodeNames;
	}
	public MappedNode getNodeWithName(String name) {
		for (List<MappedNode> nodes : getNodes().values()) {
			for (MappedNode node : nodes) {
				if (node.getName().equalsIgnoreCase(name)) {
					return node;
				}
			}
		}		
		return null;
	}	
	/**
	 * replaces any cached additional fields with this updated copy
	 * @param fields
	 */
	public MappedNode updateAdditionalFields(AdditionalFields fields) {
		for (List<MappedNode> currentNodes : getNodes().values()) {
			for (MappedNode node : currentNodes) {
				AdditionalFields currentFields = node.getAdditionalFields();
				if (fields == null || currentFields == null) {
					System.out.println("node missing fields is: " + node);
				}
				if (currentFields.getId().equals(fields.getId())) {
					node.setAdditionalFields(fields);
					return node;
				}
			}
		}		
		return null;
	}
	 
	public void adjustTierValues() {
		setTierSelectPressed(true);
	}
	public void filterNodes() {
		if (getTierSelectPressed() || !getShowAllTaxa()) {
			setupNodesBasedOnUserSelection();			
		} else {
			setDisplayedNodes(null);
		}
		createFilterOptionsCookieValues();
	}
	
	private void createFilterOptionsCookieValues() {
		CookieAndContributorSource cookieSrc = getCookieAndContributorSource();
		if (getShowDNA()) {
			cookieSrc.addShowDnaInformationCookie();
		} else {
			cookieSrc.removeShowDnaInformationCookie();
		}
		if (getShowAdult()) {
			cookieSrc.addShowAdultInformationCookie();
		} else {
			cookieSrc.removeShowAdultInformationCookie();
		}
		if (getShowLarvae()) {
			cookieSrc.addShowLarvaeInformationCookie();
		} else {
			cookieSrc.removeShowLarvaeInformationCookie();
		}
		if (getShowGeographicInfo()) {
			cookieSrc.addShowGeoDistInformationCookie();
		} else {
			cookieSrc.removeShowGeoDistInformationCookie();
		}
		if (getShowDnaNotes()) {
			cookieSrc.addShowDnaNotesCookie();
		} else {
			cookieSrc.removeShowDnaNotesCookie();
		}
		if (getSupplierFilterSelection() != null) {
			cookieSrc.addDnaSupplierFilterCriteria(getSupplierFilterSelection().getId());
		} else {
			cookieSrc.removeDnaSupplierFilterCriteria();
		}
		if (StringUtils.notEmpty(getSelectedGeoLocation())) {
			cookieSrc.addGeoDistFilterCriteria(getSelectedGeoLocation());
		} else {
			cookieSrc.removeGeoDistFilterCriteria();
		}
		cookieSrc.addTierFilterCriteria(getTierSelection());
		cookieSrc.addNeededFilterCriteria(getNeededSelection());
	}
	public IRender getAdditionalDelegate() {
		return new IRender() {
			public void render(IMarkupWriter arg0, IRequestCycle arg1) {
				arg0.printRaw("<link rel=\"stylesheet\" type=\"text/css\" href=\"/tree/css/btol.css\"/>");
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public boolean getShowExpandCollapse() {
		boolean hasName = StringUtils.notEmpty(getCurrentNode().getName());
		List children = getNodesToDisplay().get(getCurrentNode().getNodeId()); 
		return hasName && children != null && children.size() > 0;
	}
	public String getOnClickString() {
		String listId = getTaxonList().getUlIdForNode(getCurrentNode());
		return "changeclass('" + listId + "', 'show', 'hide');";
	}
	public String getAnchorLinkId() {
		return "ach" + getCurrentNode().getNodeId();
	}
	public boolean getShowInternalNodeLink() {
	    return getShowInternalNodeLinkForNode(getCurrentNode());
    }
    public boolean getShowInternalNodeLinkForNode(MappedNode node) {
    	boolean hideInternalTiers = getHideInternalTiers();
    	boolean isFullPageRender = getIsFullPageRender();
    	boolean showInternalLinkForNodeIfHidden = getShowInternalLinkForNodeIfHidden(node);
    	boolean hasTierAssigned = node.getAdditionalFields().getHasTierAssigned(); 
        return !hideInternalTiers || isFullPageRender || showInternalLinkForNodeIfHidden
        	|| hasTierAssigned;
    }
	/**
	 * Used to restore node info on rewind
	 */
	public Long getNodeId() {
		return getNode().getNodeId();
	}
	public void setNodeId(Long value) {
		if (value != null) {
			setNode(getWorkingNodeDAO().getNodeWithId(value));
		}
	}
	/**
     * Whether to show a tier for a node.  Internal nodes aren't shown
     * @param node
     * @return
     */
	@SuppressWarnings("unchecked")
    private boolean getShowInternalLinkForNodeIfHidden(MappedNode node) {
        Hashtable<Long, List<MappedNode>> nodes = getDisplayedNodes() != null ? getDisplayedNodes() : getNodes();
        List children = nodes.get(node.getNodeId());
        return children == null || children.size() == 0;
    }
	/**
	 * Filters nodes based on whether the use chose to show all tiers
	 * or show taxa without sampling information associated with
	 * them
	 */
    @SuppressWarnings("unchecked")
    private void setupNodesBasedOnUserSelection() {
    	// only worry about these if we are filtering on them
    	List displayedGeneFrags = getShowTaxaWithSequences() != null && getShowTaxaWithSequences() ? 
    			getDisplayedGeneFragments() : null;
    	Hashtable<Long, List<MappedNode>> displayedNodes = getNodeFilterHelper().getNodesFromUserSelection(getTierSelection(), 
    			getNeededSelection(), getShowAllTaxa(), getNode(), getNodes(), getSelectedGeoLocation(), displayedGeneFrags,
    			getStatusesSource(), getSupplierFilterSelection(), getShowExtinctTaxa());
    	setDisplayedNodes(displayedNodes);
	}
	public PageDAO getPageDAO() {
		return getWorkingPageDAO();
	}
	/**
	 * Used to see if a particular node should be opened when the "expand most lists" link
	 * is clicked
	 * @param node
	 * @return
	 */
	public String getExpandMostAttribute(MappedNode node) {
		Set<Long> idsWithInfo = getIdsWithAnyInfo();		
		if (idsWithInfo == null) {
			idsWithInfo = new HashSet<Long>();
			getNodeFilterHelper().filterBasedOnAnyInfo(getNodes(), getNode().getNodeId(), idsWithInfo, false, 
					true, getSelectedGeoLocation(), getDisplayedGeneFragments(), getStatusesSource(), getSupplierFilterSelection(), getShowExtinctTaxa());
			setIdsWithAnyInfo(idsWithInfo);
		}
		if (idsWithInfo.contains(node.getNodeId())) {
			return "true";
		} else {
			return null;
		}
	}
	
	// -------- security stuff.  this should go somewhere else eventually........
    @Bean(lifecycle = Lifecycle.REQUEST)
    public abstract ProjectValidator getProjectValidator();
    
	public void pageAttached(PageEvent event) {
		ProjectValidator validator = getProjectValidator();
		validator.setRequestValues(getCookieAndContributorSource().getContributorFromSessionOrAuthCookie(), 
				getCookieAndContributorSource().getProjectFromSessionOrProjectCookie(), 
				getProjectHelper(), getCookieAndContributorSource());
		addPageBeginRenderListener(getProjectValidator());
	}
	public void pageDetached(PageEvent event) {
		removePageBeginRenderListener(getProjectValidator());
	}
}