/*
 * Created on Oct 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ognl.Ognl;
import ognl.OgnlException;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.PageContributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractPageContributorDAO extends WorkingAwareDAO {
    protected ContributorDAO contributorDAO;  
    protected EditHistoryDAO editHistoryDAO;
	
	public void setContributorDAO(ContributorDAO value) {
		contributorDAO = value;
	}
	protected ContributorDAO getContributorDAO() {
		return contributorDAO;
	}
	public void fillOutPageContributorsData(Collection pgContributors) {
		Iterator it = new ArrayList(pgContributors).iterator();
		while (it.hasNext()) {
			PageContributor contr = (PageContributor) it.next();
            Contributor actualContributor = contributorDAO.getContributorWithId(contr.getContributorId() + "");
            if (actualContributor != null) {
                contr.setContributor(actualContributor);
            } else {
                pgContributors.remove(contr);
            }
		}	    
	}
	
	protected void createInitialHistory(Contributor contr, Object object) {
		Long editHistoryId = getEditHistoryDAO().createNewHistory(contr);
		try {
			Ognl.setValue("editHistoryId", object, editHistoryId);
		} catch (OgnlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public EditHistoryDAO getEditHistoryDAO() {
		return editHistoryDAO;
	}

	public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
		this.editHistoryDAO = editHistoryDAO;
	}
}
