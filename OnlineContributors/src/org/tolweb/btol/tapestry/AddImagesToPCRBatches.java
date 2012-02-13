package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;

public abstract class AddImagesToPCRBatches extends ProjectPage implements PCRInjectable,
		PageBeginRenderListener, MiscInjectable, IExternalPage {
	public abstract PCRBatch getCurrentBatch();
	@Persist("flash")
	public abstract List<PCRBatch> getBatches();
	public abstract void setBatches(List<PCRBatch> batches);
	@Persist("flash")
	public abstract boolean getUploaded();
	public abstract void setUploaded(boolean value);
	
	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		Long batchId = (Long) args[0];
		PCRBatch batch = getPCRBatchDAO().getBatchWithId(batchId);
		if (batch != null) {
			List batches = new ArrayList<PCRBatch>();
			batches.add(batch);
			setBatches(batches);
		}
	}
	
	public void pageBeginRender(PageEvent event) {
		if (!getRequestCycle().isRewinding() &&
				(getBatches() == null || getBatches().size() == 0)) {		
			List<PCRBatch> batches = getPCRBatchDAO().getBatchesWithoutImageForContributor(getContributor(), getProject().getId());
			setBatches(batches);
		}
	}
	
	public ILink doUpload() {
		setUploaded(true);
		setBatches(getBatches());
		return getTapestryHelper().getPageServiceLink(getPageName());
	}
}
