/*
 * Created on May 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrowserver.UploadBatch;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ViewBatchHistory extends BasePage implements IExternalPage, NodeInjectable,
		TreeGrowServerInjectable {
    public abstract void setBatch(UploadBatch batch);
    public abstract UploadBatch getBatch();
    
    /* (non-Javadoc)
     * @see org.apache.tapestry.IExternalPage#activateExternalPage(java.lang.Object[], org.apache.tapestry.IRequestCycle)
     */
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        Number batchId = (Number) parameters[0];
        UploadBatch batch = getUploadBatchDAO().getUploadBatchWithId(new Long(batchId.intValue()));
        setBatch(batch);
    }
    
    public MappedNode getRootNode() {
        Long rootId = getUploadBatchDAO().getRootNodeIdForBatch(getBatch());
        MappedNode node = getWorkingNodeDAO().getNodeWithId(rootId);
        return node;
    }
}
