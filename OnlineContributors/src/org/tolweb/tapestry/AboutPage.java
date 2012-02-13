/*
 * Created on Jul 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.hibernate.ArchivedPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;
import org.tolweb.treegrowserver.Download;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AboutPage extends BaseComponent implements PageBeginRenderListener, 
		PageInjectable, TreeGrowServerInjectable, BaseInjectable {
	public abstract String getCopyright();
	@SuppressWarnings("unchecked")
	public abstract Collection getContributors();
	public abstract String getCopyrightYear();
	public abstract Date getFirstOnlineDate();
	public abstract Date getContentChangedDate();
	@SuppressWarnings("unchecked")
	public abstract void setArchivedVersions(List value);
	public abstract void setMostRecentVersionDate(Date date);
	public abstract Date getMostRecentVersionDate();
	public abstract void setHasAnyVersions(boolean value);
	public abstract String getStoredStatusString();
	public abstract void setStoredStatusString(String value);
	public abstract void setPublishErrorMessage(String value);
	public abstract Integer getUsePermission();
	
	private SimpleDateFormat dateFormat;
	public static final String ABOUT_PAGE_DATE_FORMAT_STRING = "dd MMMM yyyy"; 
	
	public AboutPage() {
	    dateFormat = new SimpleDateFormat(ABOUT_PAGE_DATE_FORMAT_STRING);
	    System.out.println("hello friends");
	}
	
	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		if (getIsBranchOrLeaf() && !getPage().getRequestCycle().isRewinding()) {
			ArchivedPageDAO dao = getArchivedPageDAO();
			setMostRecentVersionDate(dao.getMostRecentVersionDateForPage(getPageId()));			
			List versions = dao.getArchivedPagesForPage(getPageId(), false, getMostRecentVersionDate());
			setHasAnyVersions(versions.size() > 0);
			List majorVersions = new ArrayList<ArchivedPage>();
			for (Iterator iter = versions.iterator(); iter.hasNext();) {
				ArchivedPage page = (ArchivedPage) iter.next();
				if (page.getMajorRevision()) {
					majorVersions.add(page);
				}
			}
			setArchivedVersions(majorVersions);
			// set up status string for later use
	    	BranchLeafPageArchiver archiver = getPageArchiver();
	    	String statusString = archiver.getArchivedPageStatusStringFromTolPageStatus(((ViewBranchOrLeaf) getPage()).getTolPage().getStatus(), true);
	    	if (StringUtils.notEmpty(statusString)) {
	    		statusString = " (" + statusString + ")";
	    	}
	    	setStoredStatusString(statusString);			
		}
	}
	
	public String getCopyrightString() {
		return getCopyrightString(getContributors(), getCopyrightYear(), getCopyright());
	}	
	
	public String getCorrespondenceString() {
		return getCorrespondenceString(getContributors(), getTextPreparer());
	}
	
	@SuppressWarnings("unchecked")
	public String getCopyrightString(Collection contributors, String copyrightYear, 
			String additionalCopyright) {
		String returnString = "Page copyright &copy; ";
		if (copyrightYear != null) {
			returnString += copyrightYear + "	";
		}
		List copyrightNames = getCopyrightNames(contributors, additionalCopyright);
		return returnString + StringUtils.returnCommaAndJoinedString(copyrightNames);		
	}
	
	@SuppressWarnings("unchecked")
	private List getCopyrightNames(Collection contributors, String additionalCopyright) {
		Iterator it = contributors.iterator();
		List copyrightNames = new ArrayList();
		while (it.hasNext()) {
			PageContributor contr = (PageContributor) it.next();
			if (contr.getIsCopyOwner()) {
				copyrightNames.add(getTextPreparer().getContributorNameString(contr.getContributor(), true, 
						false, false));
			}
		}
		if (StringUtils.notEmpty(additionalCopyright)) {
			copyrightNames.add(additionalCopyright);
		}		
		return copyrightNames;
	}
	
	@SuppressWarnings("unchecked")
	static String getCorrespondenceString(Collection contributors, TextPreparer textPreparer) {
		String returnString = "";
		Iterator it = contributors.iterator();
		List correspondentsList = new ArrayList();
		boolean seenOne = false;
		while (it.hasNext()) {
			PageContributor contr = (PageContributor) it.next();
			if (contr.getIsContact()) {
				if (!seenOne) {
					returnString += "Correspondence regarding this page should be directed to ";	
				}
				Contributor contributor = contr.getContributor();
				boolean hasEmail = StringUtils.notEmpty(contributor.getEmail());
				if (hasEmail) {
				    correspondentsList.add(contributor.getDisplayName() + " at " + textPreparer.getEncodedMailLinkString(contributor.getEmail(), contributor.getEmail()));
				}
				seenOne = true;				
			}
		}
		returnString += StringUtils.returnCommaAndJoinedString(correspondentsList);
		if (seenOne) {
			returnString += "<br>";
		}
		return returnString;		
	}
	
	public String getFirstOnlineString() {
	    return dateFormat.format(getFirstOnlineDate());
	}
	
	public String getLicenseInfo() {
		if (getUsePermission() != null) {
			int licVal = getUsePermission().intValue();
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(licVal);
			String version = (licInfo.isCreativeCommons()) ? " - " + licInfo.getLicenseVersion() : ""; 
			return "   " + licInfo.toString() + version + "   ";
		} 
		return "[No License Setting...]";
	}

	public String getContentChangedString() {
	    return dateFormat.format(getContentChangedDate());	    
	}
    public boolean getCanPublish() {
        if (getIsBranchOrLeaf()) {
            ViewBranchOrLeaf page = (ViewBranchOrLeaf) getPage();
            boolean canEdit = page.getCanEdit();
            if (canEdit) {
            	Object returnVal = getBatchPusher().getCanPublishPage(page.getTolPage());
            	if (returnVal != null) {
            		String error;
	            	if (Download.class.isInstance(returnVal)) {
	            		Download download = (Download) returnVal;
	            	    error = "This page is attached to a node that's currently downloaded in TreeGrow.  It was checked out by " + 
	            	    	download.getContributor().getDisplayName() + " on " + download.getDownloadDate() + 
	            	    	".  You won't be able to publish the page until the node is checked back into the database."; 
	            		
	            	} else {
	            		error = "This page can only be published if its containing group is also published." +
	            			"If you have questions about the status of the containing group, please contact the group coordinators or treegrow@tolweb.org.";
	            	}
	            	setPublishErrorMessage(error);	            	
	            	return false;
            	} else {
            		return true;
            	}
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public Long getPageId() {
    	return ((ViewBranchOrLeaf) getPage()).getTolPage().getPageId();
    }
    public void setPageId(Long value) {
    	MappedPage tolPage =  getWorkingPageDAO().getPageWithId(value);
    	((ViewBranchOrLeaf) getPage()).setTolPage(tolPage);
    }
    public boolean getIsBranchOrLeaf() {
    	return ViewBranchOrLeaf.class.isInstance(getPage());
    }
    @SuppressWarnings("unchecked")
    public String getAuthorCitationString() {
    	AuthorCitation ac = new AuthorCitation(getContributors());
    	return ac.getCitationString();
    }
    public String getTitleCitationString() {
    	MappedNode node = ((ViewBranchOrLeaf) getPage()).getNode();
    	TitleCitation tc = new TitleCitation(node.getPageSupertitle(), node.getPageTitle(), node.getPageSubtitle());
    	return tc.getCitationString();
    }
    public String getYearCitationString() {
    	YearCitation yc = new YearCitation(getMostRecentVersionDate());
    	return yc.getCitationString();
    }
    public String getPageStatusString() {
    	String period = getHasStatus() ? "." : "";
    	String space = getHasStatus() ? " " : "";
    	return space + getStoredStatusString() + period;
    }
    private boolean getHasStatus() {
    	return StringUtils.notEmpty(getStoredStatusString());
    }    
    public String getVersionDateString() {
    	VersionCitation vc = new VersionCitation(getMostRecentVersionDate(), getHasStatus());
    	return vc.getCitationString();
    }

    public String getCitationUrl() {
    	MappedPage page = ((ViewBranchOrLeaf) getPage()).getTolPage();
    	Date contentChangedDate = getMostRecentVersionDate();
    	if (contentChangedDate == null) {
    		contentChangedDate = new Date();
    	}
    	
    	return getUrlBuilder().getArchiveURLForBranchPage(page, contentChangedDate);
    }
    
    public MappedPage getTolPage() {
    	return ((ViewBranchOrLeaf) getPage()).getTolPage();
    }
    
    public boolean getIsPublicDomain() {
    	return (getUsePermission() != null) ? ContributorLicenseInfo.isPublicDomainCode(getUsePermission().intValue()) : false;
    }
    
    public boolean getIsCreativeCommons() {
    	return (getUsePermission() != null) ? ContributorLicenseInfo.isCreativeCommons(getUsePermission().intValue()) : false;
    }
}
