package org.tolweb.content.export;

public class ConfigurationInfo {
	private boolean includeAll;
	
	public ConfigurationInfo(boolean includeAll) {
		this.includeAll = includeAll;
	}

	public boolean includeAll() {
		return includeAll;
	}	
}
