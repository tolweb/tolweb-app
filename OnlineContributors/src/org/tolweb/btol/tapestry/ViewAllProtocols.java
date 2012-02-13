package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.btol.PCRProtocol;
import org.tolweb.btol.injections.PCRProtocolInjectable;
import org.tolweb.dao.BaseDAO;

public abstract class ViewAllProtocols extends AbstractViewAllObjects implements PCRProtocolInjectable {
	@InjectPage("btol/EditPCRProtocol")
	public abstract AbstractEditPage getEditPage();
	
	public abstract PCRProtocol getCurrentProtocol();
	@SuppressWarnings("unchecked")
	public List getProtocols() {
		return getFilteredByDefunctList(getProtocolDAO().getAllProtocolsInProject(getProject().getId()));
	}
    public BaseDAO getDAO() {
        return getProtocolDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return PCRProtocol.class;
    }   
}
