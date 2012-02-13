package org.tolweb.content.helpers;

import java.sql.Date;

import org.tolweb.content.licensing.ContentLicenseClass;

/**
 * Models the parameters passed to a content service. 
 * 
 * @author lenards
 *
 */
public class ContentParameters {
	private Long nodeId;
	private Long mediaId;
	private String groupName;
	private Date changedDate; 
	private ContentLicenseClass requestedLicenseClass;
	private int depthLimit; 
	
	/**
	 * Constructs a default instance of the object.
	 */
	public ContentParameters() {
		
	}
	
	/**
	 * Constructs an instance of the object.
	 * 
	 * @param mediaId identity of the media object being requested
	 * @param requestedLicense the license class being requested
	 */
	public ContentParameters(Long mediaId, ContentLicenseClass requestedLicense) {
		this.mediaId = mediaId;
		this.requestedLicenseClass = requestedLicense;
	}
	
	/**
	 * Constructs an instance of the object.
	 * 
	 * @param nodeId identity of the node being requested
	 * @param changedDate the last changed date for content
	 * @param requestedLicense the license class being requested
	 */
	public ContentParameters(Long nodeId, Date changedDate, ContentLicenseClass requestedLicense) {
		this.nodeId = nodeId;
		this.changedDate = changedDate;
		this.requestedLicenseClass = requestedLicense;
		this.depthLimit = 0;
	}
	
	/**
	 * Constructs an instance of the object. 
	 * 
	 * @param nodeId identity of the node being requested
	 * @param changedDate the last changed date for content
	 * @param requestedLicense the license class being requested
	 * @param depthLimit depth limit for traversing the clade (not being used as of 2/11/2009)
	 */
	public ContentParameters(Long nodeId, Date changedDate, ContentLicenseClass requestedLicense, int depthLimit) {
		this(nodeId, changedDate, requestedLicense); 
		this.depthLimit = depthLimit;
	}

	/**
	 * Returns the change date indicated. 
	 * 
	 * @return a date object representing the changed date
	 */
	public Date getChangedDate() {
		return changedDate;
	}
	
	/**
	 * Sets the changed date to the argument
	 * 
	 * @param changedDate a different changed date 
	 */
	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}
	
	/**
	 * Returns the node id being requested. 
	 * 
	 * @return a long representing the identity field of a Node instance. 
	 */
	public Long getNodeId() {
		return nodeId;
	}
	
	/**
	 * Sets the node id to the argument
	 *  
	 * @param nodeId a different node id 
	 */
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	
	/**
	 * Returns the content license class being requested.
	 * 
	 * @return an enum value indicated the requested license class for the content 
	 */
	public ContentLicenseClass getRequestedLicenseClass() {
		return requestedLicenseClass;
	}
	
	/**
	 * Sets the request license class to the argument
	 *  
	 * @param requestedLicenseClass a different content license class
	 */
	public void setRequestedLicenseClass(ContentLicenseClass requestedLicenseClass) {
		this.requestedLicenseClass = requestedLicenseClass;
	}

	/**
	 * Returns an integer representing the depth limit for which to traverse 
	 * a clade
	 * @return an integer representing the depth limit
	 */
	public int getDepthLimit() {
		return depthLimit;
	}

	/**
	 * Sets the depth limit to the argument
	 * 
	 * Assumed to be non-negative
	 * 
	 * @param depthLimit an integer representing a depth limit
	 */
	public void setDepthLimit(int depthLimit) {
		this.depthLimit = depthLimit;
	}

	/**
	 * Returns a long representing the identity of the media object being 
	 * requested 
	 * 
	 * @return a long representing the media object identity 
	 */
	public Long getMediaId() {
		return mediaId;
	}

	/**
	 * Sets the media id to the argument
	 * 
	 * @param mediaId a long representing the media object identity
	 */
	public void setMediaId(Long mediaId) {
		this.mediaId = mediaId;
	}

	/**
	 * Returns a string representing the name of the group which content is 
	 * being requested about. 
	 * 
	 * @return a string representing a group's name
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the group name to the argument
	 * 
	 * @param groupName a string representing a group's name
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
