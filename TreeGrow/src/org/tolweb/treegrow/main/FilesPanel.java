/*
 * filesPanel.java
 *
 * Created on August 19, 2003, 9:31 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

import org.jdom.*;
import org.jdom.input.*;

import org.tolweb.treegrow.page.*;

/**
 * JPanel subclass that is the center tab on the ToLManager frame
 */
public class FilesPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3813639304032800339L;
	private Vector files;
    private JButton openButton;
    private FilesTable table;
    private Hashtable rootNodeIds = new Hashtable();
    
    /** Creates a new instance of filesPanel */
    public FilesPanel() {
        initializeMetadataObjects();
        double[][] size = new double[][] {{10, TableLayoutConstants.FILL, 10}, {.5, .5}};
        setLayout(new TableLayout(size));
        size = new double[][] { {TableLayoutConstants.FILL}, {TableLayoutConstants.FILL, 10, 30}};
        JPanel topPanel = new JPanel(new TableLayout(size));
        openButton = new JButton("Open");        
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openButton.setEnabled(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Thread thread = new Thread() {
                    public void run() {
                        boolean result = openSelected();
                        if (!result) {
                            JOptionPane.showMessageDialog(Controller.getController().getManagerFrame(), "Problems opening file"); 
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                };
                SwingUtilities.invokeLater(thread);
            }
        });
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        //buttonsPanel.add(undoLastUploadButton);
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(BorderLayout.WEST, openButton);
        buttonsPanel.add(BorderLayout.EAST, containerPanel);
        String tooltipText;
        if (CheckNetConnection.hasConnection()) {
            tooltipText = "LOCAL_FILES_TOOLTIP_CONN";
        } else {
            tooltipText = "LOCAL_FILES_TOOLTIP_NO_CONN";
        }        
        JLabel tooltip = Controller.getController().getLightbulbLabel(tooltipText);
        buttonsPanel.add(BorderLayout.WEST, tooltip);
        topPanel.add(buttonsPanel, "0,2");
        table = new FilesTable(this);
        topPanel.add(Controller.getController().updateUnitIncrement(new JScrollPane(table)), "0,0");
        if (files != null && files.size() > 0) {
            setButtonsEnabled(true);
            table.setRowSelectionInterval(0, 0);
        }
        add(topPanel, "1,0");
    }
    
    public void updateFilesTable() {
        getTable().fireTableDataChanged();
        revalidate();
        repaint();    
        // In case there are no longer any local files
        Controller.getController().getManagerFrame().getDownloadPanel().resetTooltipText();
    }
    
    
    /**
     * Builds up the list of local file objects and files on the server that
     * this user can edit.  Called during construction and after a redownload
     * of a file due to an undo
     */
    public void initializeMetadataObjects() {
        files = new Vector();
        rootNodeIds.clear();
        Iterator it = Controller.getController().getFileManager().getLocalDataFiles().iterator();
        while (it.hasNext()) {
            files.add(initMetadataObject((String) it.next()));
        }
        if (table != null) {
            table.setList(files);
        }
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                mergeCheckedOutFiles();
                return null;
            }
            
            public void finished() {
                table.setList(getFiles());
                table.repaint();
            }
        };
        Controller controller = Controller.getController();
        CheckNetConnection checker = new CheckNetConnection();
        if (!controller.hasConnection() || checker.isConnected() <= 0) {
            String NO_CONNECTION_INCOMPLETE_LIST = controller.getMsgString("NO_CONNECTION_INCOMPLETE_LIST");
            JOptionPane.showMessageDialog(controller.getManagerFrame(), NO_CONNECTION_INCOMPLETE_LIST, "No connection", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            worker.start();
        }
    }
    
    /**
     * Return the list of files that the user can interact with
     *
     * @return The list of files that the user can interact with
     */
    public Vector getFiles() {
        return files;
    }
    
    /**
     * Used to enable/disable the buttons on the panel
     *
     * @param value Whether to enable or disable the panel buttons
     */
    private void setButtonsEnabled(boolean value) {
        openButton.setEnabled(value);
    }
    
    /**
     * Called so that files that aren't present on the local machine can't
     * attempt to be opened or uploaded
     */
    private void disableLocalButtons() {
        openButton.setEnabled(false);
    }
    
    public void refreshFile(FileMetadata localFile) {
        int index = files.indexOf(localFile);
        files.remove(localFile);
        files.add(index, initMetadataObject(localFile.filePath));
        table.fireTableDataChanged();
        setCursor(Cursor.getDefaultCursor());                        
    }
    
    /**
     * Gets rid of the currently selected file.  Usually called after a checkin
     */
    private void removeSelectedFileFromTable() {
        int localFileIndex = table.getSelectedRow();
        if (localFileIndex == -1) {
            return;
        }
        FileMetadata localFile = (FileMetadata) files.get(localFileIndex);
        table.deleteSelected();
        table.fireTableDataChanged();
        repaint();
    }
    
    public String getSelectedFilePath() {
        int localFileIndex = table.getSelectedRow();
        if (localFileIndex == -1) {
            return "";
        }
        FileMetadata localFile = (FileMetadata) files.get(localFileIndex);
        return localFile.filePath;
    }
    
    /**
     * Removes the passed-in file from the table
     *
     * @param the file to remove from the table
     */
    public void removeFileFromTable(FileMetadata file) {
        int index = files.indexOf(file);
        table.setRowSelectionInterval(index, index);
        removeSelectedFileFromTable();
    }
    
    /**
     * Returns the table
     *
     * @return the table
     */
    public FilesTable getTable() {
        return table;
    }
    
    /**
     * Opens the selected file
     */
    private boolean openSelected() {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        FileMetadata localFile;
        if (table.getSelectedRow() == -1) {
            return false;
        } else {
            localFile = (FileMetadata) files.get(table.getSelectedRow());
        }
        String fileName = localFile.filePath;
        File tempFile = new File(fileName);
        if( tempFile.exists() ) {
            //check if connected to the net.
            CheckNetConnection check = new CheckNetConnection();

            if(!controller.hasConnection() || check.isConnected() <= 0) { // not connected  
                //inform user that there is no net connection
                System.out.println("thinks no net connections");
                Object[] options = {"Yes" ,"No"};
                String CANT_CONFIRM_CURR_VER = controller.getMsgString("CANT_CONFIRM_CURR_VER");
                int n = JOptionPane.showOptionDialog(controller.getManagerFrame(),
                                                     CANT_CONFIRM_CURR_VER,
                                                     "Message",
                                                     JOptionPane.YES_NO_OPTION,
                                                     JOptionPane.WARNING_MESSAGE,
                                                     null,
                                                     options,
                                                     options[1]);

                if (n == JOptionPane.YES_OPTION) {
                    //open the file
                    FileReaderThread readerThread = new FileReaderThread(fileName);
                    readerThread.start();                    
                    return true;
                } else if (n == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
            System.out.println("have a net connection");
            //connected to the net.
            //pass the download id to the server and check if this is
            //the current download version
            boolean result = fileManager.getMetadataFromFile(fileName);
            if(!result) {
                System.out.println("bad metadata in the file!");
                return false;
            }
            int downloadId = fileManager.getDownloadIdFromFile(fileName);
            String current = fileManager.isVersionCurrent(downloadId);

            if((current != null) && (current.equals(XMLConstants.TRUE))) {
                // handle current file function
                // check for unlocked nodes and then open file
                FileReaderThread readerThread = new FileReaderThread(fileName);
                readerThread.start();
                
                return true;
            } else {
                if (current != null) {
                    //the file is not current
                    //check for unuploaded changes
                    if(!controller.getFileChanged()) {
                        //file has not been changed
                        //remove file, checkin and start download

                        Object[] options = {"Yes" ,"No"};
                        String DIFF_VER_STILL_DOWNLOAD = controller.getMsgString("DIFF_VER_STILL_DOWNLOAD");
                        int n = JOptionPane.showOptionDialog(controller.getManagerFrame(),
                                                             DIFF_VER_STILL_DOWNLOAD,
                                                             "Message",
                                                             JOptionPane.YES_NO_OPTION,
                                                             JOptionPane.WARNING_MESSAGE,
                                                             null,
                                                             options,
                                                             options[1]);

                        if (n == JOptionPane.YES_OPTION) {
                            //remove file and do download
                            fileManager.getMetadataFromFile(fileName);
                            boolean success = fileManager.checkIn();
                            if(!success) {
                                String CANT_CHECK_IN = controller.getMsgString("CANT_CHECK_IN");
                                JOptionPane.showMessageDialog(controller.getManagerFrame(), CANT_CHECK_IN, "Cant Check In", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                            Controller.getController().setDepth(1);
                            DataDownloader down = new DataDownloader(controller.getNodeId(), false);
                            down.startFullDownload();
                            return true;
                        } else  {
                            //remove file and return
                            fileManager.getMetadataFromFile(fileName);
                            fileManager.moveToRecycleBin(fileName);
                            
                            String MOVING_RECYCLE_BIN = controller.getMsgString("MOVING_RECYCLE_BIN");
                            JOptionPane.showMessageDialog(
                                                          controller.getManagerFrame(),
                                                          MOVING_RECYCLE_BIN,
                                                          "Message Window",
                                                          JOptionPane.INFORMATION_MESSAGE);
                            removeSelectedFileFromTable();
                            return false;
                        }
                    }

                    System.out.println("thinks that it's an out of date file and thinks there are changes");
                    
                    //the file has been changed
                    String BAD_FILE_UPLOAD = controller.getMsgString("BAD_FILE_UPLOAD");
                    String EXPORT_MOVE_RECYCLE = controller.getMsgString("EXPORT_MOVE_RECYCLE");
                    String MOVE_RECYCLE = controller.getMsgString("MOVE_RECYCLE");
                    String[] choices = new String[] { MOVE_RECYCLE };
                    int response = RadioOptionPane.showRadioButtonDialog(controller.getTreeEditor(), "What would you like to do?", BAD_FILE_UPLOAD, choices);
                    System.out.println("response = " + response);
                    if (response == 0) {
                        //ExportFrame exportForm = new ExportFrame(controller.getManagerFrame());
                        //exportForm.show();
                        fileManager.moveToRecycleBin(fileName);
                    } else {
                        fileManager.moveToRecycleBin(fileName);
                    }
                    removeSelectedFileFromTable();
                    System.out.println("thinks the version isn't current: " + current);
                    return false;                    
                }
            }

        } else {
            String NO_FILE_GIVEN_LOC = controller.getMsgString("NO_FILE_GIVEN_LOC");
            JOptionPane.showMessageDialog(controller.getManagerFrame(), NO_FILE_GIVEN_LOC,
                                          "Error Message",
                                          JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
       
    /**
     * Fetches files from the server that may not be on the local machine and
     * merges them into the list so that they may be checked in if users so
     * desire.
     */
    private void mergeCheckedOutFiles() {
        Controller controller = Controller.getController();
        try {
            String url = HttpRequestMaker.getServerUtilsUrlString(RequestParameters.CHECKED_OUT_FILES);
            byte[] bytes = HttpRequestMaker.makeHttpRequest(url);
            if (bytes == null) {
                return;
            }
            Element root = new SAXBuilder().build(new ByteArrayInputStream(bytes)).getRootElement();            
            boolean result = UserValidationChecker.checkValidation(root);
            if (!result) {
                return;
            }
            Iterator it = root.getChildren(XMLConstants.CHECKED_OUT_FILE).iterator();
            Date date;
            while (it.hasNext()) {
                Element currentFile = (Element) it.next();
                String rootNodeId = currentFile.getAttributeValue(XMLConstants.ROOTNODE_ID);
                String fileName = currentFile.getAttributeValue(XMLConstants.FILENAME);
                if (rootNodeIds.containsKey(rootNodeId)) {
                    continue;
                }
                String downloadId = currentFile.getAttributeValue(XMLConstants.DOWNLOAD_ID);
                String dateString = currentFile.getAttributeValue(XMLConstants.DATE_TIME);
                dateString = controller.parseMySQLDate(dateString);
                
                FileMetadata metadata = new FileMetadata(fileName, null, "File not present", "File not present", dateString, Integer.parseInt(downloadId));
                files.add(metadata); 
                rootNodeIds.put(rootNodeId, metadata);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
      
    /**
     * Creates a new FileMetadata object for the local file located at fileName
     *
     * @param fileName the path to the local file
     * @return The FileMetadata object corresponding to that path
     */
    private FileMetadata initMetadataObject(String fileName) {
        try {
            FileMetadata newMetadata = TreeGrowXMLReader.parseFileMetadataFromFile(fileName);
            String rootIdString = newMetadata.rootNodeId + "";
            if (rootNodeIds.containsKey(rootIdString)) {
                // Here, there was already a non-existent file entry in the list, so we need
                // to remove it since the file does exist locally
                Iterator it = files.iterator();
                while (it.hasNext()) {
                    FileMetadata metadata = (FileMetadata) it.next();
                    if (metadata.fileName.equals(fileName)) {
                        files.remove(metadata);
                    }
                }
            } else {
                rootNodeIds.put(rootIdString, newMetadata);
            }
            return newMetadata;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }  
    
    /**
     * AbstractDraggableTable subclass that contains information about
     * files the user has checked out
     */
    class FilesTable extends AbstractDraggableTable {
        /**
		 * 
		 */
		private static final long serialVersionUID = -268688693281914724L;
		// This is theoretically not necessary, but Im having nullpointer problems
        private FilesPanel panel;
        private boolean callRecursively = true;
        
        public FilesTable(FilesPanel p) {
            super(new Vector());
            if (files != null) {
                setList(files);
            }
            panel = p;
            setModel(new FilesTableModel(this));
            initIndexColumn();
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1 && openButton.isEnabled()) {
                        openButton.doClick();
                    }
                }
            });
            //getColumnModel().setColumnMargin(getColumnModel().getColumnMargin() + 1);
            //Dimension d = getPreferredSize();
            //setPreferredSize(new Dimension(d.width, d.height + 10));
        }
        
        public boolean respondToMouseEvents() {
            return false;
        }
        
        public void fireTableDataChanged() {
            ((AbstractTableModel) getModel()).fireTableDataChanged();
        }
        
        public void valueChanged(ListSelectionEvent e) {
            int selectedRow = getSelectedRow();
            repaint();
            if (panel != null) {
                if (selectedRow == -1) {
                    panel.setButtonsEnabled(false);
                } else {
                    panel.setButtonsEnabled(true);
                    FileMetadata metadata = (FileMetadata) files.get(selectedRow);
                    if (!metadata.isLocal()) {
                        panel.disableLocalButtons();
                    }
                }
            }
        }
  
        private class FilesTableModel extends AbstractDraggableTable.AbstractEditableTableModel {
            /**
			 * 
			 */
			private static final long serialVersionUID = -4274958621517000928L;

			public FilesTableModel(FilesTable t) {
                super(t);
            }
            
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            public int getColumnCount() {
                return 5;
            }
            
            public Object getValueAt(int row, int col) {
                if (row >= files.size() || row < 0) {
                    return "";
                }
                FileMetadata metadata = (FileMetadata) files.get(row);
                switch(col) {
                    case 1: return metadata.rootNodeName;
                    case 2: return metadata.downloadDate;
                    case 3: return metadata.modifiedDate;
                    case 4: return metadata.uploadDate;
                    default: return "";
                }
            }
            
            public String getColumnName(int col) {
                switch(col) {
                    case 1: return "Group";
                    case 2: return "Checked Out";
                    case 3: return "Date Modified";
                    case 4: return "Most Recent Upload";
                    default: return "";
                }
            }           
        }
    }
}

