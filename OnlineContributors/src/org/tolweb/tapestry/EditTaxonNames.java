package org.tolweb.tapestry;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class EditTaxonNames extends AbstractNodeEditingPage implements NodeInjectable {
    public abstract boolean getAddOtherNameSelected();
    public abstract void setAddOtherNameSelected(boolean value);
    public abstract int getAdditionalOtherNamesToAdd();
    public abstract void setAdditionalOtherNamesToAdd(int value);
    public abstract String getTextToReplace();
    public abstract String getReplacementText();    
    public abstract boolean getCaseSensitive();    
    public abstract boolean getOnlyWholeWords();    

    public void doSave(IRequestCycle cycle) {
        super.doSave(cycle);
        if (getValidationDelegate().getHasErrors()) {
            return;
        }
        if (getAddOtherNameSelected()) {
            addNewOtherName();
            savePage();
        } else if (getAdditionalOtherNamesToAdd() > 0) {
            for (int i = 0; i < getAdditionalOtherNamesToAdd(); i++) {
                addNewOtherName();
            }
            savePage();
            // clear so it isn't inadvertently set in another submission
            setAdditionalOtherNamesToAdd(0);
        }
    }
    public void doSearchReplace(IRequestCycle cycle) {
    	String textToReplace = getTextToReplace();
    	if (StringUtils.isEmpty(textToReplace)) {
    		setError("You must enter a value for the text you want to replace.");
    	}
    	getNodeDAO().replaceTextInTaxa(getNode().getNodeId(), textToReplace, getReplacementText(), getCaseSensitive(), getOnlyWholeWords());
    }
    public void addOtherName(IRequestCycle cycle) {
        setAddOtherNameSelected(true);
    }    
    @SuppressWarnings("unchecked")
    protected void savePage() {
    	// doing this "renewed" collection avoids a problem with hibernate - 
    	// this relates to the ToL "Great Hibernate Mapping Caper" see TolwebDocumentation/incidents
    	getNode().setSynonyms(new TreeSet(getNode().getSynonyms())); 
        getNodeDAO().saveNode(getNode());        
    }    
    @SuppressWarnings("unchecked")
    public SortedSet getOrderedCollection() {
        return getNode().getSynonyms();
    }
    public NodeDAO getNodeDAO() {
        return getWorkingNodeDAO();
    }
    private void addNewOtherName() {
        MappedOtherName name = new MappedOtherName();
        name.setAuthority("");
        name.setIsImportant(false);
        name.setIsPreferred(false);
        name.setName("New other name");
        getReorderHelper().addToSet(getNode().getSynonyms(), name);
    }
}
