/*
 * ImageDAOTest.java
 *
 * Created on May 3, 2004, 10:55 AM
 */

package org.tolweb.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.tree.Node;

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
    
    public void testAddImage() {
        NodeImage image = getTestImage();
        int id = image.getId();
        NodeImage foundImage = dao.getImageWithId(id);
        assertNotNull(foundImage);        
    }
    
    public void testImageVersions() {
        NodeImage number3 = dao.getImageWithId(3);
        ImageVersion masterVersion = new ImageVersion();
        masterVersion.setImage(number3);
        masterVersion.setVersionName("master");
        masterVersion.setContributor(david);
        masterVersion.setFileName("xxx.jpg");
        masterVersion.setFileSize("100k");
        masterVersion.setHeight(new Integer(768));
        masterVersion.setWidth(new Integer(1024));
        masterVersion.setIsMaster(true);
        masterVersion.setIsMaxSize(false);
        dao.saveImageVersion(masterVersion);
        ImageVersion dbVersion = dao.getImageVersionWithId(masterVersion.getVersionId());
        assertNotNull(dbVersion);
        compareImageVersions(masterVersion, dbVersion);
        List versionsForImage = dao.getVersionsForImage(number3);
        assertEquals(versionsForImage.size(), 1);
        ImageVersion shouldBeSameAsMasterVersion = (ImageVersion) versionsForImage.get(0);
        assertEquals(shouldBeSameAsMasterVersion.getVersionId(), masterVersion.getVersionId());
        dao.deleteImageVersion(masterVersion);
    }
    
    private void compareImageVersions(ImageVersion v1, ImageVersion v2) {
        assertEquals(v1.getVersionId(), v2.getVersionId());
        assertEquals(v1.getImage().getId(), v2.getImage().getId());
        assertEquals(v1.getVersionName(), v2.getVersionName());
        assertEquals(v1.getContributor().getId(), v2.getContributor().getId());
        assertEquals(v1.getFileName(), v2.getFileName());        
        assertEquals(v1.getFileSize(), v2.getFileSize()); 
        assertEquals(v1.getHeight(), v2.getHeight());
        assertEquals(v1.getWidth(), v2.getWidth());
        assertEquals(v1.getIsMaster(), v2.getIsMaster());
        assertEquals(v1.getIsMaxSize(), v2.getIsMaxSize());
    }
    
    public void testGetLocation() {
        String location = dao.getImageLocationWithId(1);
        assertEquals(location, "VampStellate17.jpg");
        location = dao.getImageLocationWithId(2);
        assertEquals(location, "OingensBeak.jpg");
    }
    
    public void testFieldsMatching() {
        String comments = "These are comments";
        String copyOwner = "Some copy owner";
        String copyUrl = "Some copy url";
        String copyEmail = "Some copy email";
        String copyDate = "Some copy date";
        String scientificName = "A scientific name";
        String altText = "Some alt text";
        String reference = "A reference";
        String creator = "The creator";
        String identifier = "Identifier";
        String acks = "Acknowledge nothing";
        String geoLocation = "Nogales";
        String sex = "Male";
        String stage = "Young";
        String bodyPart = "Heart";
        String size = "A size";
        String view = "Good";
        boolean fossil = true;
        byte use = NodeImage.RESTRICTED_USE;
        boolean checkedOutOnline = false;
        boolean checkedOut = true;
        String period = "Jurassic";
        String collection = "Collection";
        String collectionAcronym = "NSF";
        String type = "Odd";
        String collector = "Collector";
        String location = "NewImage.jpg";
        Date creationDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(creationDate);
        cal.add(Calendar.HOUR, 1);
        Date lastEditedDate = cal.getTime();
        cal.add(Calendar.HOUR, 1);
        Date checkoutDate = cal.getTime();
        String userCreationDate = "some creation date";
        boolean publicDomain = true;
        String additionalDateTimeInfo = "AdditionalInfo";
        String alive = "dead";
        String season = "Fall";
        String imageType = "Photo";
        String voucherNumber = "some voucher";
        String voucherNumberCollection = "some collection of voucher numbers";
        String notes = "some notes";
        String behavior = "some weird behavior";
        NodeImage image = new NodeImage();
        boolean isSpecimen = false;
        boolean isHabitat = true;
        boolean isUltrastructure = false;
        boolean isEquipment = true;
        boolean isBodyparts = true;
        boolean isPeopleWorking = true;
        image.setVoucherNumber(voucherNumber);
        image.setVoucherNumberCollection(voucherNumberCollection);
        image.setNotes(notes);
        image.setBehavior(behavior);
        image.setIsPeopleWorking(isPeopleWorking);
        image.setIsSpecimen(isSpecimen);
        image.setIsHabitat(isHabitat);
        image.setIsUltrastructure(isUltrastructure);
        image.setIsEquipment(isEquipment);
        image.setIsBodyParts(isBodyparts);
        image.setUserCreationDate(userCreationDate);
        image.setAdditionalDateTimeInfo(additionalDateTimeInfo);
        image.setAlive(alive);
        image.setSeason(season);
        image.setImageType(imageType);
        image.setCheckoutDate(checkoutDate);
        image.setCheckedOutContributor(katja);
        image.setCheckedOut(checkedOut);
        image.setOnlineCheckedOut(checkedOutOnline);
        image.setComments(comments);
        image.setCopyrightOwner(copyOwner);
        image.setCopyrightUrl(copyUrl);
        image.setCopyrightEmail(copyEmail);
        image.setCopyrightDate(copyDate);
        image.setScientificName(scientificName);
        image.setAltText(altText);
        image.setReference(reference);
        image.setCreator(creator);
        image.setIdentifier(identifier);
        image.setAcknowledgements(acks);
        image.setGeoLocation(geoLocation);
        image.setSex(sex);
        image.setStage(stage);
        image.setBodyPart(bodyPart);
        image.setSize(size);
        image.setView(view);
        image.setIsFossil(fossil);
        image.setUsePermission(use);
        image.setPeriod(period);
        image.setCollection(collection);
        image.setCollectionAcronym(collectionAcronym);
        image.setType(type);
        image.setCollector(collector);
        image.setLocation(location);
        image.setInPublicDomain(publicDomain);
        image.setContributor(david);
        image.setCopyrightOwnerContributor(katja);
        dao.addImage(image, david, false);
        image.setCreationDate(creationDate);
        image.setLastEditedDate(lastEditedDate);
        image.setLastEditedContributor(chris);
        dao.saveImage(image);
        int id = image.getId();
        
        NodeImage foundImage = dao.getImageWithId(id);
        assertEquals(foundImage.getVoucherNumber(), voucherNumber);
		assertEquals(foundImage.getVoucherNumberCollection(), voucherNumberCollection);        
        assertEquals(foundImage.getNotes(), notes);
        assertEquals(foundImage.getBehavior(), behavior);
        assertEquals(foundImage.getIsPeopleWorking(), isPeopleWorking);
        assertEquals(foundImage.getIsSpecimen(), isSpecimen);
        assertEquals(foundImage.getIsHabitat(), isHabitat);
        assertEquals(foundImage.getIsEquipment(), isEquipment);
        assertEquals(foundImage.getIsUltrastructure(), isUltrastructure);
        assertEquals(foundImage.getIsBodyParts(), isBodyparts);
		assertEquals(foundImage.getAdditionalDateTimeInfo(), additionalDateTimeInfo);
		assertEquals(foundImage.getAlive(), alive);
		assertEquals(foundImage.getSeason(), season);
		assertEquals(foundImage.getImageType(), imageType);
		assertEquals(foundImage.getUserCreationDate(), userCreationDate);
        assertEquals(foundImage.getCheckedOut(), checkedOut);
        assertEquals(foundImage.getOnlineCheckedOut(), checkedOutOnline);
        assertEquals(foundImage.getComments(), comments);
        assertEquals(foundImage.getCopyrightOwner(), copyOwner);
        assertEquals(foundImage.getCopyrightUrl(), copyUrl);
        assertEquals(foundImage.getCopyrightEmail(), copyEmail);
        assertEquals(foundImage.getCopyrightDate(), copyDate);
        assertEquals(foundImage.getScientificName(), scientificName);
        assertEquals(foundImage.getAltText(), altText);
        assertEquals(foundImage.getReference(), reference);
        assertEquals(foundImage.getCreator(), creator);
        assertEquals(foundImage.getIdentifier(), identifier);
        assertEquals(foundImage.getAcknowledgements(), acks);
        assertEquals(foundImage.getGeoLocation(), geoLocation);
        assertEquals(foundImage.getSex(), sex);
        assertEquals(foundImage.getStage(), stage);
        assertEquals(foundImage.getBodyPart(), bodyPart);
        assertEquals(foundImage.getSize(), size);
        assertEquals(foundImage.getView(), view);
        assertEquals(foundImage.getIsFossil(), fossil);
        assertEquals(foundImage.getUsePermission(), use);
        assertEquals(foundImage.getPeriod(), period);
        assertEquals(foundImage.getCollection(), collection);
        assertEquals(foundImage.getCollectionAcronym(), collectionAcronym);
        assertEquals(foundImage.getType(), type);
        assertEquals(foundImage.getCollector(), collector);
        assertEquals(foundImage.getLocation(), location);
        assertEquals(foundImage.getInPublicDomain(), publicDomain);
        assertEquals(foundImage.getContributor().getId(), david.getId());
        assertEquals(foundImage.getCopyrightOwnerContributor().getId(), david.getId());
        checkDates(foundImage.getCreationDate(), creationDate);
        checkDates(foundImage.getLastEditedDate(), lastEditedDate);
        checkDates(foundImage.getCheckoutDate(), checkoutDate);
        assertEquals(foundImage.getCheckedOutContributor().getId(), katja.getId());
        assertEquals(foundImage.getLastEditedContributor().getId(), chris.getId());
    }
    
    public void testDeleteImage() {
        NodeImage img = new NodeImage();
        img.setLocation("shouldbedeleted.jpg");
        img.setContributor(david);
        dao.addImage(img, david, false);
        
        int id = img.getId();
        dao.deleteImage(img);
        
        NodeImage shouldBeNullImg = dao.getImageWithId(id);
        assertNull(shouldBeNullImg);
    }
    
    public void testNodeManipulations() {
        NodeDAO nodeDao = (NodeDAO) context.getBean("nodeDAO");
        NodeImage image = getTestImage();
        Node life = (Node) nodeDao.findNodesExactlyNamed("Life on Earth").get(0);
        Node viruses = (Node) nodeDao.findNodesExactlyNamed("Viruses").get(0);
        Node eukaryotes = (Node) nodeDao.findNodesExactlyNamed("Eukaryotes").get(0);
        
        image.addToNodesSet(life);
        dao.saveImage(image);
        NodeImage foundImage = dao.getImageWithId(image.getId());
        assertTrue(foundImage.getNodesSet().size() == 1);
        Node firstNode = (Node) new Vector(foundImage.getNodesSet()).get(0);
        assertEquals(firstNode.getId(), life.getId());
        image.addToNodesSet(viruses);
        dao.saveImage(image);
        foundImage = dao.getImageWithId(image.getId());
        assertTrue(foundImage.getNodesSet().size() == 2);
        Vector nodes = new Vector(foundImage.getNodesSet());
        firstNode = (Node) nodes.get(0);
        Node secondNode = (Node) nodes.get(1);
        assertTrue(firstNode.getId() == life.getId() || firstNode.getId() == viruses.getId());
        assertTrue(secondNode.getId() == life.getId() || secondNode.getId() == viruses.getId());
        
        image.removeFromNodesSet(life);
        image.removeFromNodesSet(viruses);
        image.addToNodesSet(eukaryotes);
        dao.saveImage(image);
        foundImage = dao.getImageWithId(image.getId());
        assertTrue(foundImage.getNodesSet().size() == 1);
        nodes = new Vector(foundImage.getNodesSet());
        firstNode = (Node) nodes.get(0);
        assertTrue(firstNode.getId() == eukaryotes.getId());
        dao.deleteImage(image);
    }
    
    public void testGetUniqueContributorsIds() {
    	Set results = dao.getDistinctImageContributorsIds();
    	// There are 35 total image contributors in the test data
    	assertEquals(results.size(), 176);
    }
    
    public void testGetNumImages() {
    	Integer numDavidsImages = dao.getNumImagesForContributor(david);
    	assertEquals(numDavidsImages, new Integer(545));
    }
    
    public void testGetImagesForContributor() {
    	List list = dao.getImagesForContributor(david);
    	assertEquals(list.size(), 545);
    	Iterator it = list.iterator();
    	while (it.hasNext()) {
			NodeImage nextImage = (NodeImage) it.next();
			int id = nextImage.getContributor() != null ? nextImage.getContributor().getId() : -1;
			int copyrightContrId = nextImage.getCopyrightOwnerContributor() != null ? nextImage.getCopyrightOwnerContributor().getId() : -1;
			assertTrue(id == david.getId() || copyrightContrId == david.getId());    		
    	}
    }
    
    public void testQuerying() {
    	Hashtable hash = new Hashtable();
    	hash.put(ImageDAO.IMAGE_ID, new Integer(1));
        testVampStellateResult(dao.getImagesMatchingCriteria(hash));
        hash.clear();
        hash.put(ImageDAO.FILENAME, "VampStellate17.jpg");
        testVampStellateResult(dao.getImagesMatchingCriteria(hash));
        hash.put(ImageDAO.IMAGE_ID, new Integer(1));       
        testVampStellateResult(dao.getImagesMatchingCriteria(hash));
        // -- not sure what I was thinking here.  the way this used to work, it would return the image regardless of the node name.
        //hash.remove(ImageDAO.IMAGE_ID);
        //hash.put(ImageDAO.GROUP, "XXXXXXXX"); 
        //testVampStellateResult(dao.getImagesMatchingCriteria(hash));       
        hash.clear();
        hash.put(ImageDAO.GROUP, "Eukaryotes");
        testEukaryotesResult(dao.getImagesMatchingCriteria(hash));
        hash.put(ImageDAO.GROUP, "Eukarya");
        testEukaryotesResult(dao.getImagesMatchingCriteria(hash));
        hash.put(ImageDAO.GROUP, "eUKarya");        
        testEukaryotesResult(dao.getImagesMatchingCriteria(hash));
        
        hash.clear();
        hash.put(ImageDAO.SCIENTIFIC_NAME, "Crinoidea");
        List list = dao.getImagesMatchingCriteria(hash);
        NodeImage img = (NodeImage) list.get(0);
        assertEquals(img.getId(), 8);
        // This image is set to use 0, so we shouldn't get any results
        hash.put(ImageDAO.IMAGE_USE, new Byte((byte) 0));
        list = dao.getImagesMatchingCriteria(hash);
        assertEquals(list.size(), 0);
        
        hash.clear();
        hash.put(ImageDAO.CONTRIBUTOR, david);
        list = dao.getImagesMatchingCriteria(hash);
        assertEquals(list.size(), 4);
        hash.put(ImageDAO.SCIENTIFIC_NAME, "Bembidion umbratum");
        list = dao.getImagesMatchingCriteria(hash);
        assertEquals(list.size(), 1);
        // This image is set to use 2, so we should get the same result
        hash.put(ImageDAO.IMAGE_USE, new Byte((byte) 0));
        list = dao.getImagesMatchingCriteria(hash);
        assertEquals(list.size(), 1);        
        hash.clear();
        hash.put(ImageDAO.COPYOWNER_CONTRIBUTOR, david);
        list = dao.getImagesMatchingCriteria(hash);
        Iterator it = list.iterator();
        while (it.hasNext()) {
        	img = (NodeImage) it.next();
        	assertEquals(img.getCopyrightOwnerContributor().getId(), david.getId());
        }
        
        hash.clear();
        hash.put(ImageDAO.COPYOWNER, "David Maddison");
        list = dao.getImagesMatchingCriteria(hash);
        // This should match 10 imgs.  David is the copy owner contributor on 9, and his name is in the copyright
        // owner field for 1 img.
        assertEquals(list.size(), 10);
        
        hash.clear();
        hash.put(ImageDAO.ANY_DATA, "arizona");
        list = dao.getImagesMatchingCriteria(hash);
        assertEquals(list.size(), 4);
        
        hash.clear();
        hash.put(ImageDAO.ANY_DATA, "ari");
        list = dao.getImagesMatchingCriteria(hash);
        assertEquals(list.size(), 8);
    }
    
    public void testGetImagesAttachedToNodeOrAncestor() {
        Hashtable hash = new Hashtable();
        hash.put(ImageDAO.GROUP, "life");
        //hash.put(ImageDAO.SEARCH_ANCESTORS, new Boolean(true));
        List list = dao.getImagesMatchingCriteria(hash);
        System.out.println("number of images attached to life: " + list.size());
    }
    
    private void testEukaryotesResult(List list) {
        NodeImage img1 = (NodeImage) list.get(0);
        assertTrue(list.size() == 1);
        assertTrue(img1.getId() == 40);        
    }
    
    private void testVampStellateResult(List list) {
        assertTrue(list.size() == 1);
        NodeImage img = (NodeImage) list.get(0);
        assertEquals(img.getLocation(), "VampStellate17.jpg");        
    }
    
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
