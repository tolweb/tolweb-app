package org.tolweb.content.helpers;

import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.misc.ImageUtils;

/**
 * A tuple of Data Access Objects that can be passed to various web content 
 * aggregation classes that will be easily modified in the future as needs 
 * change. 
 * 
 * It serves a "packaging" to web content classes, like helpers & preparers, 
 * giving them easy access to Spring IoC managed DAO instances. 
 * 
 * @author lenards
 *
 */
public class DaoBundle {
	private PageDAO pageDAO; 
	private NodeDAO publicNodeDAO;
	private ImageDAO imageDAO;
	private ImageUtils imageUtils;
	
	/**
	 * Returns an instance of a class implementation the PageDAO interface
	 * 
	 * @return an implementation of the PageDAO interface
	 */
	public PageDAO getPageDAO() {
		return pageDAO;
	}
	/**
	 * Sets the instance referred to be the argument
	 * 
	 * @param pageDAO the interface implementation instance
	 */
	public void setPageDAO(PageDAO pageDAO) {
		this.pageDAO = pageDAO;
	}
	
	/**
	 * Returns a implementation the NodeDAO interface configure to point to 
	 * the Public database. 
	 * 
	 * @return an implementation of the NodeDAO interface
	 */	
	public NodeDAO getPublicNodeDAO() {
		return publicNodeDAO;
	}
	
	/**
	 * Sets the instance referred to be the argument
	 * 
	 * @param publicNodeDAO the interface implementation 
	 */
	public void setPublicNodeDAO(NodeDAO publicNodeDAO) {
		this.publicNodeDAO = publicNodeDAO;
	}
	
	/**
	 * Returns an instance of a class implementation the ImageDAO interface
	 * 
	 * All media is stored in the Misc database, so this DAO will point to 
	 * Misc.
	 * 
	 * @return an implementation of the ImageDAO interface
	 * 
	 */	
	public ImageDAO getImageDAO() {
		return imageDAO;
	}
	
	/**
	 * Sets the instance referred to be the argument
	 * 
	 * @param imageDAO the interface implementation 
	 */	
	public void setImageDAO(ImageDAO imageDAO) {
		this.imageDAO = imageDAO;
	}
	
	/**
	 * Returns an instance of ImageUtils class. 
	 * 
	 * @return a utility class to help manage media
	 */
	public ImageUtils getImageUtils() {
		return imageUtils;
	}
	
	/**
	 * Sets the instance referred to be the argument
	 * 
	 * @param imageUtils
	 */
	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
}
