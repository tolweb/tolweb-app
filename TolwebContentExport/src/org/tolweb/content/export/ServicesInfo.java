package org.tolweb.content.export;

public class ServicesInfo {
	private String treeServiceUrl;
	private String contentServiceUrl;
	
	public ServicesInfo(String treeServiceUrl, String contentServiceUrl) {
		super();
		this.treeServiceUrl = treeServiceUrl;
		this.contentServiceUrl = contentServiceUrl;
	}

	public String getTreeServiceUrl() {
		return treeServiceUrl;
	}

	public String getContentServiceUrl() {
		return contentServiceUrl;
	}
}