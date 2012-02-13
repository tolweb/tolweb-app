package org.tolweb.tapestry.selectionmodels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.PersistentObject;

public class PersistentObjectSelectionModel implements IPropertySelectionModel {
	@SuppressWarnings("unchecked")
    private List selections;
	
	@SuppressWarnings("unchecked")
    public PersistentObjectSelectionModel(List choices) {
        selections = new ArrayList(choices);
        Collections.sort(selections, new Comparator() {
            public int compare(Object o1, Object o2) {
                PersistentObject p1 = (PersistentObject) o1;
                PersistentObject p2 = (PersistentObject) o2;
                return p1.getDisplayName().compareTo(p2.getDisplayName());
            } });        
    }

    public int getOptionCount() {
        return selections.size();
    }

    public Object getOption(int arg0) {
        return selections.get(arg0);
    }

    public String getLabel(int arg0) {
        return ((PersistentObject) selections.get(arg0)).getDisplayName();
    }

    public String getValue(int arg0) {
        return ((PersistentObject) getOption(arg0)).getId().toString();
    }
    
    @SuppressWarnings("unchecked")
    public Object translateValue(String arg0) {
        Long id = Long.parseLong(arg0);
        for (Iterator iter = selections.iterator(); iter.hasNext();) {
            Object nextObject = iter.next();
            if (PersistentObject.class.isInstance(nextObject)) {
                if (((PersistentObject) nextObject).getId().equals(id)) {
                    return getValueFromPersistentObject((PersistentObject) nextObject);
                }
            }
        }
        return null;
    }

    /**
     * @return Returns the selections.
     */
    @SuppressWarnings("unchecked")
    protected List getSelections() {
        return selections;
    }
    
    /**
     * @param selections The selections to set.
     */
    @SuppressWarnings("unchecked")
    protected void setSelections(List selections) {
        this.selections = selections;
    }
    protected Object getValueFromPersistentObject(PersistentObject object) {
    	return object;
    }
}
