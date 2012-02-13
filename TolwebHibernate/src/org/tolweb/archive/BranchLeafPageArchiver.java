package org.tolweb.archive;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.hibernate.ArchivedPage;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.FileUtils;
import org.tolweb.treegrow.main.XMLConstants;

public class BranchLeafPageArchiver {
	private String archiveCommand;
	private String archiveDirectory;
	private ArchivedPageDAO archivedPageDAO;
	private static final String DATE_DIRECTORY_FORMAT = "yyyyMMdd";
	
	public void archivePage(MappedPage page, boolean isMajorRevision, boolean isNewPage) {
		if (page != null) {
			String cladename = page.getGroupName();
			cladename = cladename.replaceAll("\\s", "_");
			cladename = cladename.replace("(", "\\(");
			cladename = cladename.replace(")", "\\)");
			cladename = cladename.replace("'", "\\'");
			// need to strip out the slashes for the fungi groups with slashes in their names
			cladename = cladename.replace("/", "");
			Date date = getRevisionDateFromDate(new Date());
			String dateString = getDateStringFromDate(date);	
			String commandToRun = getArchiveCommand() + " " + cladename + " " + page.getMappedNode().getNodeId()
				+ " " + dateString;
			try {
				Runtime.getRuntime().exec(commandToRun);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getArchivedPageDAO().addArchivedPageForPage(page.getPageId(), cladename, 
					isMajorRevision, page.getMappedNode().getNodeId(), page.getStatus(), isNewPage);
		}
	}
	
	private String getDateStringFromDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(DATE_DIRECTORY_FORMAT);
		String dateString = dateFormat.format(date);	
		return dateString;
	}
	
	/*public String getArchiveURLForArchivedPage(ArchivedPage page) {
		String archiveURL = getArchiveURLPrefix();
		archiveURL += getPathSuffixString(page);
		return archiveURL;
	}
	*/
	
	public String getArchivedPageContent(ArchivedPage page, String currentURL) {
		String path = getArchiveDirectory() + getPathSuffixString(page);
		String fileContents = "";
		try {
			fileContents = FileUtils.getFileContents(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}
		// need to do some changing of text in order to make it a real archived page
		// 1) if the page is not a major revision, add the meta noindex tag so that
		//    spiders don't include this in their search results
		if (!page.getMajorRevision()) {
			Pattern headPattern = Pattern.compile("<head>(.*)</head>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher matcher = headPattern.matcher(fileContents);
			// use two variants because the MSN search appears to want a little different
			String noIndexTag = "\n<meta name=\"robots\" content=\"noindex, nofollow\"/>\n<meta name=\"*\" content=\"noindex, nofollow\"/>";			
			fileContents = matcher.replaceAll("<head>" + noIndexTag + "$1</head>");
		}
		// 2) add the little archive message so people realize that they are looking
		// 	  at an archived version of the page -- add the message right after the 
		Pattern quickNavPattern = Pattern.compile("(<div\\s+id=\"quicknav\"[^>]+>.*?</div>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = quickNavPattern.matcher(fileContents);
		String archiveMessageString = "<div id=\"betaalert\"> This is an archived version of a Tree of Life page. For up-to-date information, please refer to the <a href=\"" + 
			currentURL + "\">current version of this page</a>.</div>"; 
		fileContents = matcher.replaceFirst("$1" + archiveMessageString);
		return fileContents;
	}
	
	private String getPathSuffixString(ArchivedPage page) {
		String dateString = getDateStringFromDate(page.getArchiveDate());
		// escape any backslashes in case they are there.  they are necessary for scripting purposes
		// but they don't actually exist on the filesystem		
		String safePageTitle = page.getPageTitle().replace("\\", "");
		return page.getNodeId() + "/" + dateString + "/" + safePageTitle + ".html";		
	}
	
	public String getArchivedPageStatusStringFromTolPageStatus(String status, boolean ignoreComplete) {
        if (status.equals(MappedPage.PEERREVIEWED) || status.equals(XMLConstants.PEER_REVIEWED)) {
        	if (!ignoreComplete) {
        		return "peer-reviewed";
        	} else {
        		return "";
        	}
        } else if (status.equals(XMLConstants.TOL_REVIEWED)) {
        	if (!ignoreComplete) {
        		return "tol-reviewed";
        	} else {
        		return "";
        	}
        } else if (status.equals(MappedPage.COMPLETE) || status.equals(XMLConstants.COMPLETE)) {
        	if (!ignoreComplete) {
        		return "complete";
        	} else {
        		return "";
        	}
        } else if (status.equals(MappedPage.UNDERCONSTRUCTION) || status.equals(XMLConstants.UNDER_CONSTRUCTION)) {
            return "under construction";
        } else if (status.equals(MappedAccessoryPage.NOTE)) {
            return "note";
        } else {
            return "temporary";
        }		
	}
	
	/**
	 * Set the time to be midnight on that day
	 * @param date
	 * @return
	 */
	public Date getRevisionDateFromDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		//calendar.add(Calendar.DAY_OF_MONTH, 1);		
		// setting both of these because the horrible date/calendar implementation
		// is unclear as to which of these will take effect by default.
		// did I mention that the date and calendar implementation in java sucks?
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		date = calendar.getTime();
		return date;
	}

	public String getArchiveCommand() {
		return archiveCommand;
	}

	public void setArchiveCommand(String archiveCommand) {
		this.archiveCommand = archiveCommand;
	}

	public ArchivedPageDAO getArchivedPageDAO() {
		return archivedPageDAO;
	}

	public void setArchivedPageDAO(ArchivedPageDAO archivedPageDAO) {
		this.archivedPageDAO = archivedPageDAO;
	}

	public String getArchiveDirectory() {
		return archiveDirectory;
	}

	public void setArchiveDirectory(String archiveDirectory) {
		this.archiveDirectory = archiveDirectory;
	}
}
