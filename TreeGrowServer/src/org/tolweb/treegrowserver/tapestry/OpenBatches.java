/*
 * Created on May 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import java.util.List;

import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class OpenBatches extends BasePage implements TreeGrowServerInjectable {
    public List getBatches() {
        return getUploadBatchDAO().getActiveUploadBatches();
    }
}
