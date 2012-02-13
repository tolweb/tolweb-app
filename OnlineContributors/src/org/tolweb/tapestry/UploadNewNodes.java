package org.tolweb.tapestry;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.services.LinkFactory;
import org.jdom.Document;
import org.tolweb.base.xml.BaseXMLReader;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.nameparser.RankedNameParser;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.selectionmodels.PersistentObjectSelectionModelWithDefault;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.TabDelimitedNameParser;
import org.tolweb.treegrowserver.UploadBuilder;

public abstract class UploadNewNodes extends AbstractScientificContributorPage implements NodeInjectable, 
        UserInjectable, PageInjectable, PageBeginRenderListener, IExternalPage, ProjectInjectable, 
        MiscInjectable, BaseInjectable, TreeGrowServerInjectable {
	public static final int ALL_NAMED = 0;
	public static final int ALL_NON_TERMINAL = 1;
	public static final int SPECIFY_RANKS = 2;
	public static final int TAB_SEPARATED = 0;
	public static final int RANK_NAME_DATE = 1;
	public static final int UPLOAD_FILE = 0;
	public static final int USE_TEXT = 1;

	private IPropertySelectionModel fileTypeModel;

	
	public UploadNewNodes() {
		fileTypeModel = new IPropertySelectionModel() {
			public int getOptionCount() {
				return 2;
			}
			public Object getOption(int index) {
				return index;
			}
			public String getLabel(int index) {
				switch (index) {
					case TAB_SEPARATED: return "Tab separated names and dates";
					case RANK_NAME_DATE: return "Names and dates with ranks included"; 
				}
				return null;
			}
			public String getValue(int index) {
				return "" + index;
			}
			public Object translateValue(String value) {
				return Integer.parseInt(value);
			}
		};
	}
	
	public abstract int getUploadOption();
	public abstract void setUploadOption(int value);
	public abstract String getTypedText();
	public abstract void setTypedText(String value);
	public abstract Long getNodeId();
	public abstract void setNodeId(Long value);
	public abstract IUploadFile getUploadFile();
	public abstract int getCreatePageOption();
    public abstract void setCreatePageOption(int value);
	public abstract int getFileType();
	public abstract void setFileType(int value);
	public abstract boolean getCreatePagesSuperfamily();
	public abstract boolean getCreatePagesFamily();	
	public abstract boolean getCreatePagesSubfamily();	
	public abstract boolean getCreatePagesTribe();
	public abstract boolean getUseTaxonLists();
    public abstract void setUseTaxonLists(boolean value);
	public abstract boolean getAttachToExistingNodes();
	public abstract String getNameRegexString();
	public abstract String getNameRegexReplacementString();
	public abstract String getExtinctIndicatorString();
    public abstract void setExtinctIndicatorString(String value);
    public abstract String getSourceDbIdSeparatorString();
    public abstract void setSourceDbIdSeparatorString(String value);
    public abstract void setErrorMessage(String value);
    public abstract String getErrorMessage();

    @Persist("session")
    public abstract ForeignDatabase getSourceDatabase();
    @SuppressWarnings("unchecked")
    public abstract void setExistingNodeNames(List value);
    @SuppressWarnings("unchecked")
    public abstract List getExistingNodeNames();
    public abstract String getPreviousString();
    public abstract void setPreviousString(String value);
    @InjectPage("TaxaIndex")
    public abstract TaxaIndex getTaxaIndexPage();
    public abstract boolean getIncompleteSubgroups();
    public abstract void setIncompleteSubgroups(boolean value);
    @InjectObject("service:tapestry.url.LinkFactory")
    public abstract LinkFactory getLinkFactory();
    @InjectObject("engine-service:external")
    public abstract IEngineService getExternalService();
    @Persist("client")
    public abstract boolean getFromBranchOrLeaf();
    public abstract void setFromBranchOrLeaf(boolean value);
    
	public String getExistingNodeNamesString() {
		return StringUtils.returnCommaJoinedString(getExistingNodeNames());
	}
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		setNodeId((Long) parameters[0]);
		if (parameters.length > 1) {
			setFromBranchOrLeaf((Boolean) parameters[1]);
		}
	}
	
    public void pageBeginRender(PageEvent event) {
        if (!event.getRequestCycle().isRewinding()) {
            setExtinctIndicatorString("+");
            setSourceDbIdSeparatorString("^");
            setUseTaxonLists(true);
            setCreatePageOption(ALL_NON_TERMINAL);
            // for the simplified version, always use tab separated
            setFileType(TAB_SEPARATED);
            setIncompleteSubgroups(getWorkingNodeDAO().getNodeWithId(getNodeId()).getHasIncompleteSubgroups());
        }
    }
    
    @SuppressWarnings("unchecked")
    public PersistentObjectSelectionModelWithDefault getDatabaseModel() {
        List databases = getMiscNodeDAO().getAllForeignDatabases();
        PersistentObjectSelectionModelWithDefault model = new PersistentObjectSelectionModelWithDefault(databases, "No database selected");
        return model;
    }

	public IPropertySelectionModel getFileTypeModel() {
		return fileTypeModel;
	}
	
	public String getNodeName() {
		return getWorkingNodeDAO().getNameForNodeWithId(getNodeId());
	}
	
	public ILink markIncomplete() {
		markNodeIncomplete();
		if (!getFromBranchOrLeaf()) {
			Long pageNodeId = getWorkingPageDAO().getRootNodeIdOnPage(getNodeId());
			Object[] externalParams = {pageNodeId, getProject().getId()};
			return getTapestryHelper().getExternalServiceLink("TaxaIndex", externalParams);
		} else {
			returnToBranchOrLeafPage();
			// not actually reached but the compiler doesn't know that
			return null;
		}
	}
	private void returnToBranchOrLeafPage() {
		MappedNode workingNode = getWorkingNodeDAO().getNodeWithId(getNodeId());
		String workingUrl = getUrlBuilder().getWorkingURLForObject(workingNode);
		throw new RedirectException(workingUrl);
	}
	private void markNodeIncomplete() {
		MappedNode parentNode = getWorkingNodeDAO().getNodeWithId(getNodeId());
		// set up incomplete subgroups before uploading new ones
		parentNode.setHasIncompleteSubgroups(getIncompleteSubgroups());
		getWorkingNodeDAO().updateHasIncompleteSubgroupsForNode(parentNode.getNodeId(), getIncompleteSubgroups());		
	}
	
	@SuppressWarnings("unchecked")
	public IPage uploadNewNodes(IRequestCycle cycle) throws UnsupportedEncodingException {
		String namesString = null;
		if (getUploadOption() == UPLOAD_FILE) {
			namesString = getTapestryHelper().getStringFromUploadFile(getUploadFile());
		} else {
			namesString = getTypedText();
		}
		int fileType = getFileType();
		// take care of the incomplete marking before we branch out
		markNodeIncomplete();
		List nodes = null;
		NodeDAO workingNodeDAO = getWorkingNodeDAO();
		//PageDAO workingPageDAO = getWorkingPageDAO();
		MappedNode parentNode = workingNodeDAO.getNodeWithId(getNodeId());
		if (parentNode != null) {
			parentNode.setTreeOrder(Long.valueOf(0));
		}
		// try parsing it into an xml doc.  if it parses treat it as an xml upload
		Document doc = BaseXMLReader.getDocumentFromString(namesString);
		if (doc != null) {
			return handleXMLUpload(doc, namesString, true);
		}
		
		/*MappedPage parentPage;
		if (workingPageDAO.getNodeHasPage(parentNode)) {
			parentPage = workingPageDAO.getPageForNode(parentNode);
		} else {
			parentPage = workingPageDAO.getPageNodeIsOn(parentNode);
		}*/
		String separators = TabDelimitedNameParser.LINE_SEPARATORS;
		if (fileType == TAB_SEPARATED) {
			// check to see if any of the node names exist as children of the parent node
			List allNames = TabDelimitedNameParser.getAllNodeNames(namesString);
			IPage returnPage = checkForDuplicateNodes(namesString, allNames);
			if (returnPage != null) {
				return returnPage;
			}
			nodes = TabDelimitedNameParser.parseNamesString(namesString, separators, getExtinctIndicatorString(), getSourceDbIdSeparatorString());
		} else {
			String nameRegexReplacementString = getNameRegexReplacementString();
			if (StringUtils.notEmpty(nameRegexReplacementString)) {
				nameRegexReplacementString = nameRegexReplacementString.replace("<tab>", "\t");
			}
			nodes = RankedNameParser.parseNamesString(namesString, separators, getNameRegexString(), nameRegexReplacementString);
		}
		return finishUpload(nodes, parentNode, namesString);
	}
	
	@SuppressWarnings("unchecked")
	private IPage checkForDuplicateNodes(String namesString, List allNames) {
		List existingNodes = getWorkingNodeDAO().getNodesWithNames(allNames, getNodeId());		
		if (existingNodes != null && existingNodes.size() > 0) {
			// have some existing nodes with the same name.  give the user the
			// option to cancel
			setExistingNodeNames(existingNodes);
			setPreviousString(namesString);
			return this;
		} else {
			return null;
		}
	} 
	
	public UploadNewNodes cancelUpload() {
		setExistingNodeNames(null);
		setPreviousString(null);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private IPage handleXMLUpload(Document doc, String xmlString, boolean checkDuplicates) {
		List nodeNames = getServerXMLReader().getAllNodeNames(doc);
		IPage returnPage = null;
		if (checkDuplicates) {
			returnPage = checkForDuplicateNodes(xmlString, nodeNames);
		}
		// found some duplicate node names so warn the user
		if (returnPage != null) {
			return returnPage;
		} else {
			try {
				getUploadBuilder().buildUpload(xmlString, getCookieAndContributorSource().getContributorFromSessionOrAuthCookie(), true, 
						getNodeId());
			} catch (Exception e) {
				setErrorMessage("Error parsing xml.  Please verify your xml.");
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;				
			}
		}
		return returnToPreviousPage(getWorkingNodeDAO().getNodeWithId(getNodeId()));
	}
	/**
	 * Listener called in the case that the user previously tried to upload
	 * with nodes that already exist in the db.  They've elected to proceed
	 * anyway.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IPage uploadPreviousString() {
		String previousString = getPreviousString();
		Document doc = BaseXMLReader.getDocumentFromString(previousString);
		if (doc != null) {
			return handleXMLUpload(doc, previousString, false);
		} else {
			List nodes = TabDelimitedNameParser.parseNamesString(previousString, "\n\r", getExtinctIndicatorString(), getSourceDbIdSeparatorString());
			MappedNode parentNode = getWorkingNodeDAO().getNodeWithId(getNodeId());

			return finishUpload(nodes, parentNode, getPreviousString());
		}
	}
	@SuppressWarnings("unchecked")
	private IPage finishUpload(List nodes, MappedNode parentNode, String namesString) {
		List newNodes = new ArrayList();
	    UploadBuilder builder = getUploadBuilder();        
	    ForeignDatabase sourceDatabase = getSourceDatabase();
	    
		// iterate over these guys and create mapped nodes out of them.
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			Node nextNode = (Node) iter.next();
			// only worry about calling this for the root(s) of the upload because
			// their children are handled recursively
			if (nextNode.getParent() == null) {
				MappedNode newNode = null;				
				if (getAttachToExistingNodes()) {
					String nextName = nextNode.getName();
					newNode = getWorkingNodeDAO().getNodeWithNameAndParent(nextName, parentNode.getNodeId());
				}
	            // initialize it if it wasn't found or if we aren't attaching to existing
	            // tree structure
	            if (newNode == null) {
	                newNode = new MappedNode();
	            } 

				newNodes.add(newNode);
				builder.copyValuesForMappedNode(newNode, nextNode, sourceDatabase);
			}
		}
		boolean createPagesAllNamed = getCreatePageOption() == ALL_NAMED;
		boolean createPagesAllNonTerminal = getCreatePageOption() == ALL_NON_TERMINAL;
		boolean notOthers = !createPagesAllNamed && !createPagesAllNonTerminal;
		// DM -- the old attach to existing node code operated on terminal nodes on pages.
		//		 in the btol scenario, we want to attach to nodes that are immediate
		//		 children of some parent so a separate function call isn't necessary
		/*if (getAttachToExistingNodes()) {
			builder.uploadNewNodes(parentPage, newNodes, createPagesAllNamed, 
					createPagesAllNonTerminal, notOthers && getCreatePagesSuperfamily(), 
					notOthers && getCreatePagesFamily(),
					notOthers && getCreatePagesSubfamily(), notOthers && getCreatePagesTribe(), 
					getContributor(), namesString, 
					getUseTaxonLists(), true);			
		} else {*/
		builder.uploadNewNodes(parentNode, newNodes, createPagesAllNamed, 
				createPagesAllNonTerminal, notOthers && getCreatePagesSuperfamily(), notOthers && getCreatePagesFamily(),
				notOthers && getCreatePagesSubfamily(), notOthers && getCreatePagesTribe(), 
				getCookieAndContributorSource().getContributorFromSessionOrAuthCookie(), namesString, getUseTaxonLists());
		//}	
		return returnToPreviousPage(parentNode);
	}
	private IPage returnToPreviousPage(MappedNode parentNode) {
		if (!getFromBranchOrLeaf()) {
			TaxaIndex indexPage = getTaxaIndexPage(); 
			Object[] parameters = {parentNode.getNodeId(), indexPage.getProjectId()};
			indexPage.activateExternalPage(parameters, getRequestCycle());
			return indexPage;
		} else {
			returnToBranchOrLeafPage();
			// not actually reached but the compiler doesn't know that
			return null;
		}
	}
}
