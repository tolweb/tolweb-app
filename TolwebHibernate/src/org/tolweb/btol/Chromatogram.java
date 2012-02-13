package org.tolweb.btol;

import org.tolweb.hibernate.PersistentObject;

/**
 * @hibernate.class table="Chromatograms"
 * @author dmandel
 *
 */
public class Chromatogram extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -763240324852467648L;
	private String filename;

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
