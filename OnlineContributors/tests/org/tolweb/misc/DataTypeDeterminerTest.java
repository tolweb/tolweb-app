package org.tolweb.misc;

import java.util.Iterator;
import java.util.SortedSet;

import org.tolweb.content.helpers.DataTypeDeterminer;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.treegrow.main.NodeImage;

public class DataTypeDeterminerTest extends ApplicationContextTestAbstract {
	public PageDAO pageDAO;
	public ImageDAO imageDAO;
	
	public DataTypeDeterminerTest(String name) {
		super(name);
		pageDAO = (PageDAO)context.getBean("workingPageDAO");
		imageDAO = (ImageDAO)context.getBean("imageDAO");
		assertNotNull(pageDAO);
		assertNotNull(imageDAO);
	}

	public void test_data_type_correct_for_text_sections_and_media() {
		Long pageId = 1285L;
		verifyDataAssociatedWithMappedPage(pageId);
		pageId = 1892L;
		verifyDataAssociatedWithMappedPage(pageId);
		pageId = 3660L;
		verifyDataAssociatedWithMappedPage(pageId);
		pageId = 650L;
		verifyDataAssociatedWithMappedPage(pageId);
	}

	private void verifyDataAssociatedWithMappedPage(Long pageId) {
		MappedPage mpage = pageDAO.getPageWithId(pageId);
		for (Iterator<MappedTextSection> itr = mpage.getTextSections().iterator(); itr.hasNext(); ) {
			DataTypeDeterminer dtd = new DataTypeDeterminer(itr.next());
			assertEquals(dtd.getDataType(), DataTypeDeterminer.TEXT_URI);
		}
		
		SortedSet tilluses = mpage.getTitleIllustrations();
		for (Iterator<TitleIllustration> itr = tilluses.iterator(); itr.hasNext(); ) {
			TitleIllustration tillus = itr.next();
			if (tillus != null && tillus.getVersion() != null) {
				NodeImage mediaFile = tillus.getVersion().getImage();
				DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
				assertEquals(dtd.getDataType(), DataTypeDeterminer.STILL_IMAGE_URI);
			}
		}
	}
	
	public void test_each_data_type_with_bogus_object_state() {
		DataTypeDeterminer dtd; 
		
		dtd = new DataTypeDeterminer(new NodeImage());
		assertEquals(dtd.getDataType(), DataTypeDeterminer.STILL_IMAGE_URI);
		
		dtd = new DataTypeDeterminer(new Movie());
		assertEquals(dtd.getDataType(), DataTypeDeterminer.MOVING_IMAGE_URI);
		
		Document doc1 = new Document();
		doc1.setIsDataset(true);
		dtd = new DataTypeDeterminer(doc1);
		assertEquals(dtd.getDataType(), DataTypeDeterminer.DATASET_URI);
		
		Document doc2 = new Document();
		dtd = new DataTypeDeterminer(doc2);
		assertEquals(dtd.getDataType(), DataTypeDeterminer.TEXT_URI);
		
		dtd = new DataTypeDeterminer(new Sound());
		assertEquals(dtd.getDataType(), DataTypeDeterminer.SOUND_URI);
		
		dtd = new DataTypeDeterminer(new MappedTextSection());
		assertEquals(dtd.getDataType(), DataTypeDeterminer.TEXT_URI);
		
		dtd = new DataTypeDeterminer(new BogusMediaFile());
		assertEquals(dtd.getDataType(), "");
	}

	public void test_bogus_mime_type() {
		NodeImage mediaFile = new NodeImage();
		mediaFile.setScientificName("sparklyarmageddon");
		mediaFile.setId(650);
		mediaFile.setUsePermission(NodeImage.CC_BY_NC30);
		mediaFile.setLocation("sparklyarmageddon.ndy");
		mediaFile.setCopyrightOwner("Andrew J. Lenards");
		mediaFile.setComments("it's like ANDY without the A, Ndy...");
		
		DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
		
		String dataType = dtd.getDataType();
		String mimeType = dtd.getMimeType();
		assertEquals(DataTypeDeterminer.UNKNOWN_MIME_TYPE, mimeType);
		assertTrue(dtd.isUnknown());
	}
	
	public void test_each_data_type_with_real_data() {
		// first 50 are (discriminator == 0), or still images
		System.out.println("images:");
		for (int i = 0; i < 50; i++) {
			NodeImage mediaFile = imageDAO.getImageWithId(i+1);
			DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
			assertEquals(dtd.getDataType(), DataTypeDeterminer.STILL_IMAGE_URI);
			System.out.printf("\tmedia-id: %1$d mime-type: %2$s filename: %3$s \n", 
					mediaFile.getId(), dtd.getMimeType(), mediaFile.getLocation());
		}
		
		int[] sounds = new int[] { 7662, 7663, 7666, 7669, 7668, 7670, 7983,
				9446, 10162, 12056, 12226, 12227, 13802, 17899, 15407, 27041,
				23393, 16264, 16333, 23394, 18240, 17491, 23396, 23109, 23110,
				24752, 23404, 20232, 20250, 22959, 23392, 22958, 23391, 23359,
				23395, 23111, 23398, 23108, 24751, 23403, 23401, 23405, 24754,
				22465, 22466, 22467, 22468, 22469, 22470, 22471 };
		// 50 ids that are sounds
		System.out.println("sounds:");
		for (int i = 0; i < sounds.length; i++) {
			NodeImage mediaFile = imageDAO.getImageWithId(sounds[i]);
			if (mediaFile != null) {
				DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
				assertEquals(dtd.getDataType(), DataTypeDeterminer.SOUND_URI);			
				System.out.printf("\tmedia-id: %1$d mime-type: %2$s filename: %3$s \n", 
						mediaFile.getId(), dtd.getMimeType(), mediaFile.getLocation());
			} else {
				System.out.printf("media file with id: %1$d is null.\n", sounds[i]);
			}
		}
		
		int[] movies = new int[] { 6879, 6880, 6889, 7066, 7067, 7406, 7430,
				7431, 7432, 7486, 7656, 7657, 8736, 8743,
				8757, 8792, 8881, 10725, 26283, 26627, 26657, 10395,
				26968, 10484, 10544, 10545, 10548, 10549,
				10550, 10551, 10566, 10593, 20799, 10726, 10727, 10728, 10729,
				10731, 10732, 10733, 26979, 26845, 11949, 12537, 13479, 23917,
				23918, 13985, 13986, 14192, 14347 };
		// 50 ids that are movies
		System.out.println("movies:");
		for (int i = 0; i < movies.length; i++) {
			NodeImage mediaFile = imageDAO.getImageWithId(movies[i]);
			if (mediaFile != null) {
				DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
				assertEquals(dtd.getDataType(), DataTypeDeterminer.MOVING_IMAGE_URI);
				System.out.printf("\tmedia-id: %1$d mime-type: %2$s filename: %3$s \n", 
						mediaFile.getId(), dtd.getMimeType(), mediaFile.getLocation());
			} else {
				System.out.printf("media file with id: %1$d is null.\n", movies[i]);
			}
		}		
		
		int[] docs = new int[] { 10710, 19319, 7660, 7664, 7982, 7986, 8085,
				8086, 8441, 8362, 8418, 8419, 8442, 8443, 14060, 8660, 8664,
				8904, 8924, 9058, 9117, 9208, 9209, 9210, 9224, 9230, 9956,
				9526, 9609, 9620, 9621, 9622, 9623, 9624, 9626, 9707, 9744,
				9745, 9961, 10056, 10057, 10058, 10061, 10065, 10088, 10685,
				10684, 10103, 10104, 10106 };
		for (int i = 0; i < docs.length; i++) {
			NodeImage mediaFile = imageDAO.getImageWithId(docs[i]);
			Class clazz = mediaFile.getClass();
			//System.out.println(clazz);
			DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
			Document doc = (Document)mediaFile;
			if (doc.getIsDataset()) {
				assertEquals(dtd.getDataType(), DataTypeDeterminer.DATASET_URI);
			} else if (!doc.getIsDataset()) {
				assertEquals(dtd.getDataType(), DataTypeDeterminer.TEXT_URI);
			} else {
				fail();
			}
		}
	}

	
	
	
	// used to force failure of the DataTypeDeterminer class
	private class BogusMediaFile extends NodeImage {
		// unsupport media data type discriminator
	    public int getMediaType() {
	        return 650;
	    }		
	}
}
