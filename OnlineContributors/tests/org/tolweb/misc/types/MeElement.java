package org.tolweb.misc.types;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MeElement {
	private String id;
	private String nameA;
	private String nameC;
	
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        						.append("id", id)
        						.append("nameA", nameA)
        						.append("nameC", nameC)
        						.toString( );
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the nameA
	 */
	public String getNameA() {
		return nameA;
	}
	/**
	 * @param nameA the nameA to set
	 */
	public void setNameA(String nameA) {
		this.nameA = nameA;
	}
	/**
	 * @return the nameC
	 */
	public String getNameC() {
		return nameC;
	}
	/**
	 * @param nameC the nameC to set
	 */
	public void setNameC(String nameC) {
		this.nameC = nameC;
	}
	
}
