package org.tolweb.btol.tapestry;

import java.util.List;

import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.injections.PCRInjectable;

public abstract class ViewAllPCRBatches extends ProjectPage implements PCRInjectable {
	public abstract PCRBatch getCurrentBatch();

	
	public List<PCRBatch> getAllBatches() {
		return getPCRBatchDAO().getAllBatchesInProject(getProject().getId());
	}
}
