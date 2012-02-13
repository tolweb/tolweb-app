package org.tolweb.btol.tapestry;

import org.tolweb.btol.AdditionalFields;

public class PIDNASpecimensStateModel extends DNASpecimensStateModel {
	public int getOptionCount() {
		return super.getOptionCount() + 1;
	}
    public String getLabel(int arg0) {
    	if (arg0 == AdditionalFields.HAVE_DNA) {
    		return "Have Sufficient DNA";
    	} else {
    		return super.getLabel(arg0);
    	}
    }
}
