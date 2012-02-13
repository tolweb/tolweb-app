package org.tolweb.dao;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.page.PageContributor;


public class LicenseCascadeDAOTest extends ApplicationContextTestAbstract {
	NodeDAO nodeDAO;
	ContributorDAO contribDAO;
	ImageDAO imageDAO;
	PageDAO workingPageDAO;
	PageDAO publicPageDAO;
	
	public LicenseCascadeDAOTest(String name) {
		super(name);
		
		// initialize dao's 
		nodeDAO = (NodeDAO)context.getBean("nodeDAO");
        contribDAO = (ContributorDAO)context.getBean("contributorDAO");
        imageDAO = (ImageDAO)context.getBean("imageDAO");
        workingPageDAO = (PageDAO)context.getBean("workingPageDAO");
        publicPageDAO = (PageDAO)context.getBean("publicPageDAO");
        
	}
	
	public void testFindFirstAuthor() {
		//publicPageDAO

		MappedPage pg = publicPageDAO.getPageWithId(3L);
		System.out.println(pg.getContributors());
		
	}
	
	@SuppressWarnings("unchecked")
	public void testImageAlgorithm() {
		int contrId = 664;
		Contributor contr = contribDAO.getContributorWithId(contrId);
		List images = imageDAO.getImagesForContributor(contr);
		for (Iterator iter = images.iterator(); iter.hasNext();) {
			NodeImage image = (NodeImage) iter.next();
			if (isImageCopyrightOwner(image, contr)) {
				System.out.println(contr.getName() + " owns the copyright to image-id:" + image.getId());
			}
		}
	}
	
	public static boolean isImageCopyrightOwner(NodeImage nimg, Contributor cid) {
		return nimg.getCopyrightOwnerContributor() != null && nimg.getCopyrightOwnerContributor().getId() == cid.getId();
	}
	
	@SuppressWarnings("unchecked")
    public void testPageContributorAlgorithm() {
    	int contrId = 377;
    	List pages = publicPageDAO.getPagesForContributor(contrId);
    	System.out.println(pages.size());
		int i = 0;
    	for (Iterator iter = pages.iterator(); iter.hasNext();) {
			MappedPage page = (MappedPage) iter.next();
			if (!page.getContributors().isEmpty()) {
				String cmt = "copyright owner";

				if (isFirstAuthorAndCopyrightOwner(page.getContributors(), contrId)) {
					cmt = "not " + cmt;
					i++;
				} else if (isNotFirstAuthorButCopyrightOwner(page.getContributors(), contrId)) {
					System.out.println("YO - they're not first author - but they own the copyright......");
				}
				
				//System.out.println(cmt + " for page-id:" + page.getPageId());
				// we're going to change the license/use if the contributor is 
				// listed first in the authors sequence and they are the copyright-owner
				
			}
		}
    	System.out.println("Contributor " + contrId + " owns " + i + " copyright(s)");
    }
    
	@SuppressWarnings("unchecked")
    public static boolean isFirstAuthorAndCopyrightOwner(SortedSet authorSeq, int searchId) {
    	PageContributor pc = (PageContributor)authorSeq.first();
    	return isFirstAuthor(pc, searchId) && (pc != null && pc.isCopyOwner());
    }
    
    public static boolean isFirstAuthor(PageContributor pc, int searchId) {
    	return pc != null && pc.getContributorId() == searchId;
    }
    
    @SuppressWarnings("unchecked")
    public static boolean isNotFirstAuthorButCopyrightOwner(SortedSet authorSeq, int searchId) {
    	TreeSet clone = new TreeSet(authorSeq);
    	for(Iterator itr = clone.iterator(); itr.hasNext(); ) {
    		PageContributor pc = (PageContributor)itr.next();
    		if (pc != null && (pc.getContributorId() != searchId && pc.isCopyOwner())) {
    			return false;
    		}
    		if (pc != null && (pc.getContributorId() == searchId && pc.isCopyOwner())) {
    			return true;
    		}
    	}
    	return false; 
    }
}
