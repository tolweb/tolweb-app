package org.tolweb.btol.tapestry.selection;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.treegrow.main.StringUtils;

public class MinutesSecondsSelectionModel implements IPropertySelectionModel {
	// 5 min.  -- for now
	private static final int MAX_SECONDS = 300;
	private static final int SECOND_INCREMENT = 5;
	
	public int getOptionCount() {
		return MAX_SECONDS / SECOND_INCREMENT;
	}
	public Object getOption(int arg0) {
		return Integer.valueOf(translateIndexToSeconds(arg0));
	}
	public String getLabel(int arg0) {
		int totalSeconds = translateIndexToSeconds(arg0);
		return StringUtils.getTimeStringFromSeconds(totalSeconds);
	}
	private int translateIndexToSeconds(int index) {
		return (index + 1) * SECOND_INCREMENT;
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return getOption(Integer.parseInt(arg0));
	}
}
