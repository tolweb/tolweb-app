package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.injections.PCRInjectable;

public abstract class ReactionsUploaded extends ProjectPage implements PCRInjectable {
	public abstract void setBatches(List<PCRBatch> batches);
	@Persist("session")
	public abstract List<PCRBatch> getBatches();
	public abstract void setCurrentBatch(PCRBatch value);
	public abstract PCRBatch getCurrentBatch();
	
	public void deleteAll() {
		getPCRBatchDAO().deleteAll(getBatches());
		getBatches().clear();
	}
}
