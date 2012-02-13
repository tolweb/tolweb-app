package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.URLBuilder;
import org.tolweb.treegrow.main.StringUtils;

public class CitationCreator {
	private List<PageCitationComponent> components; 
	
	private BranchLeafPageArchiver pageArchiver;
	private URLBuilder urlBuilder;
	private ArchivedPageDAO dao;
	// pageArchiver
	// urlBuilder
	// mpage
	// getMostRecentVersionDate()
	
	public CitationCreator(BranchLeafPageArchiver pageArchiver, URLBuilder urlBuilder, ArchivedPageDAO dao) {
		components = new ArrayList<PageCitationComponent>();
		this.pageArchiver = pageArchiver;
		this.urlBuilder = urlBuilder;
		this.dao = dao; 
	}
	
	public String createCitation(MappedPage mpage) {
		initializeStructure(mpage); // object up a component representation
		StringBuilder citation = new StringBuilder();
		// for each component, get their contribution to the citation
		for (PageCitationComponent cmp : components) {
			citation.append(cmp.getCitationString());
		}
		components.clear(); // need this list empty for the next call to the object
		return citation.toString();
	}

	public String cleanCitation(String htmlFormatted) {
		return htmlFormatted.replaceAll("<([^>]*)>", "");
	}
	
	private void initializeStructure(MappedPage mpage) {
		String pageStatusString = pageArchiver.getArchivedPageStatusStringFromTolPageStatus(mpage.getStatus(), true);
		Date mostRecentVersion = dao.getMostRecentVersionDateForPage(mpage.getPageId());
		String archiveUrl = urlBuilder.getArchiveURLForBranchPage(mpage, mostRecentVersion);
		Date contentChangeDate = (mostRecentVersion != null) ? mostRecentVersion : new Date();
		
		AuthorCitation ac = initializeAuthorCitation(mpage);
		YearCitation yc = new YearCitation(mostRecentVersion);
		TitleCitation tc = initializeTitleCitation(mpage);
		VersionCitation vc = new VersionCitation(contentChangeDate, StringUtils.notEmpty(pageStatusString));
		PageStatusCitation pc = new PageStatusCitation(pageStatusString);
		CitationUrl cu = new CitationUrl(archiveUrl);
		components.add(ac);
		components.add(yc);
		components.add(tc);
		components.add(vc);
		components.add(pc);
		components.add(cu);
	}

	@SuppressWarnings("unchecked")
	private AuthorCitation initializeAuthorCitation(MappedPage mpage) {
		return new AuthorCitation(mpage.getContributors());
	}	

	private TitleCitation initializeTitleCitation(MappedPage mpage) {
		MappedNode mnode = mpage.getMappedNode();
		return new TitleCitation(mnode.getPageSupertitle(), mnode.getPageTitle(), mnode.getPageSubtitle());
	}	
}
