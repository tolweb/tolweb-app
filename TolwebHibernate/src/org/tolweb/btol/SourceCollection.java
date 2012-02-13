package org.tolweb.btol;

/**
 * @hibernate.class table="SourceCollections"
 * @author dmandel
 *
 */
public class SourceCollection extends NamedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6051127755661628064L;
	private String code;
	
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDisplayName() {
		return getName();
	}
}
