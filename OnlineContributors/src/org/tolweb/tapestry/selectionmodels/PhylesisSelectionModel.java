package org.tolweb.tapestry.selectionmodels;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.MappedNode;

public class PhylesisSelectionModel implements IPropertySelectionModel {

	/*
	public static final int MONOPHYLETIC = 0;
    public static final int NOT_MONOPHYLETIC = 1;
    public static final int MONOPHYLY_UNCERTAIN = 2;	 
    
                <option selected>monophyletic</option>
		<option>monophyly uncertain</option>
		<option>not monophyletic</option>    
    */
	
	public int getOptionCount() {
		return 3;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	public String getLabel(int arg0) {
		switch (arg0) {
			case MappedNode.MONOPHYLETIC: return "monophyletic";
			case MappedNode.MONOPHYLY_UNCERTAIN: return "monophyly uncertain";
			default: return "not monophyletic";
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}

}
