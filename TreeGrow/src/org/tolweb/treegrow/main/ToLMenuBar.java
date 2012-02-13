package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.jdom.input.*;
import org.tolweb.treegrow.main.undo.*;
import org.tolweb.treegrow.tree.*;

/**
 * Abstract class that contains common menu logic for both the TreeEditor and
 * PageEditor.
 */
public class ToLMenuBar extends JMenuBar implements MenuListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5739798451393334237L;
	protected JMenu fileMenu;
    protected JMenu editMenu;
    protected JMenu windowMenu;
    protected JMenu helpMenu;

    protected JMenuItem openDiffItem;
    protected JMenuItem uploadItem;
    protected JMenuItem checkInSubtreeItem;
    protected JMenuItem submitItem;

    protected JMenuItem userPrefItem;
    protected JMenuItem checkInItem;
    protected JMenuItem closeItem;
    protected JMenuItem quitItem;

    //new batch of menu items
    protected JMenuItem addPageItem;
    protected JMenuItem removePageItem;
    protected JMenuItem closeNodeItem;
    protected JMenuItem undoItem;
    protected JMenuItem redoItem;
        
    private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem;
    protected ToLJFrame frame;
        
    public ToLMenuBar(ToLJFrame f) {
        frame = f;
        fileMenu = new JMenu("File");
        fileMenu.addMenuListener(this);
        addFileMenuItems();

        editMenu = new JMenu("Edit");
        editMenu.addMenuListener(this);

        windowMenu = Controller.getController().createWindowMenu();

        helpMenu = new JMenu("Help");  
        initializeMenu();
        addEditMenuItems();
        addCopyPasteEditMenuItems();
        addHelpMenuItems();            
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public JMenu getEditMenu() {
        return editMenu;
    }

    public JMenuItem getUndoItem() {
        return undoItem;
    }

    public JMenu getWindowMenu() {
        return windowMenu;
    }

    public JMenu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Method to override in order to do initialization of menus
     */
    protected void initializeMenu() {
        add(fileMenu);
        add(editMenu);
        add(windowMenu);
        add(helpMenu);    
    }

    protected void addHelpMenuItems() {
        JMenuItem firstTimeUsersItem = new JMenuItem("First-Time Users");
        firstTimeUsersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controller.getController().showFirstTimeUsersDialog();
            }
        });
        helpMenu.add(firstTimeUsersItem);
        
        JMenuItem previewingItem = new JMenuItem("Previewing ToL Pages");
        previewingItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String PREVIEW_TOOLTIP = Controller.getController().getMsgString("PREVIEW_TOOLTIP");
                JOptionPane.showMessageDialog(frame, PREVIEW_TOOLTIP, "Previewing ToL Pages", JOptionPane.INFORMATION_MESSAGE, Controller.getController().getLightbulbLabel("").getIcon());
            }
        });
        helpMenu.add(previewingItem);
        JMenuItem aboutItem = new JMenuItem("About TreeGrow");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutTreeGrowFrame frame = new AboutTreeGrowFrame();
                frame.pack();
                frame.show();
            }
        });
        helpMenu.addSeparator();
        addHelpMenuUrl("Quickstart", "http://tolweb.org/tree/sep/quickstart.html");
        addHelpMenuUrl("Technical Reference", "http://tolweb.org/tree/sep/tech/techreference.html");
        addHelpMenuUrl("Tree Window Tools", "http://tolweb.org/tree/sep/tech/treetools.html");
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
    }
    
    private void addHelpMenuUrl(String text, String url) {
        JMenuItem quickstartItem = new JMenuItem(new WWWAction(text, url));
        helpMenu.add(quickstartItem);        
    }

    /**
     * Used to turn off menus during some server interaction so that they 
     * can't be used to do damage
     *
     * @param value Whether to enable or disable them
     */
    protected void setCriticalMenusEnabled(boolean value) {
        helpMenu.requestFocus();
        fileMenu.setEnabled(value);
        editMenu.setEnabled(value);   
    }


    /**
     * add all the required items for the file menu here.
     */
    protected void addFileMenuItems() {
        openDiffItem = new JMenuItem("Open TreeGrow Manager");
        openDiffItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CloseDialog.openTreeGrowManager();
            }
        });
        uploadItem = new JMenuItem("Upload To Server");
        uploadItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        UploadTree.doUploadDialog();
                    }
                };
                thread.start();
            }
        });

        quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CloseDialog dialog = new CloseDialog();
                Controller controller = Controller.getController();
                int success = dialog.saveConfirm();
            }
        });
        userPrefItem = new JMenuItem("User Preferences");
        userPrefItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controller.getController().showPreferencesWindow();
            }
        });
        checkInItem = new JMenuItem("Check In Tree"); //enabled only when file is open
        checkInItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        doCheckIn(false);
                    }
                };
                thread.start();
            }
        });
        checkInSubtreeItem = new JMenuItem("Check In Subtree");
        checkInSubtreeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CheckInSubtreeFrame frame = new CheckInSubtreeFrame();
            }
        });            
        submitItem = new JMenuItem("Publication Manager", WWWAction.GLOBE_ICON);
        submitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String publishUrl = HttpRequestMaker.getExternalFrontEndUrlString("UserSubmitPublication");
                OpenBrowser ob = new OpenBrowser(publishUrl);
                ob.start();
            }
        });
        
        /*previewItem = new JMenuItem("View Version On Server", WWWAction.GLOBE_ICON);
        previewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controller controller = Controller.getController();
                if (controller.getFileChanged(getRootNode())) {
                    String UPLOAD_BEFORE_PREVIEW = controller.getMsgString("UPLOAD_BEFORE_PREVIEW");
                    String UPLOAD_THEN_PREVIEW = controller.getMsgString("UPLOAD_THEN_PREVIEW");
                    String PREVIEW_NO_UPLOAD = controller.getMsgString("PREVIEW_NO_UPLOAD");
                    //String CANCEL_NO_PREVIEW = controller.getMsgString("CANCEL_NO_PREVIEW");
                    String[] options = new String[] { UPLOAD_THEN_PREVIEW, PREVIEW_NO_UPLOAD };
                    int response = RadioOptionPane.showRadioButtonDialog(controller.getTreeEditor(), "Upload Before Preview?", UPLOAD_BEFORE_PREVIEW, options);
                    if (response == 0) {
                        // They want to upload their changes
                        UploadTree.doUploadDialog();
                    } else if (response == 2 || response == -1) {
                        // They cancelled so don't do the preview
                        return;
                    }
                }
                String path = new String(controller.getPreviewUrl(getRootNode()));
                OpenBrowser ob = new OpenBrowser(path);
                ob.start();
            }
        });*/

        fileMenu.add(openDiffItem);
        fileMenu.addSeparator();
        fileMenu.add(uploadItem);
        fileMenu.addSeparator();
        fileMenu.add(checkInItem);
        fileMenu.add(checkInSubtreeItem);
        fileMenu.add(submitItem);
        fileMenu.addSeparator();
        fileMenu.add(quitItem);            
    }

    /**
     * Actually undo the last uploadd
     */
    private void undoLastUpload() {
        Controller controller = Controller.getController();
        controller.setCriticalMenusEnabled(false);
        String fileName = controller.getFileName();
        controller.getFileManager().checkIn();

        String urlString = controller.getCGIPath() + "revertLastUpload.pl?upload_id=" + controller.getUploadId() + "&user_id="+ controller.getUserName() + "&password=" + controller.getPassword();
        System.out.println("revert url is: " + urlString);
        try {
            SAXBuilder builder = new SAXBuilder();
            org.jdom.Document doc = builder.build(new URL(urlString));
            org.jdom.Element root = doc.getRootElement();               
            boolean validationSuccessful = UserValidationChecker.checkValidation(root);
            if (!validationSuccessful) {
                return;
            }
            String result = root.getTextTrim();           
            if (!result.equals(XMLConstants.SUCCESS)) {
                String PROBLEM_REVERT = controller.getMsgString("PROBLEM_REVERT");
                JOptionPane.showMessageDialog(null, PROBLEM_REVERT, "Problem undoing upload", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            String PROBLEM_REVERT = controller.getMsgString("PROBLEM_REVERT");
            JOptionPane.showMessageDialog(null, PROBLEM_REVERT, "Problem undoing upload", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //controller.getFileManager().deleteFile();
        controller.setDepth(1);
        controller.getTreeEditor().dispose();
        controller.setFileName(fileName);
        DataDownloader downloader = new DataDownloader(controller.getNodeId());
        downloader.dontCheckInOnCancel();
        downloader.startFullDownload();
    }
    
    public void doCheckIn() {
        doCheckIn(false);
    }

    /**
     * Check-in the file, perhaps doing an upload before the actual checkin
     * occurs
     * 
     * @param doSubmissionOnOpen If true, submit the currently open file for publication 
     * when the Manager Frame pops back up. 
     */
    public void doCheckIn(boolean doSubmissionOnOpen) {
        Controller controller = Controller.getController();
        if (controller.getFileChanged()) {
            String UPLOAD_BEFORE_CHECKIN = controller.getMsgString("UPLOAD_BEFORE_CHECKIN");
            String UPLOAD_THEN_CHECKIN = controller.getMsgString("UPLOAD_THEN_CHECKIN");
            String CHECKIN_NO_UPLOAD = controller.getMsgString("CHECKIN_NO_UPLOAD");
            String CANCEL_NO_CHECKIN = controller.getMsgString("CANCEL_NO_CHECKIN");
            String[] options = new String[] { UPLOAD_THEN_CHECKIN, CHECKIN_NO_UPLOAD, CANCEL_NO_CHECKIN };
            int response = RadioOptionPane.showRadioButtonDialog(controller.getManagerFrame(), "Upload Before Checkin?", UPLOAD_BEFORE_CHECKIN, options);

            switch (response) {
                case 0:
                    // Upload
                    boolean result = UploadTree.doUploadDialog();
                    if (!result) {
                        return;
                    }
                case 1:
                    break;
                default:
                    return;
            }
        }
        controller.setCriticalMenusEnabled(false);
        controller.setWaitCursors();
        boolean result = controller.getFileManager().checkIn();
        if (result) {
            controller.openFileManager(doSubmissionOnOpen);
            controller.getTreeEditor().dispose();
        } else {
            String CANT_CHECK_IN = controller.getMsgString("CANT_CHECK_IN");
            JOptionPane.showMessageDialog(controller.getTreeEditor(), CANT_CHECK_IN, "Cant Check In", JOptionPane.ERROR_MESSAGE);                    
        }        
        controller.setCriticalMenusEnabled(true);
        controller.setDefaultCursors();
    }

    /**
     * Convenience method for disabling items that shouldn't be allowed
     * during download and re-enabling them later
     *
     * @param value The boolean inidicating whether things should be enabled
     */
    public void setFileRelatedItemsEnabled(boolean value) {
        checkInItem.setEnabled(value);
        uploadItem.setEnabled(value);
    }

    /** 
     * Add the copy, paste and cut items to the "edit" menu
     * Assign each a listener that sets a controller varaible which
     *ensures that the changemonitoringTextFields will not be flumoxed 
     *by the changing focus that is caused by selecting a menu then 
     *action on the field.
     */
    private void addCopyPasteEditMenuItems() {

        createActions();
        editMenu.addSeparator();

        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);             

        ActionListener cutPasteListener =new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controller.getController().setMenuCuttingPasting(true); 
            }
        };

        cutMenuItem.addActionListener(cutPasteListener);   
        pasteMenuItem.addActionListener(cutPasteListener);            
    }    


    /**
     * Goes through all the actions associated with textfields, and 
     *picks out the ones for cut, copy, paste and select all,
     *creating a menuItem for each one
     */
    private void createActions() {
        JTextField textField = new JTextField();
        Action[] actionsArray = textField.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            String name = (String)(a.getValue(Action.NAME));
            if ( DefaultEditorKit.cutAction.equals(name)) {
                cutMenuItem = new JMenuItem(a);
                cutMenuItem.setText("Cut");
            } else if ( DefaultEditorKit.copyAction.equals(name)) {
                copyMenuItem = new JMenuItem(a);
                copyMenuItem.setText("Copy");
            } else if ( DefaultEditorKit.pasteAction.equals(name)) {
                pasteMenuItem = new JMenuItem(a);
                pasteMenuItem.setText("Paste");
            }
        }
    }

    /**
     * Abstract method that subclasses must implement to get undo behavior
     */
    protected void addEditMenuItems() {
        UndoAction action = new UndoAction(frame.getUndoManager());
        undoItem = new JMenuItem(action);
        frame.setUndoAction(action);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        RedoAction redoAction = new RedoAction(frame.getUndoManager());
        redoItem = new JMenuItem(redoAction);
        frame.setRedoAction(redoAction);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        action.setRedoAction(redoAction);
        redoAction.setUndoAction(action);

        editMenu.add(undoItem,0);
        editMenu.add(redoItem,1);        
    }

    /**
     * Method to return the root node associated with this menu
     */
    protected Node getRootNode() {
        return Controller.getController().getTree().getRoot();
    }

    /**
     * Overridden to return true so that textfields lose focus and events
     * get fired correctly
     */
    public boolean isFocusable() {
        return true;
    }

    public void menuCanceled(MenuEvent e) {
    }

    public void menuDeselected(MenuEvent e) {
    }

    public void menuSelected(MenuEvent e) {
        ((Component)e.getSource()).requestFocus();
    }
}


