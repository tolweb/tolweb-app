package org.tolweb.tapestry;

public abstract class IntToStringPropertySelectionModel {
	public Object getOption(int index) {
		return Integer.valueOf(index);
	}
	public String getValue(int index) {
		return "" + index;
	}
	public Object translateValue(String value) {
		return Integer.valueOf(value);
	}	
}
