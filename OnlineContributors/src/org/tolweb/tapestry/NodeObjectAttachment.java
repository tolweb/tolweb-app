package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;

public abstract class NodeObjectAttachment extends BaseComponent {
	public static final String PAGE_OBJECT = "page";
	public static final String ACC_PAGE_OBJECT = "accessory page";
	public static final String OTHERNAME_OBJECT = "other name";
	public static final String MEDIA_OBJECT = "media";
	
	public abstract boolean getShouldMove();
	public abstract void setShouldMove(boolean value);
	
	public abstract String getObjectType();
	public abstract void setObjectType(String typeText);

	public abstract Long getObjectIndex();
	public abstract void setObjectIndex(Long idx);
	
	public String getObjectText() {
		return getObjectType() + " id: ";
	}
}
