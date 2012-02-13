/*
 * Created on Apr 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import org.tolweb.dao.ContributorDAO;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.UsePermissable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UsePermissionHelper {
    private ContributorDAO contributorDAO;
    /**
     * @return Returns the contributorDAO.
     */
    public ContributorDAO getContributorDAO() {
        return contributorDAO;
    }
    /**
     * @param contributorDAO The contributorDAO to set.
     */
    public void setContributorDAO(ContributorDAO contributorDAO) {
        this.contributorDAO = contributorDAO;
    }
    
    public void saveContributorUsePermissionDefault(Contributor contr, UsePermissable obj, boolean isImage) {
        Byte usePermission = Byte.valueOf(obj.getUsePermission());
        Boolean modificationPermitted = Boolean.valueOf(obj.getModificationPermitted() != null && obj.getModificationPermitted().booleanValue());
        if (isImage) {
            contr.setImageModificationDefault(modificationPermitted);
            contr.setImageUseDefault(usePermission);
        } else {
            contr.setNoteModificationDefault(modificationPermitted);
            contr.setNoteUseDefault(usePermission);
        }
        contributorDAO.saveContributor(contr);
    }
    
    public void initializeNewPermissions(Contributor contr, UsePermissable obj, boolean isImage) {
        Boolean modificationPermitted = null;
        Byte usePermission = null;
        if (isImage) {
            modificationPermitted = contr.getImageModificationDefault();
            usePermission = contr.getImageUseDefault();
        } else {
            modificationPermitted = contr.getNoteModificationDefault();
            usePermission = contr.getNoteUseDefault();
        }
        if (modificationPermitted != null) {
            obj.setModificationPermitted(modificationPermitted);
        }
        if (usePermission != null) {
            obj.setUsePermission(usePermission.byteValue());
        }
    }
}
