package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.btol.GeneFragment;

@ComponentClass
public abstract class GeneFragmentSelectionList extends BaseComponent {
	/**
	 * All of the gene fragments that can possibly be selected
	 * @return
	 */
	@Parameter
	public abstract List<GeneFragment> getGeneFragments();
	/**
	 * All of the gene fragments that were selected
	 * @return
	 */
	@Parameter
	public abstract List<GeneFragment> getSelectedGeneFragments();
	public abstract GeneFragment getCurrentGeneFragment();
	public abstract boolean getShowCurrentGeneFragment();
    public abstract void setShowCurrentGeneFragment(boolean value);
    
    public void setCurrentGeneFragmentStatus() {
		setShowCurrentGeneFragment(getSelectedGeneFragments().contains(getCurrentGeneFragment()));
    }
    
    public void addOrRemoveCurrentGeneFragment() {
    	// adding/removing should only take place during rewind
    	if (getPage().getRequestCycle().isRewinding()) {
	    	if (getShowCurrentGeneFragment()) {
	    		if (!getSelectedGeneFragments().contains(getCurrentGeneFragment())) {
	    			getSelectedGeneFragments().add(getCurrentGeneFragment());
	    		}
	    	} else {
	    		getSelectedGeneFragments().remove(getCurrentGeneFragment());
	    	}
    	}
    }
    
    public void selectImportantGeneFragments() {
    	List<GeneFragment> selected = getSelectedGeneFragments();
    	for (GeneFragment gfrag : getGeneFragments()) {
    		if (gfrag.isImportant()) {
    			selected.add(gfrag);
    		}
    	}
    }
    
    public void clearSelectedGeneFragments() {
    	getSelectedGeneFragments().clear();
    }
}
