/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.misc.TitleIllustrationVersionPicker;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditTitleIllustrations extends AbstractPageEditingPage implements PageBeginRenderListener, IPrimaryKeyConvertor, 
	IExternalPage, EditIdPage, PageInjectable, ImageInjectable, MiscInjectable {
    public static final int TOL_DEFAULT_SELECTION = 1;
    public static final int BRANCH_DEFAULT_SELECTION = 2;
    public static final int CUSTOM_SELECTION = 0;
     
    @InjectObject("spring:titleIllustrationVersionPicker")
    public abstract TitleIllustrationVersionPicker getTitleIllustrationVersionPicker();
    public abstract Integer getMoveLeftIndex();
    public abstract Integer getMoveRightIndex();
    public abstract Integer getAddIndex();
    public abstract void setAddIndex(Integer value);
    public abstract TitleIllustration getCurrentIllustration();
    public abstract void setBranchDefault(int value);
    public abstract int getBranchDefault();
    public abstract boolean getTillusSubmitSelected();
    public abstract void setTillusSubmitSelected(boolean tillusSubmitSelected);    
    public void pageValidate(PageEvent event) {}

    public void pageBeginRender(PageEvent event) {
        MappedPage page = getTolPage();
        if (page != null) {
	        //int branchDefaultHeight = getWorkingPageDAO().getTitleIllustrationBranchDefaultHeight(page);
	        //setBranchDefault(branchDefaultHeight);
	        if (page.getTitleIllustrations().size() == 0) {
	            page.setTitleIllustrationHeight(TOL_DEFAULT_SELECTION);
	            page.setPrintImageData(true);
	            page.setPrintCustomCaption(false);
	        }
	        initVersions();
        }
    }
    
    public void doSave(IRequestCycle cycle) {
        super.doSave(cycle);
        // in case we are navigating in the new system
        if (getOtherEditPageName() != null || getSubmitSelected()) {
            return;
        }
        Integer moveLeftIndex = getMoveLeftIndex();
        Integer moveRightIndex = getMoveRightIndex();
        Integer deleteIndex = getDeleteIndex();
        Integer addIndex = getAddIndex();
        if (moveLeftIndex != null) {
            doSwap(moveLeftIndex.intValue(), true);
        } else if (moveRightIndex != null) {
            doSwap(moveRightIndex.intValue(), false);
        } else if (deleteIndex != null) {
            removeIllustration();
        } else if (addIndex != null) {
            ArticleNoteImageSearch page = (ArticleNoteImageSearch) cycle.getPage("ArticleNoteImageSearch");
            page.setPageId(getNode().getNodeId());
            page.setIsPageSearch(false);
            page.setGroupName(getTolPage().getGroupName());
            page.setReturnPageName(getPageName());
            cycle.activate(page);
        } else {
            MappedPage page = getTolPage();
            if (page.getTitleIllustrationHeight() == TOL_DEFAULT_SELECTION || 
            		page.getTitleIllustrationHeight() == BRANCH_DEFAULT_SELECTION) {
                getTitleIllustrationVersionPicker().adjustVersionsForPage(page);
            }
        }
        savePage();
    }
    
    private void doSwap(int index, boolean moveLeft) {
        getReorderHelper().doSwap(index, moveLeft, getTolPage().getTitleIllustrations());
    }
    
    private void removeIllustration() {
        getReorderHelper().removeObject(getDeleteIndex().intValue(), getTolPage().getTitleIllustrations());
    }
    @SuppressWarnings("unchecked")
    public void addTitleIllustration(List imageVersions) {
    	Integer index = getAddIndex();
    	if (index == null) {
    		index = getTolPage().getTitleIllustrations().size() - 1;
    	}
    	addTitleIllustrationToPage(getTolPage(), imageVersions, index, getWorkingPageDAO());
        setAddIndex(null);
    }
    
    /**
     * Moved from engine to here
     * @param page
     * @param imageVersions
     * @param index
     * @param pageDAO
     */
    @SuppressWarnings("unchecked")
    public void addTitleIllustrationToPage(MappedPage page, Collection imageVersions, int index, PageDAO pageDAO) {
        ImageVersion version;
        version = getTitleIllustrationVersionPicker().getVersionWithHeight(imageVersions, getTolDefaultTillusHeight(page));
        if (version == null) {
            // In this case it couldn't find an autogenerated version
            // matching the height, so pick whatever we find.
            version = (ImageVersion) imageVersions.iterator().next();
        }
        SortedSet ills = page.getTitleIllustrations();

        TitleIllustration ill = new TitleIllustration();
        ill.setVersionId(version.getVersionId());
        ill.setVersion(version);
        ArrayList illList = new ArrayList(ills);
        illList.add(index + 1, ill);
        int currentOrder = 0;
        ills.clear();
        // Reset the orders so that things are happy in the set world
        for (Iterator iter = illList.iterator(); iter.hasNext();) {
            TitleIllustration nextIll = (TitleIllustration) iter.next();
            nextIll.setOrder(currentOrder++);
            ills.add(nextIll);
        }
        page.setTitleIllustrations(ills);
        pageDAO.savePage(page);    	
    }  
    public int getTolDefaultTillusHeight(MappedPage page) {
    	return getTolDefaultTillusHeight(page.getMappedNode());
    }
    public int getTolDefaultTillusHeight(MappedNode node) {
	    if (node.getIsLeaf()) {
	        return TitleIllustration.LEAF_TILLUS_DEFAULT_HEIGHT;
	    } else {
	        return TitleIllustration.BRANCH_TILLUS_DEFAULT_HEIGHT;
	    }    	    	
    }    
    // -- end move
    
    public boolean getCanMoveLeft() {
        return getReorderHelper().getCanMoveLeft(getTolPage().getTitleIllustrations(), getIndex());
    }
    
    public boolean getCanMoveRight() {
        return getReorderHelper().getCanMoveRight(getTolPage().getTitleIllustrations(), getIndex());
    }
    
    public boolean getCanMoveBoth() {
        return getReorderHelper().getCanMoveBoth(getTolPage().getTitleIllustrations(), getIndex());
    }
    
    public void tillusPublicationSubmit(IRequestCycle cycle) {
        setTillusSubmitSelected(true);
    }    
    
	/**
	 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getPrimaryKey(java.lang.Object)
	 */
	public Object getPrimaryKey(Object objValue) {
		return Integer.valueOf(((TitleIllustration) objValue).getOrder());
	}

	/**
	 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object getValue(Object objPrimaryKey) {
	    Integer order = (Integer) objPrimaryKey;
	    for (Iterator iter = getTolPage().getTitleIllustrations().iterator(); iter.hasNext();) {
            TitleIllustration ill = (TitleIllustration) iter.next();
            Integer nextOrder = Integer.valueOf(ill.getOrder());
            if (nextOrder.equals(order)) {
                return ill;
            }
        }
		return null;
	} 
	
	public IPrimaryKeyConvertor getConvertor() {
		return this;
	}    
	
	@SuppressWarnings("unchecked")
    public StringPropertySelectionModel getCurrentSelectionModel() {
        NodeImage img = getCurrentIllustration().getImage();
        ArrayList versionNames = new ArrayList();
        for (Iterator iter = img.getVersionsSet().iterator(); iter.hasNext();) {
            ImageVersion version = (ImageVersion) iter.next();
            versionNames.add(version.getVersionName());
        }
        return getPropertySelectionFactory().createModelFromList(versionNames); 
    }
    
    public String getVersionName() {
        return getCurrentIllustration().getVersion().getVersionName();
    }
    
    @SuppressWarnings("unchecked")
    public void setVersionName(String value) {
        NodeImage img = getCurrentIllustration().getImage();
        for (Iterator iter = img.getVersionsSet().iterator(); iter.hasNext();) {
            ImageVersion nextVersion = (ImageVersion) iter.next();
            if (nextVersion.getVersionName().equals(value)) {
                getCurrentIllustration().setVersion(nextVersion);
            }
        }
    }
	
	public boolean getNoBranchDefaultSet() {
	    int defaultHeight = getBranchDefault();
	    return defaultHeight <= 0;
	}
	
	@SuppressWarnings("unchecked")
	public boolean getDifferentVersionHeights() {
	    int pageHeight = getTolPage().getTitleIllustrationHeight();
	    if (pageHeight != CUSTOM_SELECTION) {
	        return false;
	    } else {
	        int currentHeight = 0;
	        for (Iterator iter = getTolPage().getTitleIllustrations().iterator(); iter.hasNext();) {
                TitleIllustration nextIllustration = (TitleIllustration) iter.next();
                if (currentHeight == 0) {
                    currentHeight = nextIllustration.getVersion().getHeight().intValue();
                } else {
                    if (nextIllustration.getVersion().getHeight().intValue() != currentHeight) {
                        return true;
                    }
                }
            }
	        return false;
	    }
	}
	
	protected void doAdditionalPageProcessing(MappedPage page) {
        //int branchDefaultHeight = getWorkingPageDAO().getTitleIllustrationBranchDefaultHeight(page);
        //setBranchDefault(branchDefaultHeight);
        if (page.getTitleIllustrations().size() == 0) {
            page.setTitleIllustrationHeight(TOL_DEFAULT_SELECTION);
            page.setPrintImageData(true);
            page.setPrintCustomCaption(false);
        }
        initVersions();	    
	}
	
	public String getBranchDefaultString() {
	    if (getNoBranchDefaultSet()) {
	        return "(Disabled -- no default set)";
	    } else {
	        return "(" + getBranchDefault() + " high)";
	    }
	}
	
	@SuppressWarnings("unchecked")
    private void initVersions() {
        for (Iterator iter = getTolPage().getTitleIllustrations().iterator(); iter.hasNext();) {
            TitleIllustration ill = (TitleIllustration) iter.next();
            NodeImage img = ill.getVersion().getImage();
            if (img.getVersionsSet() == null || img.getVersionsSet().size() == 0) {
                img.setVersionsSet(new TreeSet(getImageDAO().getUsableVersionsForImage(img)));
            }
        }        
    }

    
    public int getTolDefaultHeight() {
    	return getTolDefaultTillusHeight(getNode());
    }
}
