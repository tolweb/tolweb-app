package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;

public class ProtocolTypeSelectionModel implements IPropertySelectionModel {
	public int getOptionCount() {
		return 3;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case 1: return "Step-Up";
			case 2: return "Touch-Down";
			default: return "Standard";
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return getOption(Integer.parseInt(arg0));
	}
}
