package org.tolweb.tapestry;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.URLBuilder;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class LinkedPages extends BaseComponent {
	
    private static final String THISPAGE = "thispage";
	public abstract MappedNode getNode();
    public abstract void setNode(MappedNode value);
    @SuppressWarnings("unchecked")
	public abstract void setNotes(List value);
    @SuppressWarnings("unchecked")
	public abstract void setArticles(List value);
    @SuppressWarnings("unchecked")
	public abstract void setTreehouses(List value);
    @SuppressWarnings("unchecked")
	public abstract List getTreehouses();
	public abstract Object[] getCurrentNote();
	public abstract Object[] getCurrentArticle();	
	public abstract String getTitle();
	public abstract void setIsThisPage(boolean value);
	public abstract boolean getIncludeLinkPrefix();
	public abstract void setLinkPrefixString(String value);
	public abstract boolean getIsImageGallery();
	public abstract boolean getIsMovieGallery();
	@InjectObject("spring:urlBuilder")
	public abstract URLBuilder getUrlBuilder();
	
	public void prepareForRender(IRequestCycle cycle) {
	    super.prepareForRender(cycle);
	    if (getNode() != null) {
	        setArticles(getAccessoryPageDAO().getArticlesForNode(getNode()));
	        setNotes(getAccessoryPageDAO().getNotesForNode(getNode()));
	        setTreehouses(getAccessoryPageDAO().getTreehousesForNode(getNode()));
	    } 
	    String linkPrefixString = getIncludeLinkPrefix() ? "http://tolweb.org" : "";
	    setLinkPrefixString(linkPrefixString);
	}

	@SuppressWarnings("unchecked")
	public boolean getHasNotes() {
		boolean hasNotes;
		AccessoryPageDAO dao = getAccessoryPageDAO();
	    List pages = dao.getNotesForNode(getNode());
	    if (pages != null && pages.size() > 0) {
	        setNotes(pages);
	        hasNotes = true;
	    } else {
	        hasNotes = false;
	    }				
		if (((CacheAndPublicAwarePage) getPage()).getIsWorking()) {
			return true;
		} else {
			return hasNotes;
		}
	} 
	
	public boolean getShowTreehousesMenu() {
		return getTreehouses() != null && getTreehouses().size() > 0;
	}
	
	public String getLiArticleClass() {
	    return getLiBonusPageClass(getCurrentArticle());
	}
	
	public String getLiNoteClass() {
	    return getLiBonusPageClass(getCurrentNote());
	}
	
	public String getLiBonusPageClass(Object[] currentPage) {
	    if (currentPage[0].equals(getTitle())) {
	        setIsThisPage(true);
	        return THISPAGE;
	    } else {
	        setIsThisPage(false);	        
	        return null;
	    }
	}	
	
	public String getLiImageClass() {
		return  getIsImageGallery() ? THISPAGE : null;
	}
	public String getLiMovieClass() {
		return getIsMovieGallery() ? THISPAGE : null;
	}
	public String getLiPeopleClass() {
		return getIsPeopleList() ? THISPAGE : null;
	}
	public boolean getIsPeopleList() {
		return PeopleList.class.isInstance(getPage());
	}
	
	public AccessoryPageDAO getAccessoryPageDAO() {
	    return (AccessoryPageDAO) getPage().getRequestCycle().getAttribute(CacheAndPublicAwarePage.ACC_PAGE_DAO);	    
	}
	
	public String getBranchOrLeafString() {
	    if (getNode() != null && getNode().getIsLeaf()) {
	        return "Leaf";
	    } else {
	        return "Branch";
	    }
	}
	
	public String getCurrentArticleURL() {
		Number articleId = (Number) getCurrentArticle()[1];		
		if (getIncludeLinkPrefix()) {
			return getUrlBuilder().getPublicURLForArticle(articleId.intValue());
		} else {
			return getUrlBuilder().getURLForArticle(articleId.intValue());
		}
	}
	
	public String getCurrentNoteURL() {
		Number noteId = (Number) getCurrentNote()[1];
		if (getIncludeLinkPrefix()) {
			return getUrlBuilder().getPublicURLForNote(noteId.intValue());
		} else {
			return getUrlBuilder().getURLForNote(noteId.intValue());
		}
 	}
	
	public String getPageURL() {
		if (getIncludeLinkPrefix()) {
			return getUrlBuilder().getPublicURLForObject(getNode());
		} else {
			return getUrlBuilder().getURLForObject(getNode());
		}
	}
	
	public String getActualPageTitle() {
	    return (getNode() != null) ? getNode().getActualPageTitle(false, false, true) : "";
	}
	public int getMovieClassInt() {
		return NodeImage.MOVIE;
	}
	public int getImageClassInt() {
		return NodeImage.IMAGE;
	}
	
	public String getMoviesUrl() {
		return getRelativeMediaUrl(NodeImage.MOVIE);
	}

	public String getImagesUrl() {
		return getRelativeMediaUrl(NodeImage.IMAGE);
	}	
	
	private String getRelativeMediaUrl(int mediaType) {
		String groupName = StringEscapeUtils.escapeHtml(getActualPageTitle());
		Long nodeId = getNode().getNodeId();
		String mediaName = (mediaType == NodeImage.MOVIE) ? "movies" : "images";
		return String.format("/%1$s/%2$s/%3$d", mediaName, groupName, nodeId);
	}
}
