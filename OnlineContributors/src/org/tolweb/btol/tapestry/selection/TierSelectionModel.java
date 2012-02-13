package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.AdditionalFields;

public class TierSelectionModel implements IPropertySelectionModel {
	public int getOptionCount() {
		return 4;
	}
	public Object getOption(int arg0) {
		switch (arg0) {
			case 0: return AdditionalFields.NO_TIER_SET;
			default: return arg0;
		}
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case 0: return "No Tier Selected";
			default: return "Tier " + (arg0);
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return getOption(Integer.parseInt(arg0));
	}
}
