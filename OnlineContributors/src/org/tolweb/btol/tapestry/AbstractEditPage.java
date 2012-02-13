package org.tolweb.btol.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.BaseDAO;
import org.tolweb.treegrow.main.StringUtils;

public abstract class AbstractEditPage extends ProjectPage implements IExternalPage {
	@Bean
	public abstract ValidationDelegate getValidationDelegate();
	@Persist("client")
	public abstract String getPreviousPageName();
	public abstract void setPreviousPageName(String value);
	public abstract void setWasSaved(boolean value);
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
        Number id = (Number) args[0];
        if (args.length > 1) {
        	String previousPageName = (String) args[1];
            setPreviousPageName(previousPageName);        	
        }
        setObjectToEdit(getDAO().getObjectWithId(getEditObjectClass(), id));		
	}
	public IPage gotoPreviousPage() {
		String previousPageName = getPreviousPageName();
		return getRequestCycle().getPage(previousPageName);
	}
	public abstract AbstractEditPage editNewObject(IPage prevPage);
	public abstract BaseDAO getDAO();
	@SuppressWarnings("unchecked")
	public abstract Class getEditObjectClass();
	protected abstract void setObjectToEdit(Object object);
	public abstract String getError();
	public abstract void setError(String value);
	protected IPage conditionallyGotoPreviousPage() {
		if (StringUtils.notEmpty(getPreviousPageName())) {
			return gotoPreviousPage();
		} else {
			setWasSaved(true);
			return this;
		}
	}
}
