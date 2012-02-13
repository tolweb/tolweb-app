package org.tolweb.content.preparers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;

import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.PageContentElements;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.ImageUtils;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.PageContributor;

public class PageContentPreparer extends ContentPreparer {
	public static final String NS = "http://www.eol.org/transfer/content/0.1";

	public PageContentPreparer(ContentParameters params, PageDAO pageDao,
			NodeDAO publicNodeDao, ImageDAO imageDao, ImageUtils imageUtils) {
		setParams(params);
		setPageDao(pageDao);
		setPublicNodeDao(publicNodeDao);
		setImageDao(imageDao);
		setImageUtils(imageUtils);
	}

	@Override
	public void processContent() {

		// TODO the "depth limit" value from content-parameter to temper the
		// number of objects fetched by page dao
		// TODO look at the XML Server code in the current TreeStructureService
		// as a way to implement recursively

		// use page dao to mapped page objects
		MappedNode mnode = getPublicNodeDao().getNodeWithId(
				getParams().getNodeId());
		MappedPage mpage = null;
		if (mnode != null) {
			mpage = getPageDao().getPage(mnode);
		}

		// mpage
		DaoBundle daos = new DaoBundle();
		daos.setPageDAO(getPageDao());
		daos.setPublicNodeDAO(getPublicNodeDao());
		daos.setImageDAO(getImageDao());
		daos.setImageUtils(getImageUtils());

		if (mpage != null) {
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(mpage
					.getUsePermission());
			if (licInfo.matchesContentLicenseClass(getParams()
					.getRequestedLicenseClass())) {
				finishContentProcessing(mpage, daos);
			} else {
				handleContentLicenseMismatch();
			}

		} else {
			handleContentProcessingError();
		}
	}

	@SuppressWarnings("unchecked")
	protected void finishContentProcessing(MappedPage mpage, DaoBundle daos) {
		Element root = new Element(PageContentElements.ROOT_SUCCESS,
				PageContentPreparer.NS);
		setPreparedDocument(new Document(root));

		// use co-preparers to assemble xml document response
		Element pages = new Element(PageContentElements.PAGES,
				PageContentPreparer.NS);
		PageCoPreparer pageCoPrep = new PageCoPreparer();
		pageCoPrep.setContentSource(mpage, daos, pages);
		pageCoPrep.setPreparerNamespace(PageContentPreparer.NS);
		pageCoPrep.processContent();

		Element contributorsElement = new Element(
				PageContentElements.CONTRIBUTORS, ContentPreparer.NS);
		ContributorCoPreparer contrCoPrep = new ContributorCoPreparer();
		Set contributorSet = getContributorsFromPageContributors(mpage
				.getContributors());
		contrCoPrep.setContentSource(contributorSet, daos, contributorsElement);
		contrCoPrep.setPreparerNamespace(PageContentPreparer.NS);
		contrCoPrep.processContent();

		Element sourcesElement = new Element(PageContentElements.SOURCES,
				ContentPreparer.NS);
		SourceCoPreparer srcCoPrep = new SourceCoPreparer();
		srcCoPrep.setContentSource(mpage, daos, sourcesElement);
		srcCoPrep.setPreparerNamespace(PageContentPreparer.NS);
		srcCoPrep.processContent();

		root.appendChild(pages);
		root.appendChild(contributorsElement);
		root.appendChild(sourcesElement);
	}

	protected void handleContentProcessingError() {
		System.out
				.println("\t content processing error - issues with node-id or fetching mapped page...");
		Element root = new Element(PageContentElements.ROOT_ERROR,
				PageContentPreparer.NS);
		setPreparedDocument(new Document(root));
		Element message = new Element("message", PageContentPreparer.NS);
		message
				.appendChild(new Text(
						"an error has occurred while attempting to fetch information related to node-id: "
								+ getParams().getNodeId()));
		Element reminder = new Element("suggestion", PageContentPreparer.NS);
		reminder
				.appendChild(new Text(
						"ensure that you're using the Group ID Search or Tree Structure Services discussed on http://webservices.tolweb.org/ for obtaining node-ids"));
		root.appendChild(message);
		root.appendChild(reminder);
	}

	protected void handleContentLicenseMismatch() {
		System.out.println("\t content license class mismatch occurred... ");
		Element root = new Element(PageContentElements.ROOT_SUCCESS,
				PageContentPreparer.NS);
		setPreparedDocument(new Document(root));
		Element message = new Element("message", PageContentPreparer.NS);
		message
				.appendChild(new Text(
						"Sorry, requested content not available through this service."));
		message.addAttribute(new Attribute("license-class", getParams()
				.getRequestedLicenseClass().toString()));
		root.appendChild(message);
	}

	@SuppressWarnings("unchecked")
	protected Set getContributorsFromPageContributors(SortedSet pageContributors) {
		Set contributors = new HashSet();
		for (Iterator itr = pageContributors.iterator(); itr.hasNext();) {
			PageContributor pageContr = (PageContributor) itr.next();
			Contributor contr = pageContr.getContributor();
			if (contr != null) {
				contributors.add(contr);
			}
		}
		return contributors;
	}
}
