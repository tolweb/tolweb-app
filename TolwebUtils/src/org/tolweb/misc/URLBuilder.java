/*
 * Created on Jun 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tolweb.dao.ImageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class URLBuilder {
    private ImageDAO imageDAO;
    private ImageUtils imageUtils;
    private TextPreparer textPreparer;
    private Configuration configuration;
    public static final String NO_HOST_PREFIX = "/";
    private DateFormat urlDateFormat = new SimpleDateFormat("yyyy.MM.dd"); 
    
    public static String FEED_ELEMENT_FORMAT = "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"%1$s\" href=\"%2$s\" />";
    
    public String getWorkingURLForObject(Object object) {
        return getURLForObject(object, true, false);
    }
    
    public String getPublicURLForObject(Object object) {
        return getURLForObject(object, false, false);
    }
    
    public String getURLForObject(Object object) {
        return getURLForObject(object, false, true);
    }
    
    public String getJavascriptInsertImageUrl(NodeImage image, Integer versionHeightInteger, boolean isResize, boolean includeJavascriptPrefix) {
        String url = "";
        ImageVersion version = null; 
        int versionHeight = versionHeightInteger.intValue();
        int maxHeight = getImageDAO().getMaxAllowedVersionHeight(image.getId());
        boolean isImage = true;
        if (versionHeight > 0 && image.getMediaType() == NodeImage.IMAGE) {
            // make sure that the version we are requesting isn't bigger than the max size
            if (versionHeight > maxHeight) {
                versionHeight = maxHeight;
            }
            // if the version height is the same as the master, then just return the url 
            // to the master image
            int masterHeight = getImageDAO().getMasterVersionHeight(image.getId());
            if (versionHeight == masterHeight) {
                version = getImageDAO().getMasterVersion(image.getId());
                url = getImageUtils().getImageUrl(image);
            } else {
                url = getImageUtils().getUrlPrefix() + getImageUtils().getImageVersionFilename(image.getLocation(), versionHeight);
                version = getImageDAO().getImageVersionWithHeight(image.getId(), versionHeight);                
            }
        } else {
            if (image.getMediaType() == NodeImage.IMAGE) {
                version = getImageDAO().getMaxAllowedVersion(image);
                url = getImageUtils().getUrlForVersion(version);
            } else {
                isImage = false;
            }
        }
        String insertStr;
        String javascriptString = includeJavascriptPrefix ? "javascript:" : "";
        if (isImage) {
            String imageCaptionString = getImageCaptionString(image, false);            
            Integer width = 0;
            if (isImage && version != null) {
                width = version.getWidth();
            }   
            String functionName = "insertImage";
            if (isResize) {
                functionName = "resizeImage";
            }
            insertStr = javascriptString + functionName + "(" + image.getId() + ", '" + url + "', " + width + ", " + versionHeight + ", '" + imageCaptionString + "');";
        } else {
            String insertString = getTextPreparer().getWysiwygViewMediaString(image.getId(), image.getMediaType());
            insertStr = javascriptString + "insertMedia('" + insertString + "');";
        }
	    return insertStr;        
    }
    
    public String getNonJavascriptInsertImageHtml(NodeImage image, Integer versionHeightInteger) {
        String spaces = "&nbsp;&nbsp;&nbsp;&nbsp;";
        String insertString = "&lt;div class=\"imgcenter\" &gt;<br/>";
        String sizeString = versionHeightInteger != null ? " size=&quot;" + versionHeightInteger + "&quot;" : "";
        String imageCaptionString = getImageCaptionString(image, true);
        insertString += spaces + "&lt;tolimg id=&quot;" + image.getId() + "&quot;" + sizeString + "&gt;<br/>";
        insertString += spaces + "&lt;p&gt;" + imageCaptionString + "&lt;/p&gt;<br/>";
        insertString += "&lt;/div&gt;";
        return insertString;
    }
    
    public String getImageCaptionString(NodeImage image, boolean escapeLinks) {
        String imageCaptionString = StringUtils.notEmpty(image.getScientificName()) ? image.getScientificName() + ". " : "";
        imageCaptionString += getTextPreparer().getCopyrightOwnerString(image, true, true, escapeLinks) + " ";
        imageCaptionString += StringUtils.notEmpty(image.getAcknowledgements()) ? image.getAcknowledgements() : "";
        return imageCaptionString;
    }
    
    private String getPrefix(boolean isWorking) {
    	return getPrefix(isWorking, false);
    }
    
    private String getPrefix(boolean isWorking, boolean noPrefix) {
        String url = "http://"; 
        if (isWorking) {
            url += "working.";
        }
        url += getHostPrefix() + "/";
        // Use the noprefix mode in order to link to whatever site a user is viewing the page on
        if (noPrefix) {
            url = "/";
        }
        return url;
    }

    public String getMediaLinkForGroup(String groupName, Long nodeId, NodeImage media) {
    	return getMediaLinkForGroup(groupName, nodeId, media, false);
    }
    
    public String getMediaLinkForGroup(String groupName, Long nodeId, NodeImage media, boolean omitGroupName) {
    	if (media.getMediaType() == NodeImage.MOVIE) {
    		return  getMovieLinkForGroup(groupName, nodeId, omitGroupName);
    	} else if (media.getMediaType() == NodeImage.IMAGE) {
    		return  getImageLinkForGroup(groupName, nodeId, omitGroupName);
    	}
    	return "";
    }
    
    public String getImageLinkForGroup(String groupName, Long nodeId) {
    	return getMediaLinkForGroup(groupName, nodeId, true, false, true, false);
    }
    
    public String getImageLinkForGroup(String groupName, Long nodeId, boolean omitGroupName) {
    	return getMediaLinkForGroup(groupName, nodeId, true, false, true, omitGroupName);
    }

    public String getMovieLinkForGroup(String groupName, Long nodeId) {
    	return getMediaLinkForGroup(groupName, nodeId, false, false, true, false);
    }
    
    public String getMovieLinkForGroup(String groupName, Long nodeId, boolean omitGroupName) {
    	return getMediaLinkForGroup(groupName, nodeId, false, false, true, omitGroupName);
    }
    
    private String getMediaLinkForGroup(String groupName, Long nodeId, boolean isImageGallery, boolean noPrefix, boolean useCssClass, boolean omitGroupName) {
    	String baseUrl = getPrefix(false, noPrefix); 
    	String link = getMediaURLForGroup(baseUrl, groupName, nodeId, isImageGallery, noPrefix);
    	groupName = groupName.replace(' ', '_');
    	groupName = omitGroupName ? "" : groupName;
    	String linkText = isImageGallery ? (omitGroupName ? "image" : " image") : (omitGroupName ? "movie" : " movie");
    	String cssClass = useCssClass ? " class=\"taxalink\"" : "";
    	return "<a href=\"" + link + "\"" + cssClass + ">" + groupName + linkText + " collection</a>";    	
    }
    
    private String getMediaURLForGroup(String prefix, String groupName, Long nodeId, boolean isImageGallery, boolean noPrefix) {
    	groupName = groupName.replace(' ', '_');
    	String galleryLink = isImageGallery ? "images/" : "movies/";
    	try {
			groupName = URLEncoder.encode(groupName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
        return prefix + galleryLink + groupName + (nodeId != null ? "/" + nodeId : ""); 
    }
    
    private String getURLForObject(Object object, boolean isWorking, boolean noPrefix) {
        String url = getPrefix(isWorking, noPrefix); 
        if (MappedAccessoryPage.class.isInstance(object)) {
            MappedAccessoryPage accPage = (MappedAccessoryPage) object;
            if (accPage.getIsTreehouse()) {
                return getURLForTreehouse(url, accPage.getId());
            } else {
                if (!accPage.getIsArticle()) {
                    return getURLForArticle(url, accPage.getId());
                } else {
                    return getURLForNote(url, accPage.getId());
                }
            }
        } else if (MappedPage.class.isInstance(object)) {
            MappedPage page = (MappedPage) object;
            return getURLForBranchPage(url, page.getGroupName(), page.getMappedNode().getNodeId());
        } else if (String.class.isInstance(object)) {
            return getURLForBranchPage(url, (String) object, null);
        } else if (MappedNode.class.isInstance(object)) {
        	return getURLForBranchPage(url, ((MappedNode) object).getActualPageTitle(false, false, true), ((MappedNode) object).getNodeId());
        }
        return url;
    }
    
    public String getURLForPortfolioPage(PortfolioPage page) {
        if (page.getIsExternal()) {
            return StringUtils.getProperHttpUrl(page.getExternalPageUrl());
        } else {
	        if (page.getDestinationType() == PortfolioPage.ACC_PAGE_DESTINATION) {
	            String pageType = page.getPageType();
	            if (pageType != null) {
	                int destinationId = page.getDestinationId();
	                if (pageType.equals(MappedAccessoryPage.ARTICLE)) {
	                    return getPublicURLForArticle(destinationId);
	                } else if (pageType.equals(MappedAccessoryPage.NOTE)) {
	                    return getPublicURLForNote(destinationId);
	                } else {
	                    return getPublicURLForTreehouse(destinationId);
	                }
	            } else {
	                return "";
	            }
	            
	        } else {
	            return getURLForBranchPage(page.getPageTitle());
	        }
        }
    }

	public String getURLForArticle(int id) {
		return getURLForArticle(NO_HOST_PREFIX, id);
	}

	public String getPublicURLForArticle(int id) {
        return getURLForArticle(getDefaultHostPrefix(), id);
    }
    
    public String getURLForArticle(String prefix, int id) {
        return prefix + "articles/?article_id=" + id;
    }

	public String getURLForNote(int id) {
		return getURLForNote(NO_HOST_PREFIX, id);
	}

	public String getPublicURLForNote(int id) {
        return getURLForNote(getDefaultHostPrefix(), id);
    }
    
    public String getURLForNote(String prefix, int id) {
        return prefix + "notes/?note_id=" + id;
    }
    public String getPublicURLForTreehouse(int id) {
    	return getURLForTreehouse(getDefaultHostPrefix(), id);
    }
    public String getURLForTreehouse(Number id) {
    	return getURLForTreehouse(id.intValue());
    }
    public String getURLForTreehouse(int id) {
        return getURLForTreehouse(NO_HOST_PREFIX, id);
    }
    
    public String getURLForTreehouse(String prefix, int id) {
        return prefix + "treehouses/?treehouse_id=" + id; 
    }
    
    public String getURLForBranchPage(String groupName, Long nodeId) {
    	return getURLForBranchPage(NO_HOST_PREFIX, groupName, nodeId);
    }
    
    public String getURLForBranchPage(String groupName) {
        return getURLForBranchPage(getDefaultHostPrefix(), groupName, null);
    }
    
    public String getWorkingURLForBranchPage(String groupName, Long nodeId) {
    	String prefix = getPrefix(true);
    	return getURLForBranchPage(prefix, groupName, nodeId);
    }

    public String getWorkingURLForBranchPage(MappedNode node) {
    	String prefix = getPrefix(true);
    	return getURLForBranchPage(prefix, node.getName(), node.getNodeId());
    }
    
    public String getInactiveWorkingURLForBranchPage(String groupName, Long nodeId) {
    	groupName = groupName.replace(' ', '_');
    	try {
			groupName = URLEncoder.encode(groupName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return getPrefix(true) + "inactive/" + groupName + (nodeId != null ? "/" + nodeId : "");
    }
    
    public String getPublicURLForBranchPage(String groupName, Long nodeId) {
    	String prefix = getPrefix(false);
    	return getURLForBranchPage(prefix, groupName, nodeId);
    }
    
    public String getPublicURLForBranchPage(MappedNode node) {
    	String prefix = getPrefix(false);
    	return getURLForBranchPage(prefix, node.getName(), node.getNodeId());
    }
    
    public String getArchiveURLForBranchPage(MappedPage page, Date archiveDate) {
    	String baseURL = getPublicURLForObject(page);
    	// add a slash and get the date format for the URL
    	baseURL += "/" + urlDateFormat.format(archiveDate);
    	return baseURL;
    }
    
    public Date parseArchiveDateFromURLString(String dateString) {
    	Date archiveDate = null;
    	try {
			archiveDate = urlDateFormat.parse(dateString);
		} catch (Exception e) {
		}    	
		return archiveDate;
    }    

    public String getURLForBranchPage(String prefix, String groupName, Long nodeId) {
    	groupName = groupName.replace(' ', '_');
    	try {
			groupName = URLEncoder.encode(groupName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
        return prefix + groupName + (nodeId != null ? "/" + nodeId : ""); 
    }
   
    protected String getLinkForBranchPage(String link, String groupName, boolean useCssClass) {
    	String cssClass = useCssClass ? " class=\"taxalink\"" : "";
    	return "<a href=\"" + link + "\"" + cssClass + ">" + groupName + "</a>";
    }
    
    public String getWorkingLinkForBranchPage(MappedNode nd, boolean useCssClass) {
    	return getLinkForBranchPage(getWorkingURLForBranchPage(nd), 
    			nd.getName(), useCssClass);
    }
   
    public String getWorkingLinkForBranchPage(MappedNode nd, String linkText, boolean useCssClass) {
    	return getLinkForBranchPage(getWorkingURLForBranchPage(nd), 
    			linkText, useCssClass);    	
    }
    
    public String getWorkingLinkForBranchPage(String groupName, boolean useCssClass) {
        String link = getWorkingURLForObject(groupName);
        groupName = groupName.replace(' ', '_');
        String cssClass = useCssClass ? " class=\"taxalink\"" : "";
        return "<a href=\"" + link + "\"" + cssClass + ">" + groupName + "</a>";
    }
    
    public String getWorkingLinkForBranchPage(String groupName) {
        return getWorkingLinkForBranchPage(groupName, false);
    }
    
    public String getPublicLinkForBranchPage(MappedNode nd, boolean useCssClass) {
    	return getLinkForBranchPage(getPublicURLForBranchPage(nd), 
    			nd.getName(), useCssClass);
    }
    
    public String getPublicLinkForBranchPage(MappedNode nd, String linkText, boolean useCssClass) {
    	return getLinkForBranchPage(getPublicURLForBranchPage(nd), 
    			linkText, useCssClass);    	
    }    
    
    public String getPublicLinkForBranchPage(String groupName, boolean useCssClass) {
    	String link = getPublicURLForObject(groupName);
    	groupName = groupName.replace(' ', '_');
    	String cssClass = useCssClass ? " class=\"taxalink\"" : "";
    	return "<a href=\"" + link + "\"" + cssClass + ">" + groupName + "</a>";    	
    }
    
    public String getPublicLinkForBranchPage(String groupName) {
    	return getPublicLinkForBranchPage(groupName, false);
    }
    
    public String getURLForNode(MappedNode node) {
    	return getURLForBranchPage(NO_HOST_PREFIX, node.getName(), node.getNodeId());
    }
    
    public String getDefaultManagerPageNameForContributor(Contributor contr) {
    	byte contributorType = contr.getContributorType();
    	String actualPageName;
        if (Student.class.isInstance(contr) || contributorType == Contributor.TREEHOUSE_CONTRIBUTOR) {
            actualPageName = "TreehouseMaterialsManager";
        } else if (contributorType == Contributor.SCIENTIFIC_CONTRIBUTOR || contributorType == Contributor.ACCESSORY_CONTRIBUTOR) {
            actualPageName = "ScientificMaterialsManager";
        } else if (contributorType == Contributor.OTHER_SCIENTIST) {
        	actualPageName = "ScientificContributorRegistrationOther";
        } else {
            actualPageName = "ImagesManager";
        }    	
        return actualPageName;
    }
    
    public String getRegistrationPageNameForContributor(Contributor contr) {
    	String regPageName;
    	byte contributorType = contr.getContributorType();
    	if (Student.class.isInstance(contr) || contributorType == Contributor.TREEHOUSE_CONTRIBUTOR) {
    		regPageName = "TreehouseContributorRegistration";
    	} else if (contributorType == Contributor.SCIENTIFIC_CONTRIBUTOR) {
    		regPageName = "ScientificContributorRegistration";
    	} else if (contributorType == Contributor.ACCESSORY_CONTRIBUTOR) {
    		regPageName = "GeneralContributorRegistration";
    	} else if (contributorType == Contributor.OTHER_SCIENTIST) {
    		regPageName = "ScientificContributorRegistrationOther";
    	} else {
    		regPageName = "ImageContributorRegistration";
    	}
    	return regPageName;
    }
    
	/**
	 * Returns the elements for including syndication feed information 
	 * for both images and movies in the head element of an HTML document.
	 *  
	 * Example: http://newsystem.tolweb.org/contentfeeds/movies/2382
	 * @return a string representation of the HTML needed to incorporate syndication 
	 * feeds for the gallery collections
	 */
	public String getGallerySyndication(Long nodeId, String groupName) {
		String baseUrl = getPrefix(false, true);
		String imageRSS = "contentfeeds/images/";
		String movieRSS = "contentfeeds/movies/";
		
//		Examples: 		
//		<link rel="alternate" type="application/rss+xml" title="Blogs" href="http://www.insectgeeks.com/public/rss/act_blogs/rss_20/" />
//  	<link rel="alternate" type="application/rss+xml" title="Events" href="http://www.insectgeeks.com/public/rss/act_events/rss_20/" />
		
		String elements = getSyndicationElement(groupName + " Images", baseUrl + imageRSS + nodeId);
		elements += getSyndicationElement(groupName + " Movies", baseUrl + movieRSS + nodeId);
		return elements;
	}
	
	private String getSyndicationElement(String title, String url) {
		if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(url)) {
			return String.format(FEED_ELEMENT_FORMAT, title, url);
		}
		return "";
	}    
    
  	public String getAssetUrlString(String originalPath) {
  		if (getConfiguration().getUseExternalStylesheets()) {
  			return "/tree/" + originalPath;
  		} else {
  			return originalPath;
  		}
  	}
  	private String getHostPrefix() {
  		return getConfiguration().getHostPrefix();
  	}
  	/**
  	 * Used if something doesn't specify what type of url is desired
  	 * @return
  	 */
  	private String getDefaultHostPrefix() {
  		return "http://" + getHostPrefix() + "/";
  	}
    
    /**
     * @return Returns the imageUtils.
     */
    public ImageUtils getImageUtils() {
        return imageUtils;
    }
    /**
     * @param imageUtils The imageUtils to set.
     */
    public void setImageUtils(ImageUtils imageUtils) {
        this.imageUtils = imageUtils;
    }
    /**
     * @return Returns the imageDAO.
     */
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
    /**
     * @param imageDAO The imageDAO to set.
     */
    public void setImageDAO(ImageDAO imgDAO) {
        this.imageDAO = imgDAO;
    }

    /**
     * @return Returns the textPreparer.
     */
    public TextPreparer getTextPreparer() {
        return textPreparer;
    }

    /**
     * @param textPreparer The textPreparer to set.
     */
    public void setTextPreparer(TextPreparer textPreparer) {
        this.textPreparer = textPreparer;
    }

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
