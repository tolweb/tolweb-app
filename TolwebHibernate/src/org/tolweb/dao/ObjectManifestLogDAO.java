package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.ObjectManifestRecord;;

public interface ObjectManifestLogDAO extends BaseDAO {
	public ObjectManifestRecord getObjectManifestRecordWithId(Long id);
	public ObjectManifestRecord getLatestObjectManifestRecordWithNodeId(Long basalNodeId);	
	public ObjectManifestRecord getObjectManifestRecord(String key);
	public void createObjectManifestRecord(ObjectManifestRecord record);
	public void saveObjectManifestRecord(ObjectManifestRecord record);
	public List<ObjectManifestRecord> getAllObjectManifestRecords();
}
