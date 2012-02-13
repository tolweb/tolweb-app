package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.AdditionalFields;

public class SpecimensStateModel implements IPropertySelectionModel {
    public int getOptionCount() {
        return 3;
    }
    public Object getOption(int arg0) {
        return Integer.valueOf(arg0);
    }
    public String getLabel(int arg0) {
        switch (arg0) {
            case AdditionalFields.HAVE_SOME: return "Have Some Specimens";
            case AdditionalFields.HAVE_ENOUGH: return "Have Enough Specimens";
            case AdditionalFields.HAVE_DNA: return "Have DNA";
            default: return "No Specimens";
        }
    }
    public String getValue(int arg0) {
        return "" + arg0;
    }
    public Object translateValue(String arg0) {
    	try {
    		return Integer.valueOf(arg0);
    	} catch (Exception e) {
    		return null;
    	}
    }
}
