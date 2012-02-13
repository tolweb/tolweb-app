package org.tolweb.treegrowserver.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.PublicationBatch;

public interface PublicationBatchDAO {
    public void saveBatch(PublicationBatch batch);
    public PublicationBatch getBatchWithId(Long id);
    public PublicationBatch getBatchForSubmittedNode(Long nodeId);
    public boolean getContributorCanPublishBatch(Contributor contr, PublicationBatch batch);
    public List<PublicationBatch> getOpenPublicationBatches();
    public Date getLastPublishedDateForPage(Long pageId);
    public Date getLastPublishedDateForNode(Long nodeId);
}
