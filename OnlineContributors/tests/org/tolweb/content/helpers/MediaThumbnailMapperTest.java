package org.tolweb.content.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ImageDAO;

public class MediaThumbnailMapperTest extends ApplicationContextTestAbstract {
	private ImageDAO imageDAO;
	
	public MediaThumbnailMapperTest(String name) {
		super(name);
		imageDAO = (ImageDAO)context.getBean("imageDAO");
	}

	/**
	 * Usage pattern copied from EolContentPreparer.addAttachedMediaDataObjects() method
	 */
	public void test_all_thumbnails_are_absolute_urls() {
		// As of: 2/12/2009
		// media id 35092 appears in http://tolweb.org/webservices/pagecontent/nc/28759
		// with a relative URL
		List mediaFiles = imageDAO.getNativeAttachedImagesForNode(28759L, new ArrayList());
		MediaThumbnailMapper mapper = new MediaThumbnailMapper(imageDAO, mediaFiles);
		Map<Integer, String> mediaThumbs = mapper.createMap();
		for (String thumb : mediaThumbs.values()) {
			System.out.println(thumb);
			assertTrue(thumb.startsWith(EmbeddedMediaHandler.TOLWEB_HOSTNAME));			
		}
	}
}
