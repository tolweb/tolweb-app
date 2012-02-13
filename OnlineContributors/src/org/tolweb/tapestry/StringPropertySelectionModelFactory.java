/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.form.StringPropertySelectionModel;

/**
 * @author dmandel
 *
 * Creates a StringPropertySelectionModel from a Java list
 */
public interface StringPropertySelectionModelFactory {
    /**
     * Creates a StringPropertySelectionModel from a Java list
     * @param list The List to create the model from
     * @return The new StringPropertySelectionModel
     */
	@SuppressWarnings("unchecked")
    public StringPropertySelectionModel createModelFromList(List list);
    
    /**
     * Creates a StringPropertySelectionModel from a Java list
     * @param list The List to create the model from
     * @param firstSelection A 'default' selection added to the front of the
     *  	  propertySelectionModel
     * @return The new StringPropertySelectionModel
     */    
	@SuppressWarnings("unchecked")
    public StringPropertySelectionModel createModelFromList(List list, String firstSelection);
	@SuppressWarnings("unchecked")
    public StringPropertySelectionModel createModelFromList(List list, String firstSelection, String secondSelection);
}
