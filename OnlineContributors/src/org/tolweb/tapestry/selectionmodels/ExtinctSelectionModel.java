package org.tolweb.tapestry.selectionmodels;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * A selection model for a boolean property. 
 * 
 * This class is a little convoluted because it's really 
 * modeling something that "should" have been a boolean 
 * property for a node.  It's not a boolean property 
 * because there was some intent that the extinct field 
 * also hold values to indicate endangered and protected 
 * groups (but that was never fully flushed out). 
 * 
 * Judge this with the above in mind. 
 * 
 * @author lenards
 *
 */
public class ExtinctSelectionModel implements IPropertySelectionModel {
	public static final int EXTINCT = 1;
	public static final int NOT_EXTINCT = 0;
	public int getOptionCount() {
		return 2;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case NOT_EXTINCT: return "not extinct";
			case EXTINCT: return "extinct";
			default: return "";
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}
}
