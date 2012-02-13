/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.Keywords;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.main.XMLReader;
import org.tolweb.treegrow.page.Page;
import org.tolweb.treegrow.page.TextSection;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.OtherName;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerXMLReader extends XMLReader {
	private ContributorDAO contributorDAO;
	private NodeDAO workingNodeDAO;
	
	public Document parseXML(String xmlString) {
		StringReader reader = new StringReader(xmlString);
		try {
			xmlDocument = new SAXBuilder().build(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlDocument;
	}
	
	public List getChildNodeElements(Element nodeElement) {
		Element nodesElement = nodeElement.getChild(XMLConstants.NODES);
		if (nodesElement != null) {
			return nodesElement.getChildren(XMLConstants.NODE);
		} else {
			return new ArrayList();
		}
	}
	
	/**
	 * Walks the tree and returns a list of all the node ids (if they are
	 * greater than 0 that are contained in the document)
	 * @param document
	 * @return a set of the node ids in the document
	 */
	public Set getNodeIdsFromRootNodeElement(Element rootNodeElement) {
	    Set nodeIds = new HashSet();
	    addChildNodeIdsForNodeElement(rootNodeElement, nodeIds);
	    return nodeIds;
	}
	
	private void addChildNodeIdsForNodeElement(Element nodeElement, Set nodeIdsSet) {
	    String nodeIdString = nodeElement.getAttributeValue(XMLConstants.ID);
	    try {
	        int nodeIdInt = Integer.parseInt(nodeIdString);
	        nodeIdsSet.add(new Long(nodeIdInt));
	    } catch (Exception e) {}
	    Element nodesElement = nodeElement.getChild(XMLConstants.NODES);
	    if (nodesElement != null) {
	        for (Iterator it = nodesElement.getChildren(XMLConstants.NODE).iterator(); it.hasNext();) {
                Element currentNodeElement = (Element) it.next();
                addChildNodeIdsForNodeElement(currentNodeElement, nodeIdsSet);
            }
	    }
	}
	
	public int getRootNodeId(Document document) {
		Element rootNodeElement = getRootNodeElement(document); 
		return getNodeIdFromNodeElement(rootNodeElement);
	}
	
	public Element getRootNodeElement(Document document) {
		Element nodesElement = document.getRootElement().getChild(XMLConstants.NODES);
		if (nodesElement != null) {
			return nodesElement.getChild(XMLConstants.NODE);			
		} else {
			return document.getRootElement().getChild(XMLConstants.NODE);
		}

	}
	
	public Element getPageElementFromNodeElement(Element nodeElement) {
		return nodeElement.getChild(XMLConstants.PAGE);
	}
    
    public boolean getNodeHasPage(Element nodeElement) {
        String hasPage = nodeElement.getAttributeValue(XMLConstants.HASPAGE);
        boolean returnVal = false;
        if (StringUtils.notEmpty(hasPage)) {
            if (hasPage.equals(XMLConstants.ONE)) {
                returnVal = true;
            }
        }
        return returnVal;
    }
	
	protected void setAuthorityDateForNode(Node node, int date) {
        if (date > 0) {
            ((MappedNode) node).setAuthorityDate(new Integer(date));
        }
	}
	
    protected void setNodeRankForNode(Node node, int nodeRank) {
    	((MappedNode) node).setNodeRankInteger(new Integer(nodeRank));    	
    }
    
    protected void setSequenceForNode(Node node, int sequence) {
    	((MappedNode) node).setOrderOnParent(new Integer(sequence));
    }
    
    protected Collection getSynonymCollectionInstance() {
    	return new TreeSet();
    }
    
    protected void setOtherNamesForNode(Node node, Collection syns) {
    	node.setSynonyms((SortedSet) syns);
    }    
    
    protected OtherName getOtherNameInstanceForNode(Node node) {
    	return new MappedOtherName();
    }
    
    protected void setYearForOtherName(OtherName name, int year) {
    	((MappedOtherName) name).setAuthorityYear(new Integer(year));
    }
    
    protected void setOrderForOtherName(OtherName name, int order) {
    	((MappedOtherName) name).setOrder(order);
    }

	/* (non-Javadoc)
	 * @see org.tolweb.treegrow.main.XMLReader#decodeImageListElement(org.jdom.Element, org.tolweb.treegrow.page.Page)
	 */
	protected void decodeImageListElement(Element imageListElement, Page tempPage) {
		SortedSet titleIllustrations = new TreeSet();
		Iterator it = imageListElement.getChildren(XMLConstants.IMAGE).iterator();
		while (it.hasNext()) {
			Element nextImgElement = (Element) it.next();
			TitleIllustration illustration = new TitleIllustration();
			illustration.setVersionId(new Long(nextImgElement.getAttributeValue(XMLConstants.IMAGEID)));
			illustration.setOrder(new Integer(nextImgElement.getAttributeValue(XMLConstants.ORDER)).intValue());
			titleIllustrations.add(illustration);
		}
		((MappedPage) tempPage).setTitleIllustrations(titleIllustrations);
	}
	
	protected void decodeTextListElement(Element textListElement, Page tempPage) {
		// TODO Auto-generated method stub
        List textSectionList = textListElement.getChildren();
        SortedSet textSections = new TreeSet();
        
        Iterator it = textSectionList.iterator();
        while (it.hasNext()) {
            Element textSectionElem = (Element)it.next();
            MappedTextSection tempTextSection = new MappedTextSection();
            
            String attribValue = textSectionElem.getAttributeValue(XMLConstants.CHANGED);
            tempTextSection.setChangedFromServer( attribValue!=null  && attribValue.equals(XMLConstants.TRUE) );
            
            Element headingElem = textSectionElem.getChild(XMLConstants.HEADING);
            Element textElem = textSectionElem.getChild(XMLConstants.TEXT);
            String tempOrderString = textSectionElem.getAttributeValue(XMLConstants.SEQUENCE);
            
            if (headingElem != null) {
                tempTextSection.setHeading(headingElem.getTextTrim());
            } else {
            	tempTextSection.setHeading("");
            }
            if(textElem != null) {
                tempTextSection.setText(textElem.getTextTrim());
            } else {
            	tempTextSection.setText("");
            }
            tempTextSection.setOrder(new Integer(tempOrderString).intValue());
            
            textSections.add(tempTextSection);
        }
        ((MappedPage) tempPage).setTextSections(textSections);		
	}
	
	public void readNodeProperties(Node node, Element nodeElem, boolean isNewVersion) {
		super.readNodeProperties(node, nodeElem, isNewVersion);
		// also check some properties that aren't read in when doing treegrow stuff
		String incSubgroups = nodeElem.getAttributeValue(XMLConstants.INCOMPLETESUBGROUPS);
		if (StringUtils.notEmpty(incSubgroups)) {
			node.setHasIncompleteSubgroups(XMLReader.getBooleanValue(nodeElem, XMLConstants.INCOMPLETESUBGROUPS));
		}
		String showAuthContaining = nodeElem.getAttributeValue(XMLConstants.SHOWAUTHORITYCONTAINING);
		if (StringUtils.notEmpty(showAuthContaining)) {
			((MappedNode) node).setShowAuthorityInContainingGroup(XMLReader.getBooleanValue(nodeElem, XMLConstants.SHOWAUTHORITYCONTAINING));
		}
		String italicizeName = nodeElem.getAttributeValue(XMLConstants.ITALICIZE_NAME);
		if (StringUtils.notEmpty(italicizeName)) {
			((MappedNode) node).setItalicizeName(XMLReader.getBooleanValue(nodeElem, XMLConstants.ITALICIZE_NAME));
		}
		String newCombination = nodeElem.getAttributeValue(XMLConstants.IS_NEW_COMBINATION);
		if (StringUtils.notEmpty(newCombination)) {
			((MappedNode) node).setIsNewCombination(XMLReader.getBooleanValue(nodeElem, XMLConstants.IS_NEW_COMBINATION));
		}
		String nameComment = nodeElem.getChildText(XMLConstants.NAMECOMMENT);
		if (nameComment != null) {
			((MappedNode) node).setNameComment(nameComment);
		}
		String combAuthor = nodeElem.getChildText(XMLConstants.COMBINATION_AUTHOR);
		if (combAuthor != null) {
			((MappedNode) node).setCombinationAuthor(combAuthor);
		}
		String combDate = nodeElem.getChildText(XMLConstants.COMBINATION_DATE);
		if (StringUtils.notEmpty(combDate)) {
			try {
				// if the date isn't an int just ignore it
				((MappedNode) node).setCombinationDate(new Integer(combDate));				
			} catch (Exception e) {}
		}
	}
    
    protected void fetchNote(Element nodeElem, Node node) {
        super.fetchNote(nodeElem, node);
    }
    protected void fetchSynonyms(Element nodeElem, Node node) {
        super.fetchSynonyms(nodeElem, node);
    }
    protected void fetchAuthorities(Element nodeElem, Node node) {
        super.fetchAuthorities(nodeElem, node);
    }

	/* (non-Javadoc)
	 * @see org.tolweb.treegrow.main.XMLReader#getCollectionInstanceForPageContributors()
	 */
	protected Collection getCollectionInstanceForPageContributors() {
		return new TreeSet();
	}

	/* (non-Javadoc)
	 * @see org.tolweb.treegrow.main.XMLReader#setPageContributorsForPage(org.tolweb.treegrow.page.Page, java.util.Collection)
	 */
	protected void setPageContributorsForPage(Page page, Collection pageContributors) {
		((MappedPage) page).setContributors((SortedSet) pageContributors);
	}

    public boolean getIsNewVersion(Element rootElement) {
        String newVersionString = rootElement.getAttributeValue(XMLConstants.NEW_VERSION);
        return StringUtils.notEmpty(newVersionString) && newVersionString.equals(XMLConstants.ONE);
    }	
    public void getNodeImageFromElement(NodeImage newImage, Element imgElem, Contributor contr2) {
        String copyrightOwner =  imgElem.getChildText(XMLConstants.copyrightowner);
        if (StringUtils.notEmpty(copyrightOwner)) {
            if (StringUtils.getIsNumeric(copyrightOwner)) {
                // It's an id, so lookup the contributor and set them.
                Contributor contr = getContributorDAO().getContributorWithId(copyrightOwner);
                if (contr != null) {
                    newImage.setCopyrightOwnerContributor(contr);
                }
            } else if (copyrightOwner.equalsIgnoreCase(XMLConstants.pd)) {
                // public domain
                newImage.setInPublicDomain(true);
                newImage.setCopyrightOwnerContributor(null);
            } else {
                newImage.setCopyrightOwner(copyrightOwner);
                newImage.setCopyrightEmail(imgElem.getChildText(XMLConstants.copyrightemail));
                newImage.setCopyrightUrl(imgElem.getChildText(XMLConstants.copyrighturl));
                newImage.setCopyrightOwnerContributor(null);
            }
        } else {
        	newImage.setCopyrightOwnerContributor(contr2);
        }
        String copyrightDate = imgElem.getChildText(XMLConstants.copyrightdate);
        if (StringUtils.notEmpty(copyrightDate)) {
            newImage.setCopyrightDate(copyrightDate);
        }
        String use = imgElem.getChildText(XMLConstants.license);
        boolean notEmpty = StringUtils.notEmpty(use);
        byte usePermission = NodeImage.TOL_USE;
        boolean modificationPermitted = false;
        if (notEmpty && use.equalsIgnoreCase(XMLConstants.restricted)) {
            usePermission = NodeImage.RESTRICTED_USE;
        } else if (notEmpty && use.equalsIgnoreCase(XMLConstants.tolusemod)) {
            modificationPermitted = true;
        } else if (notEmpty && use.equalsIgnoreCase(XMLConstants.tolsharenomod)) {
            usePermission = NodeImage.EVERYWHERE_USE;
        } else if (notEmpty && use.equalsIgnoreCase(XMLConstants.tolsharemod)) {
            usePermission = NodeImage.EVERYWHERE_USE;
            modificationPermitted = true;
        } else if (notEmpty && use.equalsIgnoreCase(XMLConstants.cc)) {
            usePermission = NodeImage.CC_BY_NC20;
            modificationPermitted = true;
        }
        List nodesList = getNodesFromElements(imgElem, XMLConstants.group, false);
        if (nodesList.size() > 0) {
        	Object lastElement = nodesList.get(nodesList.size() - 1);
        	if (!MappedNode.class.isInstance(lastElement)) {
        		nodesList.remove(lastElement);
        	}
        }
        Set nodes = new HashSet(nodesList);
        if (nodes.size() > 0) {
            newImage.setNodesSet(nodes);
        }
        
        newImage.setUsePermission(usePermission);
        newImage.setModificationPermitted(new Boolean(modificationPermitted));
        newImage.setReference(imgElem.getChildText(XMLConstants.reference));
        newImage.setCreator(imgElem.getChildText(XMLConstants.creator));
        newImage.setAcknowledgements(imgElem.getChildText(XMLConstants.acknowledgements));
        
        newImage.setIsSpecimen(getBooleanAttributeValue(imgElem, XMLConstants.specimen));
        newImage.setIsBodyParts(getBooleanAttributeValue(imgElem, XMLConstants.bodyparts));
        newImage.setIsUltrastructure(getBooleanAttributeValue(imgElem, XMLConstants.ultrastructure));
        newImage.setIsHabitat(getBooleanAttributeValue(imgElem, XMLConstants.habitat));
        newImage.setIsEquipment(getBooleanAttributeValue(imgElem, XMLConstants.equipment));
        newImage.setIsPeopleWorking(getBooleanAttributeValue(imgElem, XMLConstants.people));
        String subject = imgElem.getChildText(XMLConstants.subject);
        if (StringUtils.notEmpty(subject)) {
            decodeSubjects(newImage, subject);
        }
        newImage.getKeywords().setAdditionalKeywords(imgElem.getChildText(XMLConstants.keywords));
        newImage.setGeoLocation(imgElem.getChildText(XMLConstants.geolocation));
        newImage.setUserCreationDate(imgElem.getChildText(XMLConstants.time));
        String condition = imgElem.getChildText(XMLConstants.condition);
        if (StringUtils.notEmpty(condition)) {
            String conditionValue = condition;
            if (condition.equalsIgnoreCase(XMLConstants.l) || condition.equalsIgnoreCase(XMLConstants.live)) {
                conditionValue = NodeImage.ALIVE;
            } else if (condition.equalsIgnoreCase(XMLConstants.d) || condition.equalsIgnoreCase(XMLConstants.dead)) {
                conditionValue = NodeImage.DEAD;
            } else if (condition.equalsIgnoreCase(XMLConstants.f) || condition.equalsIgnoreCase(XMLConstants.fossil)) {
                conditionValue = NodeImage.FOSSIL;
            } else if (condition.equalsIgnoreCase(XMLConstants.m) || condition.equalsIgnoreCase(XMLConstants.model)) {
                conditionValue = NodeImage.MODEL;
            }
            newImage.setAlive(conditionValue);
        }
        newImage.setPeriod(imgElem.getChildText(XMLConstants.period));
        newImage.setScientificName(imgElem.getChildText(XMLConstants.scientificname));
        newImage.setIdentifier(imgElem.getChildText(XMLConstants.identifier));
        newImage.setBehavior(imgElem.getChildText(XMLConstants.behavior));
        newImage.setSex(imgElem.getChildText(XMLConstants.sex));
        newImage.setStage(imgElem.getChildText(XMLConstants.stage));
        newImage.setBodyPart(imgElem.getChildText(XMLConstants.partofbody));
        newImage.setView(imgElem.getChildText(XMLConstants.view));
        newImage.setSize(imgElem.getChildText(XMLConstants.size));
        newImage.setCollection(imgElem.getChildText(XMLConstants.collection));
        newImage.setType(imgElem.getChildText(XMLConstants.type));
        newImage.setVoucherNumber(imgElem.getChildText(XMLConstants.vouchernumber));
        newImage.setVoucherNumberCollection(imgElem.getChildText(XMLConstants.vouchercollection));
        newImage.setCollector(imgElem.getChildText(XMLConstants.collector));
        newImage.setComments(imgElem.getChildText(XMLConstants.comments));
        newImage.setAltText(imgElem.getChildText(XMLConstants.alt));
        newImage.setTechnicalInformation(imgElem.getChildText(XMLConstants.technical));
        newImage.setNotes(imgElem.getChildText(XMLConstants.notes));
        newImage.setArtisticInterpretation(getBooleanAttributeValue(imgElem, XMLConstants.artistic));
        String imgType = imgElem.getChildText(XMLConstants.imagetype);
        if (StringUtils.notEmpty(imgType)) {
            String imgTypeValue = imgType;
            if (imgType.equalsIgnoreCase(XMLConstants.photo)) {
                imgTypeValue = NodeImage.PHOTOGRAPH;
            } else if (imgType.equalsIgnoreCase(XMLConstants.drawing) || imgType.equalsIgnoreCase(XMLConstants.painting)) {
                imgTypeValue = NodeImage.DRAWING_PAINTING;
            } else if (imgType.equalsIgnoreCase(XMLConstants.diagram)) {
                imgTypeValue = NodeImage.DIAGRAM;
            }
            newImage.setImageType(imgTypeValue);
        }
    }
    /**
     * Return a list of nodes decoded from the xml.  The last element in the
     * list is a list of names that didn't match any nodes
     * @param parentElement
     * @param nodeElementName
     * @param checkPermission
     * @return
     */
    private List getNodesFromElements(Element parentElement, String nodeElementName, boolean checkPermission) {
        List nodes = new ArrayList();
        List<String> namesMissingNodes = new ArrayList<String>();
    	NodeDAO workingDAO = getWorkingNodeDAO();
    	Hashtable nodesToPermissions = new Hashtable();
        for (Iterator iter = parentElement.getChildren(nodeElementName).iterator(); iter.hasNext();) {
            Element nextGroup = (Element) iter.next();
            String nodeName = nextGroup.getTextTrim();
            List matches = workingDAO.findNodesExactlyNamed(nodeName);
            if (matches.size() > 0) {
                nodes.addAll(matches);
            } else {
            	namesMissingNodes.add(nodeName);
            }
            if (checkPermission) {
            	// record the permission for each node in a hashtable
            	boolean hasPermission = false;
            	String permissionString = nextGroup.getAttributeValue(XMLConstants.permission);
            	if (StringUtils.notEmpty(permissionString)) {
            		if (permissionString.equals(XMLConstants.ONE)) {
            			hasPermission = true;
            		}
            	}
            	for (Iterator iterator = matches.iterator(); iterator.hasNext();) {
					MappedNode nextNode = (MappedNode) iterator.next();
					nodesToPermissions.put(nextNode, hasPermission);
				}
            }
        }
        nodes.add(namesMissingNodes);
        if (checkPermission) {
        	nodes.add(nodesToPermissions);
        }
        return nodes;
    }
    
    private void decodeSubjects(NodeImage img, String subjectsString) {
        String[] subjectNumbers = subjectsString.split(",");
        Keywords keywords = img.getKeywords();
        for (int i = 0; i < subjectNumbers.length; i++) {
            String nextSubjectNumber = subjectNumbers[i];
            if (StringUtils.getIsNumeric(nextSubjectNumber)) {
                decodeSubjectNumber(Integer.parseInt(nextSubjectNumber), keywords);
            }
        }
    }
    
    private void decodeSubjectNumber(int number, Keywords keywords) {
        switch (number) {
            case 1: keywords.setEvolution(true); break;
            case 2: keywords.setPhylogenetics(true); break;
            case 3: keywords.setTaxonomy(true); break;
            case 4: keywords.setBiodiversity(true); break;
            case 5: keywords.setEcology(true); break;
            case 6: keywords.setConservation(true); break;
            case 7: keywords.setBiogeography(true); break;
            case 8: keywords.setPaleobiology(true); break;
            case 9: keywords.setMorphology(true); break;
            case 10: keywords.setLifehistory(true); break;
            case 11: keywords.setPhysiology(true); break;
            case 12: keywords.setNeurobiology(true); break;
            case 13: keywords.setHistology(true); break;
            case 14: keywords.setGenetics(true); break;
            case 15: keywords.setMolecular(true); break;
            case 16: keywords.setMethods(true); break;
        }
    }
    
    public Element getImageElementWithFilename(String filename, org.jdom.Document doc) {
        try {
            Element foundElement = null;
            for (Iterator iter = doc.getRootElement().getChildren(XMLConstants.image).iterator(); iter.hasNext();) {
                Element imgElement = (Element) iter.next();
                String location = imgElement.getChildText(XMLConstants.filename);
                if (location != null && location.equals(filename)) {
                    foundElement = imgElement;
                }
            }
            return foundElement;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }        
    }        
    
    private boolean getBooleanAttributeValue(Element imgElem, String elementName) {
        String elementValue = imgElem.getChildTextTrim(elementName);
        boolean returnVal = false;
        if (StringUtils.notEmpty(elementValue)) {
            if (elementValue.equalsIgnoreCase(XMLConstants.y) || elementValue.equals(XMLConstants.ONE)) {
                returnVal = true;
            }
        }
        return returnVal;
    }
    /**
     * Return a list of: 1) the contributor 2) the non-permission nodes 3) the permission nodes
     * 4) unmatched nodes 5) whether to send email or not
     * @param contributorElement
     * @return
     */
    public List getContributorAndNodesFromElement(Element contributorElement, Contributor existingContributor) {
    	List returnList = new ArrayList();
    	Contributor contributor = existingContributor;
    	if (contributor == null) {
    		contributor = new Contributor();
    	}
    	contributor.setSurname(contributorElement.getChildText(XMLConstants.surname));
    	contributor.setFirstName(contributorElement.getChildText(XMLConstants.firstname));
    	contributor.setEmail(contributorElement.getChildText(XMLConstants.email));
    	contributor.setHomepage(contributorElement.getChildText(XMLConstants.webpageurl));
    	contributor.setInstitution(contributorElement.getChildText(XMLConstants.institution));
    	contributor.setAddress(contributorElement.getChildText(XMLConstants.address));
    	contributor.setBio(contributorElement.getChildText(XMLConstants.bio));
    	contributor.setNotes(contributorElement.getChildText(XMLConstants.comments));
    	contributor.setPhone(contributorElement.getChildText(XMLConstants.phone));
    	contributor.setFax(contributorElement.getChildText(XMLConstants.fax));
    	contributor.setDontShowEmail(getBooleanValue(contributorElement, XMLConstants.dontpublishemail));
    	contributor.setWillingToCoordinate(getBooleanValue(contributorElement, XMLConstants.coordinator));
    	String interests = contributorElement.getChildText(XMLConstants.interests);
    	if (StringUtils.notEmpty(interests)) {
    		decodeInterests(contributor, interests);
    	}
    	contributor.setGeographicAreaInterest(contributorElement.getChildText(XMLConstants.geographicareainterest));
    	contributor.setOtherInterests(contributorElement.getChildText(XMLConstants.otherinterests));
    	byte contributorType = Contributor.OTHER_SCIENTIST;
    	String typeString = contributorElement.getChildText(XMLConstants.type);
    	if (StringUtils.notEmpty(typeString)) {
    		if (typeString.equalsIgnoreCase("core")) {
    			contributorType = Contributor.SCIENTIFIC_CONTRIBUTOR;
    		} else if (typeString.equalsIgnoreCase("general")) {
    			contributorType = Contributor.ACCESSORY_CONTRIBUTOR;
    		}
    	}
    	contributor.setContributorType(contributorType);
    	returnList.add(contributor);
    	List nodes = getNodesFromElements(contributorElement, XMLConstants.group, true);
    	// peel off the last two elements of the list because they aren't really nodes at all
    	Hashtable nodesToPermissions = (Hashtable) nodes.remove(nodes.size() - 1);
    	List missingNamesList = (List) nodes.remove(nodes.size() - 1);
    	List nodesList = new ArrayList();
    	List noPermissionNodesList = new ArrayList();
    	for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			MappedNode nextNode = (MappedNode) iter.next();
			Boolean hasPermission = (Boolean) nodesToPermissions.get(nextNode);
			if (hasPermission != null && hasPermission) {
				nodesList.add(nextNode);
			} else {
				noPermissionNodesList.add(nextNode);
			}
		}
    	returnList.add(noPermissionNodesList);
    	returnList.add(nodesList);
    	returnList.add(missingNamesList);
    	String sendEmail = contributorElement.getChildText(XMLConstants.sendemail);
    	boolean shouldSendEmail = StringUtils.notEmpty(sendEmail) && 
    		(sendEmail.equals(XMLConstants.TRUE) || sendEmail.equals(XMLConstants.ONE));
    	returnList.add(shouldSendEmail);
    	return returnList;
    }
    
    private void decodeInterests(Contributor contr, String interestsString) {
        String[] interestNumbers = interestsString.split(",");
        for (int i = 0; i < interestNumbers.length; i++) {
            String nextInterestNumber = interestNumbers[i];
            if (StringUtils.getIsNumeric(nextInterestNumber)) {
            	int interestNum = Integer.parseInt(nextInterestNumber);
                switch (interestNum) {
                    case 1: contr.setInterestedInTaxonomy(true); break;
                    case 2: contr.setInterestedInPhylogenetics(true); break;
                    case 3: contr.setInterestedInMorphology(true); break;
                    case 4: contr.setInterestedInBiogeography(true); break;
                    case 5: contr.setInterestedInImmatureStages(true); break;
                    case 6: contr.setInterestedInEcology(true); break;
                    case 7: contr.setInterestedInBehavior(true); break;
                    case 8: contr.setInterestedInCytogenetics(true); break;
                    case 9: contr.setInterestedInProteins(true); break;
                }
            }
        }
    }     

	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}

	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}

	public NodeDAO getWorkingNodeDAO() {
		return workingNodeDAO;
	}

	public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
		this.workingNodeDAO = workingNodeDAO;
	}    
}
