package org.tolweb.tapestry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.tapestry.BaseComponent;
import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.hibernate.ArchivedPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class ArchivedVersionsList extends BaseComponent implements PageInjectable, BaseInjectable {
	public abstract ArchivedPage getCurrentArchivedVersion();
	private DateFormat dateFormat;
	
	public ArchivedVersionsList() {
		dateFormat = new SimpleDateFormat(AboutPage.ABOUT_PAGE_DATE_FORMAT_STRING);
	}
	
    public String getCurrentArchiveUrl() {
    	MappedPage tolPage = ((AbstractBranchOrLeafPage) getPage()).getTolPage();
    	return getUrlBuilder().getArchiveURLForBranchPage(tolPage, getCurrentArchivedVersion().getArchiveDate());
    }
    public String getCurrentArchiveDateString() {
    	return dateFormat.format(getCurrentArchivedVersion().getArchiveDate());
    }
    public String getCurrentArchiveStatus() {
    	BranchLeafPageArchiver archiver = getPageArchiver();    	
    	return archiver.getArchivedPageStatusStringFromTolPageStatus(getCurrentArchivedVersion().getStatus(), false); 
    }	
    public String getPageTitle() {
    	return ((AbstractBranchOrLeafPage) getPage()).getNode().getActualPageTitle(true, false, false);
    }
}
