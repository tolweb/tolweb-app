package org.tolweb.content.preparers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.MediaContentAttributes;
import org.tolweb.content.helpers.PageContentAttributes;
import org.tolweb.content.helpers.PageContentElements;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.TextPreparer;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;
import org.tolweb.treegrow.tree.Node;

/* PageCoPreparer
 *   GroupCoPreparer
 *   NameCoPreparer
 *   SubgroupCoPreparer
 *   SectionCoPreparer */

public class PageCoPreparer extends AbstractCoPreparer {
	private static final String IMAGE_REPLACEMENT = "<!-- ToL Image #START# --> " + 
			"<a href=\"javascript: w = window.open('http://tolweb.org/media/%1$s'" +
			", '%1$s', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">" + 
			"<img src=\"%2$s\"/></a><!-- ToL Image #END# -->";
	private Pattern tolImgRegex; 
	private HashMap<Integer, String> embeddedMedia;
	
	public PageCoPreparer() {
		tolImgRegex = Pattern.compile("<\\s*ToLimg\\s*([^>]*)\\s*id=\"?(\\d+)\"?\\s*([^>]*)>", Pattern.CASE_INSENSITIVE);
		embeddedMedia = new HashMap<Integer, String>();
	}
	
	public void setContentSource(Object mpage, DaoBundle daos, Element doc) {
		setMappedPage((MappedPage)mpage);
		setDaoBundle(daos);
		setParentElement(doc);
		setPreparedElement(doc);		
	}
	
	@SuppressWarnings("unchecked")	
	public void processContent() {
		
		Element page = new Element(PageContentElements.PAGE, ContentPreparer.NS);
		page.addAttribute(new Attribute(PageContentAttributes.ID, "" + getMappedPage().getPageId()));
		
		String pageNodeNameUrl = getMappedPage().getMappedNode().getName();
		try {
			pageNodeNameUrl = URLEncoder.encode(pageNodeNameUrl, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		String nodeIdString = "" + getMappedPage().getMappedNode().getId();
		pageNodeNameUrl = StringUtils.notEmpty(pageNodeNameUrl) ? pageNodeNameUrl + "/" + nodeIdString : nodeIdString;
		String pageUrl = "http://tolweb.org/" + pageNodeNameUrl;
		page.addAttribute(new Attribute(PageContentAttributes.PAGE_URL, pageUrl));
		page.addAttribute(new Attribute(PageContentAttributes.PAGE_STATUS, getMappedPage().getStatus()));
		page.addAttribute(new Attribute(PageContentAttributes.DATE_CREATED, getSafeString(getMappedPage().getFirstOnlineString())));
		page.addAttribute(new Attribute(PageContentAttributes.DATE_CHANGED, safeToString(getMappedPage().getContentChangedDate())));
		
		Element group = new Element(PageContentElements.GROUP, ContentPreparer.NS);
		page.appendChild(group);
		
		group.addAttribute(new Attribute(PageContentAttributes.NODE, "" + getMappedPage().getMappedNode().getId()));
		group.addAttribute(new Attribute(PageContentAttributes.EXTINCT, (getMappedPage().getMappedNode().getExtinct() == 2) ? "true" : "false"));
		group.addAttribute(new Attribute(PageContentAttributes.PHYLESIS, getPhylesisString(getMappedPage().getMappedNode().getPhylesis())));
		group.addAttribute(new Attribute(PageContentAttributes.LEAF, getMappedPage().getMappedNode().getIsLeaf() ? "true" : "false"));
		
		Element groupDesc = new Element(PageContentElements.GROUP_DESCRIPTION, ContentPreparer.NS);
		String groupDescText = getMappedPage().getMappedNode().getDescription();
		groupDesc.appendChild(new Text(groupDescText));

		if (StringUtils.notEmpty(groupDescText)) {
			group.appendChild(groupDesc);
		}
		
		Element groupCmt = new Element(PageContentElements.GROUP_COMMENT, ContentPreparer.NS);
		String groupCmtText = getMappedPage().getLeadText();
		groupCmt.appendChild(new Text(groupCmtText));
		
		if (StringUtils.notEmpty(groupCmtText)) {
			group.appendChild(groupCmt);
		}
		
		Element names = new Element(PageContentElements.NAMES, ContentPreparer.NS);
		page.appendChild(names);
		
		Element name = new Element(PageContentElements.NAME, ContentPreparer.NS);
		names.appendChild(name);
		
		name.appendChild(new Text(getMappedPage().getMappedNode().getName()));
		name.addAttribute(new Attribute(PageContentAttributes.ITALICIZE_NAME, Boolean.valueOf(getMappedPage().getMappedNode().getItalicizeName()).toString()));
		name.addAttribute(new Attribute(PageContentAttributes.AUTHORITY, getSafeString(getMappedPage().getMappedNode().getNameAuthority())));
		name.addAttribute(new Attribute(PageContentAttributes.AUTH_DATE, safeToString(getMappedPage().getMappedNode().getAuthorityDate())));
		name.addAttribute(new Attribute(PageContentAttributes.NAME_COMMENT, getSafeString(getMappedPage().getMappedNode().getNameComment())));
		name.addAttribute(new Attribute(PageContentAttributes.NEW_COMBINATION, Boolean.valueOf(getMappedPage().getMappedNode().getIsNewCombination()).toString()));
		name.addAttribute(new Attribute(PageContentAttributes.COMBINATION_AUTHOR, getSafeString(getMappedPage().getMappedNode().getCombinationAuthor())));
		name.addAttribute(new Attribute(PageContentAttributes.COMBINATION_DATE, safeToString(getMappedPage().getMappedNode().getCombinationDate())));
		
		Element othernames = new Element(PageContentElements.OTHERNAMES, ContentPreparer.NS);
		
		SortedSet otherNamesSet = getMappedPage().getMappedNode().getSynonyms();
		
		for (Iterator itr = otherNamesSet.iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName)itr.next(); 
			Element othername = new Element(PageContentElements.OTHERNAME, ContentPreparer.NS);
			othername.addAttribute(new Attribute(PageContentAttributes.ID, "" + moname.getId()));
			othername.addAttribute(new Attribute(PageContentAttributes.ITALICIZE_NAME, Boolean.valueOf(moname.getItalicize()).toString()));
			othername.addAttribute(new Attribute(PageContentAttributes.AUTHORITY, getSafeString(moname.getAuthority())));
			othername.addAttribute(new Attribute(PageContentAttributes.AUTH_DATE, safeToString(moname.getAuthorityYear())));
			othername.addAttribute(new Attribute(PageContentAttributes.NAME_COMMENT, getSafeString(moname.getComment())));
			othername.addAttribute(new Attribute(PageContentAttributes.IS_IMPORTANT, Boolean.valueOf(moname.getIsImportant()).toString()));
			othername.addAttribute(new Attribute(PageContentAttributes.IS_PREFERRED, Boolean.valueOf(moname.getIsPreferred()).toString()));
			othername.addAttribute(new Attribute(PageContentAttributes.SEQUENCE, safeToString(moname.getOrder())));
		}

		if (!otherNamesSet.isEmpty()) {
			names.appendChild(othernames);
		}
		
		List children = getMappedPage().getMappedNode().getChildren();
		boolean isTerminal = children != null && children.isEmpty();

		// add this if not a leaf or writeaslist is false... or is terminal (e.g. no children)
		if (getMappedPage().getMappedNode().getIsLeaf() && !isTerminal) {
			Element subgroups = new Element(PageContentElements.SUBGROUPS, ContentPreparer.NS);
			page.appendChild(subgroups);
		
			if (!getMappedPage().getWriteAsList()) {
				Element treeimage = new Element(PageContentElements.TREEIMAGE, ContentPreparer.NS);
				ContributorLicenseInfo currDefault = new ContributorLicenseInfo(ContributorLicenseInfo.TREE_IMAGE_LICENSE);
				treeimage.addAttribute(new Attribute(PageContentAttributes.LICENSE, currDefault.toShortString()));
				String treeImageName = getMappedPage().getGroupName().replaceAll("\\s", "_");
				treeimage.appendChild(new Text("http://www.tolweb.org/Public/treeImages/" + treeImageName + ".png"));
				subgroups.appendChild(treeimage);
			}
			
			Element newicktree = new Element(PageContentElements.NEWICKTREE, ContentPreparer.NS);
			subgroups.appendChild(newicktree);

			Element taxonlist = new Element(PageContentElements.TAXON_LIST, ContentPreparer.NS);
			taxonlist.appendChild(new Text(StringEscapeUtils.escapeXml(getTaxonListAsHTML())));
			subgroups.appendChild(taxonlist);
			
			Element treecomment = new Element(PageContentElements.TREE_COMMENT, ContentPreparer.NS);
			subgroups.appendChild(treecomment);
			treecomment.appendChild(new Text(StringEscapeUtils.escapeXml(getMappedPage().getPostTreeText())));
		}
		
		Element sections = new Element(PageContentElements.SECTIONS, ContentPreparer.NS);
		page.appendChild(sections);
		
		SortedSet textSections = getMappedPage().getTextSections();
		for (Iterator itr = textSections.iterator(); itr.hasNext(); ) {
			MappedTextSection mtxt = (MappedTextSection)itr.next();
			Element section = new Element(PageContentElements.SECTION, ContentPreparer.NS);
			section.addAttribute(new Attribute(PageContentAttributes.ID, "" + mtxt.getTextSectionId()));
			section.addAttribute(new Attribute(PageContentAttributes.SECTION_TITLE, mtxt.getHeading()));
			section.addAttribute(new Attribute(PageContentAttributes.PAGE_ORDER, safeToString(mtxt.getOrder())));
			section.addAttribute(new Attribute(PageContentAttributes.COPYRIGHT_DATE, getMappedPage().getCopyrightDate()));
			section.addAttribute(new Attribute(PageContentAttributes.LICENSE, getLicenseShortName(getMappedPage().getUsePermission())));
			section.addAttribute(new Attribute(PageContentAttributes.AUTHORS, getAuthorsIdString(getMappedPage().getContributors())));
			section.addAttribute(new Attribute(PageContentAttributes.CORRESPONDENTS, getCorrespondentsIdString(getMappedPage().getContributors())));
			section.addAttribute(new Attribute(PageContentAttributes.COPYRIGHT_OWNERS, getCopyrightOwnersIdString(getMappedPage().getContributors())));
			section.addAttribute(new Attribute(PageContentAttributes.OTHER_COPYRIGHT, getSafeString(getMappedPage().getCopyrightHolder())));
			section.addAttribute(new Attribute(PageContentAttributes.CONTENT_CHANGED, safeToString(getMappedPage().getContentChangedDate())));
			
			// add the section-text text element
			Element sectionText = new Element(PageContentElements.SECTION_TEXT, ContentPreparer.NS);
			sectionText.appendChild(new Text(processSectionText(mtxt.getText(), pageUrl)));
			section.appendChild(sectionText);
			
			Element sectionMedia = new Element(PageContentElements.SECTION_MEDIA, ContentPreparer.NS);
			processSectionMedia(sectionMedia);
			section.appendChild(sectionMedia);
			
			// add the section-source element
			Element sectionSource = new Element(PageContentElements.SECTION_SOURCE, ContentPreparer.NS);
			// TODO add attribute data to section-source
			section.appendChild(sectionSource);
			
			String sectionAnchor = mtxt.getHeadingNoSpaces();
			sectionSource.addAttribute(new Attribute(PageContentAttributes.SOURCE_COLLECTION, "0"));
			sectionSource.addAttribute(new Attribute(PageContentAttributes.SOURCE_TITLE, mtxt.getHeading()));
			sectionSource.addAttribute(new Attribute(PageContentAttributes.SOURCE_URL, "http://tolweb.org/" + pageNodeNameUrl + "#" + sectionAnchor));
			sectionSource.addAttribute(new Attribute(PageContentAttributes.MORE_SOURCE, "[future-use]"));
			
			sections.appendChild(section);
		}
		
		Element refs = new Element(PageContentElements.REFERENCES, ContentPreparer.NS);
		page.appendChild(refs);
		TextPreparer txtPrep = new TextPreparer();
		List refsList = txtPrep.getNewlineSeparatedList(getMappedPage().getReferences());
		for (Iterator itr = refsList.iterator(); itr.hasNext(); ) {
			String ref = (String)itr.next();
			// only add the reference element if it's not empty
			if (StringUtils.notEmpty(ref)) {
				Element refEl = new Element(PageContentElements.REFERENCE, ContentPreparer.NS);
				refEl.appendChild(new Text(StringEscapeUtils.escapeXml(ref)));
				refs.appendChild(refEl);
			}
		}
		
		Element internetInfo = new Element(PageContentElements.INTERNET_INFO, ContentPreparer.NS);
		page.appendChild(internetInfo);
		internetInfo.appendChild(new Text(StringEscapeUtils.escapeXml(getMappedPage().getInternetInfo())));
		
		getElement().appendChild(page);
	}

	private void processSectionMedia(Element sectionMedia) {
		// for each embedded media found in the text-section, add it to the media section as a media-set
		for (Map.Entry<Integer, String> entry : embeddedMedia.entrySet()) {
			Element sectionMediaSet = new Element(PageContentElements.MEDIA_SET, ContentPreparer.NS);
			sectionMediaSet.addAttribute(new Attribute(PageContentAttributes.ID, "" + entry.getKey()));
			sectionMediaSet.addAttribute(new Attribute(MediaContentAttributes.THUMBNAIL, entry.getValue()));
			sectionMedia.appendChild(sectionMediaSet);
		}
		// hey - we're in a loop, and it's got "text-section" locality, so we need to clear this 
		// hashmap out before the next text-section is processed
		embeddedMedia.clear();
	}

	private String processSectionText(String sectionText, String pageUrl) {

		// handles href="#link", href="/link", and href="../"
		sectionText = processAnchors(sectionText, pageUrl);
		// as method suggests, this is a temporary solution to resolving embedded image tags in section text
		sectionText = tmpProcessToLImageTags(sectionText);
		// strip out class, id, and style attributes from embedded markup
		sectionText = stripOutClassIdStyleAttributes(sectionText);
		return StringEscapeUtils.escapeXml(sectionText);
	}
	
	private String stripOutClassIdStyleAttributes(String sectionText) {
		sectionText = sectionText.replaceAll("<\\s*([^>]*)\\s*([^>]*)\\s*((class|[^_]id|style)=\"?[^>]*\"?)\\s*([^>]*)>", "<$1>");		
		return sectionText;		
	}
	
	private String tmpProcessToLImageTags(String sectionText) {
		//javascript: w = window.open('/onlinecontributors/app?service=external/ViewImageData&sp=1539', '1539', 'resizable,height=500,width=670,scrollbars=yes'); w.focus();
//		sectionText = sectionText.replaceAll("<\\s*ToLimg\\s*([^>]*)\\s*id=\"?(\\d+)\"?\\s*([^>]*)>", 
//				"<a href=\"javascript: w = window.open('http://tolweb.org/media/$2', '$2', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">Tree of Life Image</a>");
		
		if (sectionText != null) {
			StringBuffer buffer = new StringBuffer();
			Matcher matcher = tolImgRegex.matcher(sectionText);
			while(matcher.find()) {
				String tolMediaId = matcher.group(2);
				Integer id = Integer.valueOf(tolMediaId);
				String thumbnailUrl = getDaoBundle().getImageDAO().getThumbnailUrlForImageWithId(id.intValue());
				if (!thumbnailUrl.startsWith("/tree")) {
					thumbnailUrl = "http://tolweb.org/tree" + thumbnailUrl;
				} else {
					thumbnailUrl = "http://tolweb.org" + thumbnailUrl;
				}
				embeddedMedia.put(id, thumbnailUrl);
				String fmt = IMAGE_REPLACEMENT;
				String output = String.format(fmt, id, thumbnailUrl);
				matcher.appendReplacement(buffer, output);
			}
			matcher.appendTail(buffer);
			return buffer.toString();
		} else {
			return "";
		}
	}
	
	private String processAnchors(String sectionText, String pageUrl) {
		sectionText = sectionText.replaceAll("href=\"#(.*)\"", "href=\"" + pageUrl + "#$1\"");
		sectionText = sectionText.replaceAll("href=\"(\\.\\.)?/([^>]*)\"([^<])*>", "href=\"http://tolweb.org/$2\">");
		return sectionText;
	}
	
	@SuppressWarnings("unchecked")
	private String getAuthorsIdString(Collection contributors) {
	    ArrayList names = new ArrayList();
		Iterator it = contributors.iterator();
		while (it.hasNext()) {
		    PageContributor contr = (PageContributor) it.next();
		    if (contr.getIsAuthor()) {
		        names.add(contr.getContributor().getId());
		    }
		}
		return StringUtils.returnCommaJoinedString(names);	    
	}

	@SuppressWarnings("unchecked")
	private String getCorrespondentsIdString(Collection contributors) {
	    ArrayList names = new ArrayList();
		Iterator it = contributors.iterator();
		while (it.hasNext()) {
		    PageContributor contr = (PageContributor) it.next();
		    if (contr.getIsContact()) {
		        names.add(contr.getContributor().getId());
		    }
		}
		return StringUtils.returnCommaJoinedString(names);	    
	}
	
	@SuppressWarnings("unchecked")
	private String getCopyrightOwnersIdString(Collection contributors) {
	    ArrayList names = new ArrayList();
		Iterator it = contributors.iterator();
		while (it.hasNext()) {
		    PageContributor contr = (PageContributor) it.next();
		    if (contr.getIsCopyOwner()) {
		        names.add(contr.getContributor().getId());
		    }
		}
		return StringUtils.returnCommaJoinedString(names);	    
	}
	
	private String getLicenseShortName(byte licenseCode) {
		ContributorLicenseInfo cLicInfo = new ContributorLicenseInfo(licenseCode);
		return cLicInfo.toShortString();
	}
	
	private String getPhylesisString(int phylesis) {
		if (phylesis == Node.MONOPHYLETIC) {
			return "monophyletic";
		} else if (phylesis == Node.MONOPHYLY_UNCERTAIN) {
			return "monophyletic uncertain";
		} else if (phylesis == Node.NOT_MONOPHYLETIC) {
			return "not monophyletic";
		} else {
			return "";
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getTaxonListAsHTML() {
        List nodes = getDaoBundle().getPageDAO().getOrderedByParentNodesOnPage(getMappedPage(), true);
        MappedNode rootNode = getMappedPage().getMappedNode();
        nodes.add(rootNode);
        // build a hashtable to set up the tree structure
        Hashtable<Long, MappedNode> idsToNodes = new Hashtable();
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            idsToNodes.put(nextNode.getNodeId(), nextNode);
        }
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            // get our parent and add ourselves to the children of the parent
            MappedNode parent = idsToNodes.get(nextNode.getParentNodeId());
            if (parent != null) {
                parent.addToChildren(nextNode);
            }
        }
        StringBuffer list = new StringBuffer();
        list.append("<ul>");
        traverseTaxonTree(list, rootNode);
        list.append("</ul>");
		return list.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void traverseTaxonTree(StringBuffer buff, MappedNode curr) {
		if (curr != null) {
			boolean emptyOrInternal = StringUtils.notEmpty(curr.getName());
			if (emptyOrInternal) {
				buff.append("<li>" + curr.getName());
			}
			List children = curr.getChildren();
			if (!children.isEmpty()) {
				if (emptyOrInternal) {
					buff.append("<ul>");
				}
				for (Iterator itr = children.iterator(); itr.hasNext(); ) {
					MappedNode child = (MappedNode)itr.next();
					traverseTaxonTree(buff, child);
				}
				if (emptyOrInternal) {
					buff.append("</ul>");
				}
			}
			if (emptyOrInternal) {
				buff.append("</li>");
			}			
		}
	}
/*
	private void traverseTree(NodeDAO ndao, MappedNode curr) {
		if (curr != null) {
			System.out.println(" " + curr.getName());
			increment();
			curr.setTreeOrder(getCounterValue());
		
			List children = ndao.getChildrenNodes(curr);
						
			for (Iterator i = children.iterator(); i.hasNext(); ) {
				MappedNode child = (MappedNode)i.next();
				System.out.println("\t child:" + child.getName());
				traverseTree(ndao, child);
			}
			
			ndao.saveNode(curr);
		}
	}
 */	
	
/*
    private void createListForNode(IMarkupWriter writer, MappedNode node, boolean isRoot, int currentRank, Integer parentRank, Long projectId) {
        Integer classInteger = null;
        boolean openedAdditionalList = false;
        boolean openedLi = false;
        Iterator it;
        if (getNodes() == null) { 
        	it = getNodeDAO().getChildrenNodes(node, !isRoot, false).itprocessSectionMedia(sectionMedia);erator();
        } else {
        	List childNodes = getNodes().get(node.getNodeId());
        	if (childNodes != null) {
        		it = childNodes.iterator();
        	} else {
        		List list = new ArrayList<Object>();
        		it = list.iterator();
        	}
        }        
        boolean hasChildren = it.hasNext();
        if (!isRoot && StringUtils.notEmpty(node.getName())) {
        	PageDAO dao = getPageDAO();
            boolean hasPage = dao.getNodeHasPage(node);
            boolean hadFakeParent = false;
            // If there is no page, then do this fake parent child relationship
            // with the supertitle
            if (!hasPage && node.getPageSupertitle() != null) {
                hadFakeParent = true;
                writer.begin("li");
                if ((classInteger = getRankInteger(parentRank, node, currentRank)) != null) {
                    writer.attribute("class", "over" + classInteger);                    
                }
                writer.begin("em");
                String emClass = getEmClass(((MappedOtherName) node.getFirstPreferredOtherName()).getItalicize(), 
                		false);
                String name = node.getActualPageTitle(false, true, true);
                writer.attribute("class", emClass);
                writer.printRaw(name);
                if (node.getShowAuthorityInContainingGroup()) {
                    addAuthority(writer, node.getPageAuthority());
                }   
                writer.end("em");
                writer.printRaw("\n");
                // Open an additional unordered list in order to create
                // the appearance of another node in the tree
                writer.begin("ul");
                // we won't make the fake parent collapsable or render the links
                writer.attribute("class", getDefaultUlClass());
                writer.printRaw("\n");
                openedAdditionalList = true;
            }
            openedLi = true;
            writer.begin("li");
            if ((classInteger = getRankInteger(parentRank, node, currentRank)) != null) {
                writer.attribute("class", "over" + classInteger);
                currentRank = node.getNodeRankInteger().intValue();
            }
            String emClass = getEmClass(node.getItalicizeName(), hasChildren);
            writer.begin("em");
            writer.attribute("class", emClass);
            writer.attribute("id", getEmIdForNode(node));
            String titleLinkString = getIsIndex() ? " title=\"go to Taxon Sampling page\"" : " title=\"go to ToL page\"";
             // when we want to write a taxon index link:
            boolean writeTaxonIndexLink = getNodeNameBlock() != null && hasChildren;
            
            String linkString = getPageNameForNode(node);
            if (writeTaxonIndexLink || hasPage) {
                String name = node.getName();
                if (getNodeNameBlock() == null) {
                	String branchPageUrl = getURLBuilder().getURLForBranchPage(URLBuilder.NO_HOST_PREFIX, name, node.getNodeId());                	
                    writer.printRaw("<a href=\"" + branchPageUrl + "\"" + titleLinkString + " >" + linkString + "</a>");                	
                } else if (writeTaxonIndexLink) {
                    String url = "/onlinecontributors/app?service=external&page=TaxaIndex&sp=l" + node.getNodeId() + "&sp=l" + projectId;
                    writer.printRaw("<a href=\"" + url + "\" " + titleLinkString + " >" + linkString + "</a>");            	
                } else {
                    writer.printRaw(name);
                }
            } else {
                String nodeName = node.getName();
                writer.printRaw(nodeName);
            }

            	
            
            writer.end("em");
            // tack on the authority after the name
            if (node.getShowAuthorityInContainingGroup()) {
                String authString = hadFakeParent ? node.getNodeAuthority() : node.getPageAuthority();
                addAuthority(writer, authString);
            }   
            if (!getIsIndex()) {
	            if (node.getExtinct() == Node.EXTINCT) {
	                writer.printRaw(" <img src=\"/tree/icons/extinct.gif\" width=\"7\" height=\"9\">");
	            }
	            if (StringUtils.notEmpty(node.getDescription())) {
	                writer.printRaw("  <span class=\"description\">" + node.getDescription() + "</span>");
	            }
	            if (node.getPhylesis() == Node.MONOPHYLY_UNCERTAIN) {
	                writer.printRaw("<span class=\"property\"> (monophyly uncertain) </span>");
	            } else if (node.getPhylesis() == Node.NOT_MONOPHYLETIC) {
	                writer.printRaw("<span class=\"property\"> (non-monophyletic) </span>");
	            }
	            if (node.getConfidence() == Node.INCERT_PUTATIVE || node.getConfidence() == Node.INCERT_UNSPECIFIED) {
	                writer.printRaw("<span class=\"property\"> (incertae sedis) </span>");
	            }
            } 
        }
        boolean openedList = false;
        if (!isRoot && it.hasNext() && StringUtils.notEmpty(node.getName())) {
        	String listId = getUlIdForNode(node);        	
            writer.begin("ul");
            writer.attribute("id", listId);
            writer.attribute("class", getDefaultUlClass());
            if (getIsIndex()) {
            	String expandClosedAttribute = getIndexPage().getExpandMostAttribute(node);
            	if (expandClosedAttribute != null) {
            		writer.attribute(TaxaIndex.SHOW_MOST, expandClosedAttribute);
            	}
            }
            writer.printRaw("\n");
            openedList = true;
            currentRank += 1;
        } 
        while (it.hasNext()) {
            MappedNode nextChild = (MappedNode) it.next();
            createListForNode(writer, nextChild, false, currentRank, classInteger, projectId);
        }
        if (openedList) {
        	closeList(writer);
        }
        if (openedLi) {
	        closeListElement(writer);
        }
        if (openedAdditionalList) {
        	closeList(writer);
            closeListElement(writer);
        }
    }
 */	
}
