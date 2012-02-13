package org.tolweb.tapestry.selectionmodels;

import java.util.List;

/**
 * Property selection model that takes a list
 * of strings and an offset then returns values, 
 * using the offset, that are the index of the 
 * string into the list
 * @author lenards
 *
 */
public class StringToIntWithOffsetPropertySelectionModel extends
		StringToIntPropertySelectionModel {
	private int offset; 
	
	public StringToIntWithOffsetPropertySelectionModel(List<String> strings) {
		super(strings);
		this.offset = 0;
	}

	public StringToIntWithOffsetPropertySelectionModel(List<String> strings, int offset) {
		super(strings);
		this.offset = offset;
	}
	
	@Override
	public Object getOption(int arg0) {
		return super.getOption(arg0 + offset);
	}
}
