package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;

public abstract class EditBranchLeafPageLink extends BaseComponent implements BaseInjectable, MiscInjectable {
    public abstract Long getTextSectionId();
    public abstract Object[] getEditPageParams();
    public abstract String getEditPageName();
    @Parameter
    public abstract String getTitle(); 
    
    public boolean getIsBranchLeaf() {
        return ViewBranchOrLeaf.class.isInstance(getPage());
    }
    public Object[] getEditPageParameters() {
        Object[] params = getEditPageParams();
        if (params == null) {
            params = ((ViewBranchOrLeaf) getPage()).getEditPageParameters();
        }
        if (getTextSectionId() != null) {
            Object[] newParams = new Object[4];
            System.arraycopy(params, 0, newParams, 0, 3);
            // then add the current text section id to the list
            newParams[3] = getTextSectionId();
            params = newParams;
        }
        return params;
    }
    public String getEditUrl() {
    	return getTapestryHelper().getExternalServiceUrl(getEditPageName(), getEditPageParameters());
    }
    public String getJavascriptEditFunctionString() {
        return "javascript:openOtherEditForm('" + getEditUrl() + "');";
    }
}
