/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreeStructureTip extends BaseComponent implements BaseInjectable {
    @Parameter
	public abstract boolean getIsNote();
    @Parameter    
    public abstract boolean getIsArticle();
    @Parameter
    public abstract boolean getIsTreehouse();
    @Parameter
    public abstract boolean getIsBranch();
    @Parameter
    public abstract boolean getIsLeaf();
    @Parameter
    public abstract boolean getAttachmentIsBranch();
    @Parameter
    public abstract boolean getAttachmentIsLeaf();

	
	public String getPageTypeImageLocation() {
	    if (getAttachmentIsLeaf()) {
	        return getUrlBuilder().getAssetUrlString("img/BonusLeaf.gif");
	    } else if (getAttachmentIsBranch()){
	        return getUrlBuilder().getAssetUrlString("img/BonusBranch.gif");
	    } else if(getIsBranch()) {
	        return getUrlBuilder().getAssetUrlString("img/lilbranch.gif");
	    } else {
	        return getUrlBuilder().getAssetUrlString("img/lilleaf.gif");
	    }
	}
	
	public String getImageClass() {
	    if (getIsTreehouse() || getIsNote() || getIsArticle()) {
	        return "bonus";
	    } else {
	        return "branchleaf";
	    }
	}
	
	/**
	 * we want to display the branch icon for branches,
	 * but we want a different text explanation
	 * @return
	 */
	public boolean getIsBranchPage() {
		return getIsBranch() && !getIsCollectionPage();
	}
	/**
	 * we want to display the leaf icon for leaves,
	 * but we want a different text explanation
	 * @return
	 */	
	public boolean getIsLeafPage() {
		return getIsLeaf() && !getIsCollectionPage();
	}
	private boolean getIsCollectionPage() {
		return getIsImageGallery() || getIsPeopleList();
	}
	
	
	/*public String getPageTypeString() {
	    if (getIsTreehouse()) {
	        return "Treehouse";
	    } else if (getIsBonus()) {
	        return "Bonus";
	    } else if (getIsBranch()) {
	        return "Tree of Life Branch";
	    } else {
	        return "Tree of Life Leaf";
	    }
	}*/
	
	public String getAttachmentPageType() {
	    if (getAttachmentIsLeaf()) {
	        return "leaf";
	    } else {
	        return "branch";
	    }
	}	
	public boolean getIsImageGallery() {
		return ImageGallery.class.isInstance(getPage());
	}
	public boolean getIsPeopleList() {
		return PeopleList.class.isInstance(getPage());
	}
}
