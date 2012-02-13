/*
 * Created on Nov 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.Upload;
import org.tolweb.treegrowserver.UploadBatch;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface UploadBatchDAO {
	public UploadBatch getUploadBatchWithId(Long id);
	public UploadBatch getUploadBatchForUpload(Upload upload);
    public Long getUploadBatchIdForUpload(Upload upload);
    public boolean getContributorIsEditorForBatch(Long batchId, Contributor contr);
	public UploadBatch getUploadBatchForDownload(Download download);
	public UploadBatch getActiveUploadBatchForNodes(Set nodeIds);
    public List getActiveUploadedPagesForPage(MappedPage page);
    public UploadBatch getActiveUploadBatchForPage(MappedPage page);    
	public Long getRootNodeIdForBatch(UploadBatch batch);
	public void saveBatch(UploadBatch batch);
	public List getEditorsForBatch(UploadBatch batch);
	/**
	 * Returns the list of active upload batches the user has contributed
	 * to. 
	 * @param contributor
	 * @param onlyIdAndPages TODO
	 * @return
	 */
	public List getActiveBatchesForContributor(Contributor contributor, boolean onlyIdAndPages);
	/**
	 * Returns the actice upload batches that a contributor can edit
	 * @param contributor
	 * @return
	 */
	public List getBatchesContributorCanEdit(Contributor contributor);
	/**
	 * Merges the active batches and editable batches for a contributor
	 * @param contributor
	 * @return
	 */
	public Set getAllBatchesForContributor(Contributor contributor);
	
	public List getActiveUploadBatches();
}
