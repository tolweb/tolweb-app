package org.tolweb.btol.tapestry;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.tapestry.selection.SpecimensStateModel;

public class DNASpecimensStateModel extends SpecimensStateModel {
	public int getOptionCount() {
		return super.getOptionCount() + 1;
	}
    public String getLabel(int arg0) {
    	if (arg0 == AdditionalFields.SPECIMENS_TO_LAB) {
    		return "Specimens to Molecular Lab, Decision Pending";
    	} else {
    		return super.getLabel(arg0);
    	}
    }
}
