/*
 * XmlReader.java
 *
 * Created on June 19, 2003, 5:55 PM
 */

package org.tolweb.treegrow.main;

import java.util.*;

import org.jdom.*;
import org.jdom.xpath.XPath;
import org.tolweb.base.xml.BaseXMLReader;
import org.tolweb.treegrow.page.*;
import org.tolweb.treegrow.tree.*;

/**
 * Class responsible for reading XML, either from a local file or a URLA
 */
public abstract class XMLReader extends BaseXMLReader {
    public static final int LOST_CONNECTION_MIDSTREAM = 13;

    protected org.jdom.Document xmlDocument;

    protected int downloadId;

    protected int depth;

    protected String ancestorsString;

    protected String fileNameme;

    protected Element rootElement;

    protected String errorString;

    protected int errorNum = 0;

    protected Node node;

    public void gatherContent() {
        if (errorNum != LOST_CONNECTION_MIDSTREAM) {
            startRecursiveChildLoop(rootElement, null, 1);
        }
    }
    public List getAllNodeNames(Document doc) {
		List returnList = new ArrayList();    	
        try {
			XPath nodeLocatorPath = XPath.newInstance("//NODE/NAME");
			List results = nodeLocatorPath.selectNodes(doc.getRootElement());
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				Element nameElement = (Element) iter.next();
				returnList.add(nameElement.getText());
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnList;
    }
    public int fetchDownloadId() {
        return fetchDownloadId(rootElement);
    }

    public int fetchDownloadId(Element rootElement) {
        Element downloadIdElem = rootElement.getChild(XMLConstants.DOWNLOAD_ID);
        if (downloadIdElem != null) {
            String downloadIdStr = downloadIdElem.getTextTrim();
            if (downloadIdStr != null && downloadIdStr != "") {
                downloadId = new Integer(downloadIdStr).intValue();
                return downloadId;
            }
        }
        return -1;
    }


    public void readNodeProperties(Node node, Element nodeElem) {
        readNodeProperties(node, nodeElem, true);
    }
    
    public void readNodeProperties(Node node, Element nodeElem, boolean isNewVersion) {
        fetchNodeName(nodeElem, node);
        fetchExtinct(nodeElem, node);
        fetchConfidence(nodeElem, node);
        fetchPhylesis(nodeElem, node);
        fetchLeaf(nodeElem, node);
        fetchNodeRank(nodeElem, node);        
        if (!isNewVersion) {
            fetchNote(nodeElem, node);
            fetchSynonyms(nodeElem, node);
            fetchAuthorities(nodeElem, node);            
        }
        fetchSequence(nodeElem, node);
        fetchDontPublish(nodeElem, node); 
        fetchHasAdditionalChildren(nodeElem, node);
        fetchForeignDbInfo(nodeElem, node);
        //fetchPriority(nodeElem, node);
    }
    
    private void fetchForeignDbInfo(Element nodeElem, Node node2) {
		node2.setSourceDbNodeId(nodeElem.getAttributeValue(XMLConstants.SOURCE_DB_ID));
		String sourceDb = nodeElem.getAttributeValue(XMLConstants.SOURCE_DB);
		if (StringUtils.notEmpty(sourceDb)) {
			node2.setSourceDbId(Long.valueOf(sourceDb));
		}
	}

	private void fetchPriority(Element nodeElement, Node node) {
    	int priority = new Integer(nodeElement.getAttributeValue(XMLConstants.PRIORITY)).intValue();
    	node.setPriority(priority);
    	node.setPriorityChanged(getBooleanValue(nodeElement, XMLConstants.PRIORITYCHANGED));
    }
    
    private void fetchHasAdditionalChildren(Element nodeElement, Node node) {
        boolean hasAdditionalChildren = getBooleanValue(nodeElement, XMLConstants.INCOMPLETESUBGROUPS);
        node.setHasIncompleteSubgroups(hasAdditionalChildren);
        node.setIncompleteSubgroupsChanged(getBooleanValue(nodeElement, XMLConstants.INCOMPLETESUBGROUPSCHANGED));
    }
    
    protected int constructNode(Element nodeElem, Node parent, int nodeDepth) { //recursive
        // walk
        // along
        // the
        // xml
        // doc
        Node tempNode = null;
        if (node != null) {
            tempNode = node;
        } else {
            tempNode = new Node();
        }
        if (parent != null) {
            parent.addToChildren(tempNode);
        }
        fetchNodeId(nodeElem, tempNode);
        readNodeProperties(tempNode, nodeElem);
        fetchLocked(nodeElem, tempNode);
        //still need these
        //getChildrenChanged(nodeElem, tempNode);
        fetchChildrenChanged(nodeElem, tempNode);
        System.out.println("right before page");
        fetchPage(nodeElem, tempNode);
        System.out.println("after page");
        fetchChildCount(nodeElem, tempNode);
        fetchHasPage(nodeElem, tempNode);
        // Here for the special case of a terminal node in the actual ToL
        // Otherwise our normal test fails
        String checkOutAttr = nodeElem.getAttributeValue(XMLConstants.CHECKED_OUT_FILE);
        boolean wasCheckedOut = StringUtils.notEmpty(checkOutAttr) && checkOutAttr.equals(XMLConstants.ONE);
        tempNode.setCheckedOut(nodeDepth <= depth || wasCheckedOut);
        System.out.println("finished constructing node: " + tempNode.getName());
        doAdditionalNodeProcessing(tempNode);
        startRecursiveChildLoop(
                nodeElem,
                tempNode,
                (tempNode.hasPageOnServer() || tempNode.hasPage()) ? ++nodeDepth
                        : nodeDepth);
        return 1;
    }

    protected void doAdditionalNodeProcessing(Node node) {
    }

    public static Contributor getContributorFromElement(
            Element contributorElmt, Contributor contributor) {
        if (contributor == null) {
            contributor = new Contributor();
            int id = Integer.parseInt(contributorElmt
                    .getAttributeValue(XMLConstants.ID));
            if (id >= 0) {
                contributor.setId(id);
            } else {
                contributor.setId(-1);
            }
        }
        contributor.setFirstName(contributorElmt
                .getChildTextTrim(XMLConstants.FIRSTNAME));
        contributor.setLastName(contributorElmt
                .getChildTextTrim(XMLConstants.LASTNAME));
        contributor.setEmail(contributorElmt
                .getChildTextTrim(XMLConstants.EMAIL));
        contributor.setAddress(contributorElmt
                .getChildTextTrim(XMLConstants.ADDRESS));
        contributor.setInstitution(contributorElmt
                .getChildTextTrim(XMLConstants.INSTITUTION));
        contributor.setNotes(contributorElmt
                .getChildTextTrim(XMLConstants.NOTES));
        contributor.setPhone(contributorElmt
                .getChildTextTrim(XMLConstants.PHONE));
        contributor.setFax(contributorElmt.getChildTextTrim(XMLConstants.FAX));
        contributor.setHomepage(contributorElmt
                .getChildTextTrim(XMLConstants.HOMEPAGE));
        contributor.setChangedFromServer(contributorElmt
                .getAttributeValue(XMLConstants.CHANGED) != null
                && contributorElmt.getAttributeValue(XMLConstants.CHANGED)
                        .equals(XMLConstants.ONE));
        contributor
                .setCheckedOut(contributorElmt
                        .getAttributeValue(XMLConstants.CHECKED_OUT_FILE) != null
                        && contributorElmt.getAttributeValue(
                                XMLConstants.CHECKED_OUT_FILE).equals(
                                XMLConstants.ONE));
        contributor.setDontShowEmail(contributorElmt
                .getAttributeValue(XMLConstants.NOEMAIL) != null
                && contributorElmt.getAttributeValue(XMLConstants.NOEMAIL)
                        .equals(XMLConstants.ONE));
        contributor.setDontShowAddress(contributorElmt
                .getAttributeValue(XMLConstants.NOADDRESS) != null
                && contributorElmt.getAttributeValue(XMLConstants.NOADDRESS)
                        .equals(XMLConstants.ONE));
        Iterator it = contributorElmt.getChildren(XMLConstants.PERMISSION)
                .iterator();
        while (it.hasNext()) {
            Element permissionElt = (Element) it.next();
            int id = Integer.parseInt(permissionElt
                    .getAttributeValue(XMLConstants.ID));
            String name = permissionElt.getChildText(XMLConstants.NODE);
            Permission p = new Permission(id, name, contributor);
            contributor.addToPermissions(p);
        }
        return contributor;
    }

    public static Contributor getContributorFromElement(Element contributorElmt) {
        return getContributorFromElement(contributorElmt, null);
    }
    
    public static NodeImage getNodeImageFromElement(Element imgElem) {
        return getNodeImageFromElement(imgElem, null);
    }
    
    public static NodeImage getNodeImageFromElement(Element imgElem, Node node) {
        return getNodeImageFromElement(imgElem, node, null);
    }

    /**
     * Takes in an IMAGE element and returns a NodeImage object
     * 
     * @param imgElem
     *            The IMAGE element to parse
     * @param node
     *            The node to associate the NodeImage object with
     * 
     * @return The new NodeImage
     */
    public static NodeImage getNodeImageFromElement(Element imgElem, Node node, NodeImage img) {
        boolean wasNew = img == null;
        if (wasNew) {
            img = new NodeImage();
            img.setId(new Integer(imgElem.getAttributeValue(XMLConstants.ID))
                    .intValue());
        }
        img.setCheckedOut(decodeBooleanAttribute(imgElem,
                XMLConstants.CHECKED_OUT_FILE, XMLConstants.ONE));
        img.setChangedFromServer(decodeBooleanAttribute(imgElem,
                XMLConstants.CHANGED, XMLConstants.ONE));
        img.setInPublicDomain(decodeBooleanAttribute(imgElem,
                XMLConstants.PUBLICDOMAIN, XMLConstants.ONE));
        img.setIsFossil(decodeBooleanAttribute(imgElem, XMLConstants.FOSSIL,
                XMLConstants.ONE));
        img.setIsSpecimen(decodeBooleanAttribute(imgElem,
                XMLConstants.SPECIMEN, XMLConstants.ONE));
        img.setIsBodyParts(decodeBooleanAttribute(imgElem,
                XMLConstants.BODYPARTS, XMLConstants.ONE));
        img.setIsUltrastructure(decodeBooleanAttribute(imgElem,
                XMLConstants.ULTRASTRUCTURE, XMLConstants.ONE));
        img.setIsHabitat(decodeBooleanAttribute(imgElem, XMLConstants.HABITAT,
                XMLConstants.ONE));
        img.setIsEquipment(decodeBooleanAttribute(imgElem,
                XMLConstants.EQUIPMENT, XMLConstants.ONE));
        img.setIsPeopleWorking(decodeBooleanAttribute(imgElem,
                XMLConstants.PEOPLE, XMLConstants.ONE));
        if (wasNew) {
            img.setLocation(imgElem.getAttributeValue(XMLConstants.LOCATION));
        }
        img
                .setUserCreationDate(imgElem
                        .getChildText(XMLConstants.CREATIONDATE));
        img.setAlive(imgElem.getChildText(XMLConstants.ALIVE));
        img.setBehavior(imgElem.getChildText(XMLConstants.BEHAVIOR));
        img.setVoucherNumber(imgElem.getChildText(XMLConstants.VOUCHERNUMBER));
        img.setVoucherNumberCollection(imgElem
                .getChildText(XMLConstants.VOUCHERNUMBERCOLLECTION));
        img.setNotes(imgElem.getChildText(XMLConstants.NOTES));
        img.setImageType(imgElem.getChildText(XMLConstants.IMAGETYPE));
        img.setCopyrightUrl(imgElem.getChildText(XMLConstants.COPYRIGHTURL));
        img
                .setCopyrightEmail(imgElem
                        .getChildText(XMLConstants.COPYRIGHTEMAIL));
        img.setCopyrightDate(imgElem.getChildText(XMLConstants.COPYRIGHTDATE));
        img.setSex(imgElem.getChildText(XMLConstants.SEX));
        img
                .setCopyrightOwner(imgElem
                        .getChildText(XMLConstants.COPYRIGHTOWNER));
        img
                .setScientificName(imgElem
                        .getChildText(XMLConstants.SCIENTIFICNAME));
        img.setReference(imgElem.getChildText(XMLConstants.REFERENCES));
        img.setCreator(imgElem.getChildText(XMLConstants.CREATOR));
        img.setIdentifier(imgElem.getChildText(XMLConstants.IDENTIFIER));
        img.setAcknowledgements(imgElem.getChildText(XMLConstants.ACKS));
        img.setGeoLocation(imgElem.getChildText(XMLConstants.GEOLOCATION));
        img.setStage(imgElem.getChildText(XMLConstants.STAGE));
        img.setBodyPart(imgElem.getChildText(XMLConstants.BODYPART));
        img.setSize(imgElem.getChildText(XMLConstants.SIZE));
        img.setView(imgElem.getChildText(XMLConstants.VIEW));
        img.setPeriod(imgElem.getChildText(XMLConstants.PERIOD));
        img.setCollection(imgElem.getChildText(XMLConstants.COLLECTION));
        img.setCollectionAcronym(imgElem
                .getChildText(XMLConstants.COLLECTIONAC));
        img.setType(imgElem.getChildText(XMLConstants.TYPE));
        img.setCollector(imgElem.getChildText(XMLConstants.COLLECTOR));
        img.setAltText(imgElem.getChildText(XMLConstants.ALTTEXT));
        img.setComments(imgElem.getChildText(XMLConstants.COMMENTS));
        String permission = imgElem.getAttributeValue(XMLConstants.PERMISSION);
        if (permission != null) {
            img.setUsePermission(new Byte(permission).byteValue());
        }
        String contributorId = imgElem
                .getAttributeValue(XMLConstants.COPYRIGHTOWNER);
        if (StringUtils.notEmpty(contributorId)) {
            img
                    .setCopyrightContributorId(new Integer(contributorId)
                            .intValue());
        }
        Element nodes = imgElem.getChild(XMLConstants.NODES);
        if (nodes != null) {
            Iterator it = nodes.getChildren(XMLConstants.NODE).iterator();
            while (it.hasNext()) {
                Element nodeElmt = (Element) it.next();
                String removedStr = nodeElmt
                        .getAttributeValue(XMLConstants.REMOVED);
                ImageNode in = new ImageNode(new Integer(nodeElmt
                        .getAttributeValue(XMLConstants.ID)).intValue(),
                        nodeElmt.getChildText(XMLConstants.NAME), img);
                boolean removed = removedStr != null
                        && removedStr.equals(XMLConstants.ONE);
                if (!removed) {
                    img.addToNodes(in);
                } else {
                    img.addToDeletedNodes(in);
                }
            }
        }
        Element versions = imgElem.getChild(XMLConstants.VERSIONS);
        if (versions != null) {
            decodeImageVersions(versions, img);
        }
        return img;
    }

    public static void decodeImageVersions(Element versions, NodeImage img) {
        for (Iterator iter = versions.getChildren(XMLConstants.VERSION)
                .iterator(); iter.hasNext();) {
            Element nextElement = (Element) iter.next();
            ImageVersion version = new ImageVersion();
            version.setImage(img);
            version.setVersionId(Long.valueOf(nextElement
                    .getAttributeValue(XMLConstants.ID)));
            version.setVersionName(nextElement
                    .getAttributeValue(XMLConstants.NAME));
            version.setWidth(new Integer(nextElement
                    .getAttributeValue(XMLConstants.WIDTH)));
            version.setHeight(new Integer(nextElement
                    .getAttributeValue(XMLConstants.HEIGHT)));
            img.addToVersionsSet(version);
        }
    }

    private static boolean decodeBooleanAttribute(Element element,
            String attribKey, String trueValue) {
        String attribValue = element.getAttributeValue(attribKey);
        return attribValue != null && attribValue.equals(trueValue);
    }

    private void fetchNodeName(Element nodeElem, Node tempNode) {
        String nodeName = nodeElem.getChild(XMLConstants.NAME).getTextTrim();
        tempNode.setName(nodeName);

        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.NAMECHANGED);
        tempNode.setNameChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    public int getNodeIdFromNodeElement(Element nodeElement) {
    	String attrValue = nodeElement.getAttributeValue(XMLConstants.ID);
    	if (StringUtils.notEmpty(attrValue)) {
    		return new Integer(attrValue).intValue();
    	} else {
    		return -1;
    	}
    }

    private void fetchNodeId(Element nodeElem, Node tempNode) {
        tempNode.setId(getNodeIdFromNodeElement(nodeElem));
    }

    private void fetchChildCount(Element nodeElem, Node tempNode) {
        String childCountString = nodeElem
                .getAttributeValue(XMLConstants.CHILDCOUNT);
        if (childCountString != null) {
            // This is only used in initially determining whether a node has
            // children on the server
            tempNode.setChildCountOnServer(new Integer(childCountString)
                    .intValue());
        }
    }

    private void fetchHasPage(Element nodeElem, Node tempNode) {
        String hasPageString = nodeElem.getAttributeValue(XMLConstants.HASPAGE);
        if (hasPageString != null) {
            boolean hasPage = new Integer(hasPageString).intValue() > 0;
            tempNode.setHasPageOnServer(hasPage);
        }
        String pageRemoved = nodeElem.getAttributeValue(XMLConstants.PAGEREMOVED);
        if (StringUtils.notEmpty(pageRemoved)) {
            tempNode.setPageWasRemoved(pageRemoved.equals(XMLConstants.ONE));
        }
        String pageAdded = nodeElem.getAttributeValue(XMLConstants.PAGEADDED);
        if (StringUtils.notEmpty(pageAdded)) {
            tempNode.setPageWasAdded(pageAdded.equals(XMLConstants.ONE));
        }
    }

    private void fetchLocked(Element nodeElem, Node tempNode) {
        Element lock_info = nodeElem.getChild(XMLConstants.LOCK_INFO);
        boolean locked = lock_info != null;
        tempNode.setLocked(locked);
        if (locked) {
            if (lock_info != null) {
                String tempString = lock_info
                        .getAttributeValue(XMLConstants.DATE_TIME);
                if (tempString != null) {
                    tempNode.setLockDate(tempString.trim());
                }

                tempString = lock_info.getAttributeValue(XMLConstants.USER);
                if (tempString != null) {
                    tempNode.setLockUser(tempString.trim());
                }

                tempString = lock_info.getAttributeValue(XMLConstants.TYPE);
                if (tempString != null) {
                    tempNode.setLockType(tempString.trim());
                }
            }
        }

    }

    private void fetchExtinct(Element nodeElem, Node tempNode) {
        int value = -1;
        String string = nodeElem.getAttributeValue(XMLConstants.EXTINCT);
        if (string != null) {
            value = new Integer(string).intValue();
        }
        tempNode.setExtinct(value);

        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.EXTINCTCHANGED);
        tempNode.setExtinctChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    private void fetchConfidence(Element nodeElem, Node tempNode) {
        int value = -1;
        String string = nodeElem.getAttributeValue(XMLConstants.CONFIDENCE);
        if (string != null) {
            value = new Integer(string).intValue();
        }
        tempNode.setConfidence(value);

        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.CONFIDENCECHANGED);
        tempNode.setConfidenceChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    private void fetchPhylesis(Element nodeElem, Node tempNode) {
        int value = -1;
        String string = nodeElem.getAttributeValue(XMLConstants.PHYLESIS);
        if (string != null) {
            value = new Integer(string).intValue();
        }
        tempNode.setPhylesis(value);

        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.PHYLESISCHANGED);
        tempNode.setPhylesisChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    public void fetchSequence(Element nodeElem, Node node) {
        int sequence = 0;
        String string = nodeElem.getAttributeValue(XMLConstants.SEQUENCE);
        if (string != null) {
            sequence = new Integer(string).intValue();
        }
        setSequenceForNode(node, sequence);
    }

    private void fetchDontPublish(Element nodeElem, Node node) {
        boolean dontPublish = false;
        String string = nodeElem.getAttributeValue(XMLConstants.DONTPUBLISH);
        if (string != null) {
            dontPublish = string.equals(XMLConstants.ONE);
        }
        node.setDontPublish(dontPublish);
        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.DONTPUBLISHCHANGED);
        node.setDontPublishChanged(attribValue != null
                && attribValue.equals(XMLConstants.ONE));
    }

    protected void setSequenceForNode(Node node, int sequence) {
    }

    protected void fetchAuthorities(Element nodeElem, Node tempNode) {
        String string = nodeElem.getAttributeValue(XMLConstants.SHOWAUTHORITY);
        tempNode.setShowNameAuthority(string != null
                && string.equals(XMLConstants.ONE));
        string = nodeElem.getAttributeValue(XMLConstants.SHOWAUTHORITYCHANGED);
        tempNode.setShowNameAuthorityChanged(string != null
                && string.equals(XMLConstants.TRUE));
        string = nodeElem.getAttributeValue(XMLConstants.SHOWIMPAUTHORITY);
        tempNode.setShowImportantAuthority(string != null
                && string.equals(XMLConstants.ONE));
        string = nodeElem
                .getAttributeValue(XMLConstants.SHOWIMPAUTHORITYCHANGED);
        tempNode.setShowImportantAuthorityChanged(string != null
                && string.equals(XMLConstants.TRUE));
        string = nodeElem.getAttributeValue(XMLConstants.SHOWPREFAUTHORITY);
        tempNode.setShowPreferredAuthority(string != null
                && string.equals(XMLConstants.ONE));
        string = nodeElem
                .getAttributeValue(XMLConstants.SHOWPREFAUTHORITYCHANGED);
        tempNode.setShowPreferredAuthorityChanged(string != null
                && string.equals(XMLConstants.TRUE));

        Element authElmt = nodeElem.getChild(XMLConstants.AUTHORITY);
        if (authElmt != null) {
            String authString = authElmt.getTextTrim();
            if (StringUtils.notEmpty(authString)) {
                tempNode.setNameAuthority(authString);
                String changedString = authElmt
                        .getAttributeValue(XMLConstants.CHANGED);
                tempNode.setNameAuthorityChanged(changedString != null
                        && changedString.equals(XMLConstants.TRUE));
            }
        }

        Element dateElmt = nodeElem.getChild(XMLConstants.AUTHDATE);
        if (dateElmt != null) {
            String dateString = dateElmt.getTextTrim();
            if (StringUtils.notEmpty(dateString)) {
            	try {
	                setAuthorityDateForNode(tempNode, new Integer(dateString)
	                        .intValue());
            	} catch (Exception e) {}
                String changedString = authElmt
                        .getAttributeValue(XMLConstants.CHANGED);
                tempNode.setNameDateChanged(changedString != null
                        && changedString.equals(XMLConstants.TRUE));
            }
        }
    }

    protected abstract void setAuthorityDateForNode(Node node, int date);

    private void fetchLeaf(Element nodeElem, Node tempNode) {
        int value = 0;
        String string = nodeElem.getAttributeValue(XMLConstants.LEAF);
        if (string != null) {
            value = new Integer(string).intValue();
        }
        tempNode.setIsLeaf(value == 1);

        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.LEAFCHANGED);
        tempNode.setLeafChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    public void fetchNodeRank(Element nodeElem, Node tempNode) {
        String nodeRankValue = nodeElem
                .getAttributeValue(XMLConstants.NODERANK);
        if (nodeRankValue != null && !nodeRankValue.equals("")) {
            setNodeRankForNode(tempNode, new Integer(nodeRankValue).intValue());

        }
        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.NODERANKCHANGED);
        tempNode.setNodeRankChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    protected abstract void setNodeRankForNode(Node tempNode, int rank);

    private void fetchChildrenChanged(Element nodeElem, Node tempNode) {
        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.CHILDRENCHANGED);
        tempNode.setChildrenChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    protected void fetchNote(Element nodeElem, Node tempNode) {
        Element noteElement = nodeElem.getChild(XMLConstants.DESCRIPTION);
        if (noteElement != null) {
            tempNode.setDescription(noteElement.getTextTrim());
        }
        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.DESCRIPTIONCHANGED);
        tempNode.setDescriptionChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
    }

    protected void fetchSynonyms(Element nodeElem, Node tempNode) {
        Element synonymListElem = nodeElem.getChild(XMLConstants.OTHERNAMES);
        if (synonymListElem != null) {
            Collection tempSynonymCollection = getSynonymCollectionInstance();
            String attribValue = nodeElem
                    .getAttributeValue(XMLConstants.CHANGED);
            tempNode.setOtherNamesChanged(attribValue != null
                    && attribValue.equals(XMLConstants.TRUE));

            List synList = synonymListElem.getChildren(XMLConstants.OTHERNAME);
            Iterator it = synList.iterator();
            while (it.hasNext()) {
                Element synElem = (Element) it.next();
                OtherName tempSyn = getOtherNameInstanceForNode(tempNode);
                Element nameElem = synElem.getChild(XMLConstants.NAME);
                if (nameElem != null) {
                    tempSyn.setName(nameElem.getTextTrim());
                }

                String attValue = synElem
                        .getAttributeValue(XMLConstants.ISIMPORTANT);
                tempSyn.setIsImportant(attValue != null
                        && attValue.equals(XMLConstants.ONE));
                attValue = synElem.getAttributeValue(XMLConstants.ISPREFERRED);
                tempSyn.setIsPreferred(attValue != null
                        && attValue.equals(XMLConstants.ONE));

                String date = synElem.getAttributeValue(XMLConstants.DATE);
                if (date != null && !date.equals("")) {
                	try {
                		setYearForOtherName(tempSyn, new Integer(date).intValue());
                		// don't want to break on invalid dates, so just ignore them
                	} catch (Exception e) {}
                }

                Element authElem = synElem.getChild(XMLConstants.AUTHORITY);
                if (authElem != null) {
                    tempSyn.setAuthority(authElem.getTextTrim());
                }

                attValue = synElem.getAttributeValue(XMLConstants.SEQUENCE);
                if (attValue != null) {
                    setOrderForOtherName(tempSyn, new Integer(attValue)
                            .intValue());
                }
                attValue = synElem.getAttributeValue(XMLConstants.CHANGED);
                tempSyn.setChangedFromServer(attValue != null
                        && attValue.equals(XMLConstants.TRUE));
                System.out.println("adding othername with name: "
                        + tempSyn.getName() + " to othernames");
                tempSynonymCollection.add(tempSyn);
                
                // TODO: ADD CODE THAT CHECKS FOR THE ITALICIZE and COMMENT fields
                //       SHOULD GO IN SERVERXMLREADER
            }
            setOtherNamesForNode(tempNode, tempSynonymCollection);
        }
    }

    protected abstract OtherName getOtherNameInstanceForNode(Node node);

    protected abstract Collection getSynonymCollectionInstance();

    protected abstract void setOtherNamesForNode(Node node,
            Collection otherNames);

    protected abstract void setYearForOtherName(OtherName name, int year);

    protected abstract void setOrderForOtherName(OtherName name, int order);

    private int fetchPage(Element nodeElem, Node tempNode) {
        Element pageElement = nodeElem.getChild(XMLConstants.PAGE);
        Page tempPage;
        String attribValue = nodeElem
                .getAttributeValue(XMLConstants.PAGECHANGED);
        tempNode.setPageChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        if (pageElement != null) {
            tempPage = new Page(tempNode);
            System.out.println("just constructed page object");
            int success = readPageProperties(tempPage, pageElement);
            if (success < 1) {
                return success;
            }

            tempNode.setPageObject(tempPage);
        }
        return 1;
    }

    public int readPageProperties(Page tempPage, Element pageElem) {
        String attribValue = pageElem.getAttributeValue(XMLConstants.CHANGED);
        if (attribValue != null) {
            tempPage
                    .setChangedFromServer(attribValue.equals(XMLConstants.TRUE));
        }

        /*
         * attribValue =
         * pageElem.getAttributeValue(XMLConstants.PRINTIMAGECAPTION); if
         * (attribValue != null) {
         * tempPage.setPrintCustomCaption(attribValue.equals(XMLConstants.ONE)); }
         * attribValue =
         * pageElem.getAttributeValue(XMLConstants.PRINTIMAGEDATA);
         * tempPage.setPrintImageData(attribValue != null &&
         * attribValue.equals(XMLConstants.ONE));
         /
        String statusString = pageElem.getAttributeValue(XMLConstants.STATUS);
        if (statusString == null || statusString == "") {
            statusString = XMLConstants.SKELETAL;
        }*/

        String writeAsListString = pageElem
                .getAttributeValue(XMLConstants.WRITEASLIST);
        if (writeAsListString == null || writeAsListString == "") {
            writeAsListString = XMLConstants.ZERO;
        }
        //tempPage.setStatus(statusString);
        tempPage
                .setWriteAsList(writeAsListString.equals(XMLConstants.ONE) ? true
                        : false);

        attribValue = pageElem.getAttributeValue(XMLConstants.STATUSCHANGED);
        tempPage.setStatusChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        attribValue = pageElem.getAttributeValue(XMLConstants.WRITECHANGED);
        tempPage.setWriteChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));

        System.out.println("just passed write changed");

        attribValue = pageElem
                .getAttributeValue(XMLConstants.REFERENCESCHANGED);
        tempPage.setReferencesChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        String refs = pageElem.getChildText(XMLConstants.REFERENCES);
        tempPage.setReferences(refs);
        attribValue = pageElem
                .getAttributeValue(XMLConstants.INTERNETLINKSCHANGED);
        tempPage.setInternetLinksChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        String internetLinks = pageElem
                .getChildText(XMLConstants.INTERNETLINKS);
        tempPage.setInternetLinks(internetLinks);

        System.out.println("internet links");

        attribValue = pageElem.getAttributeValue(XMLConstants.GENBANKCHANGED);
        tempPage.setGenBankChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element genBankElem = pageElem.getChild(XMLConstants.GENBANK);
        if (genBankElem != null) {
            tempPage.setGenBank(genBankElem.getTextTrim());
        }

        attribValue = pageElem.getAttributeValue(XMLConstants.NEW_FROM_SERVER);
        tempPage.setIsNewFromServer(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));

        attribValue = pageElem.getAttributeValue(XMLConstants.TREEBASECHANGED);
        tempPage.setTreeBaseChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element treeBaseElem = pageElem.getChild(XMLConstants.TREEBASE);
        if (treeBaseElem != null) {
            tempPage.setTreeBase(treeBaseElem.getTextTrim());
        }

        System.out.println("treebase");

        attribValue = pageElem
                .getAttributeValue(XMLConstants.PRETREETEXTCHANGED);
        tempPage.setLeadTextChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element preTreeTextElem = pageElem.getChild(XMLConstants.PRETREETEXT);
        if (preTreeTextElem != null) {
            tempPage.setLeadText(preTreeTextElem.getTextTrim());
        }

        attribValue = pageElem
                .getAttributeValue(XMLConstants.POSTTREETEXTCHANGED);
        tempPage.setPostTreeTextChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element postTreeTextElem = pageElem.getChild(XMLConstants.POSTTREETEXT);
        if (postTreeTextElem != null) {
            tempPage.setPostTreeText(postTreeTextElem.getTextTrim());
        }

        /*
         * attribValue =
         * pageElem.getAttributeValue(XMLConstants.IMGCAPTIONCHANGED);
         * tempPage.setImgCaptionChanged( attribValue!=null &&
         * attribValue.equals(XMLConstants.TRUE) ); Element
         * titleIllustrationElem = pageElem.getChild(XMLConstants.IMGCAPTION);
         * if (titleIllustrationElem != null) {
         * tempPage.setImageCaption(titleIllustrationElem.getTextTrim()); }
         */

        System.out.println("image caption");

        attribValue = pageElem.getAttributeValue(XMLConstants.ACKSCHANGED);
        tempPage.setAcksChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element noteElem = pageElem.getChild(XMLConstants.ACKS);
        if (noteElem != null) {
            tempPage.setAcknowledgements(noteElem.getTextTrim());
        }

        System.out.println("content changed");

        attribValue = pageElem.getAttributeValue(XMLConstants.COPYRIGHTCHANGED);
        tempPage.setCopyrightChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element copyrightElem = pageElem.getChild(XMLConstants.COPYRIGHT);
        if (copyrightElem != null) {
            Element dateElem = copyrightElem.getChild(XMLConstants.DATE);
            Element holderElem = copyrightElem.getChild(XMLConstants.HOLDER);

            if (dateElem != null) {
                tempPage.setCopyrightDate(dateElem.getTextTrim());
            }
            if (holderElem != null) {
                tempPage.setCopyrightHolder(holderElem.getTextTrim());
            }

            attribValue = pageElem.getAttributeValue(XMLConstants.DATECHANGED);
            tempPage.setCopyrightDateChanged(attribValue != null
                    && attribValue.equals(XMLConstants.TRUE));

            attribValue = pageElem
                    .getAttributeValue(XMLConstants.HOLDERCHANGED);
            tempPage.setCopyrightHolderChanged(attribValue != null
                    && attribValue.equals(XMLConstants.TRUE));
        }

        //images
        attribValue = pageElem.getAttributeValue(XMLConstants.IMAGECHANGED);
        tempPage.setImageChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element imageListElem = pageElem.getChild(XMLConstants.IMAGELIST);
        if (imageListElem != null) {
            decodeImageListElement(imageListElem, tempPage);
        }

        System.out.println("images");
        //text sections
        attribValue = pageElem.getAttributeValue(XMLConstants.LISTCHANGED);
        tempPage.setListChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element textListListElem = pageElem.getChild(XMLConstants.TEXTLIST);
        if (textListListElem != null) {
            decodeTextListElement(textListListElem, tempPage);
        }

        Element accessoryListElem = pageElem
                .getChild(XMLConstants.ACCESSORYPAGES);
        if (accessoryListElem != null) {
            List accessoryPageList = accessoryListElem.getChildren();
            Vector accessoryList = new Vector();
            Iterator it = accessoryPageList.iterator();
            while (it.hasNext()) {
                Element accessoryPageElem = (Element) it.next();
                accessoryList
                        .add(decodeAccessoryPageElement(accessoryPageElem));
            }
            Collections.sort(accessoryList);
            tempPage.setAccessoryPages(accessoryList);
        }

        // add authorlist
        attribValue = pageElem
                .getAttributeValue(XMLConstants.CONTRIBUTORCHANGED);
        tempPage.setContributorsChanged(attribValue != null
                && attribValue.equals(XMLConstants.TRUE));
        Element contrListElem = pageElem.getChild(XMLConstants.CONTRIBUTORLIST);
        if (contrListElem != null) {
            decodeContributors(contrListElem, true, tempPage, null);
        }

        return 1;
    }

    public AccessoryPage decodeAccessoryPageElement(Element accessoryPageElem) {
        AccessoryPage accPage = new AccessoryPage();
        String isArticle = accessoryPageElem
                .getAttributeValue(XMLConstants.IS_ARTICLE);
        accPage.setIsArticle(isArticle != null
                && isArticle.equals(XMLConstants.ONE));
        String isTreehouses = accessoryPageElem
                .getAttributeValue(XMLConstants.IS_TREEHOUSE);
        accPage.setIsTreehouse(isTreehouses != null
                && isTreehouses.equals(XMLConstants.ONE));
        String editableString = accessoryPageElem
                .getAttributeValue(XMLConstants.PERMISSION);
        accPage.setIsEditable(editableString != null
                && editableString.equals(XMLConstants.ONE));
        String id = accessoryPageElem.getAttributeValue(XMLConstants.ID);
        if (StringUtils.notEmpty(id)) {
            accPage.setId(new Integer(id).intValue());
        }
        Element accessoryPageTitleElem = accessoryPageElem
                .getChild(XMLConstants.PAGETITLE);
        if (accessoryPageTitleElem != null) {
            accPage.setPageTitle(accessoryPageTitleElem.getTextTrim());
        }
        return accPage;
    }

    protected abstract void decodeImageListElement(Element imageListElement,
            Page tempPage);

    protected abstract void decodeTextListElement(Element textListElement,
            Page tempPage);

    /**
     * Sets the contributors for a given page or accessory page
     */
    private void decodeContributors(Element contrListElem, boolean isPage,
            Page tempPage, AccessoryPage accPage) {
        if (contrListElem != null) {
            List contrList = contrListElem.getChildren();
            Collection contributors = getCollectionInstanceForPageContributors();
            Iterator it = contrList.iterator();
            while (it.hasNext()) {
                Element contrElem = (Element) it.next();

                String emailString = contrElem
                        .getAttributeValue(XMLConstants.EMAIL);
                String contactString = contrElem
                        .getAttributeValue(XMLConstants.CONTACT);
                String isAuthorString = contrElem
                        .getAttributeValue(XMLConstants.IS_AUTHOR);
                String isCopyOwnerString = contrElem
                        .getAttributeValue(XMLConstants.COPYRIGHT);

                int id = Integer.parseInt(contrElem
                        .getAttributeValue(XMLConstants.ID));
                int order = Integer.parseInt(contrElem
                        .getAttributeValue(XMLConstants.ORDER));
                if (contactString == null) {
                    contactString = XMLConstants.ZERO;
                }
                if (isAuthorString == null) {
                    isAuthorString = XMLConstants.ZERO;
                }
                if (isCopyOwnerString == null) {
                    isCopyOwnerString = XMLConstants.ZERO;
                }
                boolean isContact = contactString.equals(XMLConstants.ONE) ? true
                        : false;
                boolean isAuthor = isAuthorString.equals(XMLConstants.ONE) ? true
                        : false;
                boolean isCopyOwner = isCopyOwnerString
                        .equals(XMLConstants.ONE) ? true : false;

                PageContributor tempContr;
                if (isPage) {
                    tempContr = new PageContributor(tempPage, id, isAuthor,
                            isContact, isCopyOwner, order);
                } else {
                    tempContr = new AccessoryPageContributor(accPage, id,
                            isAuthor, isContact, isCopyOwner, order);
                }
                contributors.add(tempContr);
            }
            if (isPage) {
                setPageContributorsForPage(tempPage, contributors);
            } else {
                accPage.setContributorList((Vector) contributors);
            }
        }
    }

    protected abstract Collection getCollectionInstanceForPageContributors();

    protected abstract void setPageContributorsForPage(Page page,
            Collection pageContributors);

    /**
     * Returns the error string, if any, that occurred during reading
     * 
     * @return The error string, if any, that occurred during reading
     */
    public String getErrorString() {
        return errorString;
    }

    /**
     * Returns the error number, if any, that occurred during reading
     * 
     * @return The error number, if any, that occurred during reading
     */
    public int getErrorNum() {
        return errorNum;
    }

    /**
     * Method to recursively construct Nodes from a given node element
     * 
     * @param elem
     *            The element to construct the children from
     * @return An int status code indicating success or failure
     */
    private void startRecursiveChildLoop(Element elem, Node parent, int depth) {
        Element tempNodesElem = elem.getChild(XMLConstants.NODES);
        System.out.println("children? " + tempNodesElem);
        if (tempNodesElem != null) {
            List nodeElemList = tempNodesElem.getChildren(XMLConstants.NODE);
            Iterator it = nodeElemList.iterator();
            //parent.setCheckedOut(it.hasNext());
            while (it.hasNext()) {
                Element nodeElement = (Element) it.next();
                System.out.println("constructing node: "
                        + nodeElement.getChildTextTrim(XMLConstants.NAME));
                constructNode(nodeElement, parent, depth);
            }
        }
    }
}