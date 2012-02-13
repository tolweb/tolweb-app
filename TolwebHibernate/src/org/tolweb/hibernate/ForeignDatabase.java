package org.tolweb.hibernate;

import org.tolweb.btol.NamedObject;

/**
* @hibernate.class table="ForeignDatabases"
*/
public class ForeignDatabase extends NamedObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6932058869261634234L;
	private String urlFormat;
	private String url;

    /**
     * @hibernate.property
     * @return Returns the url.
     */
    public String getUrlFormat() {
        return urlFormat;
    }
    /**
     * @param url The url to set.
     */
    public void setUrlFormat(String url) {
        this.urlFormat = url;
    }
    public String getDisplayName() {
        return getName();
    }
    /**
     * @hibernate.property
     * @return
     */
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
