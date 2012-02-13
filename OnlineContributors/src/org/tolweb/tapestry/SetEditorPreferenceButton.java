package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class SetEditorPreferenceButton extends BaseComponent implements UserInjectable {
    public void setEditorPreference(IRequestCycle cycle) {
        Contributor contr = getContributor();
        boolean currentlyUsingEditor = contr.getDontUseEditor();         
        contr.setDontUseEditor(!currentlyUsingEditor);
        getContributorDAO().saveContributor(contr);
    }
    
    public String getButtonText() {
        if (getUseEditor()) {
            return "Advanced Mode";
        } else {
            return "Easy Mode";
        }
    }
    
    public boolean getUseEditor() {
        Contributor contr = getContributor();
        return !contr.getDontUseEditor();
    }
}
