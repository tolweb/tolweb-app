package org.tolweb.tapestry.wrappers;

import org.apache.tapestry.IForm;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.asset.ExternalAsset;

public abstract class EditSimpleMediaWrapper extends AbstractWrapper {
	private ExternalAsset editSimpleMediaAsset;
	
	@InjectComponent("imageForm")
	public abstract IForm getForm();
	
    public ExternalAsset getEditSimpleMediaAsset() {
    	if (editSimpleMediaAsset == null) {
    		editSimpleMediaAsset = new ExternalAsset("/tree/css/trhsmedia2.css", null);
    	}
    	return editSimpleMediaAsset;
    }
    
    public boolean getIsStep1And2() {
    	return getPage().getPageName().equals("treehouses/SimpleEditImageData");
    }
    
    public String getDoctype() {
    	return "-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
    }
}
