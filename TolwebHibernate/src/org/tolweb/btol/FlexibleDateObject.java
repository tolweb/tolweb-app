package org.tolweb.btol;

public abstract class FlexibleDateObject extends NotesObject {
	private Integer creationYear;
	private Integer creationMonth;
	private Integer creationDay;	

	public FlexibleDateObject() {
		super();
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Integer getCreationDay() {
		return creationDay;
	}
	public void setCreationDay(Integer creationDay) {
		this.creationDay = creationDay;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Integer getCreationMonth() {
		return creationMonth;
	}
	public void setCreationMonth(Integer creationMonth) {
		this.creationMonth = creationMonth;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Integer getCreationYear() {
		return creationYear;
	}
	public void setCreationYear(Integer creationYear) {
		this.creationYear = creationYear;
	}
	public String getCreationDateString() {
		String returnString = "";
		if (getCreationMonth() != null) {
			returnString += getCreationMonth();
			returnString += "/";
			if (getCreationDay() != null) {
				returnString += getCreationDay();
				returnString += "/";
			}
		}
		if (getCreationYear() != null) {
			returnString += getCreationYear();
		}
		return returnString;
	}
}