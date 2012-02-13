package org.tolweb.btol.tapestry;

import java.util.List;

import org.tolweb.btol.PCRBatch;

public abstract class FindPCRBatchesResults extends ProjectPage {
	public abstract void setBatches(List<PCRBatch> batches);
	public abstract List<PCRBatch> getBatches();
	public abstract PCRBatch getCurrentBatch();
}
