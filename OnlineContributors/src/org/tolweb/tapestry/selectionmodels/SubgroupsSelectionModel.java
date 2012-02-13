package org.tolweb.tapestry.selectionmodels;

import org.apache.tapestry.form.IPropertySelectionModel;

public class SubgroupsSelectionModel implements IPropertySelectionModel {
	public static final int ALL_SUBGROUPS = 0;
	public static final int INCOMPLETE_SUBGROUPS = 1;
	
	public String getLabel(int arg0) {
		switch (arg0) {
			case ALL_SUBGROUPS: return "all subgroups listed";
			case INCOMPLETE_SUBGROUPS: return "list of subgroups incomplete";
			default: return "";
		}
	}

	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}

	public int getOptionCount() {
		return 2;
	}

	public String getValue(int arg0) {
		return "" + arg0;
	}
	
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}
}
