/*
 * Created on Oct 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Hashtable;
import java.util.List;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class JavascriptImageDataBlock extends ImageDataBlock {
    public abstract Object getCurrentKey();
    
    public String getEscapedCurrentKey() {
        return StringUtils.escapeForJavascript((String) getCurrentKey());
    }
    
    public String getEscapedCurrentValue() {
        return StringUtils.escapeForJavascript((String) getKeysToValues().get(getCurrentKey()));        
    }
    
    @SuppressWarnings("unchecked")
	protected void addIfAvailable(List list, Hashtable hash, String name, String value) {
		if (StringUtils.notEmpty(name) && !name.equals("Specimen Condition")) {
		    super.addIfAvailable(list, hash, name, value);
		}
	}    
}
