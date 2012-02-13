/*
 * Created on Jul 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.asset.ExternalAsset;
import org.apache.tapestry.components.Block;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Portfolio;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.PortfolioSection;
import org.tolweb.hibernate.SupportMaterial;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.wrappers.AbstractWrapper;
import org.tolweb.treegrow.main.StringUtils;


/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class TreehouseWrapper extends AbstractWrapper implements BaseInjectable, AccessoryInjectable {
	private static final String ONPAGE_CLASS = "onpage";
	private ExternalAsset learnmasterCss;
	@Parameter
	public abstract MappedAccessoryPage getContentTreehouse();
	@Parameter
	public abstract SupportMaterial getSupportMaterial();
	@Parameter
	public abstract String getAdditionalPageShortName();
	@Parameter
	public abstract Block getTeacherResourceBlock();
	@Parameter(defaultValue = "true")
	public abstract boolean getShowLearningInfo();
	@Parameter
	public abstract MappedAccessoryPage getTreehouse();
	@InjectState("previousTreehousePageName")
	public abstract String getPreviousTreehouseEditPageName();
	
	public ExternalAsset getLearnmasterCss() {
		if (learnmasterCss == null) {
			learnmasterCss = new ExternalAsset("/tree/css/learnmaster.css", null);
		}
		return learnmasterCss;
	}
	@SuppressWarnings("unchecked")
	public List getStylesheets() {
		ArrayList stylesheets = new ArrayList();
		stylesheets.add(getTolCssStylesheet());
		if (!getPage().getPageName().equals("ViewBonusPage")) {
			// if it's a treehouse page add the treehouse css
			stylesheets.add(getLearnmasterCss());
		}
		return stylesheets;
	}
	public String getHeadContentRegex() {
		//return ".* charset=iso-8859-1\" \\/>[^<]*(.*?)<!-- TemplateBeginEditable.*</head>";
		return "<head>(.*)</head>";
	}
	
	public String getBeforeContentRegex() {
	    return "<body>(.*?)<div id=\"maincontent\" class=\"nosb\">";
	}
	
	public MappedAccessoryPage getActualTreehouse() {
	    if (getContentTreehouse() != null) {
	        return getContentTreehouse();
	    } else {
	        return getTreehouse();
	    }
	}
	
	public String getBranchPageLinkString() {
		MappedNode attachedNode = getActualTreehouse().getPrimaryAttachedNode();
		String url = getUrlBuilder().getURLForNode(attachedNode);
	    return url;
	}
	
	public String getPageTitle() {
	    if (getSupportMaterial() == null) {
	        return getActualTreehouse().getPageTitle();
	    } else {
	        return getSupportMaterial().getTitle();
	    }
	}
	
	public String getPageMenu() {
	    if (getSupportMaterial() == null) {
	        return getActualTreehouse().getMenu();
	    } else {
	        return getSupportMaterial().getTitle();
	    }
	}
	
	public String getTitleClass() {
	    if (getTreehouse() != null && getTreehouse().getIsTreehouse()) {
	        if (getTreehouse().getIsWebquest()) {
	            return "trhstitlenosub";
	        } else {
	            if (getSupportMaterial() == null) {
	                return "trhstitle";
	            } else {
	                return "trhstitlenosubnotop";
	            }
	        }
	    } else if (getIsLeaf()) {
	        return "leaftitle";	        
	    } else {
	        return "branchtitle";
	    }
	}
	
	public String getBranchTitleClass() {
	    if (getIsLeaf()) {
	        return "leaftitle";
	    } else {
	        return "branchtitle";
	    }	    
	}
	
	public boolean getIsLeaf() {
	    MappedNode node = getTreehouse().getPrimaryAttachedNode(); 
	    return node != null && node.getIsLeaf();
	}
	
	public boolean getHasBothRefs() {
	    return StringUtils.notEmpty(getActualTreehouse().getReferences()) && 
    	(getPortfolioPage() != null && getPortfolioPage().getIncludeReferences() && 
    	        StringUtils.notEmpty(getTreehouse().getReferences()));	    
	}
	
	public boolean getHasAnyRefs() {
	    return StringUtils.notEmpty(getActualTreehouse().getReferences()) || 
	    	(getPortfolioPage() != null && getPortfolioPage().getIncludeReferences() && 
	    	        StringUtils.notEmpty(getTreehouse().getReferences()));
	}
	
	/**
	 * In this case it doesn't have both so return the valid one
	 * @return
	 */
	public String getActualRefs() {
	    if (StringUtils.notEmpty(getActualTreehouse().getReferences())) {
	        return getActualTreehouse().getReferences();
	    } else {
	        return getTreehouse().getReferences();
	    }
	}
	
	public boolean getHasAnyInternetInfo() {
	    return getActualTreehouse().getHasInternetLinks() ||
	    	(getPortfolioPage() != null && getPortfolioPage().getIncludeInternetLinks() &&
	    	        getTreehouse().getHasInternetLinks());
	}
	
	public boolean getHasBothInternetInfo() {
	    return getActualTreehouse().getHasInternetLinks() &&
	    	(getPortfolioPage() != null && getPortfolioPage().getIncludeInternetLinks() &&
	    	        getTreehouse().getHasInternetLinks());	    
	}
	@SuppressWarnings("unchecked")
	public Set getActualInternetInfo() {
	    if (getActualTreehouse().getHasInternetLinks()) {
	        return getActualTreehouse().getInternetLinks();
	    } else {
	        return getTreehouse().getInternetLinks();
	    }
	}
	
	public boolean getIsTeacherResource() {
	    return TeacherResource.class.isInstance(getTreehouse());
	}
    
    public boolean getIsPortfolioPage() {
        return getContentTreehouse() != null || getTreehouse().getTreehouseType() == MappedAccessoryPage.PORTFOLIO;
    }
   
    public boolean getIsWebquest() {
        return getTreehouse().getIsWebquest();
    }
	
	public boolean getShowPortfolioMenu() {
	    return !getIsWebquest() && (getIsPortfolioPage() || (getIsTeacherResource() && ((TeacherResource) getTreehouse()).getHasPortfolio()));
	}
	
	public String getWebquestIntroClass() {
	    return getLiClass("WebquestViewIntroduction");
	}
	
	public String getWebquestTaskClass() {
	    return getLiClass("WebquestViewTask");
	}
	
	public String getWebquestProcessClass() {
	    return getLiClass("WebquestViewProcess");
	}
	
	public String getWebquestEvaluationClass() {
	    return getLiClass("WebquestViewEvaluation");
	}
	
	public String getWebquestConclusionClass() {
	    return getLiClass("WebquestViewConclusion");
	}
	
	public String getWebquestTeacherClass() {
	    return getLiClass("WebquestViewTeacher");
	}
	
	public boolean getHasAnyLearningInfo() {
	    if (getTreehouse().getIsTreehouse()) {
	        if (getPage().getPageName().contains("Webquest")) {
	            return false;
	        } else {
	            return getActualTreehouse().getHasAnyLearningInfo() ||
	            	(getPortfolioPage() != null && getPortfolioPage().getIncludeLearningInfo() &&
	            	        getTreehouse().getHasAnyLearningInfo());
	        }
	    } else {
	        return false;
	    }
	}
	
	public boolean getHasBothLearningInfo() {
        return getActualTreehouse().getHasAnyLearningInfo() &&
    		(getPortfolioPage() != null && getPortfolioPage().getIncludeLearningInfo() &&
    	        getTreehouse().getHasAnyLearningInfo());	    
	}
	
	public MappedAccessoryPage getLearningInfoTreehouse() {
	    if (getActualTreehouse().getHasAnyLearningInfo()) {
	        return getActualTreehouse();
	    } else {
	        return getTreehouse();
	    }
	}
	
	/**
	 * Assembles a list of 2-element arrays in order to get the special
	 * treehouse formatting necessary for the 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getPortfolioTreehouses() {
	    ArrayList treehouses = new ArrayList();
	    Portfolio port = getTreehouse().getPortfolio();
	    for (Iterator iter = port.getSections().iterator(); iter.hasNext();) {
            PortfolioSection section = (PortfolioSection) iter.next();
            for (Iterator iterator = section.getPages().iterator(); iterator.hasNext();) {
                PortfolioPage page = (PortfolioPage) iterator.next();
                if (page.getDestinationType() == PortfolioPage.ACC_PAGE_DESTINATION) {
                    Object[] treehouseInfo = new Object[3];
                    treehouseInfo[0] = page.getPageTitle();
                    treehouseInfo[1] = Integer.valueOf(page.getDestinationId());
                    treehouseInfo[2] = Byte.valueOf(page.getTreehouseType());
                    treehouses.add(treehouseInfo);
                }
            }
        }
	    return treehouses;
	}
	
	public PortfolioPage getPortfolioPage() {
	    try {
	        return ((PortfolioViewPage) getPage()).getPortfolioPage();
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	public String getH1WebquestTRId() {
	    if (getTreehouse().getIsWebquest()) {
	        return "rsctypetitlewsub";
	    } else {
	        return "rsctypetitletch";
	    }
	}
	
	private String getLiClass(String pageName) {
	    if (getPage().getPageName().equals(pageName)) {
	        return ONPAGE_CLASS;
	    } else {
	        return null;
	    }
	}
    
    public void returnToTreehouseEditing(IRequestCycle cycle) { 
        String previousTreehouseEditPageName = getPreviousTreehouseEditPageName(); 
        if (StringUtils.isEmpty(previousTreehouseEditPageName)) {
            previousTreehouseEditPageName = "TreehouseEditor";
        }
        cycle.activate(previousTreehouseEditPageName);
    }
    
    public boolean getExistsInPublic() {
    	AccessoryPageDAO publicDAO = getPublicAccessoryPageDAO();
    	return publicDAO.getAccessoryPageExistsWithId(getActualTreehouse().getAccessoryPageId());
    }
}
