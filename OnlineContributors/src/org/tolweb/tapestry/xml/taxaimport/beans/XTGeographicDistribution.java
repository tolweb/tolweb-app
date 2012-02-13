package org.tolweb.tapestry.xml.taxaimport.beans;

public class XTGeographicDistribution {
	private String description;

	public String toString() {
		return "[Geographical Distribution: " + getDescription() + "]";
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
