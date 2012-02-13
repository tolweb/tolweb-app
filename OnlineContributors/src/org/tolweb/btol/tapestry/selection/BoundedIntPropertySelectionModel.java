package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;

public class BoundedIntPropertySelectionModel implements IPropertySelectionModel {
	private int maxSelection;
	private int minSelection;
	private boolean allowNull;
	
	public BoundedIntPropertySelectionModel(int maxVal, int minVal, boolean allowNull) {
		this.maxSelection = maxVal;
		this.minSelection = minVal;
		this.allowNull = allowNull;
	}
	public int getOptionCount() {
		return maxSelection - minSelection + 1 + getAllowNullInt();
	}

	public Object getOption(int arg0) {
		if (allowNull && arg0 == 0) {
			return null;
		} else {
			return Integer.valueOf(arg0 + minSelection - getAllowNullInt());
		}
	}
	public String getLabel(int arg0) {
		if (allowNull && arg0 == 0) {
			return "";
		} else {
			return "" + (arg0 + minSelection - getAllowNullInt());
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	private int getAllowNullInt() {
		return allowNull ? 1 : 0;
	}
	public Object translateValue(String arg0) {
		return getOption(Integer.parseInt(arg0));
	}
}
