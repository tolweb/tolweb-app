package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.UsePermissable;

public abstract class UsePermissionComponent extends BaseComponent {
	@Parameter(required = true)
	public abstract UsePermissable getPermissableObject();
	/*
	 * creative commons use can be any one of MANY different licenses
	 * (exciting!) so use this to indicate that we need to figure out
	 * the cc licensing stuff
	 * DEVN - Developer Nodes:
	 * set creative commons use to the first cc license (cc-by version 2.0)
	 * this version was the first cc license when cc licenses were adopted 
	 * - unfortunately 1.0 were not adopted in the first implementation
	 */
	protected static final byte CREATIVE_COMMONS_USE = NodeImage.CC_BY20;

	public IPropertySelectionModel getSelectionModel() {
	    return new ModificationPermittedModel();
	}

	public boolean getTolModificationDisabled() {
	    return !(getPermissableObject().getUsePermission() == NodeImage.TOL_USE);
	}

	public boolean getShareModificationDisabled() {
	    return !(getPermissableObject().getUsePermission() == NodeImage.EVERYWHERE_USE);
	}

	public void setTolModificationValue(Boolean value) {
	    conditionallyDoSet(value, NodeImage.TOL_USE);
	}

	public Boolean getTolModificationValue() {
	    return getModificationValue(NodeImage.TOL_USE);
	}

	public void setShareModificationValue(Boolean value) {
	    conditionallyDoSet(value, NodeImage.EVERYWHERE_USE);
	}

	public Boolean getShareModificationValue() {
	    return getModificationValue(NodeImage.EVERYWHERE_USE);
	}

	private Boolean getModificationValue(byte use) {
	    if (getPermissableObject().getUsePermission() == use) {
	        return getPermissableObject().getModificationPermitted();
	    } else {
	        return Boolean.valueOf(false);
	    }
	}

	private void conditionallyDoSet(Boolean value, byte use) {
	    if (getPermissableObject().getUsePermission() == use) {
	        getPermissableObject().setModificationPermitted(value);
	    }	    
	}

	public byte getEverywhereUse() {
		return NodeImage.EVERYWHERE_USE;
	}
	public byte getRestrictedUse() {
		return NodeImage.RESTRICTED_USE;
	}
	public byte getTolUse() {
		return NodeImage.TOL_USE;
	}
	public byte getPublicDomainUse() {
		return NodeImage.ALL_USES;
	}
	public byte getCreativeCommonsUse() {
		return CREATIVE_COMMONS_USE;
	}
}