/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ReferencesBlock extends BaseComponent implements PageInjectable, BaseInjectable {
    public abstract String getReferences();
    @SuppressWarnings("unchecked")
    public abstract void setReferencesList(List list);
    
    protected void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
        TextPreparer preparer = getTextPreparer();
        setReferencesList(preparer.getNewlineSeparatedList(getReferences()));
    } 
}
