package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.GeneFragment;

public class GeneFragmentRequirementSelectionModel implements IPropertySelectionModel {
	
	private int offset; 
	
	public GeneFragmentRequirementSelectionModel() {
		this.offset = -1;
	}
	public String getLabel(int arg0) {
		if (arg0 <= GeneFragment.NO_TIER_REQUIREMENT) {
			arg0 = GeneFragment.NO_TIER_REQUIREMENT;
		} else {
			arg0 += offset;
		}
		switch (arg0) {
			case GeneFragment.NO_TIER_REQUIREMENT: return "Not Required";
			default: return createTierSelectionText(arg0);
		}
	}

	public Object getOption(int arg0) {
		return arg0;
	}

	public int getOptionCount() {
		return 5;
	}

	public String getValue(int arg0) {
		return "" + (arg0 + offset);
	}

	public Object translateValue(String arg0) {
		int i = Integer.parseInt(arg0);
		return getOption(i);
	}

	private String createTierSelectionText(int arg0) {
		StringBuffer selection = new StringBuffer();
		
		for(int i = 0; i < arg0; i++) {
			selection.append("Tier " + i);
			selection.append(" + ");
		}
		
		selection.append("Tier " + arg0);
		return selection.toString();
	}
}
