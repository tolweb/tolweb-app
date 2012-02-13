package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.btol.Gene;
import org.tolweb.btol.NamedObject;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.dao.BaseDAO;

public abstract class ViewAllGenes extends AbstractViewAllObjects implements GeneInjectable {
	@InjectPage("btol/EditGene")
	public abstract AbstractEditPage getEditPage();
	public abstract NamedObject getCurrentGene();
	
	@SuppressWarnings("unchecked")
	public List getGenes() {
		return getGeneDAO().getAllGenesInProject(getProject().getId());
	}
	public EditGene editGene(IRequestCycle cycle) {
		Long geneId = (Long) cycle.getListenerParameters()[0];
		Gene gene = getGeneDAO().getGeneWithId(geneId, getProject().getId());
		EditGene editPage = (EditGene) getEditPage();
		editPage.setGene(gene);
		editPage.setPreviousPageName(getPageName());
		return editPage;
	}   
    public BaseDAO getDAO() {
        return getGeneDAO();
    }
    
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return Gene.class;
    }    
}
