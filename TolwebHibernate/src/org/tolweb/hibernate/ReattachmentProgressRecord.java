package org.tolweb.hibernate;

import java.io.Serializable;

/**
 * @author lenards
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @hibernate.class table="ReattachmentProgress"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class ReattachmentProgressRecord extends AbstractObjectManifestRecord implements Serializable {
	private static final long serialVersionUID = 4406629791294011006L;
	public ReattachmentProgressRecord() {
		
	}
	
	public ReattachmentProgressRecord(ObjectManifestRecord otherRecord) {
		this.setId(otherRecord.getId());
		this.setKeyValue(otherRecord.getKeyValue());
		this.setManifest(otherRecord.getManifest());
		this.setTimestamp(otherRecord.getTimestamp());
		this.setUpdatedBy(otherRecord.getUpdatedBy());
	}
	
	public ObjectManifestRecord toObjectManifest() {
		ObjectManifestRecord newRecord = new ObjectManifestRecord();
		newRecord.setId(this.getId());
		newRecord.setKeyValue(this.getKeyValue());
		newRecord.setManifest(this.getManifest());
		newRecord.setTimestamp(this.getTimestamp());
		newRecord.setUpdatedBy(this.getUpdatedBy());
		return newRecord;
	}
	
	public int hashCode() {
		return this.getId().hashCode() + this.getKeyValue().hashCode();
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else  if (o == null) {
			return false;
		} else if (getClass().equals(o.getClass())) {
			ReattachmentProgressRecord other = (ReattachmentProgressRecord) o;
			if (getKeyValue() != null && other.getKeyValue() != null) {
				return getKeyValue().equals(other.getKeyValue());
			}else {
				return false;
			}
		}
		return false;
	}	
}
