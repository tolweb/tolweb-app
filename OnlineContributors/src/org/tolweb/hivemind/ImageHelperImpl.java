package org.tolweb.hivemind;

import java.awt.Dimension;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.request.IUploadFile;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.ImageUtils;
import org.tolweb.misc.UsePermissionHelper;
import org.tolweb.tapestry.EditImageData;
import org.tolweb.tapestry.EditTitleIllustrations;
import org.tolweb.tapestry.IImageCallback;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.ServerXMLReader;

public class ImageHelperImpl extends AppStateManagerAware implements ImageHelper {
	private ImageDAO imageDAO;
	private PageDAO workingPageDAO;
	private PageDAO publicPageDAO;
	private NodeDAO workingNodeDAO;
	private ImageUtils imageUtils;
	private UsePermissionHelper usePermissionHelper;
	private ServerXMLReader serverXMLReader;
	private IImageCallback editCallback;
	private IImageCallback deleteCallback;
	private IImageCallback copyDataCallback;
	private IEngineService externalService;
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(ImageHelperImpl.class);
	}   		

	public IImageCallback getEditCallback() {
		if (editCallback == null) {
			editCallback = new IImageCallback() {
				public void actOnImage(NodeImage img, IRequestCycle cycle) {
				    String editPageName;
				    editPageName = EditImageData.getEditPageNameForMedia(img);
					EditImageData editPage = (EditImageData) cycle.getPage(editPageName);
					editPage.setImage(img);
					img.setCheckedOut(true);
					img.setOnlineCheckedOut(true);
					img.setCheckedOutContributor(getContributor());
					img.setCheckoutDate(new Date());
					getImageDAO().saveImage(img);
					cycle.activate(editPage);    			
				}
			};   
		}
		return editCallback;
	}

	public IImageCallback getDeleteCallback(final Long editedObjectId, final String searchPageName) {
		if (deleteCallback == null) {
			deleteCallback = new IImageCallback() {
				public void actOnImage(NodeImage img, IRequestCycle cycle) {
				    if (logger.isDebugEnabled()) {
				        Contributor contr = getContributor(); 
				        logger.debug("Contributor : " + contr.getNameOrInstitution() + " just deleted image: " + 
				                img.getLocation() + " with id: " + img.getId());
				    }
					deleteImage(img);
                    IPage page = cycle.getPage(searchPageName);
                    try {
                        PropertyUtils.write(page, "editedObjectId", editedObjectId);
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
					cycle.activate(page);			
				}
			};    
		}
		return deleteCallback;     	
	}
	
	public IImageCallback getCopyDataCallback() {
		if (copyDataCallback == null) {
			copyDataCallback = new IImageCallback() {
				public void actOnImage(NodeImage img, IRequestCycle cycle) {
					EditImageData editPage = (EditImageData) cycle.getPage("EditImageData");
					editPage.getImage().setValues(img, false, false);
					cycle.activate(editPage);		
				}
			};
		}
		return copyDataCallback;
	}
	
	@SuppressWarnings("unchecked")
	public IImageCallback getTillusCallback(final Long editedObjectId, final String pageName) {
        IImageCallback addTillusCallback = new IImageCallback() {
	        public void actOnImage(NodeImage img, IRequestCycle cycle) {
	            EditTitleIllustrations editPage = (EditTitleIllustrations) cycle.getPage(pageName);
	            editPage.setEditedObjectId(editedObjectId);
	            ImageDAO dao = getImageDAO();
	            List versions = dao.getUsableVersionsForImage(img);
	            editPage.addTitleIllustration(versions);
	            cycle.activate(editPage);
	        }	            
        };
        return addTillusCallback;
	}
    
	@SuppressWarnings("unchecked")
    public void deleteImage(NodeImage img) {
        List versions = getImageDAO().getVersionsForImage(img);
        List versionIds = new ArrayList();
        // dont need to bother looking for versions if it's a document, sound, movie, etc.
        if (versions.size() > 0) {
            for (Iterator iter = versions.iterator(); iter.hasNext();) {
                ImageVersion version = (ImageVersion) iter.next();
                versionIds.add(version.getVersionId());
            }
            // check to see if there are any title illustrations
            getWorkingPageDAO().deleteTitleIllustrationsPointingAtVersionIds(versionIds);
            getPublicPageDAO().deleteTitleIllustrationsPointingAtVersionIds(versionIds);
            for (Iterator iter = versions.iterator(); iter.hasNext();) {
                ImageVersion nextVersion = (ImageVersion) iter.next();
                imageDAO.deleteImageVersion(nextVersion);
            }  
        }
        getImageDAO().deleteImage(img);
    }
	
	public void writeOutImage(NodeImage img, IUploadFile file) {
	    saveAndWriteOutImage(img, file, null);
	}
	
	/**
	 * Treat things a little differently if the contributor is 
	 * using the simple media form
	 * @param contr
	 * @return
	 */
	public boolean getContributorShouldUseSimpleMedia(Contributor contr) {
		return (contr.getContributorType() == Contributor.TREEHOUSE_CONTRIBUTOR ||
			Student.class.isInstance(contr) && !getUseRegularImageForm());
	}
	
	public boolean getContributorShouldUseSimpleMedia() {
		return getContributorShouldUseSimpleMedia(getContributor());
	}
	
	/**
	 * used for flickr importer
	 */
	public void saveAndWriteOutImageStream(NodeImage img, InputStream stream, String filename, Long nodeId) {
		saveAndWriteOutImageStream(img, stream, filename, nodeId, getContributor());
	}

	/**
	 * used for flickr importer
	 */
	public void saveAndWriteOutImageStream(NodeImage img, InputStream stream, String filename, 
			Long nodeId, Contributor contr) {
		img.setLocation("");
		initNode(img, nodeId);
		getImageDAO().addImage(img, contr, false);
		saveToDisk(img, stream, filename);
		getImageDAO().saveImage(img);
		initVersions(img);
	}
    
	/**
	 * Utility method for writing out a new image and its thumbnail
	 * @param img The image object to write out
	 * @param file The uploaded image file -- if null, called from zip uploading
	 * @param isPreview Whether to write out a preview thumbnail
	 */
	public void saveAndWriteOutImage(NodeImage img, IUploadFile file, Long nodeId) {
        Contributor contr = getContributor();        
        if (img.getId() <= 0) {
        	// only init permissions on non zip uploads
            if (file != null) {
            	img.setUsePermission(NodeImage.EVERYWHERE_USE);
            }
            String location = img.getLocation();
            if (StringUtils.isEmpty(location)) {
                // Going to set this so we don't get exceptions on the initial save
                img.setLocation("");
            }
            initNode(img, nodeId);
            if (getContributorShouldUseSimpleMedia()) {
            	img.setIsUnapproved(true);
            }
            // Create the image but only set the contributor as
            // the copyright holder if this is a single image upload
            getImageDAO().addImage(img, contr, file != null && !getContributorShouldUseSimpleMedia());        
        }
        if (file != null) {
            saveToDisk(img, file.getStream(), file.getFileName());
            getUsePermissionHelper().initializeNewPermissions(contr, img, true);            
        }
		ImageDAO dao = getImageDAO();
		dao.saveImage(img);
		initVersions(img);
	}

	private void saveToDisk(NodeImage img, InputStream stream, String filename) {
		String serverImgFile = getImageUtils().writeInputStreamToDisk(stream, filename);
		serverImgFile = getImageUtils().stripSlashesFromFilename(serverImgFile);
		img.setLocation(serverImgFile);
	}

	private void initNode(NodeImage img, Long nodeId) {
		if (nodeId != null) {
		    MappedNode node = getWorkingNodeDAO().getNodeWithId(nodeId);
		    img.addToNodesSet(node);
		}
	}

	@SuppressWarnings("unchecked")
	private void initVersions(NodeImage img) {
		List versions = new ArrayList();
		if (!Movie.class.isInstance(img) && !Sound.class.isInstance(img) && !Document.class.isInstance(img)) {
			getImageUtils().reinitializeAutogeneratedVersions(img, versions);
			saveVersions(versions);
		} else if (Movie.class.isInstance(img)) {
			Movie newMovie = (Movie) img;
			List newVersions = getImageUtils().writeMovieFilesToDisk(newMovie);
			if (newVersions != null) {
				saveVersions(newVersions);
				newMovie.setUseFlashVideo(true);
				getImageDAO().saveImage(newMovie);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void saveVersions(List versions) {
		for (Iterator iter = versions.iterator(); iter.hasNext();) {
		    ImageVersion nextVersion = (ImageVersion) iter.next();
		    getImageDAO().saveImageVersion(nextVersion);
		}
	}
	
	public void writeOutImageVersion(ImageVersion version, IUploadFile file) {
	    ImageUtils utils = getImageUtils();
		String serverImgFile = getImageUtils().writeImageFileToDisk(file);
		version.setFileName(getImageUtils().stripSlashesFromFilename(serverImgFile));
		version.setVersionName("new image version");
		Dimension dims = utils.getVersionDimensions(version);
		version.setHeight(Integer.valueOf(dims.height));
		version.setWidth(Integer.valueOf(dims.width));
		int numBytes = utils.getVersionFilesize(version);
		version.setFileSize(utils.getFileSizeStringFromInt(numBytes));
		ImageDAO dao = getImageDAO();
		dao.saveImageVersion(version);
	}	
	
    /**
     * Unzips the zip file and attempts to apply the image data contained in the
     * xml document to each individual image
     * @param file The upload file containing the image zip
     * @param doc The xml document that contains the information for each image
     * @param contr 
     * @return A 3-element array with the first element being a list of the images
     *  created, the second being a set of image filenames that weren't matched in
     *  the xml, and the third being a set of image filenames present in the xml
     *  but not present in the zip file
     */
	@SuppressWarnings("unchecked")
    public Object[] saveAndWriteOutZipFile(IUploadFile file, Document doc, Contributor contr) {
        Map filenames = getImageUtils().createImagesFromZip(file);
        // set of filenames that were found in the zip file
        Set originalImageFiles = new HashSet(filenames.keySet());
        // list from which filenames will be removed as they are seen in the zip file
        List strayXMLFilenames = getAllImageElementFilenames(doc);
        // the filenames in the xml document (as they were uploaded)
        List preservedXMLFilenames = new ArrayList(strayXMLFilenames);
        Hashtable newImages = new Hashtable();
        // TODO consider changes this to filenames.entrySet() to get the key/value
        // Map.Entry iterator 
        for (Iterator iter = filenames.keySet().iterator(); iter.hasNext();) {
            boolean removeFromLists = false;
            String oldImageName = (String) iter.next();
            String newImageName = (String) filenames.get(oldImageName);
            if (!oldImageName.startsWith(".") && !oldImageName.endsWith(".db")) {
                NodeImage newImage = new NodeImage();
                newImage.setLocation(newImageName);
                // Store things in a hashtable for easy lookup later when we do the sort.
                newImages.put(newImageName, newImage);
                // this would be the part where we'd find the xml record and decode stuff
                if (doc != null) {
                    Element imgElement = getServerXMLReader().getImageElementWithFilename(oldImageName, doc);
                    if (imgElement != null) {
                        getServerXMLReader().getNodeImageFromElement(newImage, imgElement, contr);
                        // Remove it from the list of images since we know we've seen it
                        removeFromLists = true;
                    }
                }
                saveAndWriteOutImage(newImage, null, null);
            } else {
                // we're ignoring it so make sure it gets removed from the lists
                removeFromLists = true;
            }
            if (removeFromLists) {
                originalImageFiles.remove(oldImageName);
                strayXMLFilenames.remove(oldImageName); 
            }
        }       
        // Before we return, perform a sort so that the image order matches the order in the
        // xml file
        List returnImages = new ArrayList();
        for (Iterator it = preservedXMLFilenames.iterator(); it.hasNext();) {
            String filename = (String) it.next();
            String newFilename = (String) filenames.get(filename);
            if (newFilename != null) {
                NodeImage img = (NodeImage) newImages.get(newFilename);
                // if for whatever reason the image didn't get created,
                // then don't add null to the list
                if (img != null) {
                    returnImages.add(img);
                }
                newImages.remove(newFilename);
            }
        }
        for (Iterator iter = newImages.keySet().iterator(); iter.hasNext();) {
            String filename = (String) iter.next();
            NodeImage img = (NodeImage) newImages.get(filename);
            if (img != null) {
                returnImages.add(img);
            }
        }
        Object[] returnArray = new Object[3];
        returnArray[0] = returnImages;
        returnArray[1] = originalImageFiles;
        returnArray[2] = new HashSet(strayXMLFilenames);
        return returnArray;
    }	
	
	@SuppressWarnings("unchecked")
    private List getAllImageElementFilenames(org.jdom.Document doc) {
        List filenames = new ArrayList();
        for (Iterator iter = doc.getRootElement().getChildren(XMLConstants.image).iterator(); iter.hasNext();) {
            Element imgElement = (Element) iter.next();
            String filename = imgElement.getChildText(XMLConstants.filename); 
            filenames.add(filename);
        }
        return filenames;
    }
    
	public String getEditUrlForMedia(NodeImage media, IRequestCycle cycle, Contributor contr) { 
		return getEditUrlForMedia(media, cycle, contr, false);
	}
	
	public String getEditUrlForMedia(NodeImage media, IRequestCycle cycle, Contributor contr, boolean someBool) {
    	Object[] params = {contr.getEmail(), contr.getPassword(), Integer.valueOf(media.getId()), someBool};
		ExternalServiceParameter parameters = new ExternalServiceParameter(EditImageData.getEditPageNameForMedia(media), params);
		return getExternalService().getLink(false, parameters).getURL();
	}
	
	public IRender getRedirectDelegate(final String url) {
        IRender delegate = new IRender() {
            public void render(IMarkupWriter writer, IRequestCycle cycle) {
                writer.printRaw("<meta http-equiv=\"refresh\" content=\"3;url=" + url + "\">");
            }
        };	    
        return delegate;
	}    


	public ImageDAO getImageDAO() {
		return imageDAO;
	}

	public void setImageDAO(ImageDAO imageDAO) {
		this.imageDAO = imageDAO;
	}

	public ImageUtils getImageUtils() {
		return imageUtils;
	}

	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}

	public PageDAO getPublicPageDAO() {
		return publicPageDAO;
	}

	public void setPublicPageDAO(PageDAO publicPageDAO) {
		this.publicPageDAO = publicPageDAO;
	}

	public PageDAO getWorkingPageDAO() {
		return workingPageDAO;
	}

	public void setWorkingPageDAO(PageDAO workingPageDAO) {
		this.workingPageDAO = workingPageDAO;
	}

	public NodeDAO getWorkingNodeDAO() {
		return workingNodeDAO;
	}

	public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
		this.workingNodeDAO = workingNodeDAO;
	}

	public UsePermissionHelper getUsePermissionHelper() {
		return usePermissionHelper;
	}

	public void setUsePermissionHelper(UsePermissionHelper usePermissionHelper) {
		this.usePermissionHelper = usePermissionHelper;
	}

	public ServerXMLReader getServerXMLReader() {
		return serverXMLReader;
	}

	public void setServerXMLReader(ServerXMLReader serverXMLReader) {
		this.serverXMLReader = serverXMLReader;
	}

	public IEngineService getExternalService() {
		return externalService;
	}

	public void setExternalService(IEngineService externalService) {
		this.externalService = externalService;
	}
}
