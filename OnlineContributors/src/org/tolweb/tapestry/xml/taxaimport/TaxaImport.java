package org.tolweb.tapestry.xml.taxaimport;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.collections.Transformer;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.IFieldTracking;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TaxaImportRecord;
import org.tolweb.tapestry.AbstractScientificContributorPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.xml.taxaimport.beans.XTNode;
import org.tolweb.tapestry.xml.taxaimport.beans.XTOthername;
import org.tolweb.tapestry.xml.taxaimport.beans.XTRoot;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.ParentPageInfo;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Imports new Taxa in tol-xml format (soon-to-be described with XSD) and grafts the 
 * taxa into place at the basal node.  Intended to be used as part of an Upload-Merge 
 * workflow process.
 * 
 * This step in the workflow assumes that basal node represents an empty group. 
 *  
 * Date: January 28, 2008
 * @author lenards
 */
public abstract class TaxaImport extends AbstractScientificContributorPage implements 
	NodeInjectable, PageInjectable, MiscInjectable, BaseInjectable, MetaInjectable, CookieInjectable {
	// used by radio-button to indicate which method of input has been chosen by the user
	public static final int UPLOAD_FILE = 0;
	public static final int USE_TEXT = 1;	
	
	private Transformer mappedNodeListToStringMappedNodeMap = new Transformer() {
		@SuppressWarnings("unchecked")
		public Object transform(Object obj) {
			List<MappedNode> inactiveNodes = (List<MappedNode>)obj;
			Map<String, MappedNode> inactiveMap = new HashMap<String, MappedNode>();
			for (MappedNode nd: inactiveNodes) {
				if (StringUtils.notEmpty(nd.getName())) {
					inactiveMap.put(nd.getName(), nd);
				}
			}
			return inactiveMap;
		}
	};
	
	// radio-button will set this value based on which method of input the user chose
	public abstract int getUploadOption();
	public abstract void setUploadOption(int value);
	// if text is used, the text area component will make the entered text available through this method
	public abstract String getTypedText();
	public abstract void setTypedText(String value);
	// if file is used, the multi-part portion of the file will be available through this method
	public abstract IUploadFile getUploadFile();
	
	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);

	@InitialValue("true")
	public abstract Boolean getReconcileWithPrevious();
	public abstract void setReconcileWithPrevious(Boolean value);
	
	public abstract Boolean getPreserveNodeProperties();
	public abstract void setPreserveNodeProperties(Boolean value);
	
	public abstract Boolean getPreserveNodeName();
	public abstract void setPreserveNodeName(Boolean value);
	
	@InitialValue("true")
	public abstract Boolean getDisplayListsOnNewPages();
	public abstract void setDisplayListsOnNewPages(Boolean value);
	
	public abstract Map<String, MappedNode> getInactiveNodes();
	public abstract void setInactiveNodes(Map<String, MappedNode> inactiveNodes);
	
	public abstract List<MappedNode> getNodesToCreatePagesFor(); 
	public abstract void setNodesToCreatePagesFor(List<MappedNode> nodes);
	
	public abstract List<MappedNode> getReactivatedNodes();
	public abstract void setReactivatedNodes(List<MappedNode> nodes);

	public abstract List<MappedNode> getNewNodes();
	public abstract void setNewNodes(List<MappedNode> nodes);
	
	public abstract TaxaImportRecord getCurrentTaxaImportRecord();
	public abstract void setCurrentTaxaImportRecord(TaxaImportRecord record);
	
	@Bean
	public abstract ValidationDelegate getValidationDelegate();	
	public abstract IFieldTracking getCurrentFieldTracking();
	public abstract void setCurrentFieldTracking(IFieldTracking ift);
	
	public abstract Set<String> getDuplicateNames();
	public abstract void setDuplicateNames(Set<String> dupes);
	
	@SuppressWarnings("unchecked")
	public Map<String, MappedNode> getCurrentInactiveNodes() {
		if (getInactiveNodes() == null) {
			initializeInactiveNodesMap();
		}
		return getInactiveNodes();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeInactiveNodesMap() {
		List<MappedNode> inactiveNodes = getWorkingFilteredNodeDAO().getAllInactiveNodes();
		setInactiveNodes((Map<String, MappedNode>)mappedNodeListToStringMappedNodeMap.transform(inactiveNodes));
	}
	
	private void initializeCollections() {
		// get the inactive nodes map setup before we hit it a ton of times during relationship building
		initializeInactiveNodesMap();
		setNodesToCreatePagesFor(new ArrayList<MappedNode>());
		setReactivatedNodes(new ArrayList<MappedNode>());
		setNewNodes(new ArrayList<MappedNode>());
	}
	
	private void logImport() {
		TaxaImportRecord record = new TaxaImportRecord();
		record.setBasalNodeId(getBasalNodeId());
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		record.setUploadedBy((contr != null) ? contr.getEmail() : "[unavailable]");
		record.setReconcileWithPrevious(getReconcileWithPrevious());
		record.setPreserveNodeName(getPreserveNodeName());
		record.setPreserveNodeProperties(getPreserveNodeProperties());
		record.setIngest(getUserInput());
		getTaxaImportLogDAO().createTaxaImportRecord(record);
		setCurrentTaxaImportRecord(record);
	}
	
	@SuppressWarnings("unchecked")
	public IPage processInputXml(IRequestCycle cycle) {
		TaxaImportConfirmation confirmationPage = (TaxaImportConfirmation)cycle.getPage("taxaimport/TaxaImportConfirmation");
		
		// for performance testing - to estimate the run time of the import because we're having timeout issues. 
		long startTime = System.currentTimeMillis();
		System.out.println("#TAXA-IMPORT start: " + startTime + " Date:" + (new Date()));
		
		try {
			
			String input = getUserInput();
			boolean valid = validateInput(input);
			if (!valid) {
				return null;
			} else {
				System.out.println("[T-I]: input xml file validation succeeded...");
			}
			
			XTRoot inputRoot = getInputRoot(input);
			
			if (inputRoot == null || StringUtils.isEmpty(inputRoot.getVersion()) || !inputRoot.getVersion().equals("1.0")) {
				throw new UnsupportedTaxaImportFormatVersionException();
			}
			
			// get the mapped-representation of the node
			MappedNode mroot = getWorkingNodeDAO().getNodeWithId(getBasalNodeId());
			// okay - we want the page because it's what's attached to the node
			MappedPage mpage = getWorkingPageDAO().getPage(mroot);
			ParentPageInfo parentPage = new ParentPageInfo();
			// page-id in the node is really the 'page which the node appears as a terminal taxa' - not the page attached to the node
			// so use the page id you get through the page dao.
			parentPage.setPageId(mpage.getPageId());
			
			logImport();
			initializeCollections();
			System.out.println("[T-I]: building mapped relations... ");
			buildMappedRelations(inputRoot, mroot, parentPage); 
			Set nodeAncestorIds = getMiscNodeDAO().getAncestorsForNode(mroot.getNodeId());
			
			System.out.println("[T-I]: start 'saving the children' "); 
			saveTheChildren(mroot, nodeAncestorIds);
			System.out.println("[T-I]: done 'saving the children'");
			
			System.out.println("[T-I]: start creating pages timestamp: " + System.currentTimeMillis() + " date:" + (new Date()));
			createPages();
			System.out.println("[T-I]: done creating pages timestamp: " + System.currentTimeMillis() + " date:" + (new Date()));
			
			verifyNewNodesCorrect();
			confirmationPage.setReactivatedNodes(getReactivatedNodes());
			confirmationPage.setNewNodes(getNewNodes());
			confirmationPage.setTaxaImportRecord(getCurrentTaxaImportRecord());
			
			PropertyUtils.write(confirmationPage, "heading", "Taxa Import Successful");
			PropertyUtils.write(confirmationPage, "message", "The custom taxa import import performed on node-id: " + getBasalNodeId() + " was successful.");
			PropertyUtils.write(confirmationPage, "hasErrors", Boolean.FALSE);
			PropertyUtils.write(confirmationPage, "basalNodeId", getBasalNodeId());
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			PropertyUtils.write(confirmationPage, "heading", "Taxa Import Failed");
			PropertyUtils.write(confirmationPage, "message", "The custom taxa import import performed on node-id: " + getBasalNodeId() + 
					" was not successful.  " + "An exception occurred. " + e.toString() + " :: " + e.getMessage());
			PropertyUtils.write(confirmationPage, "hasErrors", Boolean.TRUE);
			System.out.println(confirmationPage.getHasErrors());
		}
		long stopTime = System.currentTimeMillis();
		System.out.println("#TAXA-IMPORT stop: " + stopTime + " Date:" + (new Date()));
		System.out.println("#TAXA-IMPORT duration: " + (stopTime - startTime));
		return confirmationPage;
	}

	private boolean validateInput(String input) {
		XMLReader xerces = null;
		try {
			xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			Builder parser = new Builder(xerces);
			Document doc = parser.build(new StringReader(input));
			Nodes nodes = doc.query("//node/name");
			List<String> nodeNames = buildListFromXmlNodes(nodes);
			Set<String> dupes = new TreeSet<String>();
			boolean noHomonyms = TaxaImportCheck.performHomonymyCheck(nodeNames, dupes);
			setDuplicateNames(dupes);
			for (String name : dupes) {
				getValidationDelegate().record(name + " appears more than once in the XML ingest document", ValidationConstraint.CONSISTENCY);
			}
			return noHomonyms;
		} catch (SAXException e) {
			getValidationDelegate().record("Validation failed due to an exception: " + e.getMessage(), ValidationConstraint.CONSISTENCY);
			e.printStackTrace();
		} catch (ValidityException e) {
			getValidationDelegate().record("Validation failed due to an exception: " + e.getMessage(), ValidationConstraint.CONSISTENCY);
			e.printStackTrace();
		} catch (ParsingException e) {
			getValidationDelegate().record("Validation failed due to an exception: " + e.getMessage(), ValidationConstraint.CONSISTENCY);
			e.printStackTrace();
		} catch (IOException e) {
			getValidationDelegate().record("Validation failed due to an exception: " + e.getMessage(), ValidationConstraint.CONSISTENCY);
			e.printStackTrace();
		}
		return false;
	}
	
	private List<String> buildListFromXmlNodes(Nodes names) {
		List<String> namesList = new ArrayList<String>();
		if (names != null && names.size() > 0) {
			for (int i = 0; i < names.size(); i++) {
				Node n = names.get(i);
				namesList.add(n.getValue().trim());
			}
		}		
		return namesList;
	}
	@SuppressWarnings("unchecked")
	private void saveTheChildren(MappedNode mnode, Set nodeAncestorIds) {
		if (mnode != null) {
			if (mnode.getSynonyms() != null) {
				mnode.setSynonyms(new TreeSet(mnode.getSynonyms()));
			}
			getWorkingNodeDAO().saveNode(mnode);
			pushNode(mnode);

			markForPageCreation(mnode);
			// Add ourselves to the list of the ancestors (for querying purposes a node
			// is considered to be an ancestor of itself)
	    	nodeAncestorIds.add(mnode.getNodeId());
			getMiscNodeDAO().resetAncestorsForNode(mnode.getNodeId(), nodeAncestorIds);
			
			System.out.println("saving " + mnode.getName() + " ...... ");
			if (mnode.getChildren() != null) {
				System.out.println("children? " + !mnode.getChildren().isEmpty());
				for (Iterator itr = mnode.getChildren().iterator(); itr.hasNext(); ) {
					MappedNode child = (MappedNode)itr.next();
					child.setParentNodeId(Long.valueOf(child.getParent().getId()));
					System.out.println("\tchild: " + child.getName());
					saveTheChildren(child, nodeAncestorIds);
				}
			}
			
			// remove yourself from the node ancestors set
			System.out.println("removing identity relation from node-ancestors.... (" + mnode.getNodeId() + ")");
			nodeAncestorIds.remove(mnode.getNodeId());
		}
	}
	
	/**
	 * Executes the process defined by the NodePusher for pushing a mapped node to the Misc Database.
	 * @param mnode the node to push up
	 */
	private void pushNode(MappedNode mnode) {
		try {
			getNodePusher().pushNodeToDB(mnode, getMiscNodeDAO());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds the relations defined in the xml taxa ingestion object model in terms of the mapped node 
	 * structure.  In other words, it uses the xml structures defined to create an analogous structure 
	 * in the parlance of the tree-of-life object model (e.g. mapped objects defined in the tolweb 
	 * hibernate jar).
	 * @param input the xml taxa ingestion object representation
	 * @param mnode the mapped node object representation
	 * @param parentPage the parent page to associated new taxa/nodes with
	 */
	private void buildMappedRelations(XTRoot input, MappedNode mnode, ParentPageInfo parentPage) {
		XTNode rootNode = null;
		// determine what type of xml ingestion we're dealing with: 
		//    single root node element vs. nodes element w/ multiple child node elements
		if (input != null) {
			if (!input.hasSingleRootNode()) {
				rootNode = new XTNode(); // create a faux root node - hey, fake it 'til you make it!
				TaxaImportHelper.seedNodeData(rootNode, mnode); // use data from the mapped-tol-node as faux node's data
				rootNode.setNodes(input.getNodes()); // make the child node elements its' children 
			} else {
				rootNode = input.getNode(); // proceed as usual, we have a single root node element
			}
		}
		recurseMapping(mnode, rootNode, parentPage);
	}

	private void createPages() {
		boolean writeAsList = getDisplayListsOnNewPages() != null && getDisplayListsOnNewPages();
		for (MappedNode mnode : getNodesToCreatePagesFor()) {
			System.out.println("\t\t creating page for node: " + (mnode));
			Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
			MappedPage mpage = getWorkingPageDAO().addPageForNode(mnode, contr, true);
			mpage.setWriteAsList(writeAsList);
		}
	}
	
	private void markForPageCreation(MappedNode mnode) {
		if (mnode.getHasPage()) {
			getNodesToCreatePagesFor().add(mnode);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void recurseMapping(MappedNode mappedCurr, XTNode xtCurr, ParentPageInfo parentPage) {
		if (mappedCurr != null && xtCurr != null) {
			// copy relevant data from betwixt-node to mapped-node 
			TaxaImportHelper.copyNodeData(mappedCurr, xtCurr, getPreserveNodeProperties(), getPreserveNodeName());
			handleOtherNames(mappedCurr, xtCurr);	
	
			// initialize the current root's order on parent to be zero 
			// because we're determining the initial order of the parent list    	
	    	Integer counter = 0;
			
			if (hasChildren(xtCurr)) {
				
				if (xtCurr.getNodes().size() == 1) {
					// we don't roll w/ single child nodes here at tolweb
					List<XTNode> nodesToSkip = new ArrayList<XTNode>();
					// we'll loop over the child nodes until we've reached a leaf/terminal
					while(!xtCurr.getNodes().isEmpty()) {
						nodesToSkip.add(xtCurr);
						xtCurr = xtCurr.getNodes().get(0);
						
						if (xtCurr == null || xtCurr.getNodes() == null || xtCurr.getNodes().size() != 1) {
							break;
						}
					}
					reassociateOtherNames(nodesToSkip, xtCurr);
					// look - we just collapses a bunch of nodes... and we need to clear out any "synonyms"/othernames that 
					// were added when doing processing above so they don't get duplicated.  
					if (mappedCurr.getSynonyms() != null) {
						mappedCurr.getSynonyms().clear();
					}
					MappedNode reconcile = reconcile(xtCurr.getName().trim());
					if (reconcile != null) {
						mappedCurr.getParent().getChildren().remove(mappedCurr);
						reconcile.setParent(mappedCurr.getParent());
						reconcile.setParentNodeId(mappedCurr.getParentNodeId());
						reconcile.setOrderOnParent(mappedCurr.getOrderOnParent());
						reconcile.setOrderOnPage(mappedCurr.getOrderOnPage());
						mappedCurr = reconcile;
						mappedCurr.getParent().getChildren().add(mappedCurr);
					}
					// during the collapse process, we've changed the tree structure - so the current mapped node 
					// needs to be updated with the appropriate data that it now represents
					TaxaImportHelper.copyNodeData(mappedCurr, xtCurr, getPreserveNodeProperties(), false);
					// This call will reattach the other names to the mapped-node
					handleOtherNames(mappedCurr, xtCurr);
					
					// we've processed xtCurr, so we want to continue from xtCurr's children, loop over and do the mapping for them
					for (XTNode xtn : xtCurr.getNodes()) {
						String xtNodeName = xtn.getName().trim();
						MappedNode tnode = createAndMapMappedNode(xtNodeName, mappedCurr, parentPage, ++counter);
						recurseMapping(tnode, xtn, parentPage);
					}					
				} else {
					for (XTNode xtn : xtCurr.getNodes()) {
						String xtNodeName = xtn.getName().trim();
						MappedNode tnode = createAndMapMappedNode(xtNodeName, mappedCurr, parentPage, ++counter);
						recurseMapping(tnode, xtn, parentPage);
					}
				}
				
			}
		}
	}
	
	private boolean hasChildren(XTNode xtnode) {
		return xtnode.getNodes() != null && !xtnode.getNodes().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private MappedNode createAndMapMappedNode(String nodeName, MappedNode curr, ParentPageInfo parentPage, Integer counter) {
		MappedNode tnode = reconcileAndResolve(nodeName);
		tnode.setParent(curr);
		tnode.setPageId(parentPage.getPageId());
		tnode.setOrderOnParent(counter);
		tnode.setOrderOnPage(counter);
		curr.getChildren().add(tnode);
		return tnode;
	}

	/**
	 * Determines if a node with the same name as the argument has 
	 * existed in previous incarnations of the clade. 
	 * @param nodeName 
	 * @return either a new node or a reconciled node
	 */
	private MappedNode reconcileAndResolve(String nodeName) {
		MappedNode golem = reconcile(nodeName);
		if (golem == null) {
			golem = new MappedNode();
			getNewNodes().add(golem);
		}
		return golem;
	}
	
	private MappedNode reconcile(String nodeName) {
		if (getReconcileWithPrevious() && StringUtils.notEmpty(nodeName)) {
			// attempt a resurrection! (if it's found in the inactive nodes, bring it back to life
			MappedNode lazarus = getInactiveNodes().get(nodeName);
			if (lazarus != null) {
				System.out.println("\tPraise Be! " + lazarus.toString() + " has been ressurrected!");
				lazarus.setStatus(MappedNode.ACTIVE); // reconciled nodes need to be reactivated.
				getReactivatedNodes().add(lazarus);
				return lazarus;
			} 
		}
		return null;
	}
	
	
	/**
	 * Takes nodes that was skipped over and attaches naming information to the 
	 * current, argument node.
	 * @param nodesToSkip nodes that was skipped over due to single-child rules
	 * @param xtCurr current node to attach naming information to 
	 */
	private void reassociateOtherNames(List<XTNode> nodesToSkip, XTNode xtCurr) {
		int idx = 0;
		for (XTNode xtn : nodesToSkip) {
			resetOtherNameImportance(xtn.getOthernames());
			XTOthername newOtherName = new XTOthername();
			newOtherName.setName(xtn.getName());
			newOtherName.setAuthority(xtn.getAuthority());
			newOtherName.setDate(xtn.getAuthDate());
			newOtherName.setItalicizeName(xtn.getItalicizeName());
			// the first name is the list is the one we want to set as preferred
			if (idx == 0) {
				newOtherName.setIsPreferred(Boolean.valueOf(true));
			} else {
				newOtherName.setIsImportant(Boolean.valueOf(true));	
			}
			newOtherName.setSequence(idx++);
			xtCurr.getOthernames().add(newOtherName);
			xtCurr.getOthernames().addAll(xtn.getOthernames());
		}
	}
	
	/**
	 * Resets the importance settings on the argument list to be unimportant.
	 * @param othernames argument to reset importance for
	 */
	private void resetOtherNameImportance(List<XTOthername> othernames) {
		for (XTOthername xto : othernames) {
			xto.setIsImportant(Boolean.valueOf(false));
		}
	}
	
	/**
	 * Takes othername data associated with the xml-taxa-node and attached it 
	 * to the mapped-node representation (aka the tol-node).
	 * @param mappedCurr the mapped node in the tree-of-life object model
	 * @param xtCurr the xml-taxa-node that represents the taxa import ingestion xml
	 */
	@SuppressWarnings("unchecked")
	private void handleOtherNames(MappedNode mappedCurr, XTNode xtCurr) {
		if (xtCurr.getOthernames() != null && !xtCurr.getOthernames().isEmpty()) {
			int offset = mappedCurr.getSynonyms() != null ? mappedCurr.getSynonyms().size() : 0;
			for (XTOthername xto : xtCurr.getOthernames()) {
				MappedOtherName tname = new MappedOtherName();
				if (mappedCurr.getSynonyms() == null) {
					mappedCurr.setSynonyms(new TreeSet());
				}
				TaxaImportHelper.copyOtherNameData(tname, xto);
				
				// if there are othernames present, we need to adjust the 
				// sequence/order of the new entries to account for this
				if (!mappedCurr.getSynonyms().isEmpty()) {
					tname.setOrder(offset+tname.getName().hashCode());

				}
				tname.setOrder(offset+tname.hashCode());
				boolean added = mappedCurr.addSynonym(tname);
				if (added) {
					offset++;
					System.out.println("\tother-name: [" + tname.toString() + "] added to node:" + mappedCurr.getName());
				} else {
					System.out.println("\tother-name: [" + tname.toString() + "] *not* added to node:" + mappedCurr.getName());
				}
			}
			int order = 0;
    		for(Iterator itr = mappedCurr.getSynonyms().iterator(); itr.hasNext(); order++) {
    			((MappedOtherName)itr.next()).setOrder(order);
    		}
		}
	}

	/**
	 * Returns an object representation of the input xml (passed as a string)
	 * @param input the string representation of the xml taxa ingestion format
	 * @return the document represent in the xml taxa ingestion object model
	 */
	private XTRoot getInputRoot(String input) {
		try {
			BeanReader beanReader = new BeanReader();
			beanReader.registerBeanClass(XTRoot.class);
			return (XTRoot)beanReader.parse(new StringReader(input));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("whoa...something bad happened... ");
			return null;
		}
	}
	
	private void verifyNewNodesCorrect() {
		List<MappedNode> toRemove = new ArrayList<MappedNode>();
		for (MappedNode mnode : getNewNodes()) {
			// if the node-id is null, then the node didn't get saved 
			// (e.g. it's not part of the new clade)
			if (mnode == null || mnode.getNodeId() == null) {
				toRemove.add(mnode);
			}
		}
		for (MappedNode remove : toRemove) {
			getNewNodes().remove(remove);
		}
	}	
	
	/**
	 * Gets the user input - which may be a file or typed-text from a text area
	 * @return the input from the user as a String
	 */
	private String getUserInput() {
		return getTapestryHelper().getStringFromUploadFile(getUploadFile());
	}
	
    public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 350;
    	return getRendererFactory().getLinkRenderer("Find Basal Node Id", width, height);
    }	
}
