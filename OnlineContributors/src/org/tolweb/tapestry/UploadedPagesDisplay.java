package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrowserver.UploadBatch;
import org.tolweb.treegrowserver.UploadPage;

public abstract class UploadedPagesDisplay extends BaseComponent implements BaseInjectable {
	@SuppressWarnings("unchecked")
	public abstract Collection getUploadedPages();
	public abstract boolean getIsEditor();
	
	public String getCurrentUrl() {
		String currentPageName = ((UserSubmitPublication) getPage()).getCurrentPageName(); 
		return getUrlBuilder().getWorkingURLForObject(currentPageName);
	}
	public boolean getShowCheckSubgroupsLink() {
		int index = ((UserSubmitPublication) getPage()).getIndex();
		int numPages = getUploadedPages().size();
		return index == 0 && numPages > 1 && !getIsEditor(); 
	}
	public String getCheckSubgroupsLinkText() {
		UploadBatch currentBatch = (UploadBatch) ((UserSubmitPublication) getPage()).getCurrentBatch();
		String linkString = "";
		if (!currentBatch.getAllChildrenUnchecked()) {
			linkString += "un";
		}
		linkString += "check all subgroups";
		return linkString;
	}
	@SuppressWarnings("unchecked")
	public void handleSubgroupsChecking(Integer index) {
		List batches = ((UserSubmitPublication) getPage()).getBatches();
		if (batches == null || index == null || index < 0 || index >= batches.size()) {
			return;
		}
		UploadBatch batchToCheck = (UploadBatch) batches.get(index);

		if (batchToCheck != null && batchToCheck.getSortedUploadedPages() != null) {
			boolean allChildrenUnchecked = batchToCheck.getAllChildrenUnchecked();
			// toggle the setting
			batchToCheck.setAllChildrenUnchecked(!allChildrenUnchecked);
			// now go through the child pages and set all of them to be 
			// published (or not)
			index = 0;
			for (Iterator iter = batchToCheck.getSortedUploadedPages().iterator(); iter.hasNext();) {
				UploadPage nextPage = (UploadPage) iter.next();
				if (index++ == 0) {
					// first page is the root group so skip it
					continue;
				}
				nextPage.setShouldBePublished(allChildrenUnchecked);
			}
			((UserSubmitPublication) getPage()).setDontRefetchBatches(true);
		}
	}
}
