package org.tolweb.btol.tapestry.selection;

import org.tolweb.btol.AdditionalFields;

public class TierExtensionSelectionModel extends TierSelectionModel {
	private int offset;
	
	public TierExtensionSelectionModel() {
		super();
		this.offset = -1;
	}
	
	@Override
	public int getOptionCount() {
		return super.getOptionCount() + 1;
	}
	
	@Override
	public Object getOption(int arg0) {
		switch (arg0) {
			case -1: return AdditionalFields.NO_TIER_SET;
			default: return arg0;
		}
	}
	public String getLabel(int arg0) {
		arg0 += offset;
		switch (arg0) {
			case -1: return "No Tier Selected";
			default: return "Tier " + (arg0);
		}
	}
	public String getValue(int arg0) {
		return "" + (arg0 + offset);
	}	
	@Override
	public Object translateValue(String arg0) {
		int i = Integer.parseInt(arg0);
		return getOption(i);
	}
}
