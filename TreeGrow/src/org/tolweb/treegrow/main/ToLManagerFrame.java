/*
 * FileManagerFrame.java
 *
 * Created on August 18, 2003, 1:03 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import layout.TableLayout;
import layout.TableLayoutConstants;

/**
 * The ToLManager frame that first pops up when the user starts the application
 * Contains tabs for searching/downloading, local file management, and 
 * editing tasks
 * 
 */
public class ToLManagerFrame extends ToLJFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6680508541936469037L;
	public static final int HEIGHT = 500;
    public static final int WIDTH = 600;
    
    private JTabbedPane tabs;
    private DownloadPanel downloadPanel;
    private FilesPanel filesPanel;
    private ManagerPreferencesPanel prefPanel;
    
    public ToLManagerFrame() {
        this(false);
    }
    
    /** Creates a new instance of FileManagerFrame */
    public ToLManagerFrame(boolean doSubmit) {
        super("TreeGrow Manager");//,false, true, false, false);//iconifiable
        Container SearchWindowContainer = getContentPane();

        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        tabs = new JTabbedPane();
        filesPanel = new FilesPanel();
        downloadPanel = new DownloadPanel();
        JPanel prefContainerPanel = new JPanel(new BorderLayout());
        prefPanel = new ManagerPreferencesPanel();
        prefContainerPanel.add(BorderLayout.CENTER,  prefPanel);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(new HtmlLabel("Version " + Controller.VERSION + ", build " + Controller.BUILD_NUMBER, "-2"), BorderLayout.EAST);
        prefContainerPanel.add(southPanel, BorderLayout.SOUTH);

        tabs.add("Download from server", downloadPanel);
        tabs.add("Local files", filesPanel);
        tabs.add("Preferences", prefContainerPanel);
        // If there is no net connection, only allow them to work on the local
        // files panel
        CheckNetConnection checker = new CheckNetConnection();
        if (checker.isConnected() < 0) {
            tabs.setEnabledAt(0, false);
            tabs.setEnabledAt(2, false);
            //tabs.setEnabledAt(3, false);
            tabs.setSelectedComponent(filesPanel);
        }
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, tabs);
        if (filesPanel.getFiles().size() > 0) {
            tabs.setSelectedComponent(filesPanel);
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (!Controller.getController().getPreferenceManager().dontShowCloseDialog() && Controller.getController().getFileManager().getLocalDataFiles().size() > 0) {
                    System.out.println("showing closing stuff");
                    ClosingFrame frame = new ClosingFrame();
                } else {
                    System.out.println("local files size is: " + Controller.getController().getFileManager().getLocalDataFiles().size());
                    System.out.println("dontshowclose is: " + Controller.getController().getPreferenceManager().dontShowCloseDialog());
                    System.out.println("not showing closing stuff");
                    System.exit(0);
                }
            }
        });
        setResizable(false);       
    }
    
    public void selectDownloadTab() {
        tabs.setSelectedComponent(downloadPanel);
    }
    
    public DownloadPanel getDownloadPanel() {
        return downloadPanel;
    }
    
    public FilesPanel getFilesPanel() {
        return filesPanel;
    }
    
    private class ClosingFrame extends ToLJFrame {
        /**
		 * 
		 */
		private static final long serialVersionUID = 5176591528235496968L;
		private JCheckBox dontShowAgainBox;
        
        public ClosingFrame() {
            super("Your Changes Have Been Saved");
            double[][] size = new double[][] {{20, 20, 10, TableLayoutConstants.FILL, 10}, {30, 125, 10, 20, 10, 30, 10}}; 
            Container contentPane = getContentPane();
            contentPane.setLayout(new TableLayout(size));
            contentPane.add(Controller.getController().getLightbulbLabel(""), "1,1,c,t");
            String TREEGROW_CLOSING = Controller.getController().getMsgString("TREEGROW_CLOSING");
            contentPane.add(new JLabel(TREEGROW_CLOSING), "3,1");
            dontShowAgainBox = new JCheckBox("Don't show me this message again");
            contentPane.add(dontShowAgainBox, "3,3");
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleClose();
                }
            });
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // close ourself but don't exit the program
                    setVisible(false);
                }
            });
            JPanel buttonsPanel = new JPanel();
            if (Controller.getController().isMac()) {
                buttonsPanel.add(cancelButton);
                buttonsPanel.add(okButton);
            } else {
                buttonsPanel.add(okButton);
                buttonsPanel.add(cancelButton);
            }
            contentPane.add(buttonsPanel, "3,5,r");
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    handleClose();
                }
            });
            pack();
            show();
        }
        
        private void handleClose() {
            if (dontShowAgainBox.isSelected()) {
                Thread thread = new Thread() {
                    public void run() {
                        Controller.getController().getPreferenceManager().setDontShowCloseDialog(true);
                        Controller.getController().getPreferenceManager().writePreferencesToDisk();
                        System.exit(0);                            
                    }
                };
                thread.start();
                try {
                    thread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        }
    }
}