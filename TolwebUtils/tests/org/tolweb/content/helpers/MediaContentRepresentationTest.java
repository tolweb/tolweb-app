package org.tolweb.content.helpers;

import junit.framework.TestCase;

import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

public class MediaContentRepresentationTest extends TestCase {
	private NodeImage mediaFile;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mediaFile = createImage("Wombats", DescriptionElements.None);
		mediaFile.setId(650);
	}

	public void test_class_type_equality_for_derived_classes() {
		Movie mov = new Movie();
		Sound snd = new Sound();
		Document doc = new Document();
		NodeImage img = new NodeImage();
		
		assertFalse(!NodeImage.class.equals(mediaFile.getClass()));
		assertFalse(!NodeImage.class.equals(img.getClass()));
		assertTrue(!NodeImage.class.equals(mov.getClass()));
		assertTrue(!NodeImage.class.equals(snd.getClass()));
		assertTrue(!NodeImage.class.equals(doc.getClass()));
	}
	
	// write a failing test for creating a media-rep for public domain media 
	// (right now it returns null for public domain) 
	public void test_public_domain_media() {
		mediaFile.setUsePermission((byte)-1);
		mediaFile.setInPublicDomain(true);
		mediaFile.setLocation("something.jpg");

		assertEquals("something.jpg", mediaFile.getLocation());
		String mockMediaURL = "/ToLImage/images/" + mediaFile.getLocation();
		MediaContentRepresentation mediaRep = create(mediaFile, mockMediaURL);
		
		assertNotNull(mediaRep);
		assertTrue(mediaRep.isPublicDomain());
		assertEquals("http://creativecommons.org/licenses/publicdomain", mediaRep.getLicenseLink());

		assertNotNull(mediaRep.getCopyrightHolder());
		assertEquals(MediaContentRepresentation.HOST_MEDIA_URL+mockMediaURL, mediaRep.getMediaURL());
	}
	
	public void test_media_url_tree_dir_starting_out() {
		mediaFile.setUsePermission((byte)-1);
		mediaFile.setInPublicDomain(true);
		mediaFile.setLocation("something.jpg");

		assertEquals("something.jpg", mediaFile.getLocation());
		String mockMediaURL = "tree/ToLImages/images/" + mediaFile.getLocation();
		MediaContentRepresentation mediaRep = create(mediaFile, mockMediaURL);
		assertEquals("http://tolweb.org/tree/ToLImages/images/something.jpg", mediaRep.getMediaURL());
		
		mockMediaURL = "/tree/ToLImages/images/" + mediaFile.getLocation();
		mediaRep = create(mediaFile, mockMediaURL);
		assertEquals("http://tolweb.org/tree/ToLImages/images/something.jpg", mediaRep.getMediaURL());
	}
	
	public void test_tol_use_only_media() {
		mediaFile.setUsePermission(NodeImage.TOL_USE);
		mediaFile.setLocation("sparklyarmageddon.jpg");
		mediaFile.setCopyrightOwner("Andrew J. Lenards");
		
		assertEquals("sparklyarmageddon.jpg", mediaFile.getLocation());
		String mockMediaURL = "/ToLImage/images/" + mediaFile.getLocation();
		MediaContentRepresentation mediaRep = create(mediaFile, mockMediaURL);

		assertNotNull(mediaRep);
		assertFalse(mediaRep.isPublicDomain());
		assertTrue(mediaRep.isToLRelated());
		assertFalse(mediaRep.isExcludedMediaType());

		assertEquals("http://tolweb.org"+ContributorLicenseInfo.TOL_URL, mediaRep.getLicenseLink());
		assertNotNull(mediaRep.getCopyrightHolder());
		assertEquals(MediaContentRepresentation.HOST_MEDIA_URL+mockMediaURL, mediaRep.getMediaURL());

	}
	
	public void test_creative_commons_media() {
		mediaFile.setUsePermission(NodeImage.CC_BY_NC30);
		mediaFile.setLocation("sparklyarmageddon.jpg");
		mediaFile.setCopyrightOwner("Andrew J. Lenards");
		
		assertEquals("sparklyarmageddon.jpg", mediaFile.getLocation());
		String mockMediaURL = "/ToLImage/images/" + mediaFile.getLocation();
		MediaContentRepresentation mediaRep = create(mediaFile, mockMediaURL);
		
		assertNotNull(mediaRep);
		assertFalse(mediaRep.isPublicDomain());
		assertFalse(mediaRep.isToLRelated());
		assertFalse(mediaRep.isExcludedMediaType());
		
		assertEquals("http://creativecommons.org/licenses/by-nc/3.0/", mediaRep.getLicenseLink());
		assertNotNull(mediaRep.getCopyrightHolder());
		assertEquals(MediaContentRepresentation.HOST_MEDIA_URL+mockMediaURL, mediaRep.getMediaURL());		
	}
	
	public void test_unknown_mime_data_type() {
		mediaFile.setUsePermission(NodeImage.CC_BY_NC30);
		mediaFile.setLocation("sparklyarmageddon.ndy");
		mediaFile.setCopyrightOwner("Andrew J. Lenards");
		mediaFile.setComments("it's like ANDY without the A, Ndy...");
		
		assertEquals("sparklyarmageddon.ndy", mediaFile.getLocation());
		String mockMediaURL = "/ToLImage/images/" + mediaFile.getLocation();
		MediaContentRepresentation mediaRep = create(mediaFile, mockMediaURL);

		assertNotNull(mediaRep);
		assertFalse(mediaRep.isPublicDomain());
		assertFalse(mediaRep.isToLRelated());
		DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
		
		String dataType = dtd.getDataType();
		String mimeType = dtd.getMimeType();
		System.out.printf("data-type: %1$s mime-type: %2$s", dataType, mimeType);
		
		assertTrue(mediaRep.isExcludedMediaType());		
	}
	
	public void test_description_appears() {
		NodeImage img = createImage("Koalas");
		MediaContentRepresentation mcr = create(img);
		assertTrue(StringUtils.notEmpty(mcr.getDescription()));
		assertEquals("Group: Koalas;", mcr.getDescription());
		
		Movie mov = createMovie("Koalas");
		String description = "a koala sleeping at the Melbourne zoo, koalas sleep or rest 19 hours a day to make up for their poor diet";
		mov.setDescription(description);
		mcr = create(mov);
		assertTrue(StringUtils.notEmpty(mcr.getDescription()));
		assertEquals("Group: Koalas; Description: " + description +";", mcr.getDescription());
		
		Sound snd = createSound("Tasmanian-Devil");
		description = "the sound of several tasmanian devils fighting over roadkill";
		snd.setDescription(description);
		mcr = create(snd);
		assertTrue(StringUtils.notEmpty(mcr.getDescription()));
		assertEquals("Group: Tasmanian-Devil; Description: " + description +";", mcr.getDescription());		
	}
	
	private MediaContentRepresentation create(NodeImage media, String mediaURL) {
		MediaContentRepresentation mediaRep = new MediaContentRepresentation(media, mediaURL);
		return mediaRep;		
	}
	
	private MediaContentRepresentation create(NodeImage media) {
		return create(media, "/ToLImage/images/" + mediaFile.getLocation());
	}
	
	private NodeImage createImage(String groupName) {
		return createImage(groupName, DescriptionElements.None);
	}
	
	private NodeImage createImage(String groupName, DescriptionElements elements) {
		NodeImage media = new NodeImage();
		media.setScientificName(groupName);
		media.setLocation(groupName.toLowerCase() + ".png");
		return media; 
	}
	
	private Movie createMovie(String groupName) {
		return createMovie(groupName, DescriptionElements.None);
	}
	
	private Movie createMovie(String groupName, DescriptionElements elements) {
		Movie media = new Movie();
		media.setScientificName(groupName);
		media.setLocation(groupName.toLowerCase() + ".mp4");
		return media; 
	}
	
	private Sound createSound(String groupName) {
		return createSound(groupName, DescriptionElements.None);
	}
	
	private Sound createSound(String groupName, DescriptionElements elements) {
		Sound media = new Sound();
		media.setScientificName(groupName);
		media.setLocation(groupName.toLowerCase() + ".mp3");
		return media; 
	}	

//  The right way to test this would be to get something that would do general setting 
//  and getting properties that make of the description and then test the output.  I 
//	don't have time to do it right - so I'll leave the start in case I come back to it
//  guilty-party: lenards
//	
//	private void addDescriptionElements(NodeImage media, DescriptionElements elements) {
//		if (elements == DescriptionElements.All) {
//			
//		} else if (elements == DescriptionElements.Random) {
//			
//		}
//	}
//	
//	public void test_reflection_methods_creation() {
//		Map<String, Method> methods = getDescriptionElementMethods();
//		assertNotNull(methods);
//		assertTrue(!methods.isEmpty());
//		assertTrue(methods.size() >= 1);
//		try {
//			Method m = methods.get("Group:");
//			Object obj = m.invoke(mediaFile);
//			assertNotNull(obj);
//			String str = obj.toString();
//			assertTrue(StringUtils.notEmpty(obj.toString()));
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//			fail();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//			fail();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//	
//	private Map<String, Method> getDescriptionElementMethods() {
//		HashMap<String, Method> methods = new HashMap<String, Method>();
//
//		Method m;
//		try {
//			m = getMethod(NodeImage.class,"getScientificName");
//			methods.put("Group:", m);
//			m = getMethod(NodeImage.class,"getIdentifier");
//			methods.put("Identified by: ", m);
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return methods;
//	}
//	
//	private Method getMethod(Class clazz, String property) throws SecurityException, NoSuchMethodException {
//		return clazz.getMethod(property);
//	}
	
	// write a simple test for a media file, w/ contributor, w/ creative commons lic 
	
	// write a simple test for a media file, w/o contributor, w/ creative commons lic 
	// (consider writing "isValid" or "shouldInclude" helper for web services 
	//  - impl would be something like "right holder null && not public domain")
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	private enum DescriptionElements { All, Random, None }
}
