package org.tolweb.tapestry;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.treegrowserver.SubmittedPage;

public class RevisionTypeModel implements IPropertySelectionModel {
	public int getOptionCount() {
		return 3;
	}
	public Object getOption(int index) {
		return Integer.valueOf(index);
	}
	public String getLabel(int index) {
		switch (index) {
			case SubmittedPage.MINOR_REVISION: return "Minor revision - not a new page version";
			case SubmittedPage.REGULAR_REVISION: return "Regular revision";
			case SubmittedPage.MAJOR_REVISION: return "Major revision";
			default: return "";
		}
	}
	public String getValue(int index) {
		return Integer.toString(index);
	}
	public Object translateValue(String value) {
		return Integer.valueOf(value);
	}
}
