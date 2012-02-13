package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.NodeImage;

public class ImageDAOGetLatestGalleryImagesTest extends ApplicationContextTestAbstract {
	private ImageDAO dao;
	private NodeDAO nodeDAO; 
	
	public ImageDAOGetLatestGalleryImagesTest(String name) {
		super(name);
		dao = (ImageDAO) context.getBean("imageDAO");
		nodeDAO = (NodeDAO) context.getBean("publicNodeDAO");
	}
	
	@SuppressWarnings("unchecked")
	public void testGetLatestGalleryImageFunctionality() {
		MappedNode life = nodeDAO.getNodeWithId(1L);
		List imgs = dao.getLatestGalleryImagesForNode(life, 10, NodeImage.IMAGE);
		System.out.println(imgs.size());
	}
}
