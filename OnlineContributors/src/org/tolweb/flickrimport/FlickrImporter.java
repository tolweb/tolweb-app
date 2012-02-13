package org.tolweb.flickrimport;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.tolweb.base.http.BaseHttpRequestMaker;
import org.tolweb.base.xml.BaseXMLWriter;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * Dubbed the "FlickrSuckr" - this component uses the REST API exposed by the Flickr photo site 
 * to pull image from their site into the Tree of Life.  As the API has gotten more sophisticated,  
 * we've taken advantage of the metadata provided to automatically perform more actions as the 
 * media are brought into the ToL system. 
 * 
 * @author dmandel
 *
 * DEVN: if you want to see the return document for a rest-flickr request, use the URL below: 
 * 
 * http://www.flickr.com:80/services/rest/?method=flickr.photos.getInfo&api_key=440805fc34ad9354bb3da595d6b4545a&photo_id=3182024007
 */
public class FlickrImporter {
	public static final String SECRET = "470755ab3ee57d37";
	public static final String API_KEY = "440805fc34ad9354bb3da595d6b4545a";
	private static final String BASE_SERVICE_URL = "http://www.flickr.com:80/services/rest/?method=flickr.photos.";
	private static final String GET_INFO = "getInfo";
	private static final String GET_SIZES = "getSizes";
	private static final Long FLICKR_DB_ID = 2L;
	private ImageHelper imageHelper;
	private ImageDAO imageDAO;
	private NodeDAO nodeDAO;
	
	public NodeImage importFlickrPicture(String photoId) {
		//Document validLicenses = BaseHttpRequestMaker.getHttpResponseAsDocument(getServiceUrl("", "licenses.getInfo", false));
		Document infoDoc = BaseHttpRequestMaker.getHttpResponseAsDocument(getInfoUrl(photoId));
		Document sizeDoc = BaseHttpRequestMaker.getHttpResponseAsDocument(getSizesUrl(photoId));
		String largestImageUrl = getLargestImageUrl(sizeDoc);
		NodeImage image = parseInfoDocAndCreateImage(infoDoc);
		try {
			URL url = new URL(largestImageUrl);
			String file = url.getFile();
			System.out.println("file is: " + file);
			InputStream stream = url.openStream();
			imageHelper.saveAndWriteOutImageStream(image, stream, file, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	@SuppressWarnings("unchecked")
	private String getLargestImageUrl(Document sizeDoc) {
		/*
		 * <?xml version="1.0" encoding="ISO-8859-1"?>
			<rsp stat="ok">
  			 <sizes>
    			<size label="Square" width="75" height="75" 
    					source="http://farm1.static.flickr.com/38/83424671_9b9e64f2a1_s.jpg" 
    					url="http://www.flickr.com/photo_zoom.gne?id=83424671&amp;size=sq" />
    			<size label="Thumbnail" width="100" height="75" 
    					source="http://farm1.static.flickr.com/38/83424671_9b9e64f2a1_t.jpg" 
    					url="http://www.flickr.com/photo_zoom.gne?id=83424671&amp;size=t" />
    			<size label="Small" width="240" height="180" 
    					source="http://farm1.static.flickr.com/38/83424671_9b9e64f2a1_m.jpg" 
    					url="http://www.flickr.com/photo_zoom.gne?id=83424671&amp;size=s" />
    			<size label="Medium" width="500" height="375" 
    					source="http://farm1.static.flickr.com/38/83424671_9b9e64f2a1.jpg" 
    					url="http://www.flickr.com/photo_zoom.gne?id=83424671&amp;size=m" />
    			<size label="Large" width="1024" height="768" 
    					source="http://farm1.static.flickr.com/38/83424671_9b9e64f2a1_b.jpg" 
    					url="http://www.flickr.com/photo_zoom.gne?id=83424671&amp;size=l" />
  			 </sizes>
			</rsp>
		 */
			
		Element sizes = sizeDoc.getRootElement().getChild("sizes");
		if (sizes == null) {
			System.out.println("bad size is: " + BaseXMLWriter.getDocumentAsString(sizeDoc));
		}
		String largestImageUrl = null;
		int largestHeight = 0;
		
		org.jdom.Element rootNode = null;
		try {
		    rootNode = (org.jdom.Element) XPath.selectSingleNode(sizeDoc, "/rsp[@stat='ok']");
		} catch (Exception e) {}
		
		if (rootNode != null ) { // this means the status is 'ok' 
			for (Iterator iter = sizes.getChildren("size").iterator(); iter.hasNext();) {
				Element currentSizeElement = (Element) iter.next();
				String heightAttr = currentSizeElement.getAttributeValue("height");
				if (heightAttr != null) {
					int height = Integer.parseInt(heightAttr);
					if (largestImageUrl == null || height > largestHeight) {
						largestImageUrl = currentSizeElement.getAttributeValue("source");
						largestHeight = height;
					}
				}
			}
		}
		return largestImageUrl;
	}
	
	@SuppressWarnings("unchecked")
	private NodeImage parseInfoDocAndCreateImage(Document doc) {
		/*
		 * <rsp stat="ok">
  			<photo id="83424671" secret="9b9e64f2a1" server="38" farm="1" dateuploaded="1136653798" 
  				isfavorite="0" license="0" rotation="0" originalsecret="9b9e64f2a1" originalformat="jpg">
			    <owner nsid="26527959@N00" username="danny_mandel" realname="" location="" />
			    <title>DSC02505</title>
			    <description>Danny and Vene say &amp;quot;Get us out of here, we need to get back to Tucson 
			    as soon as possible, we're going insane!&amp;quot;</description>
			    <visibility ispublic="1" isfriend="0" isfamily="0" />
			    <dates posted="1136653798" taken="2006-01-04 11:37:36" takengranularity="0" lastupdate="1137095351" />
			    <editability cancomment="0" canaddmeta="0" />
			    <comments>0</comments>
			    <notes />
			    <tags>
			      <tag id="2140142-83424671-3272" author="26527959@N00" raw="europe" machine_tag="0">europe</tag>
			    </tags>
			    <urls>
			      <url type="photopage">http://www.flickr.com/photos/dannymandel/83424671/</url>
			    </urls>
  			</photo>
		   </rsp>

			dateuploaded - use that as the copyright date
			license - if licensed under creative commons, select the proper license in the Use Permitted by Copyright Owner, 
					  if All rights reserved, select ToL use only, minor modification
			nsid - use this to construct link to copyright owner's url. I think they usually work like this: http://flickr.com/people/[nsid]
			realname - use that as the name of the copyright owner
			title - use in the Title of Source File field
			url type="photopage" - use in the Url of Source File field
		 */
		NodeImage returnImage = new NodeImage();
		returnImage.setImageType(NodeImage.PHOTOGRAPH);
		Element photoElement = doc.getRootElement().getChild("photo");
		if (isResponseStatusOk(doc)) {
			processMetadata(returnImage, photoElement);
			processDates(returnImage, photoElement);
			processOwner(returnImage, photoElement);
			processTitle(returnImage, photoElement);
			processDescription(returnImage, photoElement);
			processPhotoPageUrls(returnImage, photoElement);
			processSourceDatabase(returnImage);
			processMediaLicense(returnImage, photoElement);
		}
		return returnImage;
	}

	private void processDescription(NodeImage returnImage, Element photoElement) {
		Element descElement = photoElement.getChild("description");
		if (descElement != null) {
			returnImage.setComments(descElement.getText());
		}
	}

	private void processSourceDatabase(NodeImage returnImage) {
		// a hack!  should be fixed at some point to lookup Flickr, but works for now.
		returnImage.setSourceDbId(FLICKR_DB_ID);
	}

	private void processMediaLicense(NodeImage returnImage, Element photoElement) {
		/*
		 * flickr licenses:
		 * <?xml version="1.0" encoding="ISO-8859-1"?>
			<rsp stat="ok">
			  <licenses>
			   <license id="0" name="All Rights Reserved" url="" />
			   <license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
			   <license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
			   <license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
			   <license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
			   <license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
			   <license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
			  </licenses>
			</rsp>
		 */		
		String license = photoElement.getAttributeValue("license");
		int licenseInt = Integer.parseInt(license);
		if (licenseInt == 0) {
			returnImage.setUsePermission(NodeImage.TOL_USE);
			returnImage.setModificationPermitted(false);
		} else {
			switch (licenseInt) {
				case 1: returnImage.setUsePermission(NodeImage.CC_BY_NC_SA20); break;
				case 2: returnImage.setUsePermission(NodeImage.CC_BY_NC20); break;
				case 3: returnImage.setUsePermission(NodeImage.CC_BY_NC_ND20); break;
				case 4: returnImage.setUsePermission(NodeImage.CC_BY20); break;
				case 5: returnImage.setUsePermission(NodeImage.CC_BY_SA20); break;
				case 6: returnImage.setUsePermission(NodeImage.CC_BY_ND20); break;
			}
			returnImage.setModificationPermitted(true);
		}
	}

	private void processPhotoPageUrls(NodeImage returnImage,
			Element photoElement) {
		Element urlsElement =  photoElement.getChild("urls");
		if (urlsElement != null) {
			for (Iterator iter = urlsElement.getChildren("url").iterator(); iter.hasNext();) {
				Element nextUrl = (Element) iter.next();
				String type = nextUrl.getAttributeValue("type"); 
				if (StringUtils.notEmpty(type) && type.equals("photopage")) {
					String sourceUrl = nextUrl.getText();
					returnImage.setSourceCollectionUrl(sourceUrl);
					break;
				}
			}
		}
	}

	private void processTitle(NodeImage returnImage, Element photoElement) {
		String title = photoElement.getChildText("title");
		returnImage.setTitle(title);
		returnImage.setSourceCollectionTitle(title);
	}

	private void processOwner(NodeImage returnImage, Element photoElement) {
		Element ownerElement = photoElement.getChild("owner");
		if (ownerElement != null) {
			String nsid = ownerElement.getAttributeValue("nsid");
			returnImage.setCopyrightUrl("http://flickr.com/people/" + nsid);
			String realName = ownerElement.getAttributeValue("realname");
			if (StringUtils.isEmpty(realName)) {
				realName = ownerElement.getAttributeValue("username");
			}
			returnImage.setCopyrightOwner(realName);
		}
	}

	private void processDates(NodeImage returnImage, Element photoElement) {
		String takenString = photoElement.getChild("dates").getAttributeValue("taken");
		if (StringUtils.notEmpty(takenString)) {
			Calendar calendar = StringUtils.parseMySqlDateString(takenString);
			returnImage.setCopyrightDate("" + calendar.get(Calendar.YEAR));			
		} else {
			// is there any greater failure in the JDK than the Date class? 
			//returnImage.setCopyrightDate("" + new Date().getYear());
			// the below is hopefully correct - it's what the JavaDoc indicates at the replacement
			returnImage.setCopyrightDate("" + Calendar.getInstance().get(Calendar.YEAR));
			
		}
	}

	@SuppressWarnings("unchecked")
	private void processMetadata(NodeImage returnImage, Element photoElement) {
		List machineTags = getMachineTags(photoElement);
		attachNode(returnImage, machineTags);
		String country = getCountryInfo(machineTags, photoElement);
		if (!StringUtils.isEmpty(country)) {
			returnImage.setGeoLocation(country);
		}
		
	}

	@SuppressWarnings("unchecked")
	private void attachNode(NodeImage returnImage, List tags) {
		Element binomial = null; 
		Element genus = null; 
		for(Iterator itr = tags.iterator(); itr.hasNext(); ) {
			Element tag = (Element)itr.next();
			if (tag.getText().startsWith("taxonomy:binomial")) {
				binomial = tag; 
			}
			if (tag.getText().startsWith("taxonomy:genus")) {
				genus = tag;
			}
		}
		
		String binomialName = (binomial != null) ? getGroupName(binomial) : null;
		String genusName = (genus != null) ? getGroupName(genus) : null;
		List nodes;
		boolean attached = false;
		// we want to auto-attach the image to the binomial name
		if (!StringUtils.isEmpty(binomialName)) {
			nodes = getNodeDAO().findNodesExactlyNamed(binomialName);
			if (nodes != null && nodes.size() > 0) {
				returnImage.getNodesSet().addAll(nodes);
				attached = true;
			}
		}
		// if couldn't could attach to binomial, try genus attachment 
		if (!attached && !StringUtils.isEmpty(genusName)) {
			nodes = getNodeDAO().findNodesExactlyNamed(genusName);
			if (nodes != null && nodes.size() > 0) {
				returnImage.getNodesSet().addAll(nodes);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List getMachineTags(Element photoElement) {
		List machineTags = new ArrayList();
		List tags = photoElement.getChild("tags").getChildren();
		for(Iterator itr = tags.iterator(); itr.hasNext(); ) {
			Element tag = (Element)itr.next();
			if ("1".equals(tag.getAttributeValue("machine_tag"))) {
				machineTags.add(tag);
			}
		}
		return machineTags;
	}
	
	@SuppressWarnings("unchecked")
	private String getGroupName(Element groupTag) {
		if (groupTag != null) {
			String rawText = groupTag.getAttributeValue("raw");
			String[] pieces = rawText.split("=");
			if (pieces.length > 1) {
				return pieces[1];
			}
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	private String getCountryInfo(List tags, Element photoElement) {
		Element country = findGeoCountryTag(tags);
		if (country != null) {
			String rawText = country.getAttributeValue("raw");
			String[] pieces = rawText.split("=");
			if (pieces.length > 1) {
				return pieces[1];
			}			
		} else {
			return getLocation(photoElement);
		}
		return "";
	}
	
	private String getLocation(Element photoElement) {
		Element loc = photoElement.getChild("location");
		if (loc != null) {
			Element country = loc.getChild("country");
			if (country != null) {
				return country.getText();
			}
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	private Element findGeoCountryTag(List tags) {
		Element geoCountry = null;
		for(Iterator itr = tags.iterator(); itr.hasNext(); ) {
			Element tag = (Element)itr.next();
			if (tag.getText().startsWith("geo:country")) {
				geoCountry = tag; 
			}
		}		
		return geoCountry;
	}
	
	private String getSizesUrl(String photoId) {
		return getServiceUrl(photoId, GET_SIZES, false);
	}
	
	private String getInfoUrl(String photoId) {
		return getServiceUrl(photoId, GET_INFO, false);
	}
	
	private String getServiceUrl(String photoId, String serviceName, boolean includeSecret) {
		String secretString = includeSecret ? "&secret=" + SECRET : "";
		return BASE_SERVICE_URL + serviceName + "&api_key=" + API_KEY + "&photo_id=" + photoId + secretString;
	}
	
	private boolean isResponseStatusOk(Document sizeDoc) {
		org.jdom.Element rootNode = null;
		try {
		    rootNode = (org.jdom.Element) XPath.selectSingleNode(sizeDoc, "/rsp[@stat='ok']");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootNode != null;
	}
	
	public static void main(String[] args) {
		FlickrImporter importer = new FlickrImporter();
		importer.importFlickrPicture("83424671");
	}
	
	public ImageHelper getImageHelper() {
		return imageHelper;
	}
	
	public void setImageHelper(ImageHelper imageHelper) {
		this.imageHelper = imageHelper;
	}

	public ImageDAO getImageDAO() {
		return imageDAO;
	}

	public void setImageDAO(ImageDAO imageDAO) {
		this.imageDAO = imageDAO;
	}

	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}

	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}
}
