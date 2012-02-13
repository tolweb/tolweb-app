package org.tolweb.dao;

import org.tolweb.hibernate.ReattachmentProgressRecord;

public interface ReattachmentProgressDAO extends BaseDAO {
	public ReattachmentProgressRecord getReattachmentProgressWithId(Long id);
	public ReattachmentProgressRecord getReattachmentProgress(String key);
	public void createReattachmentProgressRecord(ReattachmentProgressRecord record);
	public void saveReattachmentProgressRecord(ReattachmentProgressRecord record);
}
