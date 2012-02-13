package org.tolweb.treegrowserver.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.Upload;
import org.tolweb.treegrowserver.dao.UploadDAO;

/*
 * Created on Nov 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadDAOTest extends ApplicationContextTestAbstract {
    private UploadDAO uploadDao;
    
    public UploadDAOTest(String name) {
        super(name);
        uploadDao = (UploadDAO) context.getBean("uploadDAO");
    }
    
    public void testGetUploadWithId() {
        /*Upload upload = uploadDao.getUploadWithId(new Long(1));
        assertEquals(upload.getIsClosed(), true);
        assertEquals(upload.getIsUndoable(), false);
        Download download = upload.getDownload();
        assertNotNull(download);
        assertEquals(download.getIpAddress(), "150.135.45.57");
        assertEquals(download.getIsActive(), false);
        assertEquals(download.getContributor().getId(), 664);
        assertEquals(download.getDownloadId(), new Long(1));
        assertEquals(download.getRootNode().getName(), "Bembidion");
        assertEquals(download.getDownloadedNodes().size(), 2);*/
    }
    
    public void testChangedQuery() {
    	Calendar calendar = GregorianCalendar.getInstance();
    	calendar.set(Calendar.YEAR, 2006);
    	calendar.set(Calendar.MONTH, 3);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	List<Object[]> changes = uploadDao.getContributorsDatesAndNodesRecentlyChanged(9076L, calendar.getTime());
    	System.out.println("number of rows fetched: " + changes.size());
    	for (Object[] objects : changes) {
			System.out.println("contributor: " + objects[0] + "  " + objects[1] + ", date: " + objects[2] + ", root: " + objects[3]);
		}
    }
}
