/*
 * Created on Jun 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.treegrow.main.OrderedObject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OrderedObjectConvertor implements IPrimaryKeyConvertor {
	@SuppressWarnings("unchecked")
    private Collection collection;
    
	@SuppressWarnings("unchecked")
    public OrderedObjectConvertor(Collection values) {
        collection = values;
    }
    
    /* (non-Javadoc)
     * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getPrimaryKey(java.lang.Object)
     */
    public Object getPrimaryKey(Object objValue) {
        return Integer.valueOf(((OrderedObject) objValue).getOrder());
    }

    /* (non-Javadoc)
     * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Object getValue(Object objPrimaryKey) {
	    Integer order = (Integer) objPrimaryKey;
	    for (Iterator iter = collection.iterator(); iter.hasNext();) {
            OrderedObject obj = (OrderedObject) iter.next();
            Integer nextOrder = Integer.valueOf(obj.getOrder());
            if (nextOrder.equals(order)) {
                return obj;
            }
        }
		return null;
    }

}