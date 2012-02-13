/*
 * Created on Jul 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Portfolio;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PortfolioPages extends BaseComponent {
    public MappedAccessoryPage getTreehouse() {
        return ((ViewTreehouse) getPage()).getTreehouse();
    }
    public Portfolio getPortfolio() {
        return getTreehouse().getPortfolio();
    }
}
