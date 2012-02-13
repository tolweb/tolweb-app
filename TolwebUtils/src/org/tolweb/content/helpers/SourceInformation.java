package org.tolweb.content.helpers;

import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * Represents source metadata for content objects. 
 * 
 * @author lenards
 *
 */
public class SourceInformation {
	private boolean tolNative;
	private String sourceCollection;
	private String sourceCollectionUrl;
	
	public SourceInformation(NodeImage mediaFile, ForeignDatabase foreignDB) {
		this(mediaFile);
		if (foreignDB != null && mediaFile.getSourceDbId() != null) {
			tolNative = false;
			sourceCollection = foreignDB.getDisplayName();
			sourceCollectionUrl = foreignDB.getUrl();
		} 
	}
	
	public SourceInformation(NodeImage mediaFile) {
		if (StringUtils.notEmpty(mediaFile.getSourceCollectionUrl())) {
			tolNative = false;
			sourceCollection = mediaFile.getSourceCollectionTitle();
			sourceCollectionUrl = mediaFile.getSourceCollectionUrl();
		} else {
			tolNative = true;
		}		
	}

	public SourceInformation(MappedTextSection mtxt) {
		tolNative = true;
	}
	
	public boolean isTolNative() {
		return tolNative;
	}

	public void setTolNative(boolean tolNative) {
		this.tolNative = tolNative;
	}

	public String getSourceCollection() {
		return sourceCollection;
	}

	public void setSourceCollection(String sourceCollection) {
		this.sourceCollection = sourceCollection;
	}

	public String getSourceCollectionUrl() {
		return sourceCollectionUrl;
	}

	public void setSourceCollectionUrl(String sourceCollectionUrl) {
		this.sourceCollectionUrl = sourceCollectionUrl;
	}	
}
