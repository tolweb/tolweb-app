package org.tolweb.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tolweb.btol.dao.ChromatogramBatchDAO;

public class ChromatogramBatchDAOTest extends PCRBatchDAOTest {
	private ChromatogramBatchDAO chroBatchDAO;
	
	public ChromatogramBatchDAOTest(String name) {
		super(name);
		chroBatchDAO = (ChromatogramBatchDAO) context.getBean("chromatogramBatchDAO");
	}
	
	public void testGetChromatogramFilenamesInBatchNamed() {
		List<String> filenames = chroBatchDAO.getChromatogramFilenamesInBatchWithName("BTOL001");
		for (String string : filenames) {
			System.out.println("next chro is: " + string);
		}
		List<String> otherfilenames = doTestChroFilenameQuery();
		Set<String> chroBatchSet = new HashSet<String>(filenames);
		Set<String> pcrBatchSet = new HashSet<String>(otherfilenames);
		chroBatchSet.retainAll(pcrBatchSet);
		for (String string : chroBatchSet) {
			System.out.println("set chro is: " + string);
		}		
	}
}
