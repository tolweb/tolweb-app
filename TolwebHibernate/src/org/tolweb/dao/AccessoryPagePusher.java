/*
 * Created on Jul 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.dao;

import org.tolweb.hibernate.MappedAccessoryPage;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AccessoryPagePusher {
	
	public void pushAcessoryPageToDB(MappedAccessoryPage pageToPush, AccessoryPageDAO dao) {
		MappedAccessoryPage page = dao.getAccessoryPageWithId(pageToPush.getAccessoryPageId());
		if (page == null) {
			System.out.println("\n\n\ninserting new page!\n\n\n");
			dao.addAccessoryPageWithId(pageToPush.getAccessoryPageId(), pageToPush.getDiscriminatorValue());
			System.out.println("\n\n\njust inserted with id: " + pageToPush.getAccessoryPageId() + "\n\n\n");
		}
		// also need to check and see if the portfolio exists.  if it doesn't, create it
		// before we do the copy
		Long portfolioId = pageToPush.getPortfolio() != null ? pageToPush.getPortfolio().getId() : null;
		if (portfolioId != null) {
			if (dao.getPortfolioWithId(portfolioId) == null) {
				dao.addPortfolioWithId(portfolioId);
			}
		}
		/*System.out.println("saving page from other dao");
		try {
			MappedAccessoryPage otherPage = (MappedAccessoryPage) ObjectCloner.deepCopy(pageToPush);
			if (otherPage.getContributors() != null) {
			    otherPage.setContributors(new TreeSet(otherPage.getContributors()));
			}
			if (otherPage.getNodesSet() != null) {
				otherPage.setNodesSet(new TreeSet(otherPage.getNodesSet()));			    
			}
			if (otherPage.getInternetLinks() != null) {
			    otherPage.setInternetLinks(new TreeSet(otherPage.getInternetLinks()));
			}
			dao.saveAccessoryPage(otherPage);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		dao.copyAccessoryPageToDB(pageToPush);
		System.out.println("------------- just finished copying ---------------\n\n");
	}
}
