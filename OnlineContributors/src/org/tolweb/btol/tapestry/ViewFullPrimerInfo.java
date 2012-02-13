package org.tolweb.btol.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.btol.Primer;
import org.tolweb.btol.injections.PrimerInjectable;

public abstract class ViewFullPrimerInfo extends AbstractViewAllObjects implements IExternalPage, PrimerInjectable {
    public abstract void setPrimer(Primer value);

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
    	Long primerId = (Long) parameters[0];
    	setPrimer(getPrimerDAO().getPrimerWithId(primerId, getProject().getId()));
    }
}
