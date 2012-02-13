/*
 * Created on Jun 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.components.Block;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.helpers.OtherGroupsHelper;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class QuickNav extends OtherGroups implements BaseInjectable {
    @Parameter(required = false, defaultValue="page.node")
	public abstract MappedNode getNode();	
    @Parameter(defaultValue="component:containingGroupsBlock")
    public abstract Block getContainingGroupsBlock(); 
    @Parameter(defaultValue="component:containingGroupBlock")
    public abstract Block getContainingGroupBlock(); 
    @Parameter(defaultValue="component:otherGroupsBlock")
    public abstract Block getOtherGroupsBlock(); 
    @Parameter(defaultValue="component:previousSiblingBlock")
    public abstract Block getPreviousSiblingBlock(); 
    @Parameter(defaultValue="component:nextSiblingBlock")
    public abstract Block getNextSiblingBlock(); 
    @Parameter(defaultValue="component:subgroupsBlock")
    public abstract Block getSubgroupsBlock();
	@Parameter
	public abstract OtherGroupsHelper getGroupsHelper();
	@Parameter
	public abstract boolean getShowByDefault();
    
    @Asset("img/quicknav/CompassHover.png")
    public abstract IAsset getCompassHoverImage();
    @Asset("img/quicknav/Compass.png")
    public abstract IAsset getCompassImage();    
    @Asset("img/quicknav/LeftDoubleArrows.png")
    public abstract IAsset getLeftDoubleArrowsImage();
    @Asset("img/quicknav/LeftDoubleArrowsDisabled.png")
    public abstract IAsset getLeftDoubleArrowsImageDisabled();
    @Asset("img/quicknav/UpDownArrows.png")
    public abstract IAsset getUpDownArrowsImage();
    @Asset("img/quicknav/UpDownArrowsDisabled.png")
    public abstract IAsset getUpDownArrowsImageDisabled();  
    @Asset("img/quicknav/RightArrow.png")
    public abstract IAsset getRightArrowsImage();
    @Asset("img/quicknav/RightArrowDisabled.png")
    public abstract IAsset getRightArrowsImageDisabled(); 
    
    public IAsset getCompassAsset() {
    	if (getShowByDefault()) {
    		return getCompassImage();
    	} else {
    		return getCompassHoverImage();
    	}
    }
    
    public String getNavClass() {
    	if (getShowByDefault()) {
    		return "shown";
    	} else {
    		return "hidden";
    	}
    } 
    
    protected String getCachedOutput() {
        String result = getCacheAccess().getQuickNavForNode(getNode());
        return result;
    }
    protected void setCachedOutput(String value) {
        getCacheAccess().setQuickNavForNode(getNode(), value);
    }
    public IAsset getLeftDoubleArrows() {
        if (getNotRoot()) {
            return getLeftDoubleArrowsImage();
        } else {
            return getLeftDoubleArrowsImageDisabled();
        }
    }
    public String getParentGroupClassName() {
        if (getNotRoot()) {
            return null;
        } else {
            return "disabled";
        }
    }
    public IAsset getUpDownArrows() {
        if (getNotRoot()) {
            return getUpDownArrowsImage();
        } else {
            return getUpDownArrowsImageDisabled();
        }
    }
    public IAsset getSubgroupsArrow() {
        if (getHasSubgroups()) {
            return getRightArrowsImage();
        } else {
            return getRightArrowsImageDisabled();
        }
    }
}
