package org.tolweb.content.export;

public class HostInfo {
	public static final String PROTOCOL = "http://";
	public static final String URL_SEPERATOR = "/";
	private String hostPrefix; 
	
	public HostInfo(String host) {
		this.hostPrefix = host;
	}

	public String getHostPrefix() {
		return hostPrefix;
	}
	
	public String getProtocol() {
		return PROTOCOL;
	}
	
	@Override
	public String toString() {
		String host = PROTOCOL + hostPrefix;
		return !host.endsWith(URL_SEPERATOR) ? host + URL_SEPERATOR : host;
	}	
}
