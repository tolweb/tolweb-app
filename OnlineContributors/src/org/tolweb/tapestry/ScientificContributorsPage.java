package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ScientificContributorsPage extends BasePage implements PageBeginRenderListener, PageInjectable, 
		UserInjectable, AccessoryInjectable, MiscInjectable {
	@SuppressWarnings("unchecked")
	public abstract List getContributors();
	@SuppressWarnings("unchecked")
	public abstract void setContributors(List value);
	public abstract Object[] getCurrentContributor();
	
	@SuppressWarnings("unchecked")
	private static Comparator objectArrayComparator  = new Comparator() {
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			if (o1 == null && o2 != null) {
				return -1;
			} else if (o2 == null && o1 != null) {
				return 1;
			} else if (o1 == null && o2 == null) {
				return 0;
			}
			Object[] arr1 = (Object[]) o1;
			Object[] arr2 = (Object[]) o2;
			String lastName1 = (String) arr1[0];
			String lastName2 = (String) arr2[0];
			if (lastName1 == null && lastName2 != null) {
				return -1;
			} else if (lastName2 == null && lastName1 != null) {
				return 1;
			} else if (lastName2 == null && lastName1 == null) {
				return 0;
			}
			return lastName1.toUpperCase().compareTo(lastName2.toUpperCase());
		}
	};
	
	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		//if (getContributors() == null) {
			Collection contributorIds = getPublicPageDAO().getPageContributorIds();
			contributorIds.addAll(getPublicAccessoryPageDAO().getArticleNoteContributorIds());
			// put them into a set to remove duplicates
			Set idsSet = new HashSet(contributorIds);
			List contributors = getContributorDAO().getContributorIdsNamesInstitutionsWithIds(idsSet); 
			Collections.sort(contributors, objectArrayComparator);
			setContributors(contributors);
		//}
	}
    public AlphabeticalObjectArrayTableModel getTableModel() {
    	Object[] data = null;
    	if (getContributors() != null) {
    		data = getContributors().toArray();
    	} else {
    		data = new Object[]{};
    	}
    	return getTapestryHelper().getAlphabeticalObjectArrayTableModel("displayName, institution, numPages", this, data);    	
    }
    public String getName() {
    	String lastName = (String) getCurrentContributor()[0];
    	String firstName = (String) getCurrentContributor()[1];
    	String returnString = "";
    	if (StringUtils.notEmpty(firstName)) {
    		returnString += firstName + " ";
    	}
    	if (StringUtils.notEmpty(lastName)) {
    		returnString += lastName;
    	}
    	return returnString;
    }
	public Integer getNumPagesForContributor() {
		int contributorId = ((Integer) getCurrentContributor()[3]).intValue();
		int numPages = getPublicPageDAO().getNumPagesForContributor(contributorId);
		int numAccPages = getPublicAccessoryPageDAO().getNumPagesForContributor(contributorId);
		return numPages + numAccPages;
	}
}
