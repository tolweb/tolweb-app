/*
 * AboutTreeGrowFrame.java
 *
 * Created on April 9, 2004, 8:36 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

/**
 *
 * @author  dmandel
 */
public class AboutTreeGrowFrame extends ToLJFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1766294345141492603L;

	/** Creates a new instance of AboutTreeGrowFrame */
    public AboutTreeGrowFrame() {
        super("About TreeGrow");
        initComponents();
        setSize(400, 400);
    }
    
    private void initComponents() {
        Container contentPane = getContentPane();
        double[][] size = new double[][] {{10, TableLayoutConstants.FILL, 10}, {10, TableLayoutConstants.FILL, 10, 25, 10}};
        contentPane.setLayout(new TableLayout(size));
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        HtmlLabel label = new HtmlLabel("<center>TreeGrow Version " + Controller.VERSION + "<br>Build " + Controller.BUILD_NUMBER + "</center><br> Developed by the Tree of Life home team:<ul>" +
            "<li>Danny Mandel</li><li>Travis Wheeler</li><li>Katja Schulz</li><li>David Maddison</li></ul><br>For detailed information on how to use TreeGrow, please refer to the <br>");

        HyperlinkPanel linkPanel = new HyperlinkPanel("Creating Branch and Leaf Pages", "http://tolweb.org/tree/sep/usingtreegrow.html");
        HtmlLabel otherLabel = new HtmlLabel("<br>pages in the ToL Scientific Contributors Documentation.");
        JPanel southPanel = new JPanel(new BorderLayout());
        mainPanel.add(label, BorderLayout.CENTER);
        JPanel centeringPanel = new JPanel(new TableLayout(new double[][] {{TableLayoutConstants.FILL}, {TableLayoutConstants.FILL}}));
        centeringPanel.add(linkPanel, "0,0,c");
        southPanel.add(centeringPanel, BorderLayout.CENTER);
        southPanel.add(otherLabel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        contentPane.add(mainPanel, "1,1");
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(okButton, "1,3,c");
    }
    
}
