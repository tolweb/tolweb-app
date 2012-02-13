package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;

public class NeededFilterSelectionModel implements IPropertySelectionModel {
	public static final int SHOW_ALL = 0;
	public static final int SHOW_NEEDED_DNA = 1;
	public static final int SHOW_NEEDED_ADULTS = 2;
	public static final int SHOW_NEEDED_LARVAE = 3;

	public int getOptionCount() {
		return 4;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case SHOW_NEEDED_DNA: return "Specimens for DNA";
			case SHOW_NEEDED_ADULTS: return "Adults for Morphology";
			case SHOW_NEEDED_LARVAE: return "Larvae for Morphology";
			default: return "Show All";
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}
}
