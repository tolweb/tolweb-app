/*
 * ImageDAOTest.java
 *
 * Created on May 3, 2004, 10:55 AM
 */

package org.tolweb.dao;

import java.util.ArrayList;
import java.util.List;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 *
 * @author  dmandel
 */
public class ImageDAOTest extends ApplicationContextTestAbstract {
    private ImageDAO dao;
    private ContributorDAO contrDao;
    private Contributor katja, david, chris;
    private NodeImage testImage;
    
    /** Creates a new instance of ImageDAOTest */
    public ImageDAOTest(String name) {
        super(name);
        dao = (ImageDAO) context.getBean("imageDAO");
        contrDao = (ContributorDAO) context.getBean("contributorDAO");
        katja = contrDao.getContributorWithEmail("treegrow@ag.arizona.edu");
        david = contrDao.getContributorWithEmail("beetle@ag.arizona.edu");
        chris = contrDao.getContributorWithEmail("ponerine@tolweb.org");
    }

//    @SuppressWarnings("unchecked")
//    public void test_unattached_media_fetch() {
//    	Set unattached = dao.getUnattachedMedia();
//    	assertNotNull(unattached);
//    	assertTrue(!unattached.isEmpty());
//    	List media = dao.getImagesWithIds(unattached);
//    	assertNotNull(media);
//    	assertTrue(!media.isEmpty());
//    }
    
    public void test_native_image_filtering_for_eol_web_services() {
    	List node2677 = dao.getNativeAttachedImagesForNode(2677L);
    	assertNotNull(node2677);
    	assertTrue(!node2677.isEmpty());
    	assertTrue(node2677.size() >= 179);
    	System.out.println(node2677.size());
    	assertTrue(node2677.size() == 179);    	
    	
    	node2677 = dao.getNativeAttachedImagesForNode(2677L, new ArrayList(), true);
    	assertNotNull(node2677);
    	assertTrue(!node2677.isEmpty());
    	assertTrue(node2677.size() >= 179);
    	System.out.println(node2677.size());
    	assertTrue(node2677.size() == 179);

    	List node19386 = dao.getNativeAttachedImagesForNode(19386L);
    	assertNotNull(node19386);
    	assertTrue(!node19386.isEmpty());
    	System.out.println(node19386.size());
    	assertTrue(node19386.size() >= 119);
    	System.out.println(node19386.size());
    	assertTrue(node19386.size() == 119);
    	
    	node19386 = dao.getNativeAttachedImagesForNode(19386L, new ArrayList(), true);
    	assertNotNull(node19386);
    	assertTrue(!node19386.isEmpty());
    	System.out.println(node19386.size());
    	assertTrue(node19386.size() >= 114);
    	System.out.println(node19386.size());
    	assertTrue(node19386.size() == 114);
    }
    
//    public void testAddImage() {
//        NodeImage image = getTestImage();
//        int id = image.getId();
//        NodeImage foundImage = dao.getImageWithId(id);
//        assertNotNull(foundImage);        
//    }
//    
//    public void testGetLocation() {
//        String location = dao.getImageLocationWithId(1);
//        assertEquals(location, "VampStellate17.jpg");
//        location = dao.getImageLocationWithId(2);
//        assertEquals(location, "OingensBeak.jpg");
//    }
//    
//    public void testFieldsMatching() {
//        String comments = "These are comments";
//        String copyOwner = "Some copy owner";
//        String copyUrl = "Some copy url";
//        String copyEmail = "Some copy email";
//        String copyDate = "Some copy date";
//        String scientificName = "A scientific name";
//        String altText = "Some alt text";
//        String reference = "A reference";
//        String creator = "The creator";
//        String identifier = "Identifier";
//        String acks = "Acknowledge nothing";
//        String geoLocation = "Nogales";
//        String sex = "Male";
//        String stage = "Young";
//        String bodyPart = "Heart";
//        String size = "A size";
//        String view = "Good";
//        boolean fossil = true;
//        byte use = NodeImage.RESTRICTED_USE;
//        boolean checkedOutOnline = false;
//        boolean checkedOut = true;
//        String period = "Jurassic";
//        String collection = "Collection";
//        String collectionAcronym = "NSF";
//        String type = "Odd";
//        String collector = "Collector";
//        String location = "NewImage.jpg";
//        Date creationDate = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(creationDate);
//        cal.add(Calendar.HOUR, 1);
//        Date lastEditedDate = cal.getTime();
//        cal.add(Calendar.HOUR, 1);
//        Date checkoutDate = cal.getTime();
//        String userCreationDate = "some creation date";
//        boolean publicDomain = true;
//        String additionalDateTimeInfo = "AdditionalInfo";
//        String alive = "dead";
//        String season = "Fall";
//        String imageType = "Photo";
//        String voucherNumber = "some voucher";
//        String voucherNumberCollection = "some collection of voucher numbers";
//        String notes = "some notes";
//        String behavior = "some weird behavior";
//        NodeImage image = new NodeImage();
//        boolean isSpecimen = false;
//        boolean isHabitat = true;
//        boolean isUltrastructure = false;
//        boolean isEquipment = true;
//        boolean isBodyparts = true;
//        boolean isPeopleWorking = true;
//        image.setVoucherNumber(voucherNumber);
//        image.setVoucherNumberCollection(voucherNumberCollection);
//        image.setNotes(notes);
//        image.setBehavior(behavior);
//        image.setIsPeopleWorking(isPeopleWorking);
//        image.setIsSpecimen(isSpecimen);
//        image.setIsHabitat(isHabitat);
//        image.setIsUltrastructure(isUltrastructure);
//        image.setIsEquipment(isEquipment);
//        image.setIsBodyParts(isBodyparts);
//        image.setUserCreationDate(userCreationDate);
//        image.setAdditionalDateTimeInfo(additionalDateTimeInfo);
//        image.setAlive(alive);
//        image.setSeason(season);
//        image.setImageType(imageType);
//        image.setCheckoutDate(checkoutDate);
//        image.setCheckedOutContributor(katja);
//        image.setCheckedOut(checkedOut);
//        image.setOnlineCheckedOut(checkedOutOnline);
//        image.setComments(comments);
//        image.setCopyrightOwner(copyOwner);
//        image.setCopyrightUrl(copyUrl);
//        image.setCopyrightEmail(copyEmail);
//        image.setCopyrightDate(copyDate);
//        image.setScientificName(scientificName);
//        image.setAltText(altText);
//        image.setReference(reference);
//        image.setCreator(creator);
//        image.setIdentifier(identifier);
//        image.setAcknowledgements(acks);
//        image.setGeoLocation(geoLocation);
//        image.setSex(sex);
//        image.setStage(stage);
//        image.setBodyPart(bodyPart);
//        image.setSize(size);
//        image.setView(view);
//        image.setIsFossil(fossil);
//        image.setUsePermission(use);
//        image.setPeriod(period);
//        image.setCollection(collection);
//        image.setCollectionAcronym(collectionAcronym);
//        image.setType(type);
//        image.setCollector(collector);
//        image.setLocation(location);
//        image.setInPublicDomain(publicDomain);
//        image.setContributor(david);
//        image.setCopyrightOwnerContributor(katja);
//        dao.addImage(image, david, false);
//        image.setCreationDate(creationDate);
//        image.setLastEditedDate(lastEditedDate);
//        image.setLastEditedContributor(chris);
//        dao.saveImage(image);
//        int id = image.getId();
//        
//        NodeImage foundImage = dao.getImageWithId(id);
//        assertEquals(foundImage.getVoucherNumber(), voucherNumber);
//		assertEquals(foundImage.getVoucherNumberCollection(), voucherNumberCollection);        
//        assertEquals(foundImage.getNotes(), notes);
//        assertEquals(foundImage.getBehavior(), behavior);
//        assertEquals(foundImage.getIsPeopleWorking(), isPeopleWorking);
//        assertEquals(foundImage.getIsSpecimen(), isSpecimen);
//        assertEquals(foundImage.getIsHabitat(), isHabitat);
//        assertEquals(foundImage.getIsEquipment(), isEquipment);
//        assertEquals(foundImage.getIsUltrastructure(), isUltrastructure);
//        assertEquals(foundImage.getIsBodyParts(), isBodyparts);
//		assertEquals(foundImage.getAdditionalDateTimeInfo(), additionalDateTimeInfo);
//		assertEquals(foundImage.getAlive(), alive);
//		assertEquals(foundImage.getSeason(), season);
//		assertEquals(foundImage.getImageType(), imageType);
//		assertEquals(foundImage.getUserCreationDate(), userCreationDate);
//        assertEquals(foundImage.getCheckedOut(), checkedOut);
//        assertEquals(foundImage.getOnlineCheckedOut(), checkedOutOnline);
//        assertEquals(foundImage.getComments(), comments);
//        assertEquals(foundImage.getCopyrightOwner(), copyOwner);
//        assertEquals(foundImage.getCopyrightUrl(), copyUrl);
//        assertEquals(foundImage.getCopyrightEmail(), copyEmail);
//        assertEquals(foundImage.getCopyrightDate(), copyDate);
//        assertEquals(foundImage.getScientificName(), scientificName);
//        assertEquals(foundImage.getAltText(), altText);
//        assertEquals(foundImage.getReference(), reference);
//        assertEquals(foundImage.getCreator(), creator);
//        assertEquals(foundImage.getIdentifier(), identifier);
//        assertEquals(foundImage.getAcknowledgements(), acks);
//        assertEquals(foundImage.getGeoLocation(), geoLocation);
//        assertEquals(foundImage.getSex(), sex);
//        assertEquals(foundImage.getStage(), stage);
//        assertEquals(foundImage.getBodyPart(), bodyPart);
//        assertEquals(foundImage.getSize(), size);
//        assertEquals(foundImage.getView(), view);
//        assertEquals(foundImage.getIsFossil(), fossil);
//        assertEquals(foundImage.getUsePermission(), use);
//        assertEquals(foundImage.getPeriod(), period);
//        assertEquals(foundImage.getCollection(), collection);
//        assertEquals(foundImage.getCollectionAcronym(), collectionAcronym);
//        assertEquals(foundImage.getType(), type);
//        assertEquals(foundImage.getCollector(), collector);
//        assertEquals(foundImage.getLocation(), location);
//        assertEquals(foundImage.getInPublicDomain(), publicDomain);
//        assertEquals(foundImage.getContributor().getId(), david.getId());
//        assertEquals(foundImage.getCopyrightOwnerContributor().getId(), david.getId());
//        checkDates(foundImage.getCreationDate(), creationDate);
//        checkDates(foundImage.getLastEditedDate(), lastEditedDate);
//        checkDates(foundImage.getCheckoutDate(), checkoutDate);
//        assertEquals(foundImage.getCheckedOutContributor().getId(), katja.getId());
//        assertEquals(foundImage.getLastEditedContributor().getId(), chris.getId());
//    }
//    
//    public void testDeleteImage() {
//        NodeImage img = new NodeImage();
//        img.setLocation("shouldbedeleted.jpg");
//        img.setContributor(david);
//        dao.addImage(img, david, false);
//        
//        int id = img.getId();
//        dao.deleteImage(img);
//        
//        NodeImage shouldBeNullImg = dao.getImageWithId(id);
//        assertNull(shouldBeNullImg);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testNodeManipulations() {
//        NodeDAO nodeDao = (NodeDAO) context.getBean("nodeDAO");
//        NodeImage image = getTestImage();
//        Node life = (Node) nodeDao.findNodesExactlyNamed("Life on Earth").get(0);
//        Node viruses = (Node) nodeDao.findNodesExactlyNamed("Viruses").get(0);
//        Node eukaryotes = (Node) nodeDao.findNodesExactlyNamed("Eukaryotes").get(0);
//        
//        image.addToNodesSet(life);
//        dao.saveImage(image);
//        NodeImage foundImage = dao.getImageWithId(image.getId());
//        assertTrue(foundImage.getNodesSet().size() == 1);
//        Node firstNode = (Node) new Vector(foundImage.getNodesSet()).get(0);
//        assertEquals(firstNode.getId(), life.getId());
//        image.addToNodesSet(viruses);
//        dao.saveImage(image);
//        foundImage = dao.getImageWithId(image.getId());
//        assertTrue(foundImage.getNodesSet().size() == 2);
//        Vector nodes = new Vector(foundImage.getNodesSet());
//        firstNode = (Node) nodes.get(0);
//        Node secondNode = (Node) nodes.get(1);
//        assertTrue(firstNode.getId() == life.getId() || firstNode.getId() == viruses.getId());
//        assertTrue(secondNode.getId() == life.getId() || secondNode.getId() == viruses.getId());
//        
//        image.removeFromNodesSet(life);
//        image.removeFromNodesSet(viruses);
//        image.addToNodesSet(eukaryotes);
//        dao.saveImage(image);
//        foundImage = dao.getImageWithId(image.getId());
//        assertTrue(foundImage.getNodesSet().size() == 1);
//        nodes = new Vector(foundImage.getNodesSet());
//        firstNode = (Node) nodes.get(0);
//        assertTrue(firstNode.getId() == eukaryotes.getId());
//        dao.deleteImage(image);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testGetUniqueContributorsIds() {
//    	Set results = dao.getDistinctImageContributorsIds();
//    	// There are 35 total image contributors in the test data
//    	assertEquals(results.size(), 35);
//    }
//    
//    public void testGetNumImages() {
//    	Integer numDavidsImages = dao.getNumImagesForContributor(david);
//    	assertEquals(numDavidsImages, new Integer(13));
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testGetImagesForContributor() {
//        System.out.println("LOOKING FOR DAVID'S IMAGES\n\n\n\n\n");
//    	List list = dao.getImagesForContributor(david);
//    	assertEquals(list.size(), 13);
//    	Iterator it = list.iterator();
//    	while (it.hasNext()) {
//			NodeImage nextImage = (NodeImage) it.next();
//			int id = nextImage.getContributor() != null ? nextImage.getContributor().getId() : -1;
//			int copyrightContrId = nextImage.getCopyrightOwnerContributor() != null ? nextImage.getCopyrightOwnerContributor().getId() : -1;
//			assertTrue(id == david.getId() || copyrightContrId == david.getId());    		
//    	}
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testQuerying() {
//    	Hashtable hash = new Hashtable();
//    	hash.put(ImageDAO.IMAGE_ID, new Integer(1));
//        testVampStellateResult(dao.getImagesMatchingCriteria(hash));
//        hash.clear();
//        hash.put(ImageDAO.FILENAME, "VampStellate17.jpg");
//        testVampStellateResult(dao.getImagesMatchingCriteria(hash));
//        hash.put(ImageDAO.IMAGE_ID, new Integer(1));       
//        testVampStellateResult(dao.getImagesMatchingCriteria(hash));
//        // -- not sure what I was thinking here.  the way this used to work, it would return the image regardless of the node name.
//        //hash.remove(ImageDAO.IMAGE_ID);
//        //hash.put(ImageDAO.GROUP, "XXXXXXXX"); 
//        //testVampStellateResult(dao.getImagesMatchingCriteria(hash));       
//        hash.clear();
//        hash.put(ImageDAO.GROUP, "Eukaryotes");
//        testEukaryotesResult(dao.getImagesMatchingCriteria(hash));
//        hash.put(ImageDAO.GROUP, "Eukarya");
//        testEukaryotesResult(dao.getImagesMatchingCriteria(hash));
//        hash.put(ImageDAO.GROUP, "eUKarya");        
//        testEukaryotesResult(dao.getImagesMatchingCriteria(hash));
//        
//        hash.clear();
//        hash.put(ImageDAO.SCIENTIFIC_NAME, "Crinoidea");
//        List list = dao.getImagesMatchingCriteria(hash);
//        NodeImage img = (NodeImage) list.get(0);
//        assertEquals(img.getId(), 8);
//        // This image is set to use 0, so we shouldn't get any results
//        hash.put(ImageDAO.IMAGE_USE, Byte.valueOf((byte) 0));
//        list = dao.getImagesMatchingCriteria(hash);
//        assertEquals(list.size(), 0);
//        
//        hash.clear();
//        hash.put(ImageDAO.CONTRIBUTOR, david);
//        list = dao.getImagesMatchingCriteria(hash);
//        assertEquals(list.size(), 4);
//        hash.put(ImageDAO.SCIENTIFIC_NAME, "Bembidion umbratum");
//        list = dao.getImagesMatchingCriteria(hash);
//        assertEquals(list.size(), 1);
//        // This image is set to use 2, so we should get the same result
//        hash.put(ImageDAO.IMAGE_USE, Byte.valueOf((byte) 0));
//        list = dao.getImagesMatchingCriteria(hash);
//        assertEquals(list.size(), 1);        
//        hash.clear();
//        hash.put(ImageDAO.COPYOWNER_CONTRIBUTOR, david);
//        list = dao.getImagesMatchingCriteria(hash);
//        Iterator it = list.iterator();
//        while (it.hasNext()) {
//        	img = (NodeImage) it.next();
//        	assertEquals(img.getCopyrightOwnerContributor().getId(), david.getId());
//        }
//        
//        hash.clear();
//        hash.put(ImageDAO.COPYOWNER, "David Maddison");
//        list = dao.getImagesMatchingCriteria(hash);
//        // This should match 10 imgs.  David is the copy owner contributor on 9, and his name is in the copyright
//        // owner field for 1 img.
//        assertEquals(list.size(), 10);
//        
//        hash.clear();
//        hash.put(ImageDAO.ANY_DATA, "arizona");
//        list = dao.getImagesMatchingCriteria(hash);
//        assertEquals(list.size(), 4);
//        
//        hash.clear();
//        hash.put(ImageDAO.ANY_DATA, "ari");
//        list = dao.getImagesMatchingCriteria(hash);
//        assertEquals(list.size(), 8);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testGetImagesAttachedToNodeOrAncestor() {
//        Hashtable hash = new Hashtable();
//        hash.put(ImageDAO.GROUP, "life");
//        hash.put(ImageDAO.SEARCH_ANCESTORS, new Boolean(true));
//        List list = dao.getImagesMatchingCriteria(hash);
//        System.out.println("number of images attached to life: " + list.size());
//    }
//    
//    @SuppressWarnings("unchecked")
//    private void testEukaryotesResult(List list) {
//        NodeImage img1 = (NodeImage) list.get(0);
//        assertTrue(list.size() == 1);
//        assertTrue(img1.getId() == 40);        
//    }
//    
//    @SuppressWarnings("unchecked")
//    private void testVampStellateResult(List list) {
//        assertTrue(list.size() == 1);
//        NodeImage img = (NodeImage) list.get(0);
//        assertEquals(img.getLocation(), "VampStellate17.jpg");        
//    }
//    
    private NodeImage getTestImage() {
        if (testImage == null) {
            testImage = new NodeImage();
            testImage.setUsePermission(NodeImage.EVERYWHERE_USE);
            testImage.setLocation("foo.jpg");
            dao.addImage(testImage, katja, false);            
        }
        return testImage;
    }
}
