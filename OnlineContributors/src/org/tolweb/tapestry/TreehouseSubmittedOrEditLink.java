package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.MappedAccessoryPage;

public abstract class TreehouseSubmittedOrEditLink extends BaseComponent {
	public abstract MappedAccessoryPage getTreehouse();
	public abstract boolean getIsTeacher();
	
	public boolean getCanEdit() {
	    MappedAccessoryPage treehouse = getTreehouse();
	    boolean isSubmitted = treehouse.getIsSubmitted();
	    if (!getIsTeacher()) {
	    	isSubmitted = isSubmitted || treehouse.getIsSubmittedToTeacher();
	    }
	    return !isSubmitted;		
	}
	
	public String getObjectType() {
		if (getTreehouse().getIsTreehouse()) {
			return "Treehouse";
		} else {
			return getTreehouse().getArticleNoteTypeString();
		}
	}
}
