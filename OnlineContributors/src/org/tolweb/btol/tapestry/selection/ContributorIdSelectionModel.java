package org.tolweb.btol.tapestry.selection;

import java.util.List;

import org.tolweb.tapestry.ContributorSelectionModel;

public class ContributorIdSelectionModel extends ContributorSelectionModel {
	@SuppressWarnings("unchecked")
	public ContributorIdSelectionModel(List value) {
		this(value, false);
	}
	@SuppressWarnings("unchecked")
	public ContributorIdSelectionModel(List value, boolean allowNoSelection) {
		super(value, allowNoSelection, false, null);
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}	
}
