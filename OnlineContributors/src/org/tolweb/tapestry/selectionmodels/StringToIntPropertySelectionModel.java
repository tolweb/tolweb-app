package org.tolweb.tapestry.selectionmodels;

import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * Property selection model that takes a list
 * of strings and returns values that are the
 * index of the string into the list
 * @author dmandel
 *
 */
public class StringToIntPropertySelectionModel implements
		IPropertySelectionModel {
	private List<String> strings;
	
	public StringToIntPropertySelectionModel(List<String> strings) {
		this.strings = strings;
	}
	public String getLabel(int arg0) {
		return strings.get(arg0);
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	public int getOptionCount() {
		return strings.size();
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}
}
