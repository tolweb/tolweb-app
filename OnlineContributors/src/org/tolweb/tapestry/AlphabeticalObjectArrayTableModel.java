package org.tolweb.tapestry;

import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.ITableColumnModel;
import org.apache.tapestry.contrib.table.model.ITableDataModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableState;

public class AlphabeticalObjectArrayTableModel extends AlphabeticalTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -972780964488581034L;
	public AlphabeticalObjectArrayTableModel(Object[] arrData, ITableColumn[] arrColumns) {
        super(arrData, arrColumns);
    }
    public AlphabeticalObjectArrayTableModel(Object[] arrData, ITableColumnModel objColumnModel) {
        super(arrData, objColumnModel);
    }
    public AlphabeticalObjectArrayTableModel(ITableDataModel objDataModel, ITableColumnModel objColumnModel) {
        super(objDataModel, objColumnModel, new SimpleTableState());
    }
    public AlphabeticalObjectArrayTableModel(ITableDataModel objDataModel, ITableColumnModel objColumnModel, SimpleTableState objState) {
        super(objDataModel, objColumnModel, objState);
    }
    public AlphabeticalObjectArrayPagingState getPagingState() {
    	return (AlphabeticalObjectArrayPagingState) constructPagingState(false);
    } 
}
