package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.btol.Gene;

@ComponentClass
public abstract class GeneSelectionList extends BaseComponent {
	/**
	 * All of the genes that can possibly be selected
	 * @return
	 */
	@Parameter
	public abstract List<Gene> getGenes();
	/**
	 * All of the genes that were selected
	 * @return
	 */
	@Parameter
	public abstract List<Gene> getSelectedGenes();
	public abstract Gene getCurrentGene();
	public abstract boolean getShowCurrentGene();
    public abstract void setShowCurrentGene(boolean value);
    
    public void setCurrentGeneStatus() {
		setShowCurrentGene(getSelectedGenes().contains(getCurrentGene()));
    }
    public void addOrRemoveCurrentGene() {
    	// adding/removing should only take place during rewind
    	if (getPage().getRequestCycle().isRewinding()) {
	    	if (getShowCurrentGene()) {
	    		if (!getSelectedGenes().contains(getCurrentGene())) {
	    			getSelectedGenes().add(getCurrentGene());
	    		}
	    	} else {
	    		getSelectedGenes().remove(getCurrentGene());
	    	}
    	}
    }	
}
