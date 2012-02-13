/*
 * Created on Jul 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.callback.ExternalCallback;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Student;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.misc.CacheAccess;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.treehouse.injections.BetaTreehouseInjectable;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ViewTreehouse extends CacheAndPublicAwarePage implements IExternalPage, PageBeginRenderListener,
		BetaTreehouseInjectable, BaseInjectable {
    private static final String DAO_KEY = "dao";
	public abstract void setTreehouse(MappedAccessoryPage page);
	public abstract MappedAccessoryPage getTreehouse();
	public abstract void setJavascriptVars(String value);
	public abstract void setIsMinorAuthor(boolean value);
	public abstract void setMinorAuthor(Student value);
	public abstract Student getMinorAuthor();
	public abstract void setIsFromPreview(boolean value);
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		super.activateExternalPage(parameters, cycle);
		String searchUrl = getSearchURL(cycle);
		try {
		    Object param = parameters[0];
			Long accessoryId = null;
			if (param instanceof Integer) {
			    accessoryId = Long.valueOf(((Integer) param).longValue());
			} else if (param instanceof Long) {
			    accessoryId = (Long) param;
			}
			boolean isBeta = false;
			if (parameters.length > 1) {
			    Object nextParam = parameters[1];
			    if (String.class.isInstance(nextParam)) {
				    String beta = (String) nextParam;
				    if (beta != null && beta.equals("beta")) {
				        isBeta = true;
				        setIsFromPreview(true);
				    }
			    } else {
			        setIsFromPreview(true);
			    }
			}
			AccessoryPageDAO dao;
			if (getIsWorking()) {
			    if (!isBeta) {
			        dao = getWorkingAccessoryPageDAO();
			    } else {
			        dao = getBetaAccPageDAO();
			    }
			} else {
			    dao = getPublicAccessoryPageDAO();
			}
			cycle.setAttribute(DAO_KEY, dao);
			MappedAccessoryPage page = dao.getAccessoryPageWithId(accessoryId);
			checkForRedirect(page, cycle, parameters);
			if (page == null) {
			    // trigger the handler
			    throw new RuntimeException();
			}
			setTreehouse(page);
		} catch (RedirectException e1) {
		    throw (e1);
		} catch (Exception e) {
		    throw new RedirectException(searchUrl);
		}
	}
    
    protected boolean checkForRedirect(MappedAccessoryPage page, IRequestCycle cycle, Object[] parameters) {
        if (page != null) {
            // Check to see if the page passed-in is the same as the actual treehouse.
            // If it isn't, then we are looking at a view portfolio page.  If we are
            // viewing a separate portfolio, we need to forward to the viewintroduction
            // page for that portfolio
            boolean isPortfolioView = getTreehouse() != null && 
                page.getAccessoryPageId().intValue() != getTreehouse().getAccessoryPageId().intValue();
            
            if (page.getIsWebquest() && !getPageName().contains("Webquest")) {
                doExternalCallback("WebquestViewIntroduction", cycle, parameters);
                return true;
            } else if (page.getTreehouseType() == MappedAccessoryPage.PORTFOLIO && (!getPageName().contains("Portfolio") || isPortfolioView)) {
                doExternalCallback("PortfolioViewIntroduction", cycle, parameters);
                return true;
            } else if (page.getIsTeacherResourceInstance() && ((TeacherResource) page).getResourceType() != TeacherResource.OTHER && 
                    !getPageName().contains("TeacherResource")) {
                doExternalCallback("TeacherResourceMain", cycle, parameters);
                return true;
            }
        }
        return false;
    }
	
	protected AccessoryPageDAO getDAO() {
	    return (AccessoryPageDAO) getRequestCycle().getAttribute(DAO_KEY);
	}
	
	private void doExternalCallback(String pageName, IRequestCycle cycle, Object[] parameters) {
	    IExternalPage page = (IExternalPage) cycle.getPage(pageName);
	    ExternalCallback callback = new ExternalCallback(page, parameters);
	    callback.performCallback(cycle);	    
	}
	
	public void pageBeginRender(PageEvent event) {
	    super.pageBeginRender(event);
	    Student stud = findMinorAuthor(getActualTreehouse());
	    if (stud != null) {
	        setIsMinorAuthor(true);
	        setMinorAuthor(stud);
	    }
	}
	
	public MappedAccessoryPage getActualTreehouse() {
	    return getTreehouse();
	}
	
	protected Student findMinorAuthor(MappedAccessoryPage page) {
	    return (page != null) ? page.getMinorAuthor() : null;
	}
		
	public String getMainText() {
	    return getPreparedText(getTreehouse().getText(), CacheAccess.ACCESSORY_PAGE_TEXT_CACHE);
	}
	
	protected String getPreparedText(String text) {
	    return getPreparedText(text, (byte) -1);
	}
	
	@SuppressWarnings("unchecked")
	protected String getPreparedText(String text, byte cacheKey) {
	    TextPreparer preparer = getTextPreparer();
	    boolean useCache = cacheKey > 0 ? getUseCache() : false;
	    List result = preparer.prepareText(text, cacheKey, getTreehouse(), useCache, getUseGlossary());
	    String resultText = (String) result.get(0);
	    if (getUseGlossary()) {
	        String javascript = (String) result.get(1);
	        setJavascriptVars(javascript);
	    }
		return resultText;	    
	}
	
	public String getTreehouseHeadlineClass() {
	    return getHeadlineClassForPage(getTreehouse());
	}
	
	protected String getHeadlineClassForPage(MappedAccessoryPage treehouse) {
	    byte treehouseType = (treehouse != null) ? treehouse.getTreehouseType() : -1;
	    if (getTreehouse().getIsWebquest()) {
	        return "webquest";
	    } else {
		    switch (treehouseType) {
		    	case MappedAccessoryPage.ARTANDCULTURE: return "artculture";
		    	case MappedAccessoryPage.BIOGRAPHY: return "bio";
		    	case MappedAccessoryPage.GAME: return "games";
		    	case MappedAccessoryPage.INVESTIGATION: return "investigation";
		    	case MappedAccessoryPage.STORY: return "stories";
		    	case MappedAccessoryPage.TEACHERRESOURCE: return "teacherresource";
		    	default: return "";
		    }
	    }	    
	}
	
	public String getTreehouseBodyId() {
	    return (getTreehouse() != null) ? getBodyIdForPage(getTreehouse()) : null;
	}
	
	protected String getBodyIdForPage(MappedAccessoryPage treehouse) {
	    byte treehouseType = (treehouse != null) ? treehouse.getTreehouseType() : -1;
	    switch (treehouseType) {
	    	case MappedAccessoryPage.ARTANDCULTURE: return "trarttheme";
	    	case MappedAccessoryPage.BIOGRAPHY: return "trbiotheme";
	    	case MappedAccessoryPage.GAME: return "trgametheme";
	    	case MappedAccessoryPage.INVESTIGATION: return "trinvestheme";
	    	case MappedAccessoryPage.STORY: return "trstorytheme";
	    	case MappedAccessoryPage.TEACHERRESOURCE: return "trteachtheme";	    
	    	default: return null;
	    }	    
	}
	
	public String getPageTitleClass() {
	    if (getTreehouse().getIsTreehouse()) {
	        return "trhstitle";
	    } else {
	        return "nosub";
	    }
	}
	@SuppressWarnings("unchecked")
	public Collection getContributors() {
	    return getTreehouse().getContributors();
	}
	
	public String getPublicURL() {
	    return getUrlBuilder().getPublicURLForObject(getTreehouse());
	}
	
	public String getProjectName() {
	    if (getMinorAuthor() != null) {
	        return getMinorAuthor().getProject().getName();
	    } else {
	        return null;
	    }
	}
	public Long getProjectId() {
		if (getMinorAuthor() != null) {
			return getMinorAuthor().getProject().getProjectId();
		} else {
			return null;
		}
	}
    public Block getPageContentMenuBlock() {
        return (Block) getComponents().get("contentMenuBlock");
    }
    
    public boolean getCanEdit() {
        boolean isWorking = getIsWorking();
        boolean isArticleNote = !getTreehouse().getIsTreehouse();
        if (isWorking && getContributor() != null && isArticleNote) {
            return getPermissionChecker().checkEditingPermissionForPage(getContributor(), getActualTreehouse());
        } else {
            return false;
        }
    }
}
