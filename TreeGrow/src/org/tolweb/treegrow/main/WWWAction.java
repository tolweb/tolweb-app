/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WWWAction extends AbstractAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5145212253139147251L;
	private String url;
    private static final String ICON_PATH = "Images/wwwglobe.trsp.gif";
    public static ImageIcon GLOBE_ICON;
    
    static {
        GLOBE_ICON = new ImageIcon(ClassLoader.getSystemResource(ICON_PATH)); 
    }
    
    public WWWAction(String text, String url) {
        super(text, GLOBE_ICON);
        this.url = url;
    }
    public void actionPerformed(ActionEvent e) {
        OpenBrowser ob = new OpenBrowser(url);
        ob.start();        
    }
}
