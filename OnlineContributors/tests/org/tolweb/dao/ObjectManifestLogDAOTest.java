package org.tolweb.dao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.hibernate.ObjectManifestRecord;

public class ObjectManifestLogDAOTest extends ApplicationContextTestAbstract {
	private ObjectManifestLogDAO dao; 
	
	public ObjectManifestLogDAOTest(String name) {
		super(name);
		//objectManifestLogDAO
		dao = (ObjectManifestLogDAO)context.getBean("objectManifestLogDAO");
	}
	
	public void rahrtestCreate() {
		ObjectManifestRecord record = new ObjectManifestRecord();
		record.setKeyValue("RHRHR-54444");
		record.setManifest("<xml/>");
		record.setUpdatedBy("lenards");
		dao.createObjectManifestRecord(record);
	}
	
	public void testFetchXPath() {
		List<ObjectManifestRecord> records = dao.getAllObjectManifestRecords();
		assertNotNull(records);
		assertTrue(records.size() >= 1);
		ObjectManifestRecord record = records.get(0);
		// basal-node-id=\"([0-9]*)\"

		Pattern pattern = Pattern.compile("basal-node-id=\"([0-9]*)\"", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(record.getManifest());
		if (matcher.find()) {
			String match = matcher.group(1);
			System.out.println("match: " + match);
		}
//		try {		
//	    	Builder parser = new Builder();
//	    	
//	    	Document doc = parser.build(new StringReader(record.getManifest()));
//	    	Nodes result = doc.query("/tree-of-life-web/@basal-node-id");
//		} catch (ParsingException ex) {
//			System.out.println("manifest is not well-formed. [" + record.getManifest() + "]");
//			System.out.println(ex.getMessage());
//		} catch (IOException ioe) {
	 
	//}   		
		System.out.println("basal-node-id: " + record.getBasalNodeId());
		assertNotNull(record.getBasalNodeId());
	}
}
