package org.tolweb.dao;

import org.tolweb.hibernate.ReattachmentProgressRecord;

public class ReattachmentProgressDAOTest extends ApplicationContextTestAbstract {
	private ReattachmentProgressDAO dao;
	
	public ReattachmentProgressDAOTest(String name) {
		super(name);
		//reattachmentProgressDAO
		dao = (ReattachmentProgressDAO)context.getBean("reattachmentProgressDAO");
	}
	
	public void testCreate() {
		ReattachmentProgressRecord record = dao.getReattachmentProgressWithId(new Long(1));
		record.setKeyValue("RHRHR-54444");
		record.setManifest("<xml><foo id=\"34343\"/></xml>");
		record.setUpdatedBy("lenards");
		dao.saveReattachmentProgressRecord(record);
	}
}
