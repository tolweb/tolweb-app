package org.tolweb.tapestry.selectionmodels;

import org.apache.tapestry.form.IPropertySelectionModel;

public class BranchLeafSelectionModel implements IPropertySelectionModel {
	public static final int BRANCH = 0;
	public static final int LEAF = 1;
	
	public String getLabel(int arg0) {
		switch (arg0) {
			case BRANCH: return "branch";
			case LEAF: return "leaf";
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
