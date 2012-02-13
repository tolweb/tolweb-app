/*
 * Created on Aug 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PasswordSent extends AbstractWrappablePage implements BaseInjectable {
    public abstract void setContributor(Contributor contr);
    public abstract Contributor getContributor();
    
    public boolean getIsTreehouses() {
        return getContributor().getContributorType() == Contributor.TREEHOUSE_CONTRIBUTOR;
    }
}
