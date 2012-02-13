package org.tolweb.tapestry;

import java.util.List;

import org.tolweb.tapestry.injections.TreeGrowServerInjectable;

/**
 * Provides a view of all outstanding, or open, publication batches.  
 * 
 * These batches may have been unresolved for a few reasons - perhaps 
 * an error occurred during publication or an editor overlooked a 
 * submission email or the submission email never made it to the editor. 
 * 
 * This interface provides a lens into the publication batch process so 
 * that nothing slips through the cracks and is lost.  
 * 
 * @author dmandel (comment written by lenards) 
 *
 */
public abstract class ViewOpenBatches extends EditorSubmitPublication implements TreeGrowServerInjectable {
	/**
	 * Provides a list of publication batches that are open. 
	 * 
	 * An open publication batch is one that does not have true for 
	 * 'isClosed.'  In other words, the batch has not be resolved and 
	 * is waiting to be closed. 
	 * @return a list of open publication batch objects
	 */
	@SuppressWarnings("unchecked")
	public List getOpenBatches() {
		List batches = getPublicationBatchDAO().getOpenPublicationBatches();
		return batches;
	}
}
