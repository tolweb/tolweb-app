/*
 * HTMLLabel.java
 *
 * Created on March 12, 2004, 8:58 AM
 */

package org.tolweb.treegrow.main;

import javax.swing.*;

/**
 * Class used to negotiate the Mac HTML font wackiness
 */
public class HtmlLabel extends JLabel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 480978667869364274L;
	public static final String MAC_FONT_STRING = "<font face=\"Lucida Grande\">";
    private String size;

    public HtmlLabel(String str) {
        this(str, null);
    }
    
    /** Creates a new instance of HTMLLabel */
    public HtmlLabel(String str, String sz) {
        super(str);
        size = sz;
        setText(str);
    }
    
    public void setText(String val) {
        super.setText("<html>" + getFontString() + val);
    }
    
    public String getFontString() {
        return getGenericFontString(size);
    }
    
    public static String getGenericFontString() {
        return getGenericFontString(null);
    }
    
    public static String getGenericFontString(String fontSize) {
        String fontString = Controller.getController().isMac() ? MAC_FONT_STRING : "";
        String sizeString = fontSize != null ? "<font size=\"" + fontSize + "\">" : "";
        return fontString + sizeString;        
    }
    
}
