/*
 * Created on Jun 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.Portfolio;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.PortfolioSection;
import org.tolweb.misc.ReorderHelper;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditPortfolio extends AbstractTreehouseEditingPage implements PageBeginRenderListener, 
		PageInjectable, MiscInjectable {
    public abstract Integer getNumNewFields();
    public abstract void setNumNewFields(Integer value);
    public abstract PortfolioSection getCurrentSection();
    public abstract PortfolioPage getCurrentPage();
    public abstract int getSectionIndex();
    public abstract int getPageIndex();
    public abstract String getMoveUpPageIndex();
    public abstract String getMoveDownPageIndex();
    public abstract String getDeletePageIndex();
    public abstract Integer getMoveUpSectionIndex();
    public abstract Integer getMoveDownSectionIndex();
    public abstract Integer getDeleteSectionIndex();    
    public abstract PortfolioSection getSectionToAddPageTo();
    public abstract void setSectionToAddPageTo(PortfolioSection section);
    public abstract PortfolioSection getSavedSection();
    public abstract void setSavedSection(PortfolioSection section);
    public abstract PortfolioPage getPageToAddImageTo();
    public abstract void setPageToAddImageTo(PortfolioPage value);
    public abstract PortfolioPage getPageToDeleteImageFrom();
    public abstract PortfolioPage getSavedPage();
    public abstract void setSavedPage(PortfolioPage value);    
    
    public static final String PROGRESS_PROPERTY = "portfolioProgress";
    private static final String SEPARATOR = ",";
    private static final int MOVE_UP = 0;
    private static final int MOVE_DOWN = 1;
    private static final int DELETE = 2;
    
    public int getStepNumber() {
        if (getIsTeacherResource() && !getIsOther()) {
            return 7;
        } else {
            return 6;
        }
    }
    
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}
	
	@SuppressWarnings("unchecked")
	public boolean doAdditionalFormProcessing(IRequestCycle cycle) {
	    if (getNumNewFields() != null) {
	        // Go ahead and create a few new sections.
		    ReorderHelper helper = getReorderHelper();
		    SortedSet sections = getTreehouse().getPortfolio().getSections();
		    int currentSize = sections.size();
		    int numSections = getNumNewFields().intValue();
		    for (int i = 0; i < numSections; i++) {
		        PortfolioSection newSection = constructNewSection();
		        helper.addToSet(sections, newSection);
		    }
		    String anchorName = getSectionAnchorName(currentSize); 
		    setSelectedAnchor(anchorName);	
		    setNumNewFields(null);
	    } else if (getMoveUpPageIndex() != null) {
	        movePageUp();
	    } else if (getMoveDownPageIndex() != null) {
	        movePageDown();
	    } else if (getDeletePageIndex() != null) {
	        deletePage();
	    } else if (getMoveUpSectionIndex() != null) {
	        moveSectionUp();
	    } else if (getMoveDownSectionIndex() != null) {
	        moveSectionDown();
	    } else if (getDeleteSectionIndex() != null) {
	        deleteSection();
	    } else if (getSectionToAddPageTo() != null) {
	        setSavedSection(getSectionToAddPageTo());
	        setOtherEditPageName("TreehousePortfolioSearchPage");
	    } else if (getPageToAddImageTo() != null) {
	        setSavedPage(getPageToAddImageTo());
            IPage searchPage = cycle.getPage("PortfolioPageImageSearch");
            PropertyUtils.write(searchPage, "returnPageName", getPageName());
            throw new PageRedirectException(searchPage);
        } else if (getPageToDeleteImageFrom() != null) {
            PortfolioPage page = getPageToDeleteImageFrom();
            page.setImageId(null);
        }
	    return true;
	}
	
	@SuppressWarnings("unchecked")
	public void doSave() {
	    if (getPortfolio() != null) {
		    // create a new set to force a delete followed by a save -- hibernate issue
		    getPortfolio().setSections(new TreeSet(getPortfolio().getSections()));
		    // set all the page collections to new instances to do the same for the portfolio pages
		    for (Iterator iter = getPortfolio().getSections().iterator(); iter.hasNext();) {
	            PortfolioSection section = (PortfolioSection) iter.next();
	            section.setPages(new TreeSet(section.getPages()));
	        }
	    }
	    super.doSave();
	}
	
	protected void goToOtherEditPageName(IRequestCycle cycle) {
	    if (getOtherEditPageName() != null && getOtherEditPageName().equals("TreehousePortfolioSearchPage")) { 
	        TreehousePortfolioSearchPage searchPage = (TreehousePortfolioSearchPage) cycle.getPage("TreehousePortfolioSearchPage");
	        searchPage.setReturnPageName(getPageName());
	        searchPage.setDontCheckPermissions(!getCheckPermissionsForSearch());
	        searchPage.setEditedObjectType(getEditTypeName());
	        cycle.activate(searchPage);
	    } else {
	        super.goToOtherEditPageName(cycle);
	    }
	}
	
	protected boolean getCheckPermissionsForSearch() {
	    return true;
	}
	
	protected String getEditTypeName() {
	    return "Portfolio";
	}
	
	public String getSectionAnchorName(int index) {
	    return "linksection" + index;
	}
	
	public String getPageAnchorName(int sectionIndex, int pageIndex) {
	    return "section" + sectionIndex + "page" + pageIndex;
	}
	
	private void moveSectionUp() {
	    ReorderHelper helper = getReorderHelper();
	    int index = getMoveUpSectionIndex().intValue();
	    helper.doSwap(index, true, getPortfolio().getSections());
	    setSelectedAnchor(getSectionAnchorName(index - 1));
	}
	
	private void moveSectionDown() {
	    ReorderHelper helper = getReorderHelper();
	    int index = getMoveDownSectionIndex().intValue();
	    helper.doSwap(index, false, getPortfolio().getSections());
	    setSelectedAnchor(getSectionAnchorName(index + 1));
	}
	
	private void deleteSection() {
	    ReorderHelper helper = getReorderHelper();
	    int index = getDeleteSectionIndex().intValue();
	    helper.removeObject(index, getPortfolio().getSections());
	    setSelectedAnchor(getSectionAnchorName(index - 1));
	}
	
	private void movePageUp() {
	    doSectionAndPageWork(getMoveUpPageIndex(), MOVE_UP);
	}
	
	private void movePageDown() {
	    doSectionAndPageWork(getMoveDownPageIndex(), MOVE_DOWN);
	}
	
	private void deletePage() {
	    doSectionAndPageWork(getDeletePageIndex(), DELETE);
	}
	
	/**
	 * decodes the index to locate the correct section and page to work on
	 * @param index
	 * @param opType TODO
	 * @return
	 */
	private void doSectionAndPageWork(String index, int opType) {
	    String[] pieces = index.split(SEPARATOR);
	    // First index is the section index
	    int sectionIndex = Integer.parseInt(pieces[0]);
	    ReorderHelper helper = getReorderHelper();
	    PortfolioSection section = (PortfolioSection) helper.getObject(sectionIndex, getPortfolio().getSections());
	    int pageIndex = Integer.parseInt(pieces[1]);
	    String selectedAnchor = "";
	    if (opType == MOVE_UP) {
	        helper.doSwap(pageIndex, true, section.getPages());
	        pageIndex--;
	        selectedAnchor = getPageAnchorName(sectionIndex, pageIndex);
	    } else if (opType == MOVE_DOWN) {
	        helper.doSwap(pageIndex, false, section.getPages());
	        pageIndex++;
	        selectedAnchor = getPageAnchorName(sectionIndex, pageIndex);
	    } else if (opType == DELETE) {
	        helper.removeObject(pageIndex, section.getPages());
	        if (section.getPages().size() == 0) {
	            selectedAnchor = getSectionAnchorName(sectionIndex);
	        } else {
	            pageIndex = pageIndex == 0 ? 0 : pageIndex - 1;
	            selectedAnchor = getPageAnchorName(sectionIndex, pageIndex);
	        }
	    }
	    setSelectedAnchor(selectedAnchor);
	}
	
	private PortfolioSection constructNewSection() {
	    PortfolioSection section = new PortfolioSection();
	    section.setTitle("New portfolio section");
	    return section;
	}
    
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
    }
    
    public Portfolio getPortfolio() {
        return getTreehouse().getPortfolio();
    }
    
    @SuppressWarnings("unchecked")
    public boolean getHasAnyPages() {
        SortedSet sections = getPortfolio().getSections(); 
        return sections.size() > 1 || ((PortfolioSection) sections.first()).getPages().size() > 0;
    }
    
    public boolean getPageCanMoveUp() {
        return getReorderHelper().getCanMoveLeft(getCurrentSection().getPages(), getPageIndex());
    }
    
    public boolean getPageCanMoveDown() {
        return getReorderHelper().getCanMoveRight(getCurrentSection().getPages(), getPageIndex());
    }
    
    public boolean getPageCanMoveBoth() {
        return getReorderHelper().getCanMoveBoth(getCurrentSection().getPages(), getPageIndex());
    }
    
    public boolean getSectionCanMoveUp() {
        return getReorderHelper().getCanMoveLeft(getPortfolio().getSections(), getSectionIndex());
    }
    
    public boolean getSectionCanMoveDown() {
        return getReorderHelper().getCanMoveRight(getPortfolio().getSections(), getSectionIndex());
    }
    
    public boolean getSectionCanMoveBoth() {
        return getReorderHelper().getCanMoveBoth(getPortfolio().getSections(), getSectionIndex());
    }    
    
    public String getSelectedPageTag() {
        return getSectionIndex() + SEPARATOR + getPageIndex(); 
    }
    
    public String getCurrentURL() {
        URLBuilder builder = getUrlBuilder();
        return builder.getURLForPortfolioPage(getCurrentPage());
    }
    
    public void addToPortfolio(MappedNode node, IRequestCycle cycle) {
        Long pageId = getWorkingPageDAO().getPageIdForNode(node);
        MappedPage page = getWorkingPageDAO().getPageWithId(pageId);
        PortfolioPage portPage = constructNewPortfolioPage(page.getPageId(), PortfolioPage.PAGE_DESTINATION);
        portPage.setPageTitle(node.getName());
        portPage.setPageType(node.getIsLeaf() ? "Leaf" : "Branch");        
        finishAdd(portPage, cycle);
    }
    
    public void addToPortfolio(MappedAccessoryPage page, IRequestCycle cycle) {
        PortfolioPage portPage = constructNewPortfolioPage(Long.valueOf(page.getId()), PortfolioPage.ACC_PAGE_DESTINATION);
        portPage.setPageTitle(page.getMenu());
        portPage.setPageType(page.getTreehouseTypeString());
        finishAdd(portPage, cycle);
    }
    
    public void addImageToPortfolioPage(NodeImage img, IRequestCycle cycle) {
        PortfolioPage portPage = getSavedPage();
        if (portPage != null) {
            portPage.setImageId(Integer.valueOf(img.getId()));
            doSave();
        }
        setSavedPage(null);
        cycle.activate(this);
    }
    
    private void finishAdd(PortfolioPage portPage, IRequestCycle cycle) {
        PortfolioSection section = getSavedSection();
        if (section == null) {
            section = (PortfolioSection) getPortfolio().getSections().first();
        }
        getReorderHelper().addToSet(section.getPages(), portPage);
        doSave();
        setLastPageAnchorForSection(section);
        cycle.activate(this);        
    }
    
    protected void setLastPageAnchorForSection(PortfolioSection section) {
        int index = Arrays.asList(getPortfolio().getSections().toArray()).indexOf(section);
        int pageIndex = section.getPages().size() - 1;
        setSelectedAnchor(getPageAnchorName(index, pageIndex));        
    }
    

    
    protected PortfolioPage constructNewPortfolioPage(Long pageId, int destinationType) {
        PortfolioPage portPage = new PortfolioPage();
        portPage.setDestinationId(pageId.intValue());
        portPage.setDestinationType(destinationType);
        portPage.setComments("");
        return portPage;
    }
    
    public OrderedObjectConvertor getSectionConvertor() {
        return new OrderedObjectConvertor(getPortfolio().getSections());
    }
    
    public OrderedObjectConvertor getPageConvertor() {
        return new OrderedObjectConvertor(getCurrentSection().getPages());
    }
    
    public Block getHelpBlock() {
        String blockName = "";
        if (getIsPortfolio()) {
            blockName = "portfolioHelpBlock";
        } else {
            blockName = "teacherResourceHelpBlock";
        }
        return (Block) getComponents().get(blockName);
    }    
}
