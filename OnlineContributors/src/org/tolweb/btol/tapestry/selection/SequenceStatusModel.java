package org.tolweb.btol.tapestry.selection;

import java.util.Arrays;

import org.tolweb.tapestry.selectionmodels.StringToIntPropertySelectionModel;

public class SequenceStatusModel extends StringToIntPropertySelectionModel {
	public SequenceStatusModel() {
		super(Arrays.asList(new String[] {"Not needed", "No sequence, no pcr", "Have bright pcr bands", "Have partial sequence", "Have sequence"}));
	}
}
