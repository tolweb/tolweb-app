/*
 * Created on Oct 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.apache.tapestry.markup.UTFMarkupFilter;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.misc.CacheAccess;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TaxonList extends AbstractComponent implements NodeInjectable {
    @Parameter
	public abstract MappedNode getRootNode();
    @Parameter
    public abstract Hashtable<Long, List<MappedNode>> getNodes();
    @Parameter
    public abstract Block getPreNameBlock();
    @Parameter
    public abstract Block getExpandCollapseBlock();
    @Parameter
    public abstract Block getColumnHeaderBlock();
    @Parameter
    public abstract Block getNodeNameBlock();
    @Parameter
    public abstract Block getAfterNodeBlock();
    @Parameter
    public abstract boolean getIsIndex();
    @Parameter(defaultValue = "'taxonlist'")
    public abstract String getListId();
    
    @InjectObject("spring:urlBuilder")
    public abstract URLBuilder getURLBuilder();
    @InjectObject("spring:cacheAccess")
    public abstract CacheAccess getCacheAccess();
    
    @InjectPage("TaxaIndex")
    public abstract TaxaIndex getIndexPage();
    
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        long currentTime = System.currentTimeMillis();
    	String output = null;//getCachedOutput();
        Boolean useCache = (Boolean) cycle.getAttribute(CacheAndPublicAwarePage.USE_CACHE);
        
        if (output == null || (useCache == null || !useCache.booleanValue())) {
		    ByteArrayOutputStream stream = new ByteArrayOutputStream(2000);
		    IMarkupWriter otherWriter = new MarkupWriterImpl("text/html", new PrintWriter(stream), new UTFMarkupFilter());
	        otherWriter.begin("ul");
	        otherWriter.attribute("id", getListId());
	        otherWriter.printRaw("\n");
	        if (getColumnHeaderBlock() != null) {
	        	otherWriter.begin("li");
	        	getColumnHeaderBlock().renderForComponent(otherWriter, cycle, this);
	        	otherWriter.end("li");
	        }
	        Block preNameNodeInfoBlock = getPreNameBlock();
	        Block expandCollapseBlock = getExpandCollapseBlock();
	        writeListForNode(otherWriter, getRootNode(), true, 0, null, cycle, 
	        		preNameNodeInfoBlock, expandCollapseBlock, getIndexPage().getProjectId(), getIndexPage());
	        otherWriter.end("ul");
		    otherWriter.flush();   
		    output = stream.toString();
		    if (useCache != null && useCache.booleanValue()) {
		    	setCachedOutput(output);
		    }
        }
	    writer.printRaw(output);        
	    System.out.println("render component took: " + (System.currentTimeMillis() - currentTime));
    }
    
    private boolean getConsiderRank() {
    	// only worry about node ranks if this actually on a page 
    	return getNodes() == null;
    }
    
    public String getUlIdForNode(MappedNode node) {
    	return getUlIdPrefix() + node.getNodeId().toString();
    }
    
    public String getUlIdPrefix() {
    	return "l";
    }
    
    public String getDefaultUlClass() {
    	if (getNodes() != null) {
    		return "hide";
    	} else {
    		return "show";
    	}
    }
    
    public String getPageNameForNode(MappedNode node) {
    	return node.getActualPageTitle(false, true, true);
    }
    
    @SuppressWarnings("unchecked")
    private void writeListForNode(IMarkupWriter writer, MappedNode node, boolean isRoot, 
    		int currentRank, Integer parentRank, IRequestCycle cycle, Block preNameNodeInfoBlock,
    		Block expandCollapseBlock, Long projectId, TaxaIndex indexPage) {
        Integer classInteger = null;
        boolean openedAdditionalList = false;
        boolean openedLi = false;
        Iterator it;
        if (getNodes() == null) { 
        	it = getNodeDAO().getChildrenNodes(node, !isRoot, false).iterator();
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
        PropertyUtils.write(getPage(), "currentNode", node);
        if (!isRoot && StringUtils.notEmpty(node.getName())) {
        	PageDAO dao = getPageDAO();
            boolean hasPage = false;
            if (getIsIndex()) {
                hasPage = node.getAdditionalFields().getHasPage();
            } else {
                hasPage = dao.getNodeHasPage(node);
            }
            boolean hadFakeParent = false;
            // If there is no page, then do this fake parent child relationship
            // with the supertitle
            if (!hasPage && node.getPageSupertitle() != null) {
                hadFakeParent = true;
                writer.begin("li");
                if ((classInteger = getRankInteger(parentRank, node, currentRank)) != null) {
                    writer.attribute("class", "over" + classInteger);                    
                }
                if (preNameNodeInfoBlock != null) {
                	preNameNodeInfoBlock.renderForComponent(writer, cycle, this);
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
            if (preNameNodeInfoBlock != null) {
            	preNameNodeInfoBlock.renderForComponent(writer, cycle, this);
            }

            // The below logic is in place to make sure the decision to italicize is based on the taxa-name that 
            // will be displayed. If a page supertitle is not empty, that name will be picked up as the "linkString" 
            // by the call to getPageNameForNode - so using the node's italicize name boolean is not appropriate when 
            // we will not even be display the node's name anyway.  (god I hope that makes sense) - lenards[1/17/2008]
            boolean italicizeName = node.getItalicizeName();
            
            // DEVN - lenards - changed 01/26/09 - Bug #2381
            // There was this odd case where a node that has a preferred label but lacks both a page and children 
            // was being displayed without italics (their "emClass" ended up being 'taxon' instead of 'taxoni').  
            // What that meant was, the preferred other name's setting were trumping the node, so the change was 
            // to say - "we only care about the page-supertitle when it has-children or has-a-page."  If those are 
            // both false then we don't want to trump the node's italics attribute. 
            if ((hasChildren || hasPage) && StringUtils.notEmpty(node.getPageSupertitle())) {
            	italicizeName = ((MappedOtherName) node.getFirstPreferredOtherName()).getItalicize();
            }
            String emClass = getEmClass(italicizeName, hasChildren);
            writer.begin("em");
            writer.attribute("class", emClass);
            writer.attribute("id", getEmIdForNode(node));
            if (expandCollapseBlock != null) {
            	expandCollapseBlock.renderForComponent(writer, cycle, this);
            }            
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
            } else {
	            if (getAfterNodeBlock() != null) {
	            	getAfterNodeBlock().renderForComponent(writer, cycle, this);
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
            writeListForNode(writer, nextChild, false, currentRank, classInteger, cycle, 
            		preNameNodeInfoBlock, expandCollapseBlock, projectId, indexPage);
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
    private void closeList(IMarkupWriter writer) {
        writer.end("ul");
        writer.printRaw("\n");    	
    }
    private void closeListElement(IMarkupWriter writer) {
        writer.end("li");
        writer.printRaw("\n");    	
    }
    private void addAuthority(IMarkupWriter writer, String authString) {
        if (StringUtils.notEmpty(authString)) {
            writer.printRaw("<span class=\"description\"> " + authString + "</span>");
        }
    }
    
    /*private String getNodeNameString(MappedNode node, boolean hasChildren) {
        String supertitleString = node.getActualPageTitle(false, true, true);
        if (StringUtils.notEmpty(node.getPageSupertitle())) {
            supertitleString = conditionallyAddItalics(supertitleString, 
            		((MappedOtherName) node.getFirstPreferredOtherName()).getItalicize(), hasChildren);
        } else {
            supertitleString = conditionallyAddItalics(supertitleString, node.getItalicizeName(), hasChildren);
        }   
        return supertitleString;
    }
    
    private String conditionallyAddItalics(String nodeName, MappedNode node) {
        return conditionallyAddItalics(nodeName, node.getItalicizeName(), false);
    }*/
    
    private String getEmClass(boolean italicizeName, boolean hasChildren) {
    	String italicsClass = getIsIndex() && hasChildren ? "taxonic" : "taxoni";
    	String noItalicsClass = getIsIndex() && hasChildren ? "taxonc" : "taxon";
        if (italicizeName) {
        	return italicsClass;
        } else {
        	return noItalicsClass;
        }
    }
    public String getEmIdForNode(MappedNode node) {
    	return "em" + node.getNodeId();
    }
    private Integer getRankInteger(Integer parentRank, MappedNode node, int currentRank) {
        if (getConsiderRank()) {
	    	if (parentRank != null || node.getNodeRankInteger() != null && (node.getNodeRankInteger().intValue() - currentRank > 0)) {
	            Integer classInteger = Integer.valueOf(node.getNodeRankInteger().intValue() - currentRank);
	            if (parentRank != null && parentRank.intValue() > classInteger.intValue()) {
	                classInteger = parentRank;
	            }
	            return classInteger;
	        }
        }
        return null;
    }
    public NodeDAO getNodeDAO() {
        return ((AbstractBranchOrLeafPage) getPage()).getNodeDAO();
    }
    
    public PageDAO getPageDAO() {
        return ((AbstractBranchOrLeafPage) getPage()).getPageDAO();
    }
    
    protected String getCachedOutput() {
        return getCacheAccess().getTaxonListForPage(((AbstractBranchOrLeafPage) getPage()).getTolPage()); 
    }
    
    protected void setCachedOutput(String value) {
        getCacheAccess().setTaxonListForPage(((AbstractBranchOrLeafPage) getPage()).getTolPage(), value);
    }
}
