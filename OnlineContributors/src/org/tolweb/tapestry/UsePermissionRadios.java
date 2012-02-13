/*
 * Created on Apr 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class UsePermissionRadios extends UsePermissionComponent implements IFormComponent {
	
	private StringPropertySelectionModel ccLicenseModel;
	private StringPropertySelectionModel ccLicenseVersionModel;
	
	@Parameter(required = true)
    public abstract boolean getIsImage();
    
    @Parameter(defaultValue = "use1BlockDefault")
    public abstract Block getUse1Block();
    @Parameter(defaultValue = "use2BlockDefault")
    public abstract Block getUse2Block();
    @Parameter(defaultValue = "use3BlockDefault")
    public abstract Block getUse3Block();
    @Parameter(defaultValue = "use4BlockDefault")
    public abstract Block getUse4Block();
    @Parameter(defaultValue = "use5BlockDefault")
    public abstract Block getUse5Block();    
    @Parameter(defaultValue = "false")
    public abstract boolean getDontShowOption3();
    @Parameter(defaultValue = "false")
    public abstract boolean getDontShowTolModOption();
    
    @InjectComponent("use1BlockDefault")
    public abstract Block getUse1BlockDefault();
    @InjectComponent("use2BlockDefault")
    public abstract Block getUse2BlockDefault();
    @InjectComponent("use3BlockDefault")
    public abstract Block getUse3BlockDefault();
    @InjectComponent("use4BlockDefault")
    public abstract Block getUse4BlockDefault();
    @InjectComponent("use5BlockDefault")
    public abstract Block getUse5BlockDefault();
    
    public abstract String getCcLicenseSelection();
    public abstract void setCcLicenseSelection(String value);
    public abstract String getCcLicenseVersionSelection();
    public abstract void setCcLicenseVersionSelection(String value);
    public abstract void setWasCCSelection(boolean value);
    public abstract boolean getWasCCSelection();
    
    public void prepareForRender(IRequestCycle cycle) {
    	super.prepareForRender(cycle);
    	// don't interfere with rewind -- use only for init purposes
    	if (!getPage().getRequestCycle().isRewinding()) {
    		// Wow - that's an ugly line of code.. but we want the Version to default to whatever is the current CC default as they produce more and more versions
    		setCcLicenseVersionSelection((String)getCcLicenseVersionModel().getOption(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_VERSION_DEFAULT));
    		byte use = getPermissableObject().getUsePermission();
	    	boolean modPermitted = getPermissableObject().getModificationPermitted();
	    	// use the license object to abstract the "dirty work" of figuring the license combos
	    	ContributorLicenseInfo currLicInfo = new ContributorLicenseInfo(use, modPermitted);
	    	
	    	// process the use permission selection - which needs to fall into one of 
	    	// the four radio-buttons
	    	setUsePermissionSelection(use);
	    	
	    	// sets the values displayed in the responding selection models
	    	processSelectionModelSettings(currLicInfo);
    	}
    }
	
    /**
     * Sets the radio-group selection. 
     * This method is called from the an editing page when they 
     * want to manipulate the license selection.
     * @param value byte representing the contributor license
     */
    public void setRadiosSelection(byte value) {
    	setUsePermissionSelection(value);
    	processSelectionModelSettings(value);
    	getPermissableObject().setUsePermission(value);
    }

    /**
     * Determines the correct settings for the selection models (drop-downs) on 
     * the UI, License Name & License Version, so the proper match is displayed.
     * @param licCode the byte representing the contributor license
     */    
    public void processSelectionModelSettings(byte licCode) {
    	ContributorLicenseInfo licInfo = new ContributorLicenseInfo(licCode);
    	processSelectionModelSettings(licInfo);
    }
    
    /**
     * Determines the correct settings for the selection models (drop-downs) on 
     * the UI, License Name & License Version, so the proper match is displayed.
     * @param currLicInfo the object representing the license
     */
    public void processSelectionModelSettings(ContributorLicenseInfo currLicInfo) {
    	boolean modPermitted = currLicInfo.isModificationPermitted();
    	if (currLicInfo.isCreativeCommons()) {
    		setCcLicenseSelection(currLicInfo.getLicenseName());
    		setCcLicenseVersionSelection(currLicInfo.getLicenseVersion());
    	} else if (currLicInfo.isToLRelated()) {
    		setTolModificationValue(modPermitted);
    		setShareModificationValue(modPermitted);
    	}    	
    }
    
    /**
     * Returns a byte representing the corresponding license radio-button 
     * selected.  There can be between 3 and 5 license categories that 
     * map to radio-buttons (CC, TOL, TOL Parnters, Restricted/1-time-use, 
     * public domain).  Even though CC can be one of 24-some license, it's 
     * just returned as the first supported CC license (which was By-2.0 
     * when first implemented).
     * @return a byte representing one of the radio-buttons selected
     */
    public byte getUsePermissionSelection() {
    	byte permission = getPermissableObject().getUsePermission();
    	if (ContributorLicenseInfo.isToLRelatedLicenseCode(permission) || 
    		ContributorLicenseInfo.isPublicDomainCode(permission)) {
    		return permission;
    	} else {
    		return CREATIVE_COMMONS_USE;
    	}
    }
    
    /**
     * In the simple cases, it sets the usePermission selection on the 
     * permissable object being manipulated.  In the case of a Creative 
     * Commons license it defers to the listener method, setCCLicense(), 
     * to discern which of the 24-some license have been selected.
     * @param value byte representing the license code
     */
    public void setUsePermissionSelection(byte value) {
    	if (value < CREATIVE_COMMONS_USE) {
    		getPermissableObject().setUsePermission(value);
    	} else {
    		// this is used by the trigger-listener method, setCCLicense(), to discern
    		// if it should go aheard and process the CC license values and set them 
    		// on the permissable object being manipulated by this component
    		setWasCCSelection(true);
    	}
    }
    
    /**
     * Takes the Version and Name of a Creative Commons license from the UI and 
     * process into the corresponding license code for that CC license after which 
     * it sets the permissable object's usePermission property to that value.
     */
    public void setCCLicense() {
    	// DEVN - Developer Note: this is called by a listener component in the template
    	// only worry about this if they selected CC and we are rewinding
    	if (getWasCCSelection() && getPage().getRequestCycle().isRewinding()) {
    		ContributorLicenseInfo cLicInfo = new ContributorLicenseInfo(NodeImage.ALL_USES);
    		
    		cLicInfo.setLicenseName(getCcLicenseSelection());
    		cLicInfo.setLicenseVersion(getCcLicenseVersionSelection());
    		
    		getPermissableObject().setUsePermission((byte)(cLicInfo.produceNewLicenseCode()));
    	}
    }

	public IPropertySelectionModel getCcLicenseModel() {
		if (ccLicenseModel == null) {
			ccLicenseModel = new StringPropertySelectionModel(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_NAMES);
		}
		return ccLicenseModel;
	}
	public void setCcLicenseModel(StringPropertySelectionModel ccLicenseModel) {
		this.ccLicenseModel = ccLicenseModel;
	}
	
	public IPropertySelectionModel getCcLicenseVersionModel() {
		if (ccLicenseVersionModel == null) {
			ccLicenseVersionModel = new StringPropertySelectionModel(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_VERSIONS);
		}
		return ccLicenseVersionModel;
	}
	public void setCcLicenseVersionModel(StringPropertySelectionModel ccLicenseVersionModel) {
		this.ccLicenseVersionModel = ccLicenseVersionModel;
	}
}
