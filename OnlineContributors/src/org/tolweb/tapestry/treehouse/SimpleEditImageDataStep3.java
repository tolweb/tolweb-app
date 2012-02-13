package org.tolweb.tapestry.treehouse;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.treegrow.main.Keywords;


public abstract class SimpleEditImageDataStep3 extends SimpleEditImageData {
	@Persist("session")
	public abstract Keywords getNewKeywords();
	public abstract void setNewKeywords(Keywords value);
	public abstract void setGoToStep1And2Selected(boolean value);
	public abstract boolean getGoToStep1And2Selected();
	
	@InjectPage("treehouses/SimpleEditImageData")
	public abstract SimpleEditImageData getStep1And2Page();
	
	public void pageBeginRender(PageEvent event) {
		if (getImage().getKeywords() == null) {
			setNewKeywords(new Keywords());
		}
	    initSelectionModels();		
	}
	
	public Keywords getKeywords() {
		// if the image hasn't been saved, it won't have a keywords
		// object attached to it.  So, return a transient instance
		// to store the values until the user submits their data
		if (getImage().getKeywords() != null) {
			return getImage().getKeywords();
		} else {
			return getNewKeywords(); 
		}
	}
	public IPage checkForPageRedirect(IRequestCycle cycle) {
		IPage page = super.checkForPageRedirect(cycle);
		if (page == null) {
			if (getGoToStep1And2Selected()) {
				SimpleEditImageData editPage = getStep1And2Page();
				editPage.setImage(getImage());
				return editPage;
			}
		}
		return null;
	}	
	public void goToStep1And2() {
		setGoToStep1And2Selected(true);
	}
}