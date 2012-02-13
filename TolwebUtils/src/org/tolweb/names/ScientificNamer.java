package org.tolweb.names;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.treegrow.main.StringUtils;

public class ScientificNamer {
	private String nodeName;
	private String authority; 
	private int authorityYear;
	private boolean newCombination;
	private StringBuilder name;  	
	
	public ScientificNamer(MappedNode nd) {
		nodeName = nd.getName();
		authority = nd.getNameAuthority();
		if (nd.getAuthorityDate() != null) {
			authorityYear = nd.getAuthorityDate().intValue();
		}
		newCombination = nd.getIsNewCombination();
		name = new StringBuilder();
		createFormatName();
	}	

	public ScientificNamer(MappedOtherName moname) {
		nodeName = moname.getName();
		authority = moname.getAuthority();
		if (moname.getAuthorityYear() != null) {
			authorityYear = moname.getAuthorityYear().intValue();
		}
		newCombination = false;
		name = new StringBuilder();
		createFormatName();
	}
	
	private void createFormatName() {
		name.append(nodeName + " ");
		boolean authPresent = StringUtils.notEmpty(authority);
		boolean yearPresent = authorityYear != 0;
		
		if (newCombination && (authPresent || yearPresent)) {
			name.append("(");
		}
		if (authPresent) {
			name.append(authority);
		}
		if (yearPresent) {
			name.append(authPresent ? " " : "");
			name.append(authorityYear);
		}
		if (newCombination && (authPresent || yearPresent)) {
			name.append(")");			
		}		
	}
	
	public String getName() {
		return name.toString().trim();
	}
}
