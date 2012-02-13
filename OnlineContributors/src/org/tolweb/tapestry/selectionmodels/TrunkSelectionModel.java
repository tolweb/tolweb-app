package org.tolweb.tapestry.selectionmodels;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.MappedNode;

public class TrunkSelectionModel implements IPropertySelectionModel {
	public int getOptionCount() {
		return 4;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case MappedNode.NOT_TRUNK_NODE: return "not a trunk node";
			case MappedNode.TRUNK_NODE: return "trunk node";
			case MappedNode.TRUNK_NODE_PROMINENT: return "prominent trunk node";
			default: return "prominent non-trunk node";
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}
}
