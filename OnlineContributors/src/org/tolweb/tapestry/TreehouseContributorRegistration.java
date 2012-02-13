/*
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class TreehouseContributorRegistration extends AbstractContributorRegistration {
	public static final int ENTHUSIAST_SELECTION = 0;
	public static final int STUDENT_SELECTION = 1;	
	public static final int EDUCATOR_SELECTION = 2;
	public static final int SCIENTIST_SELECTION = 3;
	public static final int OTHER_SELECTION = 4;
	private static final String NO_EDUCATOR = "Select an option from the list";
	@SuppressWarnings("unchecked")
	private static final ArrayList EDUCATORS;
	public static final IPropertySelectionModel EDUCATOR_MODEL;
	
	private int categorySelection = -1;
	private String educatorString = null;
	
	public abstract String getOtherCategoryString();
	
	static {
		EDUCATORS = new ArrayList(Arrays.asList(new String[] {"Home School/After School Educator", "Museum Educator", "Elementary School Teacher (approx. ages 5-11)", 
		 	"Middle School Teacher (approx. ages 12-14)", "High School Teacher (approx. ages 15-17)",
			"College/University Professor", "Government/Non-Profit Science Educator (not school affiliated)",
			"Private Sector Science Educator (not school affiliated)"}));
		ArrayList totalEducators = new ArrayList();
		totalEducators.add(NO_EDUCATOR);
		totalEducators.addAll(EDUCATORS);
		String[] educatorsArray = new String[EDUCATORS.size() + 1];
		educatorsArray[0] = NO_EDUCATOR;
		System.arraycopy(EDUCATORS.toArray(), 0, educatorsArray, 1, EDUCATORS.size()); 
		EDUCATOR_MODEL = 
				new StringPropertySelectionModel(educatorsArray);			
	}
	
	public void detach() {
		super.detach();
		categorySelection = -1;
		educatorString = null;
	}	
	
	public byte getContributorType() {
		return Contributor.IMAGES_CONTRIBUTOR;
	}
	
	public int getCategorySelection() {
		if (categorySelection == -1) {
			String category = getEditedContributor().getCategory();
			if (category != null && category.equals(Contributor.ENTHUSIAST)) {
				categorySelection = ENTHUSIAST_SELECTION;	
			} else if (category != null && category.equals(Contributor.STUDENT)) {
				categorySelection = STUDENT_SELECTION;
			} else if (category != null && EDUCATORS.indexOf(category) != -1) {
				categorySelection = EDUCATOR_SELECTION;
			} else if (category != null && category.equals(Contributor.SCIENTIST)) {
				categorySelection = SCIENTIST_SELECTION;
			} else {
				categorySelection = OTHER_SELECTION;
			}
		}
		return categorySelection;
	}
	
	public void setCategorySelection(int value) {
		categorySelection = value;
	}
	
	public String getEducatorString() {
		if (educatorString == null) {
			if (EDUCATORS.contains(getEditedContributor().getCategory())) {
				educatorString = getEditedContributor().getCategory();
			} else {
				educatorString = NO_EDUCATOR;
			}
		}
		return educatorString;
	}
	
	public void setEducatorString(String value) {
		educatorString = value;
	}
	
	/**
	 * Overridden to set the category on the contributor
	 */
	protected void doAdditionalProcessing() {
		Contributor contr = getEditedContributor();
		if (categorySelection == ENTHUSIAST_SELECTION) {
			contr.setCategory(Contributor.ENTHUSIAST);
		} else if (categorySelection == STUDENT_SELECTION) {
			contr.setCategory(Contributor.STUDENT);
		} else if (categorySelection == EDUCATOR_SELECTION && !getEducatorString().equals(NO_EDUCATOR)) {
			contr.setCategory(getEducatorString());
		} else if (categorySelection == SCIENTIST_SELECTION) {
			contr.setCategory(Contributor.SCIENTIST);
		} else {
			contr.setCategory(getOtherCategoryString());
		}
		super.doAdditionalProcessing();
	}
	
	protected byte getUnapprovedContributorType() {
		return Contributor.TREEHOUSE_CONTRIBUTOR;
	}
	
	protected void checkForAdditionalErrors() {
	    if (categorySelection == OTHER_SELECTION && StringUtils.isEmpty(getOtherCategoryString())) {
	        setError("Please select a category for yourself from the list, or enter text into the 'Other' field.");
	    }
	}
	
	protected String getConfirmationPageName() {
		return "TreehouseContributorsConfirmation";
	}
	
	protected Object getExistingContributorPageDestination() {
	    return "TreehouseMaterialsManager";
	}
    
    public String getSurnameDisplayName() {
        return "<strong class=\"red\">Surname*</strong>";
    }
    public String getFirstNameDisplayName() {
        return "<strong class=\"red\">First Name*</strong>";
    }
    public String getEmailDisplayName() {
        return "<strong class=\"red\">Email*</strong>";
    }    
}
