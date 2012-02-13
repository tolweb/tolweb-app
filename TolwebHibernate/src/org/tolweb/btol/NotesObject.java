package org.tolweb.btol;

import org.tolweb.treegrow.main.StringUtils;

public abstract class NotesObject extends NamedObject {

	private String notes;

	public NotesObject() {
		super();
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	public boolean getHasNotes() {
		return StringUtils.notEmpty(getNotes());
	}

}