/**
 * 
 */
package org.tolweb.tapestry;

import org.apache.tapestry.form.IPropertySelectionModel;

public final class ModificationPermittedModel implements IPropertySelectionModel {
	public int getOptionCount() {
	    return 2;
	}

	public Object getOption(int index) {
	    if (index == 0) {
	        return Boolean.valueOf(false);
	    } else {
	        return Boolean.valueOf(true);
	    }
	}

	public String getLabel(int index) {
	    if (index == 0) {
	        return "Minor Modification Only";
	    } else {
	        return "Modification Permitted";
	    }
	}

	public String getValue(int index) {
	    return Integer.toString(index);
	}

	public Object translateValue(String value) {
	    int index = Integer.parseInt(value);
	    return getOption(index);
	}
}