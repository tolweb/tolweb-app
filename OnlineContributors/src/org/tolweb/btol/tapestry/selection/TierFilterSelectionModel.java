package org.tolweb.btol.tapestry.selection;

import org.tolweb.btol.AdditionalFields;

public class TierFilterSelectionModel extends TierSelectionModel {
	public static final int SHOW_ALL_TIERS = AdditionalFields.NO_TIER_SET;
	public static final int TIER_0 = 1;	
	public static final int TIER_1 = 2;
	public static final int TIER_1_MT_GENOME_SOON = 3;
	public static final int MT_GENOME_COMPLETE = 4;
	public static final int TIER_2 = 5;
	public static final int TIER_3 = 6;
	public static final int DEFAULT_SELECTION = TIER_2;
	
	private String initialSelection;
	
	public TierFilterSelectionModel() {
		this("Show All");
	}
	
	public TierFilterSelectionModel(String string) {
		initialSelection = string;
	}

	public int getOptionCount() {
		return 7;
	}
	public String getLabel(int index) {
		switch (index) {
			case 0: return initialSelection;
			case 1: return "Tier 0";
			case 2: return "Tier 0 + Tier 1";
			case 3: return "Tier 0 + Tier 1 with mtGenomes to do in next year";
			case 4: return "mtGenome completed";
			case 5: return "Tier 0 + Tier 1 + Tier 2";
			default: return "Tier 0 + Tier 1 + Tier 2 + Tier 3";
		}
	}
}
