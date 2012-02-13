package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.AdditionalFields;

public class MtGenomeSelectionModel implements IPropertySelectionModel {
	public int getOptionCount() {
		return 4; 
	}
	public Object getOption(int arg0) {
		return arg0 - 1; // substract one to support 'not-needed', -1, value.
	}
	public String getLabel(int arg0) {
		arg0--; // decrement by one to support the 'not-needed', -1, value.
		switch (arg0) {
			case AdditionalFields.MT_GENOME_FIRST_YEAR: return "BYU has specimens for mt genome, mt genome in progress";
			case AdditionalFields.HAVE_MT_GENOME: return "Have Complete mtGenome";
			case AdditionalFields.NO_MT_GENOME: return "BYU needs specimens for mt genome";
			default: return "Not needed";
		}
	}
	public String getValue(int arg0) {
		return Integer.toString(arg0);
	}
	public Object translateValue(String arg0) {
		return Integer.parseInt(arg0);
	}
}
