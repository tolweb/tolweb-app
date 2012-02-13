/*
 * DownloadPanel.java
 *
 * Created on August 18, 2003, 5:04 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

import org.jdom.*;

/**
 * JPanel subclass that does the searching and downloading on the ToLManager
 * 
 */
public class DownloadPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3224759332654517932L;
	private JTextField searchField, depthField;
    private JButton findGroupButton, downloadButton;
    private JList resultsList;
    private DefaultListModel resultsListModel;
    private JTextArea nodeDetailsArea;
    private JLabel depthLabel, tooltipLabel;
    private Hashtable userMessages = new Hashtable();

    public DownloadPanel() {
        double[][] size = new double[][] {{10, TableLayoutConstants.FILL, 10}, 
            {10, 25, 10, TableLayoutConstants.FILL, 10}};
        setLayout(new TableLayout(size));
        add(initTopPanel(), "1, 1");
        add(initBottomPanel(), "1, 3");
        setDownloadItemsEnabled(false);        
    }
    
    /**
     * Returns the selected node in the search results list
     *
     * @return The selected node in the search results list
     */
    public NodeDetails getSelectedValue() {
        return (NodeDetails) resultsList.getSelectedValue();
    }

    private JPanel initTopPanel() {
        JPanel topPanel = new JPanel();
        KeyListener searchListener = new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    findGroupButton.doClick();
                }
            }
        };            
        topPanel.setLayout(new TableLayout(new double[][] {{185, TableLayoutConstants.FILL, 10, 105}, {TableLayoutConstants.FILL}}));
        topPanel.add(new JLabel("Name of group to download"), "0,0");
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                checkText();
            }
            public void insertUpdate(DocumentEvent e) {
                checkText();
            }
            public void removeUpdate(DocumentEvent e) {
                checkText();
            }

            private void checkText() {
                findGroupButton.setEnabled(searchField.getText() != null && !searchField.getText().equals(""));
            }
        });
        searchField.addKeyListener(searchListener);
        topPanel.add(searchField, "1,0");
        findGroupButton = new JButton("Find group");
        findGroupButton.addKeyListener(searchListener);
        findGroupButton.setEnabled(false);
        findGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchThread().start();
            }
        });
        topPanel.add(findGroupButton, "3,0");
        return topPanel;
    }

    private JPanel initBottomPanel() {
        Controller controller = Controller.getController();
        JPanel bottomPanel = new JPanel();       
        bottomPanel.setLayout(new TableLayout(new double[][] {{280, 10, 20, 10, 170}, {25, TableLayoutConstants.FILL, 10, 25, 10, 25, 10}}));
        resultsListModel = new DefaultListModel();
        resultsList = new JList(resultsListModel);
        resultsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateUIToSelectedDetails();
            }
        });
        resultsList.setCellRenderer(new NodeDetailsCellRenderer());
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);            
        bottomPanel.add(new JLabel("Matching group names in the ToL Database:"), "0, 0");
        bottomPanel.add(controller.updateUnitIncrement(new JScrollPane(resultsList)), "0, 1, 0, 5");
        String tooltipText;
        if (controller.getFileManager().getLocalDataFiles().size() > 0) {
            tooltipText = "DOWNLOAD_TOOLTIP_LOCAL";
        } else {
            tooltipText = "DOWNLOAD_TOOLTIP_NO_LOCAL";            
        }
        tooltipLabel = controller.getLightbulbLabel(tooltipText);
        //resetTooltipText();
        bottomPanel.add(tooltipLabel, "2,1,l,t");
        nodeDetailsArea = new JTextArea();
        nodeDetailsArea.setEditable(false);
        nodeDetailsArea.setCursor(null);
        nodeDetailsArea.setLineWrap(true);
        nodeDetailsArea.setWrapStyleWord(true);
        nodeDetailsArea.setOpaque(false);
        bottomPanel.add(nodeDetailsArea, "4, 1");
        JPanel pageDepthPanel = new JPanel();
        depthLabel = new JLabel("Page depth  ");
        pageDepthPanel.add(depthLabel); 
        depthField = new JTextField("1  ");
        pageDepthPanel.add(depthField);
        bottomPanel.add(pageDepthPanel, "4, 3, r");
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadButton.setEnabled(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                boolean result = doDownload();
                // User didn't donwnload for some reason, so set the cursor
                // back to the regular
                if (!result) {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        bottomPanel.add(downloadButton,  "4, 5, r");
        return bottomPanel;
    }
    
    /**
     * Downloads the selected node from the list
     */
    private boolean doDownload() {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        JFrame parentFrame = controller.getManagerFrame();
        int depth = -1;
        try {
            depth = Integer.parseInt(depthField.getText().trim());
            if (depth > 5) {
                throw new RuntimeException();
            }
        } catch (Exception ex) {
            String VALID_DEPTH = controller.getMsgString("VALID_DEPTH");
            JOptionPane.showMessageDialog(controller.getManagerFrame(), VALID_DEPTH, "Bad number", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        controller.setDepth(depth);
        NodeDetails tempDetails = (NodeDetails) resultsList.getSelectedValue();
        String fileName = tempDetails.getParentPageName() != null ? tempDetails.getParentPageName() : tempDetails.getNodeName();
        int nodeIdToDownload = tempDetails.getParentPageName() != null ? tempDetails.getParentPageNodeIdInt() : tempDetails.getNodeIdInt();
        controller.setFileName(fileName);
        if (tempDetails.getStatus().equals("APPROVAL_PENDING_EDITING_PRIVILEGES")) {
            controller.setEditorBatchId(tempDetails.getBatchId());
        }
        String file = getFileName();
        
        // At this point, just make sure the depth is set correctly in case of weirdness
        if (controller.getDepth() <= 0) {
            controller.setDepth(1);
        }
       
        if(file == null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            DataDownloader down;
            if (tempDetails.getBatchId() != null) {
                down = new DataDownloader(nodeIdToDownload, tempDetails.getBatchId(), true);
            } else {
                down = new DataDownloader(nodeIdToDownload, true);            
            }
            down.startFullDownload();
            return true;
        }

        //check if file exists
        fileName = null;

        if(file.indexOf("Resource") < 0) {
            fileName = fileManager.getDataPath() + file;
        } else {
            fileName = file;
        }
        File tempFile = new File(fileName);
        
        // At this 

        if (tempDetails.getRoot() != null) {
        // if the node is internal then do these steps given below.
        if(!(tempDetails.getRoot().equals(tempDetails.getNodeName()))) {
            //if the root file doesnt exist on this comp, just download.
            if(!tempFile.exists()) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DataDownloader down = new DataDownloader(nodeIdToDownload, true);
                down.startFullDownload();
                return true;
            }

            //if the file exists
            //prompt the user as to whether they need this file or not.

            Object[] options = {"Yes" ,"No"};
            String ROOT_EXIST_DOWNLOAD = controller.getMsgString("ROOT_EXIST_DOWNLOAD");
            int n = JOptionPane.showOptionDialog(parentFrame,
                            ROOT_EXIST_DOWNLOAD,
                            "Message",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);

            if (n == JOptionPane.YES_OPTION) {
                    //check for changes in the file.
                    boolean result = fileManager.getMetadataFromFile(fileName);
                    if(!result) {
                            return false;
                    }
                   
                    String EXPORT_DOWNLOAD = controller.getMsgString("EXPORT_DOWNLOAD");
                    String EXPORT_MOVE_RECYCLE = controller.getMsgString("EXPORT_MOVE_RECYCLE");
                    String MOVE_RECYCLE = controller.getMsgString("MOVE_RECYCLE");
                    String[] choices = new String[] { MOVE_RECYCLE };
                    int response = RadioOptionPane.showRadioButtonDialog(controller.getTreeEditor(), "What would you like to do?", EXPORT_DOWNLOAD, choices);

                    // The user cancelled, so dont continuie
                    if (response == -1) {
                        return false;
                    } else if (response == 0) {
                        fileManager.moveToRecycleBin(fileName);
                        /*ExportFrame exportForm = new ExportFrame(controller.getManagerFrame());
                        exportForm.show();
                        /*while(!controller.exportDone()) {
                            try {
                                System.out.println("spinning");
                                Thread.sleep(250);
                            } catch (Exception ex) {}
                        }
                        System.out.println("done spinning");
                         /
                        fileManager.moveToRecycleBin(fileName);
                        controller.setExportDone(false);*/
                    } else if (response == 1) {
                        fileManager.moveToRecycleBin(fileName);
                    }                    

                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    DataDownloader down = new DataDownloader(nodeIdToDownload);
                    down.startFullDownload();

                    return true;
            } else if (n == JOptionPane.NO_OPTION) {
                return false;
            }

            return false;
        }
        }

        if(!tempFile.exists()) {
            Controller.getController().getFileManager().checkIn(tempDetails.getDownloadId());
            DataDownloader down = new DataDownloader(nodeIdToDownload);
            down.startFullDownload();
            return true;
        }

        //file exists then.
        //get the downloadid and changed status from file.

        boolean result = fileManager.getMetadataFromFile(fileName);
        if(!result) {
            return false;
        }

        String current = fileManager.isVersionCurrent(controller.getDownloadId());

        if((current != null) && (current.equals("false"))) {
            // handle non current file function

            //check for unuploaded changes
            if(!controller.getFileChanged()) {
                //file has not been changed
                //remove file, checkin and start download
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                result = fileManager.checkIn();
                if(!result) {
                        return false;
                }

                DataDownloader down = new DataDownloader(nodeIdToDownload);
                down.startFullDownload();
                return true;
            }

            String BAD_FILE_UPLOAD = controller.getMsgString("BAD_FILE_UPLOAD");
            String EXPORT_MOVE_RECYCLE = controller.getMsgString("EXPORT_MOVE_RECYCLE");
            String MOVE_RECYCLE = controller.getMsgString("MOVE_RECYCLE");
            String[] choices = new String[] { MOVE_RECYCLE };
            int response = RadioOptionPane.showRadioButtonDialog(controller.getTreeEditor(), "What would you like to do?", BAD_FILE_UPLOAD, choices);

            if (response == 0) {
                //ExportFrame exportForm = new ExportFrame(controller.getManagerFrame());
                //exportForm.show();
                fileManager.moveToRecycleBin(fileName);
            } else {
                fileManager.moveToRecycleBin(fileName);
            }

            return false;
        }

        if(!controller.getFileChanged()) {
            //file has not been changed
            //check if requested depth == old depth
            String requestedDepth = depthField.getText().trim();
            if((tempDetails.getDepth() == null) || (!(requestedDepth.equals(tempDetails.getDepth())))) {
                //remove file, checkin and start download
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                result = fileManager.checkIn();
                if(!result) {
                    return false;
                }

                controller.setDepth(new Integer(requestedDepth).intValue());
                DataDownloader down = new DataDownloader(nodeIdToDownload);
                down.startFullDownload();
            } else {
                //open local file
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                FileReaderThread fileDownloadThread = new FileReaderThread(fileName);
                fileDownloadThread.start();
            }
            return true;
        }

        return true;
    }
    
    /**
     * Returns the local filename for the selected node in the list
     * 
     * @return the local filename for the selected node in the list
     */
    private String getFileName() {
        NodeDetails tempDetails = (NodeDetails) resultsList.getSelectedValue();
        if(tempDetails.getRoot() == null || tempDetails.getBatchId() != null) {
            return null;
        }
        
        return (tempDetails.getParentPageName() != null ? tempDetails.getParentPageName() : tempDetails.getNodeName()) + Controller.EXTENSION;
    }
    
    public void setSearchText(String value) {
        searchField.setText(value);
    }
    
    public void doSearch() {
        findGroupButton.doClick();
    }
    
    public void resetTooltipText() {
        /*String tooltipText;
        Controller controller = Controller.getController();

        tooltipLabel = setToolTipText(tooltipText);        */
    }

    /**
     * Thread class responsible for performing the search script on the
     * server and populating the results list box
     */
    final class SearchThread extends Thread {
        public void run() {
            Controller controller = Controller.getController();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            findGroupButton.setEnabled(false);
            searchField.setEnabled(false);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            resultsListModel.removeAllElements();

            String nametosearch = URLEncoder.encode(searchField.getText().trim());

            try {
                searchDatabase(nametosearch);
            } catch (Exception e) {
                System.out.println("error  searching 2: " + e.getMessage());
            }

            findGroupButton.setEnabled(true);
            searchField.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public int searchDatabase(String s) throws IOException {
        Controller controller = Controller.getController();
        CheckNetConnection check = new CheckNetConnection();
        if(!(check.isConnected() > 0)) {
            //inform user that there is no net connection
            String NO_NET_CONNECTION = controller.getMsgString("NO_NET_CONNECTION");
            JOptionPane.showMessageDialog(
                                    controller.getManagerFrame(),
                                    NO_NET_CONNECTION,
                                    "Message Window",
                                    JOptionPane.INFORMATION_MESSAGE);
            return -1;
        }
        NodeNameSearcher searcher = new NodeNameSearcher();
        Object result = searcher.searchDatabase(s, 0);
        if (result == null) {
            searchField.requestFocus();
            setDownloadItemsEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return -1;
        }        
        if (result instanceof Element) {
            handleDownloadError((Element) result);
        } else if (result instanceof Stack) {
            Stack stack = (Stack) result;
            while (!stack.isEmpty()) {
                NodeDetails tmp = (NodeDetails)stack.pop();
                resultsListModel.addElement(tmp);
            }

            resultsList.setSelectedIndex(0);
            resultsList.requestFocus();
        }
        return 1;
    }

    /**
     * Sets the text in the details text area and updates UI components
     * to be enabled or disabled depending on whether things can be
     * downloaded
     *
     * @param selected The selected NodeDetails object
     */
    private void updateUIToSelectedDetails() {
        Controller controller = Controller.getController();
        if (resultsList.getSelectedIndex() == -1) {
            setDownloadItemsEnabled(false);
            return;
        }
        NodeDetails tempDetails = (NodeDetails) resultsList.getSelectedValue();
        String NOT_CHKD_OUT = controller.getMsgString("NOT_CHKD_OUT");
        Vector tempVec = new Vector();
        tempVec.add(tempDetails.getUser());
        tempVec.add(tempDetails.getTimeStamp());
        String CHKD_BY_ELSE = controller.getMsgString("CHKD_BY_ELSE",tempVec);

        tempVec.clear();
        tempVec.add(tempDetails.getRoot());
        tempVec.add(tempDetails.getDepth());
        String CHKD_BY_U_INTERNAL = controller.getMsgString("CHKD_BY_U_INTERNAL", tempVec);

        tempVec.clear();
        tempVec.add(tempDetails.getDate());
        String U_AT_ROOT_FILE_EXISTS = controller.getMsgString("U_AT_ROOT_FILE_EXISTS", tempVec);

        tempVec.clear();
        tempVec.add(tempDetails.getDate());
        String U_AT_ROOT_NO_FILE = controller.getMsgString("U_AT_ROOT_NO_FILE", tempVec);

        tempVec.clear();
        String APPROVAL_PENDING = controller.getMsgString("APPROVAL_PENDING");
        
        String APPROVAL_PENDING_EDITING_PRIVILEGES = controller.getMsgString("APPROVAL_PENDING_EDITING_PRIVILEGES");
        
        tempVec.clear();
        tempVec.add(tempDetails.getParentPageName());
        String INTERNAL_NODE = controller.getMsgString("INTERNAL_NODE", tempVec);
        
        String NO_PERMISSIONS = controller.getMsgString("NO_PERMISSIONS");
        

        if(tempDetails.getStatus().equals("NOT_CHKD_OUT")) {
            nodeDetailsArea.setText(NOT_CHKD_OUT);
            setDownloadItemsEnabled(true);
        } else if(tempDetails.getStatus().equals("CHKD_BY_ELSE")) {
            nodeDetailsArea.setText(CHKD_BY_ELSE);
            setDownloadItemsEnabled(false);
        } else if(tempDetails.getStatus().equals("CHKD_BY_U_INTERNAL")) {
            nodeDetailsArea.setText(CHKD_BY_U_INTERNAL);
            setDownloadItemsEnabled(false);
        } else if(tempDetails.getStatus().equals("CHKD_BY_U_AT_ROOT_FILE_EXISTS")) {
            nodeDetailsArea.setText(U_AT_ROOT_FILE_EXISTS);
            setDownloadItemsEnabled(true);
        } else if(tempDetails.getStatus().equals("CHKD_BY_U_AT_ROOT_FILE_DOESNT_EXIST")) {
            nodeDetailsArea.setText(U_AT_ROOT_NO_FILE);
            setDownloadItemsEnabled(true);
        } else if(tempDetails.getStatus().equals("APPROVAL_PENDING")) {
            nodeDetailsArea.setText(APPROVAL_PENDING);
            setDownloadItemsEnabled(false);
        } else if (tempDetails.getStatus().equals("APPROVAL_PENDING_EDITING_PRIVILEGES")) {
            nodeDetailsArea.setText(APPROVAL_PENDING_EDITING_PRIVILEGES);
            setDownloadItemsEnabled(true);            
        } else if (tempDetails.getStatus().equals("INTERNAL_NODE")) {
            nodeDetailsArea.setText(INTERNAL_NODE);
            setDownloadItemsEnabled(true);
        } else if (tempDetails.getStatus().equals("NO_PERMISSIONS")) {
            nodeDetailsArea.setText(NO_PERMISSIONS);
            setDownloadItemsEnabled(false);
        }
    }

    /**
     * Pops up an appropriate error based on the root element passed-in
     *
     * @param root The root element to check
     */
    private void handleDownloadError(Element root) {
        String errorString = new NodeNameSearcher().getSearchErrorString(root);
        searchField.requestFocus();
        setDownloadItemsEnabled(false);
        JOptionPane.showMessageDialog(Controller.getController().getManagerFrame(), errorString, "Search Error",
                                                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Enables or disables the download-related UI widgets
     *
     * @param val Whether to enable or disable the widgets
     */
    private void setDownloadItemsEnabled(boolean val) {
        downloadButton.setEnabled(val);
        depthField.setEnabled(val);
        depthLabel.setEnabled(val);
    }
    
    /**
     * Renderer subclass that shows the node name in a list
     */
    private class NodeDetailsCellRenderer extends DefaultListCellRenderer {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8954497093013834835L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ((JLabel) result).setText(((NodeDetails) value).getNodeName());
            return result;
        }  
    }    
}
