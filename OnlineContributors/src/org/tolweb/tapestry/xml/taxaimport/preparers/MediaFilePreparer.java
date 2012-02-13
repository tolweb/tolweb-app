package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.treegrow.main.NodeImage;

public class MediaFilePreparer extends XmlPreparerImpl {
	private List<NodeImage> media;
	
	public MediaFilePreparer(List<NodeImage> media) {
		this(media, NS);
	}
	
	public MediaFilePreparer(List<NodeImage> media, String namespace) {
		super(namespace);
		this.media = media;
	}
	
	@Override
	public Element toElement() {
		Element mediaFiles = createElement("mediafiles");
		processMedia(mediaFiles);
		return mediaFiles;
	}

	public void processMedia(Element mediaFiles) {
		for (NodeImage mediaFile : getMedia()) {
			Element childElement = createElement("mediafile");
			addAttributes(childElement, mediaFile);
			mediaFiles.appendChild(childElement);
		}
	}

	private void addAttributes(Element accessorypage, NodeImage mediaFile) {
		accessorypage.addAttribute(new Attribute("id", ""+mediaFile.getId()));
		accessorypage.addAttribute(new Attribute("type", NodeImage.getWordForMediaType(mediaFile.getMediaType())));
		accessorypage.addAttribute(new Attribute("data-table", "IMAGES"));
	}	
	
	/**
	 * @return the media
	 */
	public List<NodeImage> getMedia() {
		return media;
	}

	/**
	 * @param media the media to set
	 */
	public void setMedia(List<NodeImage> media) {
		this.media = media;
	}
}
