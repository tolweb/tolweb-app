/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.form.StringPropertySelectionModel;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StringPropertySelectionModelFactoryImpl implements StringPropertySelectionModelFactory {    
	@SuppressWarnings("unchecked")
	public StringPropertySelectionModel createModelFromList(List list) {
        return createModelFromList(list, null);
    }
    
    @SuppressWarnings("unchecked")
    public StringPropertySelectionModel createModelFromList(List list, String firstSelection) {
        return createModelFromList(list, firstSelection, null);
    }
    
    @SuppressWarnings("unchecked")
    public StringPropertySelectionModel createModelFromList(List list, String firstSelection,
            String secondSelection) {
		List newList = new ArrayList(list);
		if (firstSelection != null) {
		    newList.add(0, firstSelection);
		}
		if (secondSelection != null) {
		    newList.add(1, secondSelection);
		}
		String[] selections = new String[newList.size()];
		selections = (String[]) newList.toArray(selections);
		return new StringPropertySelectionModel(selections);        
    }
}
