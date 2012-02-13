package org.tolweb.tapestry.selectionmodels;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.MappedNode;

public class ConfidenceSelectionModel implements IPropertySelectionModel {
/*
	        <li><label>Confidence of Placement in Tree
            <select>
                <option selected>confident</option>

		<option>incertae sedis in putative position</option>
		<option>incertae sedis position unspecified</option>
            </select>
            </label></li>
 */
	public int getOptionCount() {
		return 3;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(arg0);
	}
	/* legacy type-info - notes
	 * int | old text constants | new taxa import xml "ingest" usage 
	 * -----------------------------------------------------------
	    0 = "incertae sedis off"  = confident
	    1 = "incertae sedis in putative position"   = tentative
	    2 = "incertae sedis position unspecified"   = incertaesedis
		public static final int INCERT_OFF = 0;
		public static final int INCERT_PUTATIVE = 1;
		public static final int INCERT_UNSPECIFIED = 2;        
	*/		
	public String getLabel(int arg0) {
		switch (arg0) {
			case MappedNode.INCERT_OFF: return "confident";
			case MappedNode.INCERT_PUTATIVE: return "incertae sedis in putative position";
			//MappedNode.INCERT_UNSPECIFIED 
			default: return "incertae sedis position unspecified";
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return Integer.valueOf(arg0);
	}	
}
