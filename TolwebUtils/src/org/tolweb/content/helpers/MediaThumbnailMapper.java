package org.tolweb.content.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolweb.dao.ImageDAO;
import org.tolweb.treegrow.main.NodeImage;

public class MediaThumbnailMapper {
	private ImageDAO imageDAO;
	private List<NodeImage> mediaFiles;
	
	public MediaThumbnailMapper(ImageDAO imageDAO, List<NodeImage> mediaFiles) {
		super();
		this.imageDAO = imageDAO;
		this.mediaFiles = mediaFiles;
	}
	
	public Map<Integer, String> createMap() {
		HashMap<Integer, String> mediaToThumbs = new HashMap<Integer, String>();
		for (NodeImage img : mediaFiles) {
			String thumbnailUrl = imageDAO.getThumbnailUrlForImageWithId(img.getId());
			// we need to add the host name to the urls - this work is already done 
			// by the embedded media handler, so we'll use that functionality
			thumbnailUrl = EmbeddedMediaHandler.resolveThumbnailURL(thumbnailUrl);
			mediaToThumbs.put(Integer.valueOf(img.getId()), thumbnailUrl);
		}
		return mediaToThumbs;
	}
}
