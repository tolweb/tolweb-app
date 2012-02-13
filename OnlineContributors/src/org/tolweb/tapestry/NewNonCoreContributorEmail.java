package org.tolweb.tapestry;

import org.tolweb.treegrow.main.StringUtils;

public abstract class NewNonCoreContributorEmail extends NewScientificContributorEmail {
	public abstract Long getFirstNodeId();
	public abstract String getNodesString();
	public String getListPageUrl() {
		return "http://tolweb.org/onlinecontributors/app?service=external&page=PeopleList&sp=l" + getFirstNodeId(); 		
	}
	public String getGroupOrGroups() {
		String returnString = "group";
		if (getIsMultipleGroups()) {
			returnString += "s";
		}
		return returnString;
	}
	public String getFirstNodeString() {
		String nodesString = getNodesString();
		if (StringUtils.notEmpty(nodesString)) {
			int commaIndex = nodesString.indexOf(',');
			if (commaIndex == -1) {
				// just one node, no comma, so return the whole thing
				return nodesString;
			} else {
				return nodesString.substring(0, commaIndex);
			}
		} else {
			return "";
		}
	}
	private boolean getIsMultipleGroups() {
		return getNodesString().indexOf(',') != -1;
	}
}
