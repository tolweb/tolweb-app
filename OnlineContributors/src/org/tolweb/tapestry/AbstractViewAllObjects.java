package org.tolweb.tapestry;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.dao.BaseDAO;

/**
 * Offers a base-class for all "views" over groups of data-objects.  Similar 
 * to the class by the same name in the btol sub-package, but does not have 
 * the project validation constraints present. 
 * 
 * @author lenards
 *
 */
public abstract class AbstractViewAllObjects extends AbstractContributorPage {
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
}
