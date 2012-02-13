package org.tolweb.btol.tapestry;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Gene;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class EditGene extends AbstractEditPage implements UserInjectable, GeneInjectable {
	@Persist("client")
	public abstract Gene getGene();
	public abstract void setGene(Gene gene);
	
	public IPage saveGene(IRequestCycle cycle) {
		getGeneDAO().saveGene(getGene(), getProject().getId());
		// if there is no previous page we were called from a popup window so 
		// display a gene saved message instead of going to another page
		return conditionallyGotoPreviousPage();
	}
	public boolean getIsNewGene() {
		return getGene().getId() == null;
	}
	public AbstractEditPage editNewObject(IPage prevPage) {
		setGene(new Gene());
		if (prevPage != null) {
			setPreviousPageName(prevPage.getPageName());
		}
		return this;
	}
	public BaseDAO getDAO() {
		return getGeneDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return Gene.class;
	}
	protected void setObjectToEdit(Object value) {
		setGene((Gene) value);
	}
}
