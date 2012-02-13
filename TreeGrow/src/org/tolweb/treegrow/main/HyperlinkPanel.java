/*
 * HyperlinkPanel.java
 *
 * Created on April 9, 2004, 8:49 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Panel class that mimics a hyperlink.  When clicked, the link opens in
 * a browser on the user's computer
 */
public class HyperlinkPanel extends JPanel {  
    /**
	 * 
	 */
	private static final long serialVersionUID = -3536816548508829202L;
	protected String linkText;
    protected String linkDestination;
    protected int x1, y, x2;
    private boolean inPress;
        
    public HyperlinkPanel(String text, String destination) {
        linkText = text;
        linkDestination = destination;
        FontMetrics fm = getFontMetrics(getFont());
        int stringWidth = fm.stringWidth(linkText);
        int width = stringWidth + 2;
        int height = fm.getHeight() + 2;
        x1 = 1;
        y = height - 5;
        x2 = x1 + stringWidth;
        setPreferredSize(new Dimension(width, height));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                inPress = true;
                repaint();
            }
            
            public void mouseClicked(MouseEvent e) {
                OpenBrowser ob = new OpenBrowser(linkDestination);
                ob.start();     
            }
            
            public void mouseReleased(MouseEvent e) {
                inPress = false;
                repaint();
            }
        });

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        Controller controller = Controller.getController();
        setToolTipText(linkDestination);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!inPress) {
            g.setColor(Color.blue);
        } else {
            g.setColor(Color.red);
        }
        g.drawString(linkText, 1, y-2);
        g.drawLine(x1, y, x2, y);
    }
    
    public void setLinkDestination(String destination) {
        linkDestination = destination;
        setToolTipText(linkDestination);
    }
}
