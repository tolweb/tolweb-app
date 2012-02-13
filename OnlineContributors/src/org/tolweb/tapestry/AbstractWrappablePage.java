package org.tolweb.tapestry;

import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.html.BasePage;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractWrappablePage extends BasePage {
	public static final byte DEFAULT_WRAPPER = 0;
	public static final byte LEARNING_WRAPPER = 1;
	public static final byte FORM_WRAPPER = 2;
	public static final byte TREEHOUSE_WRAPPER = 5;
	public static final byte SEARCH_WRAPPER = 4;
	public static final byte NEW_FORM_WRAPPER = 5;
    public static final byte SCIENTIFIC_WRAPPER = 6;
    public static final byte BUILD_TREEHOUSE_WRAPPER = 7;
	public static final String TREEHOUSE_BODY = "trhsdatabody";
	public static final String MEDIA_SEARCH_BODY = "mediasearch";
	
	public abstract void setWrapperType(byte value);
	@Persist("client")
	public abstract byte getWrapperType();
	public abstract void setBodyStyle(String value);
	public abstract String getBodyStyle();
}
