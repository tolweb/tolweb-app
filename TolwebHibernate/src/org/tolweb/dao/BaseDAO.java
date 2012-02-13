package org.tolweb.dao;

import java.util.Collection;

public interface BaseDAO {
	public void deleteAll(Collection objects);
    public void deleteObject(Object object);
    public Object getObjectWithId(Class classObj, Number id);
}
