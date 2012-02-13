/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MediaEditPrivilegesNotesSubmit extends EditMediaComponent {
    public IPrimaryKeyConvertor getPermissionConvertor() {
        return new PermissionConvertor(getMedia().getPermissionsSet());
    }
}
