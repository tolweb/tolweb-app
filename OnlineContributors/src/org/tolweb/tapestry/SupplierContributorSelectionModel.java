package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.treegrow.main.Contributor;

public class SupplierContributorSelectionModel extends ContributorSelectionModel implements IPropertySelectionModel {

	private boolean allowShowAllSelection;
	private String showAllSelectionText; 

	public static final String TEXT_SHOW_ALL = "Show All";
	public static final String SHOW_ALL_VALUE = "-2";
	
	@SuppressWarnings("unchecked")
	public SupplierContributorSelectionModel(List values) {
		super(values);
	}
	
	@SuppressWarnings("unchecked")
	public SupplierContributorSelectionModel(List values, boolean allowNoSelection, boolean allowMeSelection, Contributor meContributor) {
		super(values, allowNoSelection, allowMeSelection, meContributor);
		this.allowShowAllSelection = true;
		this.showAllSelectionText = TEXT_SHOW_ALL;
		this.setAllowMeSelection(checkForMeContributor());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkForMeContributor() {
		List members = super.getContributors();
		Contributor me = getMeContributor();
		boolean meFound = false;
		for (Object contrib : members) {
			Contributor tmp = (Contributor)contrib;
			if (tmp.equals(me)) {
				meFound = true;
				break;
			}
		}
		return meFound;
	}

	@Override
	public String getLabel(int arg0) {
		if (getAllowShowAllSelection() && arg0 == 0) {
			return getShowAllSelectionText();
		} else if (getAllowNoSelection() && arg0 == 1) {
			return getNoSelectionText();
		} else if (getAllowMeSelection() && arg0 == 2 || (getIsZeroMe(arg0))) {
			return "Me";
		} 
		return getContributorName(arg0);
	}

	@Override
	public Object getOption(int arg0) {
		if (getAllowNoSelection() || getAllowMeSelection() || getAllowShowAllSelection()) {
			if (arg0 == 0 && getAllowShowAllSelection()) {
				return null;
			} else if (arg0 == 1 && getAllowNoSelection()) {
				return Contributor.BLANK_CONTRIBUTOR;
			} else if (arg0 == 2 && getAllowMeSelection()) {
				return getMeContributor();
			} else {
				return getContributors().get(arg0 - (getExtraSelectionCount()));
			}
		} else {
			return getContributors().get(arg0);
		}
	}
	
	private int getExtraSelectionCount() {
		int count = 0; 
		count += (getAllowNoSelection()) ? 1 : 0;
		count += (getAllowMeSelection()) ? 1 : 0;
		count += (getAllowShowAllSelection()) ? 1 : 0;
		return count;
	}
	
	@Override
	public int getOptionCount() {
		return getContributors().size() + getExtraSelectionCount();
	}
	
	@Override
	public int getMeIndex() {
		if (getAllowMeSelection()) {
			return determineMeIndex();
		}  else {
			// 'me' selection not allowed, so return an invalid
			// number
			return -1;
		}		
	}
	
	@Override
	public String getValue(int arg0) {
		if (arg0 == 0 && getAllowShowAllSelection()) {
			return SHOW_ALL_VALUE;
		} else if (arg0 == 1 && getAllowNoSelection()) {
			return ContributorSelectionModel.NONE_SELECTED_VALUE;
		} else if (arg0 == 2 && getAllowMeSelection() || (getIsZeroMe(arg0))) {
			return getMeContributor().getId() + "";
		} else {
			return ((Contributor) getOption(arg0)).getId() + "";
		}
	}
	
	@Override
	public Object translateValue(String arg0) {
		if (arg0.equals(SHOW_ALL_VALUE)) {
			return null;
		} else if (arg0.equals(ContributorSelectionModel.NONE_SELECTED_VALUE)) {
			return Contributor.BLANK_CONTRIBUTOR;
		} else {
			return super.translateValue(arg0);
		}
	}
	
	private int determineMeIndex()
	{
		return (getAllowNoSelection() ? 1 : 0) + (getAllowMeSelection() ? 1 : 0);
	}
	
	public boolean getAllowShowAllSelection() {
		return allowShowAllSelection;
	}
	
	public void setAllowShowAllSelection(boolean allowShowAllSelection) {
		this.allowShowAllSelection = allowShowAllSelection;
		if (this.allowShowAllSelection)
			showAllSelectionText = TEXT_SHOW_ALL;
	}

	public String getShowAllSelectionText() {
		return showAllSelectionText;
	}
	
	public void setShowAllSelectionText(String showAllSelectionText) {
		this.showAllSelectionText = showAllSelectionText;
	}
}
