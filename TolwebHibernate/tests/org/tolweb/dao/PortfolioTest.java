/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.hibernate.Portfolio;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.PortfolioSection;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PortfolioTest extends ApplicationContextTestAbstract {
    private AccessoryPageDAO dao;
    public PortfolioTest(String name) {
        super(name);
        dao = (AccessoryPageDAO) context.getBean("workingAccessoryPageDAO");
    }
    
    public void testPortfolioSave() {
        Portfolio portfolio = constructNewPortfolio();
        dao.savePortfolio(portfolio);
        
        dao.getPortfolioWithId(portfolio.getId());
    }
    
    private Portfolio constructNewPortfolio() {
        Portfolio portfolio = new Portfolio();
        PortfolioSection newSection = new PortfolioSection();
        newSection.setOrder(0);
        SortedSet sections = new TreeSet();
        sections.add(newSection);
        portfolio.setSections(sections);
        SortedSet pages = new TreeSet();
        PortfolioPage page = new PortfolioPage();
        page.setComments("this is a new page");
        page.setIncludeInternetLinks(true);
        page.setIncludeLearningInfo(true);
        page.setIncludeReferences(true);
        page.setOrder(0);
        page.setDestinationId(1);
        page.setDestinationType(PortfolioPage.ACC_PAGE_DESTINATION);
        pages.add(page);
        newSection.setPages(pages);
        return portfolio;
    }
}
