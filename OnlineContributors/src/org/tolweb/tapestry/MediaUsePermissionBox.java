package org.tolweb.tapestry;

import org.apache.tapestry.annotations.InjectComponent;

public abstract class MediaUsePermissionBox extends EditMediaComponent {
	@InjectComponent("usePermissionRadios")
	public abstract UsePermissionComponent getUsePermissionRadios();
	
	/* DEVN - used to gain access to the subclass of UsePermissionComponent. 
	 * The UsePermissionComponent has no way of manipulating state, so the 
	 * subclass is need in order to update the state which is represents */
	private UsePermissionRadios getStrictUsePermissionRadiosType() {
		return (UsePermissionRadios)getUsePermissionRadios();
	}

	/**
	 * Changes the selected values on the UsePermissionRadios  
	 * child-component.
	 * @param licenseSelection
	 */
	/* DEVN - the chief reason for this was so that a usePermission could 
	 * be updated immediately following the selection of another ToL 
	 * contributor.  That way the license defaults for that contributor 
	 * would be used with the media or text. */	
	public void setUsePermissionSelection(byte licenseSelection) {
		/* This method is used by an editing page in order to manipulate the 
		 * settings/values displayed by the usePermissionRadios component. */
		getStrictUsePermissionRadiosType().setRadiosSelection(licenseSelection);

	}
}
