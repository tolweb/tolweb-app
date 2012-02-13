/*
 * ContributorDAOTest.java
 *
 * Created on April 29, 2004, 9:00 AM
 */

package org.tolweb.dao;

import java.sql.DriverManager;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.tolweb.treegrow.main.Contributor;
 
/**
 *
 * @author  dmandel
 */
public class ContributorDAOTest extends ApplicationContextTestAbstract {
    private ContributorDAO dao;
    private ImageDAO imgDao;
    private EditHistoryDAO editHistoryDao;
    
    public ContributorDAOTest(String name) {
        super(name);
        dao = (ContributorDAO) context.getBean("contributorDAO");
        imgDao = (ImageDAO) context.getBean("imageDAO");
        editHistoryDao = (EditHistoryDAO) context.getBean("editHistoryDAO");
java.io.PrintWriter w =
             new java.io.PrintWriter
             (new java.io.OutputStreamWriter(System.out));
         DriverManager.setLogWriter(w);     
        
    }

    @SuppressWarnings("unchecked")
    public void testNewTreehouseContributors() {
        // To begin with there shouldn't be any
        List list = dao.getNewTreehouseContributors();
        assertEquals(list.size(), 0);
        Contributor contr = new Contributor();
        contr.setEmail("bubba@bubbahotep.com");
        contr.setFirstName("Bubba");
        contr.setLastName("Hotep");
        contr.setContributorType(Contributor.TREEHOUSE_CONTRIBUTOR);
        dao.addContributor(contr, editHistoryDao);
        list = dao.getNewTreehouseContributors();
        assertEquals(list.size(), 1);
    }    
    
    public void testAddContributor() {
        Contributor contr = new Contributor();
        String email = "joey@joeyramone.com";
        String firstName = "Joey";
        String lastName = "Ramone";
        contr.setEmail(email);
        contr.setFirstName(firstName);
        contr.setLastName(lastName);
        contr.setPassword("bubba");
        dao.addContributor(contr, editHistoryDao);
        Contributor foundContributor = dao.getContributorWithEmail(email);
        assertNotNull(foundContributor);
    }
    
    public void testDeleteContributor() {
        Contributor contr = dao.getContributorWithEmail("joey@joeyramone.com");
        assertNotNull(contr);
        dao.deleteContributor(contr, null);
        contr = dao.getContributorWithEmail("joey@joeyramone.com");
        assertNull(contr);
    }
    
    public void testGetContributorWithEmail() {
        Contributor contr = dao.getContributorWithEmail("dmandel@tolweb.org");
        assertNotNull(contr);
    }
    
    public void testFieldsMatching() {
        Contributor contr = new Contributor();
        String email = "mike@michaelmoore.com";
        String firstName = "Michael";
        String lastName = "Moore";
        String institution = "University of Arizona";
        String homepage = "http://www.michaelmoore.com";
        String bio = "Mike makes movies";
        String address = "Michigan";
        String phone = "888-888-8888";
        String fax = "777-777-7777";
        String notes = "Bowling for Columbine is a great movie";
        String additionalInfo = "Stupid White Men is a great book";
        String imageFile = "michaelmoore.gif";
        String city = "tucson";
        String state ="arizona";
        String country ="usa";
        String category = "educator";
        byte type = Contributor.TREEHOUSE_CONTRIBUTOR;
        boolean showEmail = false;
        boolean showAddress = true;
        boolean isImageEditor = true;
        boolean isLearningEditor = true;
        contr.setCategory(category);
        contr.setCity(city);
        contr.setState(state);
        contr.setCountry(country);
        contr.setEmail(email);
        contr.setFirstName(firstName);
        contr.setLastName(lastName);
        contr.setInstitution(institution);
        contr.setHomepage(homepage);
        contr.setBio(bio);
        contr.setAddress(address);
        contr.setPhone(phone);
        contr.setFax(fax);
        contr.setNotes(notes);
        contr.setAdditionalInfo(additionalInfo);
        contr.setImageFilename(imageFile);
        contr.setContributorType(type);
        contr.setShowEmail(showEmail);
        contr.setShowAddress(showAddress);
        contr.setPassword("bubba");
        contr.setIsImageEditor(isImageEditor);
        contr.setIsLearningEditor(isLearningEditor);
        dao.addContributor(contr, editHistoryDao);

        Contributor foundContributor = dao.getContributorWithEmail(email);
        assertNotNull(foundContributor);
        assertEquals(foundContributor.getCategory(), category);
        assertEquals(foundContributor.getState(), state);
        assertEquals(foundContributor.getCity(), city);
        assertEquals(foundContributor.getCountry(), country);
        assertEquals(foundContributor.getEmail(), email);
        assertEquals(foundContributor.getFirstName(), firstName);
        assertEquals(foundContributor.getLastName(), lastName);
        assertEquals(foundContributor.getPhone(), phone);
        assertEquals(foundContributor.getAddress(), address);
        assertEquals(foundContributor.getBio(), bio);
        assertEquals(foundContributor.getHomepage(), homepage);
        assertEquals(foundContributor.getInstitution(), institution);
        assertEquals(foundContributor.getFax(), fax);
        assertEquals(foundContributor.getNotes(), notes);
        assertEquals(foundContributor.getAdditionalInfo(), additionalInfo);
        assertEquals(foundContributor.getImageFilename(), imageFile);
        assertEquals(foundContributor.getContributorType(), type);
        assertEquals(foundContributor.getShowEmail(), showEmail);
        assertEquals(foundContributor.getShowAddress(), showAddress);
        assertEquals(foundContributor.getIsImageEditor(), isImageEditor);        
        assertTrue(foundContributor.getId() > 0);
        
        foundContributor = dao.getContributorWithEmail(email, Contributor.IMAGES_CONTRIBUTOR);
        // This shouldn't be null because treehouse contributors are also image contributors
        assertNotNull(foundContributor);
    }
    
    @SuppressWarnings("unchecked")
    public void testGetContributorsWithIds() {
    	List list = dao.getContributorsWithIds(imgDao.getDistinctImageContributorsIds(), true);
    	assertEquals(list.size(), 34);
    	// Check to make sure they are sorted according to lastname
    	Iterator it = list.iterator();
    	String lastSeen = null;
    	while (it.hasNext()) {
    		Contributor contr = (Contributor) it.next();
    		if (lastSeen != null) {
    			assertTrue(lastSeen.compareTo(contr.getLastName()) <= 0);
    		}
    		lastSeen = contr.getLastName();
    	}
    }
    
    public void testGetContributorWithId() {
        Contributor contr = dao.getContributorWithId("344");
        assertNotNull(contr);
        assertEquals("David R.", contr.getFirstName());
        assertEquals("Maddison", contr.getLastName());
        assertEquals(344, contr.getId());
    }
    
    @SuppressWarnings("unchecked")
    public void testFindContributor() {
    	Hashtable args = new Hashtable();
    	args.put(ContributorDAO.FIRSTNAME, "David");
    	List list = dao.findContributors(args);
    	// Make sure we get 10 Davids
    	assertEquals(10, list.size());
    	
    	// Make sure we get 1 David Maddison
    	args.clear();
    	args.put(ContributorDAO.FIRSTNAME, "David");
    	args.put(ContributorDAO.SURNAME, "Maddison"); 
    	list = dao.findContributors(args);
    	assertEquals(1, list.size());
    	
    	args.clear();
    	args.put(ContributorDAO.ADDRESS, "arizona");
    	list = dao.findContributors(args);
    	assertEquals(7, list.size());
    	
    	args.clear();
    	args.put(ContributorDAO.EMAIL, "treegrow@ag.arizona.edu");
    	list = dao.findContributors(args);
    	assertEquals(1, list.size());
    	
    	args.clear();
    	args.put(ContributorDAO.NAME, "David Maddison");
    	list = dao.findContributors(args);
    	
    	assertEquals(1, list.size());
    }
      
    public void testUpdatePassword() {
        String newFirstName = "Talleyrand";
        Contributor contr = dao.getContributorWithEmail("dmandel@tolweb.org");
        contr.setFirstName(newFirstName);
        dao.saveContributor(contr);
        contr = dao.getContributorWithEmail("dmandel@tolweb.org");
        assertEquals(contr.getFirstName(), newFirstName);
    }
    
    
    public void testTypeHierarchies() {
        Contributor contr = dao.getContributorWithEmail("beetle@ag.arizona.edu", Contributor.IMAGES_CONTRIBUTOR);
        assertNotNull(contr);
        contr = dao.getContributorWithEmail("beetle@ag.arizona.edu", Contributor.TREEHOUSE_CONTRIBUTOR);
        assertNotNull(contr);
        contr = dao.getContributorWithEmail("beetle@ag.arizona.edu", Contributor.ACCESSORY_CONTRIBUTOR);
        assertNotNull(contr);
        //contr = dao.getContributorWithEmail("beetle@ag.arizona.edu", Contributor.SCIENTIFIC_CONTRIBUTOR);
        //assertNotNull(contr);
        contr = dao.getContributorWithEmail("mike@michaelmoore.com", Contributor.TREEHOUSE_CONTRIBUTOR);
        assertNotNull(contr);
        contr = dao.getContributorWithEmail("mike@michaelmoore.com", Contributor.ACCESSORY_CONTRIBUTOR);
        assertNull(contr);
        contr = dao.getContributorWithEmail("mike@michaelmoore.com", Contributor.SCIENTIFIC_CONTRIBUTOR);
        assertNull(contr);
    }    
}
