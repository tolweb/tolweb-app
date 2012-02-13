package org.tolweb.btol.tapestry;

import java.util.Map;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.BaseDAO;

public abstract class AbstractXTileEditPage extends AbstractEditPage {

	public void handleCallback(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();
		if (params.length == 0) return;
		
		String callbackTypeString = (String) params[0];
		int callbackType = Integer.parseInt(callbackTypeString);
		Long objectId = Long.valueOf((String) params[1]);
		String newValue = (String) params[2];
		doCallback(callbackType, objectId, newValue, cycle);
	}
	protected abstract void doCallback(int callbackType, Long id, String newValue, IRequestCycle cycle);
	public abstract Map<String, Object> getScriptSymbols();
	
	/**
	 * stubs for subclasses that may not need this
	 */
	public AbstractEditPage editNewObject(IPage prevPage) {
		return null;
	}
	public BaseDAO getDAO() {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return null;
	}
	protected void setObjectToEdit(Object object) {
		
	}
	protected String getOnChangeString(int callbackType) {
		return "prepareSendValue(" + callbackType + ", this.value)";
	}
}
