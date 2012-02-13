package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.page.TextSection;

public abstract class EditManageTextSections extends AbstractPageEditingPage {
    public abstract MappedTextSection getCurrentTextSection();
    public abstract void setAddNewSectionSelected(boolean value);
    public abstract boolean getAddNewSectionSelected();
    
    public boolean getCanMoveTextSection() {
        return getCanEditHeading() || getCurrentTextSection().getHeading().equals(TextSection.DISCUSSION);
    }
    public boolean getCanRemoveTextSection() {
        return getCanEditHeading();
    }
    public boolean getCanEditHeading() {
        return !TextSection.getImmutableNames().contains(getCurrentTextSection().getHeading());
    }
    public String getCurrentTextSectionHeading() {
        return getCurrentTextSection().getHeading();
    }
    /**
     * Makes sure that the user doesn't try to set the heading to a reserved name
     * @param value
     */
    public void setCurrentTextSectionHeading(String value) {
        if (!TextSection.getImmutableNames().contains(value)) {
            getCurrentTextSection().setHeading(value);
        } else {
            setError("You may not use the reserved Text Section heading '" + value + "' in a custom text section.");
        }
    }
    public String getTextSectionEditTag() {
        String editTag = ((EditBranchLeafPageWrapper) getComponent("wrapper")).getTextSectionEditTag(getCurrentTextSection());
        return editTag;
    }
    @SuppressWarnings("unchecked")
    public void doSave(IRequestCycle cycle) {
        if (getAddNewSectionSelected()) {
            MappedTextSection newSection = getNewTextSection();
            getReorderHelper().addToSet(getTolPage().getTextSections(), newSection);
        }
        getTolPage().setTextSections(new TreeSet(getTolPage().getTextSections()));
        super.doSave(cycle);
    }
    public void addNewTextSection(IRequestCycle cycle) {
        setAddNewSectionSelected(true);
    }
    protected void doAdditionalDeletionIfNecessary(Object objectToDelete) {
        getDAO().deleteTextSection((MappedTextSection) objectToDelete); 
    }
    
    @SuppressWarnings("unchecked")
    protected SortedSet getOrderedCollection() {
        return getTolPage().getTextSections();
    } 
    
    @SuppressWarnings("unchecked")
    public IPrimaryKeyConvertor getTextSectionsConvertor() {
        return new IPrimaryKeyConvertor() {
            public Object getPrimaryKey(Object objValue) {
                return ((MappedTextSection) objValue).getTextSectionId();
            }
            public Object getValue(Object objPrimaryKey) {
                Long textSectionId = (Long) objPrimaryKey;
                for (Iterator iter = getTolPage().getTextSections().iterator(); iter.hasNext();) {
                    MappedTextSection nextSection = (MappedTextSection) iter.next();
                    if (nextSection.getTextSectionId().equals(textSectionId)) {
                        return nextSection;
                    }
                }
                // not found, so first try to fetch the section, if not found then create a new one
                MappedTextSection section = getDAO().getTextSectionWithId(textSectionId);
                if (section == null) {
                    section = getNewTextSection();
                }
                return section;
            }
        };
    }
    private MappedTextSection getNewTextSection() {
        MappedTextSection newSection = new MappedTextSection();
        newSection.setHeading("New Text Section");
        newSection.setText("");
        return newSection;
    }
}
