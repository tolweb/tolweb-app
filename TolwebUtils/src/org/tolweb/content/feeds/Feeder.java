package org.tolweb.content.feeds;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.ImageUtils;
import org.tolweb.misc.XmlPrettyPrinter;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.xml.sax.SAXException;

import com.sun.syndication.feed.module.mediarss.MediaEntryModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.module.mediarss.types.UrlReference;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public class Feeder {
	public static final String FEED_LINK = "http://tolweb.org/";
	public static final String MEDIA_LINK = "http://tolweb.org/media/";
	public static final String DEFAULT_FEED_TYPE = "rss_1.0";
	public static final String TITLE_FORMAT = "Tree of Life %1$s Feed%2$s";
	public static final String FILENAME_FORMAT = "%1$s/%2$s/%3$s_rss.xml";
	public static final String DEFAULT_DESC = "This feed has been created using ROME (Java syndication utilities)";
	public static final String HOSTNAME = "http://tolweb.org";
	public static final String TREE_DIR = "/tree";
	
	private SyndFeed feed; 
	private String feedType;
	
	private MappedNode mnode;
	private int mediaType; 
	private String mediaTypeWord;
	private String feedStore; 
	
	private String thumbnailUrl;
	private String mimeTypeName;
	private ImageDAO imageDAO;
	private ImageUtils imageUtils;

	private boolean prettyPrinting;
	
	public Feeder(Long nodeId, int mediaType, String feedStore, NodeDAO nodeDAO, ImageDAO imageDAO, ImageUtils imageUtils) {
		feedType = DEFAULT_FEED_TYPE;
		
		feed = new SyndFeedImpl();
		feed.setFeedType(feedType);
		
		this.mnode = nodeDAO.getNodeWithId(nodeId);
		this.mediaType = mediaType;
		this.feedStore = feedStore;
		this.imageDAO = imageDAO;
		this.imageUtils = imageUtils;
		this.prettyPrinting = true;
		
		setFeedMetadata();
		generateEntries();
	}

	private void setFeedMetadata() {
        String mediaTypeWord = NodeImage.getWordForMediaType(mediaType);
        setMediaTypeWord(mediaTypeWord + "s"); // make plural because the URI requests are plural
        mediaTypeWord = mediaTypeWord.substring(0, 1).toUpperCase() + mediaTypeWord.substring(1);
        feed.setTitle(String.format(TITLE_FORMAT, mediaTypeWord, getConditionalTitleSuffix()));
        feed.setLink(FEED_LINK);
        feed.setDescription(DEFAULT_DESC); 		
	}
	
	private String getConditionalTitleSuffix() {
		return (mnode != null) ? " For " + mnode.getName() : "";
	}

	private SyndEntry createEntryFor(NodeImage media, String scientificName) {
		SyndEntry entry = new SyndEntryImpl();
		
		String title = scientificName;
		if (mediaType == NodeImage.MOVIE) {
			title += " - " + media.getTitle();
		}
        entry.setTitle(title);
        entry.setLink(MEDIA_LINK + media.getId());
        entry.setPublishedDate(media.getLastEditedDate());
        return entry;
	}
	
	@SuppressWarnings("unchecked")
	public void addMediaEntryTo(SyndEntry entry, NodeImage media) {
        try {
        	MediaEntryModuleImpl mediaContentModule = createMediaContentModule(media, entry.getLink(), mediaType);
        	entry.getModules().add(mediaContentModule);
        } catch (MalformedURLException mal) {
        	mal.printStackTrace();
        	System.out.println("ERROR: "+mal.getMessage());
        }		
	}
	
	@SuppressWarnings("unchecked")
	private void generateEntries() {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        
        SyndEntry entry;
        SyndContent description;
        int i = 0;
        
        List<NodeImage> images = imageDAO.getLatestGalleryImagesForNode(mnode, 20, mediaType);
        
        for (NodeImage image : images) {
			String sciName = determineScientificName(image);				    
		
			entry = createEntryFor(image, sciName);
			addMediaEntryTo(entry, image);
            description = createDescriptionFor(entry, image);
            addEnclosureTo(entry);
            entry.setDescription(description);
            
            entries.add(entry);
		}
        feed.setEntries(entries);
	}

	@SuppressWarnings("unchecked")
	private void addEnclosureTo(SyndEntry entry) {
		SyndEnclosure enclosure = new SyndEnclosureImpl();
		enclosure.setUrl(getThumbnailUrl());
		enclosure.setType(getMimeTypeName());
		entry.getEnclosures().add(enclosure);
	}

	private SyndContent createDescriptionFor(SyndEntry entry, NodeImage image) {
		SyndContent description;
		description = new SyndContentImpl();
		description.setType("text/html");
		String copyrightOwner = "";
		if (image.getCopyrightContributorId() != 0) {
			Contributor copyrightContr = image.getCopyrightOwnerContributor();
			copyrightOwner = copyrightContr.getName();
			entry.setAuthor(copyrightOwner);
			List<String> contrs = new ArrayList<String>();
			contrs.add(copyrightContr.getName());
			entry.setContributors(contrs);
		} else {
			copyrightOwner = image.getCopyrightOwner();
		}
		
		String descText = copyrightOwner + " - " + image.getCreationDate().toString();
		description.setValue(descText + getThumbnailHtml());
		return description;
	}

	private String determineScientificName(NodeImage image) {
		String sciName = image.getScientificName();
		if (StringUtils.notEmpty(sciName)) {
			//sciName = sciName;
		} else if (image.getNodesSet().size() > 0) {
			sciName = ((MappedNode) image.getNodesSet().iterator().next()).getName();
		} else {
			sciName = "";
		}
		return sciName;
	}	
	
	public String rawFeed() {
        String rawFeed = null;
		try {
			StringWriter writer = new StringWriter();
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			rawFeed = writer.toString();
		} catch (FeedException fe) {
			System.out.println("ERROR: " + fe.getMessage());
		} catch (IOException ioe) {
            System.out.println("ERROR: " + ioe.getMessage());
		}		
		return rawFeed;
	}	

	public String formattedFeed() {
		try {
			return XmlPrettyPrinter.prettyPrint(rawFeed());
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// we've horribly failed if we reach this so let them eat null!
		return null;
	}
	
	public String getFeedFileName() {
		return String.format(FILENAME_FORMAT, feedStore, getMediaTypeWord(), mnode.getNodeId().toString());
	}
	
	public void writeFeed() {
		String filename = getFeedFileName();
		
		try {
			FileWriter writer = new FileWriter(filename);
			writer.write(formattedFeed());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getThumbnailHtml() {
		String fmt = "<p><a href=\"%1$s\"><img src=\"%1$s\" /></a></p>";
		return String.format(fmt, getThumbnailUrl());
	}
	
	private MediaEntryModuleImpl createMediaContentModule(NodeImage image, String mediaLink, int mediaType) throws MalformedURLException {
		ImageVersion masterImgVrsn = (ImageVersion)imageDAO.getMasterVersion(image.getId());
		String thumbnailUrl = imageDAO.getThumbnailUrlForImageWithId(image.getId());
		thumbnailUrl = (!thumbnailUrl.startsWith(TREE_DIR)) ? TREE_DIR + thumbnailUrl : thumbnailUrl;
		String mediaUrl = imageUtils.getMediaUrl(mediaType, image.getLocation());
		MediaContent[] contents = new MediaContent[1]; 
		MediaContent mediaItem = new MediaContent(new UrlReference(HOSTNAME + mediaUrl));
		contents[0] = mediaItem; 
		
		if (masterImgVrsn != null) {
			if (StringUtils.notEmpty(masterImgVrsn.getFileSize())) {
				//mediaItem.setFileSize(new Long(masterImgVrsn.getFileSize()));
			}
			mediaItem.setWidth(masterImgVrsn.getWidth());
			mediaItem.setHeight(masterImgVrsn.getHeight());
		}

		if (image.getMediaType() == NodeImage.IMAGE) {
			if (masterImgVrsn.getFileName().toLowerCase().endsWith("png")) {
				mediaItem.setType("image/png");
			} else if (masterImgVrsn.getFileName().toLowerCase().endsWith("gif")) {
				mediaItem.setType("image/gif");
			} else {
				mediaItem.setType("image/jpeg");
			}
			
		}
		
		if (image.getMediaType() == NodeImage.MOVIE) {
			//Movie mov = (Movie)image;
			String fileExtension = image.getLocation().substring(image.getLocation().lastIndexOf(".")+1);
			mediaItem.setType("video/" + fileExtension);
		}
		
		setMimeTypeName(mediaItem.getType());
		
		Thumbnail[] thumbs = new Thumbnail[1];
		thumbs[0] = new Thumbnail(new URL(HOSTNAME + thumbnailUrl));
		setThumbnailUrl(HOSTNAME + thumbnailUrl);
		ContributorLicenseInfo licInfo = new ContributorLicenseInfo(image.getUsePermission());
		
		Metadata mdata = new Metadata();
		mdata.setKeywords(new String[]{image.getScientificName()});
		mdata.setThumbnail(thumbs);
		mdata.setTitle(image.getScientificName());
		mdata.setCopyright(licInfo.toString());
		mdata.setCopyrightUrl(new URL(ContributorLicenseInfo.linkString(licInfo)));
		mediaItem.setMetadata(mdata);
		MediaEntryModuleImpl module = new MediaEntryModuleImpl();
		module.setMediaContents(contents);
		return module;
	}

	/**
	 * @return the mediaType
	 */
	public int getMediaType() {
		return mediaType;
	}

	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * @return the thumbnailUrl
	 */
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	/**
	 * @param thumbnailUrl the thumbnailUrl to set
	 */
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	/**
	 * @return the mimeTypeName
	 */
	public String getMimeTypeName() {
		return mimeTypeName;
	}

	/**
	 * @param mimeTypeName the mimeTypeName to set
	 */
	public void setMimeTypeName(String mimeTypeName) {
		this.mimeTypeName = mimeTypeName;
	}

	/**
	 * @return the prettyPrinting
	 */
	public boolean isPrettyPrinting() {
		return prettyPrinting;
	}

	/**
	 * @param prettyPrinting the prettyPrinting to set
	 */
	public void setPrettyPrinting(boolean prettyPrinting) {
		this.prettyPrinting = prettyPrinting;
	}

	public String getMediaTypeWord() {
		return mediaTypeWord;
	}

	public void setMediaTypeWord(String mediaTypeWord) {
		this.mediaTypeWord = mediaTypeWord;
	}	
}
