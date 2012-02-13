package org.tolweb.tapestry.xml.taxaimport.beans;

public class XTSourceInformation {
	/*
	    <source-information>
			<source-id>some unique identifier</source-id>
			<source-key>some source-specified value</source-key>
	    </source-information>
	 */
	private String sourceId;
	private String sourceKey;
	
	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}
	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	/**
	 * @return the sourceKey
	 */
	public String getSourceKey() {
		return sourceKey;
	}
	/**
	 * @param sourceKey the sourceKey to set
	 */
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
}
