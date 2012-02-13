package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.ITableColumnModel;
import org.apache.tapestry.contrib.table.model.ITableDataModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleListTableDataModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumnModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableState;

public class AlphabeticalTableModel extends SimpleTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6436233716012299659L;
	protected AlphabeticalPagingState pagingState;
    
    public AlphabeticalTableModel(Object[] arrData, ITableColumn[] arrColumns) {
        super(new SimpleListTableDataModel(arrData), new SimpleTableColumnModel(arrColumns));
    }

    public AlphabeticalTableModel(Object[] arrData, ITableColumnModel objColumnModel) {
        super(new SimpleListTableDataModel(arrData), objColumnModel);
    }

    public AlphabeticalTableModel(ITableDataModel objDataModel, ITableColumnModel objColumnModel) {
        super(objDataModel, objColumnModel, new SimpleTableState());
    }

    public AlphabeticalTableModel(ITableDataModel objDataModel, ITableColumnModel objColumnModel, SimpleTableState objState) {
        super(objDataModel, objColumnModel, objState);
    }
    
    public AlphabeticalPagingState getPagingState() {
    	return constructPagingState(true);
    }
    
    @SuppressWarnings("unchecked")
    protected AlphabeticalPagingState constructPagingState(boolean isActualContributors) {
        if (pagingState == null) {
            List contributors = new ArrayList();
            for (Iterator iter = getDataModel().getRows(); iter.hasNext();) {
                contributors.add(iter.next());
            }
            if (isActualContributors) {
            	pagingState = new AlphabeticalPagingState(contributors);
            } else {
            	pagingState = new AlphabeticalObjectArrayPagingState(contributors);
            }
        }
        return pagingState;    	
    }
    
    public int getPageCount() {
        return getPagingState().getPageCount();
    }
    
    @SuppressWarnings("unchecked")
    public Iterator getCurrentPageRows() {
        return getPagingState().getCurrentPageRows();
    }
}
