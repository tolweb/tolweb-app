package org.tolweb.tapestry;

import org.apache.tapestry.contrib.table.components.TablePages;
import org.apache.tapestry.util.ComponentAddress;

public abstract class AlphabeticalTablePages extends TablePages {
    public abstract int getCurrentLetterIndex(); 
    /** 
     * We will show all of the pages here
     */
    public int getPagesDisplayed() {
        return getTableModelSource().getTableModel().getPageCount();
    }
    
    public Object[] getLetterList() {
        return ((AlphabeticalPagingState) getTableModelSource().getTableModel().getPagingState()).getSelectableLetters();
    }
    
    public Object[] getDisplayPageContext() {
        ComponentAddress objAddress = new ComponentAddress(getTableModelSource());
        return new Object[] { objAddress, Integer.valueOf(getCurrentLetterIndex() + 1)};
    }    
}
