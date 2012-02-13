package org.tolweb.content.services;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

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


/**
 * Images:
 * onlinecontributors/app?page=content/FeedGenerator&service=external&nodeId=8221&mediaType=0
 * Movies:
 * onlinecontributors/app?page=content/FeedGenerator&service=external&nodeId=8221&mediaType=2
 */
public abstract class FeedGenerator extends XMLContentBaseService implements NodeInjectable, ImageInjectable, UserInjectable {

    /** Namespace URI for content:encoded elements */
    //private static String CONTENT_NS = "http://purl.org/rss/1.0/modules/content/";

    public abstract String getThumbnailUrl(); 
    public abstract void setThumbnailUrl(String thumb);
    
    public abstract String getMediaType(); 
    public abstract void setMediaType(String mtype);
    
    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
/* an algorithm for one possible refactor of the page to generate multiple feeds 
(algorithm) 
	for each node with a page attached
		Get latest gallery media since "last run" 

		if you get back any results
			create a feed for that media type (images, movies) 
			write the page to the feed directory 
 */    	
        boolean ok = false;

    	String nodeIdText = getRequest().getParameterValue("nodeId");
    	String mediaTypeText = getRequest().getParameterValue("mediaType");
    	
    	Long nodeId = Long.valueOf(1); 
    	if (StringUtils.notEmpty(nodeIdText)) {
    		nodeId = Long.valueOf(nodeIdText);
    	}
    	
    	int mediaType = NodeImage.IMAGE;
    	if (StringUtils.notEmpty(mediaTypeText)) {
    		mediaType = Integer.parseInt(mediaTypeText);
    	}
    	MappedNode mnode = getPublicNodeDAO().getNodeWithId(nodeId);
    	
        String feedType = "rss_1.0";
        
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        String mediaTypeWord = NodeImage.getWordForMediaType(mediaType);
        mediaTypeWord = mediaTypeWord.substring(0, 1).toUpperCase() + mediaTypeWord.substring(1);
        
        feed.setTitle("Tree of Life " + mediaTypeWord + " Feed" + ((mnode != null) ? " For " + mnode.getName() : ""));
        feed.setLink("http://tolweb.org/");
        feed.setDescription("This feed has been created using ROME (Java syndication utilities)");
        
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;
        int i = 0;
        List images = getImageDAO().getLatestGalleryImagesForNode(mnode, 20, mediaType);
        
        for (Iterator iter = images.iterator(); iter.hasNext(); i++) {
			NodeImage image = (NodeImage) iter.next();
		    
			String sciName = image.getScientificName();
			if (StringUtils.notEmpty(sciName)) {
				//sciName = sciName;
			} else if (image.getNodesSet().size() > 0) {
				sciName = ((MappedNode) image.getNodesSet().iterator().next()).getName();
			} else {
				sciName = "";
			}				    
			
			entry = new SyndEntryImpl();
			
			String title = sciName;
			if (mediaType == NodeImage.MOVIE) {
				title += " - " + image.getTitle();
			}
            entry.setTitle(title);
            String mediaLink = "http://tolweb.org/media/" + image.getId(); 
            entry.setLink(mediaLink);

            //ContentModule cmodule = ((ContentModule) entry.getModule(CONTENT_NS));                
            
            try {
            	MediaEntryModuleImpl mediaContentModule = createMediaContentModule(image, mediaLink, mediaType);
            	entry.getModules().add(mediaContentModule);
            } catch (MalformedURLException mal) {
            	mal.printStackTrace();
            	System.out.println("ERROR: "+mal.getMessage());
            }
            
            entry.setPublishedDate(image.getLastEditedDate());
            
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
            
            SyndEnclosure enclosure = new SyndEnclosureImpl();
            enclosure.setUrl(getThumbnailUrl());
            enclosure.setType(getMediaType());
            entry.getEnclosures().add(enclosure);
            
            entry.setDescription(description);
            entries.add(entry);
		}

        feed.setEntries(entries);

        
        String preparedFeed = null;
		try {
			StringWriter writer = new StringWriter();
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			preparedFeed = writer.toString();
		} catch (FeedException fe) {
			System.out.println("ERROR: " + fe.getMessage());
		} catch (IOException ioe) {
            System.out.println("ERROR: " + ioe.getMessage());
		} 
        
        System.out.println("The feed has been written to the response...");   	        
        
        
        if (preparedFeed != null) {
			try {
				SAXBuilder saxBuilder = new SAXBuilder(
						"org.apache.xerces.parsers.SAXParser");
				Reader stringReader = new StringReader(preparedFeed);
				Document jdoc = saxBuilder.build(stringReader);
				setResultDocument(jdoc);
			} catch (JDOMException e) {
				System.out.println("ERROR: " + e.getMessage());
			} catch (IOException ioe) {
	            System.out.println("ERROR: " + ioe.getMessage());
			} 
			ok = true;
		}
	        
		if (!ok) {
            System.out.println();
            System.out.println("FeedGenerator creates a RSS/Atom feed and writes it to a file.");
            System.out.println("The first parameter must be the syndication format for the feed");
            System.out.println("  (rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0, rss_2.0 or atom_0.3)");
            System.out.println("The second parameter must be the file name for the feed");
            System.out.println();
        }
        
    }

	private String getThumbnailHtml() {
		String fmt = "<p><img src=\"%1$s\" /></p>";
		return String.format(fmt, getThumbnailUrl());
	}
	
	private MediaEntryModuleImpl createMediaContentModule(NodeImage image, String mediaLink, int mediaType) throws MalformedURLException {
		ImageVersion masterImgVrsn = (ImageVersion)getImageDAO().getMasterVersion(image.getId());
		String thumbnailUrl = getImageDAO().getThumbnailUrlForImageWithId(image.getId());
		thumbnailUrl = (!thumbnailUrl.startsWith("/tree")) ? "/tree" + thumbnailUrl : thumbnailUrl;
		String mediaUrl = getImageUtils().getMediaUrl(mediaType, image.getLocation());
		MediaContent[] contents = new MediaContent[1]; 
		MediaContent mediaItem = new MediaContent(new UrlReference("http://tolweb.org" + mediaUrl));
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
		
		setMediaType(mediaItem.getType());
		
		Thumbnail[] thumbs = new Thumbnail[1];
		thumbs[0] = new Thumbnail(new URL("http://tolweb.org" + thumbnailUrl));
		setThumbnailUrl("http://tolweb.org" + thumbnailUrl);
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
    
	
}
