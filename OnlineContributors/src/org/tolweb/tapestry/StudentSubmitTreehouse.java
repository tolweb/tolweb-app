package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class StudentSubmitTreehouse extends AbstractContributorPage implements UserInjectable, 
		TreehouseInjectable, BaseInjectable {
    public abstract void setReturnToEditingSelected(boolean value);
    public abstract boolean getReturnToEditingSelected();
    
    public String getWorkingUrl() {
        return getUrlBuilder().getWorkingURLForObject(getTreehouse());
    }
    
    public void submitTreehouse(IRequestCycle cycle) {
        if (!getReturnToEditingSelected()) {
            if (!getDelegate().getHasErrors()) {
                getAccessoryPageHelper().submitTreehouse(cycle, getTreehouse(), 
                        TreehouseDataConfirm.TREEHOUSE_SUBMITTED_PAGE, TreehouseDataConfirm.TREEHOUSE_PROPERTY, 
                        true, null, null);
            }
        } else {
            cycle.activate("TreehouseEditor");
        }
    }
    
    public void returnToEditing(IRequestCycle cycle) {
        setReturnToEditingSelected(true);
    }
    
    public ValidationDelegate getDelegate() {
        return (ValidationDelegate) getBeans().getBean("delegate");
    }
}
