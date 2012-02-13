package org.tolweb.tapestry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class ContributorSelectionModel implements IPropertySelectionModel {
	@SuppressWarnings("unchecked")
	private List contributors;
	private boolean allowNoSelection;
	private boolean allowMeSelection;
	private Contributor meContributor;
	private String noSelectionText;

	public static final String TEXT_NONE_SELECTED = "None selected";
	public static final String NONE_SELECTED_VALUE = "-1";
	
	@SuppressWarnings("unchecked")
	public ContributorSelectionModel(List value) {
		this(value, false, false, null);
	}
	
	@SuppressWarnings("unchecked")
	public ContributorSelectionModel(List value, boolean allowNoSelection, boolean allowMeSelection, Contributor meContributor) {
		contributors = value;
		Collections.sort(contributors, new ContributorNameComparator());
		this.allowMeSelection = allowMeSelection;
		this.allowNoSelection = allowNoSelection;
		this.meContributor = meContributor;
		this.noSelectionText = TEXT_NONE_SELECTED;
	}
	
	@SuppressWarnings("unchecked")
	protected List getContributors() {
		return contributors;
	}
	/**
	 * index of the 'me' selection
	 * @return
	 */
	public int getMeIndex() {
		if (getAllowMeSelection()) {
			return getIsZeroMe(0) ? 0 : 1;
		}  else {
			// 'me' selection not allowed, so return an invalid
			// number
			return -1;
		}
	}
	public int getOptionCount() {
		return contributors.size() + getAllowNoSelectionInt() + getAllowMeSelectionInt();
	}
	private int getAllowNoSelectionInt() {
		return getAllowNoSelection() ? 1 : 0;
	}
	private int getAllowMeSelectionInt() {
		return getAllowMeSelection() ? 1 : 0;
	}

	public Object getOption(int arg0) {
		if (getAllowNoSelection() || getAllowMeSelection()) {
			if (arg0 == 0) {
				if (getAllowNoSelection()) {
					return null;
				} else {
					return meContributor;
				}
			} else if (arg0 == 1 && getAllowMeSelection()) {
				return meContributor;
			} else { 
				return contributors.get(arg0 - (getAllowMeSelectionInt() + getAllowNoSelectionInt()));
			}
		} else {
			return contributors.get(arg0);
		}
	}
	protected boolean getIsZeroMe(int index) {
		return !getAllowNoSelection() && getAllowMeSelection() && index == 0;
	}
	protected String getContributorName(int arg0) {
		Contributor contr = (Contributor) getOption(arg0);
		String firstName = contr.getFirstName();
		String lastName = contr.getLastName();
		String firstNameString = StringUtils.notEmpty(firstName) ? ", " + firstName : "";
		return lastName + firstNameString;
	}
	public String getLabel(int arg0) {
		if (getAllowNoSelection() && arg0 == 0) {
			return getNoSelectionText();
		} else if (getAllowMeSelection() && arg0 == 1 || (getIsZeroMe(arg0))) {
			return "Me";
		} 
		return getContributorName(arg0);
	}

	public String getValue(int arg0) {
		if (arg0 == 0 && getAllowNoSelection()) {
			return NONE_SELECTED_VALUE;
		} else if (arg0 == 1 && getAllowMeSelection() || (getIsZeroMe(arg0))) {
			return getMeContributor().getId() + "";
		} else {
			return ((Contributor) getOption(arg0)).getId() + "";
		}
	}
	@SuppressWarnings("unchecked")
	public Object translateValue(String arg0) {
		try {
			int id = Integer.parseInt(arg0);
			for (Iterator iter = contributors.iterator(); iter.hasNext();) {
				Contributor contributor = (Contributor) iter.next();
				if (contributor.getId() == id) {
					return contributor;
				}
			}
		} catch (Exception e) {}
		return null;
	}
	public boolean getAllowNoSelection() {
		return allowNoSelection;
	}
	public void setAllowNoSelection(boolean allowNoSelection) {
		this.allowNoSelection = allowNoSelection;
	}
	public boolean getAllowMeSelection() {
		return allowMeSelection;
	}
	public void setAllowMeSelection(boolean allowMeSelection) {
		this.allowMeSelection = allowMeSelection;
	}
	
	public Contributor getMeContributor() {
		return meContributor;
	}
	public void setMeContributor(Contributor meContributor) {
		this.meContributor = meContributor;
	}
	public String getNoSelectionText() {
		return noSelectionText;
	}
	public void setNoSelectionText(String noSelectionText) {
		this.noSelectionText = noSelectionText;
	}
}
