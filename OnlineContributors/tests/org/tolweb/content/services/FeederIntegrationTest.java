package org.tolweb.content.services;

import java.io.File;

import org.tolweb.content.feeds.Feeder;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.misc.ImageUtils;
import org.tolweb.treegrow.main.NodeImage;

public class FeederIntegrationTest extends ApplicationContextTestAbstract {
	private NodeDAO nodeDAO;
	private ImageDAO imageDAO;
	private ImageUtils imageUtils;
	
	public FeederIntegrationTest(String name) {
		super(name);
		nodeDAO = (NodeDAO)context.getBean("publicNodeDAO");
		imageDAO = (ImageDAO)context.getBean("imageDAO");
		imageUtils = (ImageUtils)context.getBean("imageUtils");
	}

	public void test_object_construction() {
		Feeder fdr = new Feeder(15994L, NodeImage.IMAGE, "/data/1.XLive/feeds", nodeDAO, imageDAO, imageUtils);
		assertNotNull(fdr);
		assertEquals(NodeImage.IMAGE, fdr.getMediaType());
		assertEquals("images", fdr.getMediaTypeWord());
	}

	public void test_can_write_images_content_feed() {
		Feeder fdr = new Feeder(15994L, NodeImage.IMAGE, "/data/1.XLive/feeds", nodeDAO, imageDAO, imageUtils);
		assertTrue(fdr.getFeedFileName().startsWith("/data/1.XLive/feeds"));
		assertEquals("/data/1.XLive/feeds/images/15994_rss.xml", fdr.getFeedFileName());
		fdr.writeFeed();
		File f = new File("/data/1.XLive/feeds/images/15994_rss.xml");
		assertTrue(f.exists());		
	}
	
	public void test_can_write_movies_content_feed() {
		Feeder fdr = new Feeder(15994L, NodeImage.MOVIE, "/data/1.XLive/feeds", nodeDAO, imageDAO, imageUtils);
		assertTrue(fdr.getFeedFileName().startsWith("/data/1.XLive/feeds"));
		assertEquals("/data/1.XLive/feeds/movies/15994_rss.xml", fdr.getFeedFileName());
		fdr.writeFeed();
		File f = new File("/data/1.XLive/feeds/movies/15994_rss.xml");
		assertTrue(f.exists());		
	}
}
