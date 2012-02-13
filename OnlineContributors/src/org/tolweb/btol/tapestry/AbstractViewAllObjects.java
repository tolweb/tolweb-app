package org.tolweb.btol.tapestry;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Defunct;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class AbstractViewAllObjects extends ProjectPage implements BaseInjectable {
    private Format dateFormat;
    public abstract BaseDAO getDAO();
    @SuppressWarnings("unchecked")
    public abstract Class getObjectClass();
	@Persist("session")
	public abstract boolean getShowDefunct();    
    
    public void deleteObject(Number objectId) {
        Object toDelete = getDAO().getObjectWithId(getObjectClass(), objectId);
        if (toDelete != null) {
            getDAO().deleteObject(toDelete);
        }
    }
    public Object getObjectFromRequestCycle(IRequestCycle cycle) {
        Number id = (Number) cycle.getListenerParameters()[0];
        return getDAO().getObjectWithId(getObjectClass(), id);
    }
    public Format getDateFormat() {
      if (dateFormat == null) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      } 
      return dateFormat;
    }
    public abstract AbstractEditPage getEditPage();
    
    public AbstractEditPage editNewObject() {
    	return getEditPage().editNewObject(this);
    }
    @SuppressWarnings("unchecked")
    protected List getFilteredByDefunctList(List value) {
    	ArrayList returnList = new ArrayList();
    	for (Iterator iter = value.iterator(); iter.hasNext();) {
			Defunct defunctObj = (Defunct) iter.next();
			if (getShowDefunct() || !defunctObj.getDefunct()) {
				returnList.add(defunctObj);
			}
		}
    	return returnList;
    }
}
