package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.hibernate.EditPermission;

public class PermissionConvertor implements IPrimaryKeyConvertor {
	@SuppressWarnings("unchecked")
	private Collection permissions;
	
	@SuppressWarnings("unchecked")
	public PermissionConvertor(Collection permissions) {
		this.permissions = permissions;
	}

    public Object getPrimaryKey(Object objValue) {
        return Integer.valueOf(((EditPermission) objValue).getContributorId());
    }
    
    @SuppressWarnings("unchecked")
    public Object getValue(Object objPrimaryKey) {
        Integer contributorId = (Integer) objPrimaryKey;
        for (Iterator iter = permissions.iterator(); iter.hasNext();) {
            EditPermission permission = (EditPermission) iter.next();
            int contrId = permission.getContributorId();
            if (contrId == contributorId.intValue()) {
                return permission;
            }
        }
        return null;
    }
}
