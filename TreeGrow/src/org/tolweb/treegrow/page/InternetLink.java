/*
 * Created on Jun 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.treegrow.page;

import java.io.Serializable;

import org.tolweb.treegrow.main.OrderedObject;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InternetLink extends OrderedObject implements Comparable, Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1505758099546473703L;
	private Long linkId;
	private String url;
	private String siteName;
	private String comments;
	private static final Integer UNSAVED_ID = Integer.valueOf(0);
	
	public boolean isEmpty() {
		return linkId == null || StringUtils.isEmpty(url) || StringUtils.isEmpty(siteName) || StringUtils.isEmpty(comments);
	}
	
	/**
	 * @hibernate.property column="link_order"
	 * @return
	 */
	public int getOrder() {
		return super.getOrder();
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @hibernate.id generator-class="native" column="link_id" unsaved-value="null"
	 * @return
	 */
	public Long getLinkId() {
		return linkId;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param string
	 */
	public void setComments(String string) {
		comments = string;
	}

	/**
	 * @param long1
	 */
	public void setLinkId(Long long1) {
		linkId = long1;
	}

	/**
	 * @param string
	 */
	public void setSiteName(String string) {
		siteName = string;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string) {
		url = string;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null) {
			return false;
		} else if (!(o.getClass() == getClass())) {
			return false;
		} else {
			InternetLink other = (InternetLink) o;
			return getOrder() == other.getOrder();
		}
	}
	
	public int hashCode() {
		return Integer.valueOf(getOrder()).hashCode();
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}  
}
