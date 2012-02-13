package org.tolweb.tapestry.xml.taxaimport.beans;

public abstract class AbstractOMElement {
	private Long id;
	private String type;
	private String datatable;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the datatable
	 */
	public String getDatatable() {
		return datatable;
	}
	/**
	 * @param datatable the datatable to set
	 */
	public void setDatatable(String datatable) {
		this.datatable = datatable;
	}
}
