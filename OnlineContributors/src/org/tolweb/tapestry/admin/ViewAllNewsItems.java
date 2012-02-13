package org.tolweb.tapestry.admin;

import java.util.List;

import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.NewsItem;
import org.tolweb.tapestry.AbstractViewAllObjects;
import org.tolweb.tapestry.injections.NewsInjectable;

public abstract class ViewAllNewsItems extends AbstractViewAllObjects implements NewsInjectable {
	public abstract NewsItem getCurrentNewsItem();
	
    public BaseDAO getDAO() {
        return getNewsItemDAO();
    }
    
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return NewsItem.class;
    }
    
    @SuppressWarnings("unchecked")
    public List getNewsItems() {
    	return getNewsItemDAO().getAllNewsItems();
    }
    
    
    public String getColumnsString() {
    	String colsString =  "!edit, ID:newsItemId, Text:newsItemText, Active:active, Created:createdDate, User:createdBy";
    	return colsString;
    }
    
    public String getTableId() {
    	return "newItemsTable";
    }
}
