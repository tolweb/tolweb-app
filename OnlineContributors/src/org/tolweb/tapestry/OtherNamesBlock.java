package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.treegrow.main.StringUtils;

@ComponentClass
public abstract class OtherNamesBlock extends BaseComponent implements PageBeginRenderListener {
	
	@Parameter(required = true)
	public abstract SortedSet getSynonyms();
	
	public abstract SortedSet<MappedOtherName> getFilteredSynonyms();
	public abstract void setFilteredSynonyms(SortedSet<MappedOtherName> synonyms);
	
	public abstract MappedOtherName getCurrentOtherName();
	public abstract void setCurrentOtherName(MappedOtherName otherName);
	
	public abstract String getCommonNamesString();
	public abstract void setCommonNamesString(String nameString);
	
	public String getFormattedCurrentOtherName() {
		return otherNameFormatter(getCurrentOtherName());
	}
	
	public String otherNameFormatter(MappedOtherName othername) {
		StringBuilder output = new StringBuilder();
		if (othername.getItalicize()) {
			output.append("<em>" + othername.getName() + "</em>");
		} else {
			output.append(othername.getName());
		} 
		if (othername.hasAuthorityInfo() || othername.hasIncompleteAuthorityInfo()) {
			output.append(" <span class=\"authority\">");
			if (StringUtils.notEmpty(othername.getAuthority())) {
				output.append(othername.getAuthority());
			}
			if (hasAuthorityYearValue(othername)) {
				output.append(" " + othername.getAuthorityYear());
			}
			if (StringUtils.notEmpty(othername.getComment())) {
				output.append(" " + othername.getComment());
			}			
			output.append("</span>");
		} 
		return output.toString();
	}

	private boolean hasAuthorityYearValue(MappedOtherName othername) {
		return othername.getAuthorityYear() != null
				&& !othername.getAuthorityYear().equals(Integer.valueOf(0));
	}
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			initializeStructures();
			determineFilterSynonymsAndBuildCommonNames();
		}
	}

	private void determineFilterSynonymsAndBuildCommonNames() {
		StringBuilder commonNames = new StringBuilder();
		if (!getSynonyms().isEmpty()) {
			for (Iterator itr = getSynonyms().iterator(); itr.hasNext();) {
				MappedOtherName moname = (MappedOtherName) itr.next();
				if (moname.shouldDisplay()) {
					getFilteredSynonyms().add(moname);
				}
				if (moname.getIsCommonName() && !moname.getIsDontList()) {
					commonNames.append(moname.getName() + ", ");
				}
			}
			if (commonNames.length() > 2) {
				setCommonNamesString(commonNames.toString().substring(0,
						commonNames.length() - 2));
			}
		}
	}

	private void initializeStructures() {
		setFilteredSynonyms(new TreeSet<MappedOtherName>());
		setCommonNamesString("");
	}
}
