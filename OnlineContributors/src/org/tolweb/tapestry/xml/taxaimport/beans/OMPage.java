package org.tolweb.tapestry.xml.taxaimport.beans;

import java.io.Serializable;

public class OMPage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1589145595949509092L;
/*
<page id="2905" type="content" data-table="PAGES"/>
 */
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
