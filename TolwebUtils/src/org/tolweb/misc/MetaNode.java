package org.tolweb.misc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 * 
 * @author lenards
 *
 */
public class MetaNode implements Serializable {
	/** */
	private static final long serialVersionUID = 8322295462304399971L;
	private MappedNode node;
	private MappedPage page; 
	private List<MappedAccessoryPage> accessoryPages;
	private Map<Long, String> otherNameIds;
	private List<NodeImage> media;
	private Set<Contributor> contributors;
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("[");
		retVal.append("node-name: " + getNodeName());
		retVal.append(" synonyms: " + getSynonymInfo());
		retVal.append(" other-name-info: " + otherNameIds.toString()); 
		retVal.append("]");
		return retVal.toString(); 
	}

	private int getSynonymInfo() {
		return (getNode() != null && getNode().getSynonyms() != null ? getNode().getSynonyms().size() : 0);
	}

	private String getNodeName() {
		return getNode() != null ? getNode().getName() : "[null]";
	}
	
	public boolean getHasAttachments() {
		return page != null || (accessoryPages != null && !accessoryPages.isEmpty()) || 
			(otherNameIds != null && !otherNameIds.isEmpty()) || (media != null && !media.isEmpty()) ||
			(contributors != null && !contributors.isEmpty());
	}

	public boolean getHasPageAttached() {
		return page != null;
	}
	
	/**
	 * @return the node this meta-object relates to
	 */
	public MappedNode getNode() {
		return node;
	}
	/**
	 * @param node the node to relate to
	 */
	public void setNode(MappedNode node) {
		this.node = node;
	}
	/**
	 * @return the page attached to node
	 */
	public MappedPage getPage() {
		return page;
	}
	/**
	 * @param page the page to set
	 */
	public void setPage(MappedPage page) {
		this.page = page;
	}
	/**
	 * @return the accessory pages attached to node
	 */
	public List<MappedAccessoryPage> getAccessoryPages() {
		return accessoryPages;
	}
	/**
	 * @param accessoryPages the accessoryPages to set
	 */
	public void setAccessoryPages(List<MappedAccessoryPage> accessoryPages) {
		this.accessoryPages = accessoryPages;
	}
	/**
	 * @return the other names attached to node 
	 */
	public Map<Long, String> getOtherNameIds() {
		return otherNameIds;
	}
	/**
	 * @param otherNames the otherNames to set
	 */
	public void setOtherNameIds(Map<Long, String>  otherNameIds) {
		this.otherNameIds = otherNameIds;
	}
	/**
	 * @return the media objects attached to node
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
	/**
	 * @return the contributors attached to node
	 */
	public Set<Contributor> getContributors() {
		return contributors;
	}
	/**
	 * @param contributors the contributors to set
	 */
	public void setContributors(Set<Contributor> contributors) {
		this.contributors = contributors;
	}

}
