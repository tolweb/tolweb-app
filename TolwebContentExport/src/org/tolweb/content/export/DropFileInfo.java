package org.tolweb.content.export;

import java.io.File;

/**
 * Models metadata associated with the "drop file" 
 * creator from the Exporter by the SingleFileDumper. 
 * 
 * This information is injected via Spring from the 
 * application.properties file.  
 * 
 * @author lenards
 *
 */
public class DropFileInfo {
	
	private String fileName;
	private String location;
	
	public DropFileInfo(String location, String fileName) {
		super();
		this.fileName = fileName;
		this.location = location;
	}

	public String getFileName() {
		return fileName;
	}

	public String getLocation() {
		return addTrailingSeparator(location);
	}
	
	private String addTrailingSeparator(String location) {
		return location.endsWith(File.separator) ? location : location + File.separator;
	}
	
	public String getFullPath() {
		return getLocation() + getFileName();
	}
}
