/*
 * Created on Dec 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PagePusher {
	public MappedPage pushPageToDB(MappedPage pageToPush, MappedNode destinationNode, PageDAO dao, PageDAO workingDAO) throws Exception {
		boolean exists = dao.getPageExistsWithId(pageToPush.getPageId());
		Date today = new Date();
		if (!exists) {
			dao.addPageWithId(pageToPush.getPageId(), destinationNode.getNodeId());
			pageToPush.setFirstOnlineDate(today);
			if (workingDAO != null) {
			    workingDAO.setFirstOnlineDateForPageWithId(pageToPush.getPageId(), today);
			}
		}
        // make sure all the text sections exist in the target db
        for (Iterator iter = pageToPush.getTextSections().iterator(); iter.hasNext();) {
            MappedTextSection nextSection = (MappedTextSection) iter.next();
            if (!dao.getTextSectionExistsWithId(nextSection.getTextSectionId())) {
                dao.addTextSectionWithId(nextSection.getTextSectionId());
            }
        }
		MappedPage otherPage = dao.getPageWithId(pageToPush.getPageId());
		otherPage.copyValues(pageToPush, destinationNode);
		dao.savePage(otherPage);
		return otherPage;
	}
	
	public void pushTitleIllustrationsToDB(MappedPage pageToPush, PageDAO dao) {
	    boolean exists = dao.getPageExistsWithId(pageToPush.getPageId());
	    if (exists) {
	        MappedPage otherPage = dao.getPageWithId(pageToPush.getPageId());
	        Set titleIllustrations = pageToPush.getTitleIllustrations();
	        if (titleIllustrations != null) {
	            otherPage.setTitleIllustrations(new TreeSet(titleIllustrations));
	            otherPage.setPrintCustomCaption(pageToPush.getPrintCustomCaption());
	            otherPage.setPrintImageData(pageToPush.getPrintImageData());
	            otherPage.setImageCaption(pageToPush.getImageCaption());
	            dao.savePage(otherPage);
	        }
	    }
	}
	
	public void pushPageStatusToDB(MappedPage pageToPush, PageDAO dao) {
	    boolean exists = dao.getPageExistsWithId(pageToPush.getPageId());
	    if (exists) {
	        MappedPage otherPage = dao.getPageWithId(pageToPush.getPageId());
	        otherPage.setStatus(pageToPush.getStatus());
	        dao.savePage(otherPage);
	    }
	}
}
