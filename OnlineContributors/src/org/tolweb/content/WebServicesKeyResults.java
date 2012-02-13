package org.tolweb.content;

import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.WebServicesKey;

public abstract class WebServicesKeyResults extends BasePage {
	public abstract WebServicesKey getWebServicesKey();
	public abstract void setWebServicesKey(WebServicesKey wsKey);
	
	public abstract boolean isDuplicateEntry();
	public abstract void setDuplicateEntry(boolean value);
	
	public abstract boolean isFromRegistration();
	public abstract void setFromRegistration(boolean value);
	
	public String getTitle() {
		return "Tree of Life Web Services - User Key Registration Results";
	}
}