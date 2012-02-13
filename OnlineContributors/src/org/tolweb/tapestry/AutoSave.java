package org.tolweb.tapestry;

import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author mb
 */
public abstract class AutoSave extends BaseComponent implements PageInjectable, CookieInjectable, BaseInjectable {
	protected abstract Integer getAutoSaveFrequency();
    protected abstract String getValuePath();
    protected abstract String getTextareaId();
    protected abstract Long getEditedObjectId();
    protected abstract Integer getContributorId();
    protected abstract Long getEditHistoryId();
    
    @SuppressWarnings("unchecked")
    public Map getScriptSymbols() {
		Map mapSymbols = new HashMap();
		mapSymbols.put("autoSaveFrequency", getAutoSaveFrequency() * 1000);
        mapSymbols.put("textareaId", getTextareaId());
        mapSymbols.put("editedObjectId", getEditedObjectId());
        mapSymbols.put("valuePath", getValuePath());
        mapSymbols.put("contributorId", getContributorId());
        mapSymbols.put("editHistoryId", getEditHistoryId());
		return mapSymbols;
	}
	
	public void doAutoSave(IRequestCycle cycle) {
        String valuePath = cycle.getListenerParameters()[0].toString();
        String text = cycle.getListenerParameters()[1].toString();
        Long editedObjectId = null, contributorId = null, editHistoryId = null;
        try {
            editedObjectId = Long.valueOf((String) cycle.getListenerParameters()[2]);
        } catch (Exception e) {}
        try {
            contributorId = Long.valueOf((String) cycle.getListenerParameters()[3]);
        } catch (Exception e) {}
        try {
            editHistoryId = Long.valueOf((String) cycle.getListenerParameters()[4]);
        } catch (Exception e) {}
        // check to see if the visit is alive and if the contributor is there
        Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
        if (contr == null) {
            String[] params = new String[1];
            params[0] = "false";
            cycle.setListenerParameters(params);
        } else {        
            saveText(editedObjectId, text, valuePath, contributorId, editHistoryId);
        }
	}
    
    private void saveText(Long objId, String text, String valuePath, Long contributorId, Long editHistoryId) {
        Object toSave = null;
        // if there is no value path don't bother trying to do a save, just
        // update the edit lock as necessary
        if (objId != null && StringUtils.notEmpty(valuePath)) {
            toSave = getObjectWithId(objId);
            String preparedText = getTextPreparer().translateImagesFromAllEditFormats(text);
            try {
                Ognl.setValue(valuePath, toSave, preparedText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveObject(toSave);
        }
        updateLastEdited(contributorId, editHistoryId, toSave);
    }
    protected void updateLastEdited(Long contributorId, Long editHistoryId, Object toSave) {
        if (editHistoryId != null) {
            getEditHistoryDAO().updateLock(editHistoryId, contributorId);
        }
    }
    protected Object getObjectWithId(Long id) {
        return getWorkingPageDAO().getPageWithId(id);
    }
    protected void saveObject(Object toSave) {
        getWorkingPageDAO().savePage((MappedPage) toSave);
    }
}
