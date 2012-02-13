/*
 * Created on Jun 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PagePropertySetter {
	@SuppressWarnings("unchecked")
    public void setPageProperties(IPage page, Hashtable values) {
        if (page != null && values != null) {
        	for (Iterator itr = values.entrySet().iterator(); itr.hasNext(); ) {
        		Map.Entry entry = (Map.Entry)itr.next();
        		String nextKey = (String) entry.getKey();
        		Object nextValue = entry.getValue();
	            try {
	                PropertyUtils.write(page, nextKey, nextValue);
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }        		
        	}
// OLD WAY TO DO IT:         	
//	        for (Iterator iter = values.keySet().iterator(); iter.hasNext();) {
//	            String nextKey = (String) iter.next();
//	            Object nextValue = values.get(nextKey);
//	            try {
//	                PropertyUtils.write(page, nextKey, nextValue);
//	            } catch (Exception e) {}
//	        }
        }
    }
}
