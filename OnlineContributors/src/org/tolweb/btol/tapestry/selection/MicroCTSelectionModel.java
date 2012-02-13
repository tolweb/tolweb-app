package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.AdditionalFields;

public class MicroCTSelectionModel implements IPropertySelectionModel {
	public int getOptionCount() {
		return 4; 
	}
	public Object getOption(int arg0) {
		return arg0; 
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case AdditionalFields.MICRO_CT_COMPLETED: return "microCT completed";
			case AdditionalFields.MICRO_CT_HAS_SPECIMEN: return "Beutel has specimens for microCT";
			case AdditionalFields.MICRO_CT_NEED_SPECIMEN: return "Beutel needs specimens for microCT";
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