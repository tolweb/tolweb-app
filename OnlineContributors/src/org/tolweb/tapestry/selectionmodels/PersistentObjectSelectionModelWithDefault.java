package org.tolweb.tapestry.selectionmodels;

import java.util.List;

public class PersistentObjectSelectionModelWithDefault extends
        PersistentObjectSelectionModel {
	@SuppressWarnings("unchecked")
    public PersistentObjectSelectionModelWithDefault(List objects, String defaultSelection) {
        super(objects);
        if (defaultSelection != null) { 
        	getSelections().add(0, defaultSelection);
        }
    }
    public String getLabel(int arg0) {
        Object option = getOption(arg0);
        if (String.class.isInstance(option)) {
            return (String) option;
        } else {
            return super.getLabel(arg0);
        }
    }
    public String getValue(int arg0) {
        if (arg0 == 0) {
            // if it's 0, it's the first selection so we want to return a number
            // that won't be assigned to an object
            return "-1";
        } else {
            return super.getValue(arg0);
        }
    }
}
