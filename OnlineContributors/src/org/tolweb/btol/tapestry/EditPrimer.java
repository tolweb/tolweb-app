package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.link.DefaultLinkRenderer;
import org.apache.tapestry.valid.ValidationConstraint;
import org.tolweb.btol.Gene;
import org.tolweb.btol.Primer;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.btol.injections.PrimerInjectable;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.btol.tapestry.selection.GeneSelectionModel;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.ContributorSelectionModel;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class EditPrimer extends AbstractEditPage implements GeneInjectable, 
	PrimerInjectable, ProjectInjectable, PageBeginRenderListener, BaseInjectable {
	public abstract void setExistingPrimer(Primer value);
	@Persist("session")
	public abstract Primer getPrimer();
	public abstract void setPrimer(Primer value);
	@InjectPage("btol/EditGene")
	public abstract EditGene getEditGenePage();
	public abstract void setAddedOrRemovedSynonym(boolean value);
	public abstract boolean getAddedOrRemovedSynonym();
	public abstract int getIndex();
	public abstract void setSynonyms(List<String> value);
	public abstract List<String> getSynonyms();
	public abstract int getIndexToRemove();
	
	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		setSynonyms(new ArrayList<String>(getPrimer().getSynonyms()));
		if (getSynonyms().size() == 0) {
			getSynonyms().add("");
		}
	}
	public DefaultLinkRenderer getGeneLinkRenderer() {
		return getLinkRenderer("geneWindow");
	}
	public DefaultLinkRenderer getPeopleLinkRenderer() {
		return getLinkRenderer("peopleWindow");
	}
	private DefaultLinkRenderer getLinkRenderer(String windowName) {
		return getRendererFactory().getLinkRenderer(windowName, 900, 550);
	}
	@SuppressWarnings("unchecked")
	public IPage savePrimer(IRequestCycle cycle) {
		if (!getAddedOrRemovedSynonym()) {
			String primerName = getPrimer().getName();
			
			// we need to make sure there aren't duplicates in the db!
			List existingPrimers = getPrimerDAO().getPrimersWithNameOrSynonymAndNotId(primerName, getPrimer().getId(), getProject().getId());
			if (existingPrimers != null && existingPrimers.size() > 0) {
				getValidationDelegate().record("There is an existing primer named " + primerName + 
						".  Primer names must be unique.", ValidationConstraint.CONSISTENCY);
				return null;
			}
			if (getIsNewPrimer()) {
				getPrimer().setCreatedContributor(getContributor());
			}
			getPrimerDAO().savePrimer(getPrimer(), getProject().getId());			
			return conditionallyGotoPreviousPage();
		} else {
			return null;
		}
	}
	public AbstractEditPage editNewObject(IPage prevPage) {
		return editNewObject(prevPage, null);
	}
	public AbstractEditPage editNewObject(IPage prevPage, Gene gene) {
		Primer primer = new Primer();
		primer.setGene(gene);
		setPrimer(primer);
		setPreviousPageName(prevPage.getPageName());
		return this;		
	}
	public EditPrimer editNewPrimerWithName(String primerName) {
		Primer primer = new Primer();
		primer.setName(primerName);
		primer.setCode(primerName);
		setPrimer(primer);
		return this;
	}
	public boolean getIsNewPrimer() {
		return getPrimer().getId() == null;
	}
	@SuppressWarnings("unchecked")
	public GeneSelectionModel getGeneModel() {
		List allGenes = getGeneDAO().getAllGenesInProject(getProject().getId());
		return new GeneSelectionModel(allGenes);
	}
	public ContributorSelectionModel getContributorModel() {
		return new ContributorSelectionModel(getProject().getAllProjectMembers(), true, true, getContributor());
	}
	public EditGene editNewGene() {
		EditGene editPage = getEditGenePage();
		return (EditGene) editPage.editNewObject(null);
	}
	public BaseDAO getDAO() {
		return getGeneDAO();
	}
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return Primer.class;
	}
	public void setObjectToEdit(Object value) {
		setPrimer((Primer) value);
	}
	@SuppressWarnings("unchecked")
	public void addSynonym() {
		getPrimer().getSynonyms().add("New Synonym");
		setAddedOrRemovedSynonym(true);
	}
	@SuppressWarnings("unchecked")
	public void setCurrentSynonym(String value) {
		if (getIndex() == 0) {
			getPrimer().setSynonyms(new TreeSet<String>());
		}
		if (StringUtils.notEmpty(value)) {
			getPrimer().getSynonyms().add(value);
		}
	}
	public String getCurrentSynonym() {
		return getSynonyms().get(getIndex());
	}
	@SuppressWarnings("unchecked")
	public void removeSynonym() {
		if (getIndexToRemove() >= 0) {
			List syns = new ArrayList(getPrimer().getSynonyms());
			String synToRemove = (String) syns.get(getIndexToRemove());
			getPrimer().getSynonyms().remove(synToRemove);
			setAddedOrRemovedSynonym(true);
		}
	}
	public boolean getCanSetPrivate() {
		return getIsNewPrimer() || (getPrimer().getCreatedContributorId() != null && 
				getPrimer().getCreatedContributorId().equals(getContributor().getId()));
	}
}
