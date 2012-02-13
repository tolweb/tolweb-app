/*
 * UploadBatch.java
 *
 * Created on September 29, 2003, 1:43 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import org.jdom.*;
import org.jdom.input.*;

/**
 * Class used to store data about an UploadBatch.  An UploadBatch is a sequence
 * of uploads that are somehow tied to one another and can involve multiple
 * users and pages
 */
public class UploadBatch {
    int batchId = -1;
    int rootId;
    String user;
    String rootGroup;
    String dateTime;
    ArrayList affectedPages;
    ArrayList downloadIDs;
    ArrayList localFiles;
    ArrayList changedLocalFiles;
    boolean submitted;
    boolean checkedOut;
    boolean canPushPublic;
    boolean isSoleAuthor;
    boolean hasOtherActiveDownload;

    public UploadBatch(int id, int rootGroupId, String user, String group, String dateString, ArrayList pages, ArrayList downIDs, boolean sub, boolean chkOut, boolean pushLive, boolean soleAuthor, boolean hasOtherActive) {
        batchId = id;
        rootId = rootGroupId;
        if (pages != null) {
            affectedPages = pages;
        } else {
            affectedPages = new ArrayList();
        }
        downloadIDs = downIDs;
        this.user = user;
        rootGroup = group;
        if (dateString != null) {
            dateTime = Controller.getController().parseMySQLDate(dateString);
        } else {
            dateTime = null;
        }
        submitted = sub;
        checkedOut = chkOut;
        canPushPublic = pushLive;
        isSoleAuthor = soleAuthor;
        hasOtherActiveDownload = hasOtherActive;
        localFiles = new ArrayList();
        changedLocalFiles = new ArrayList();
    }
    
    public boolean isUploaded() {
        return dateTime != null;
    }
    
    public boolean isChanged() {
        return changedLocalFiles.size() > 0;
    }
    
    /**
     * Checks in all of the local files that are associated with this
     * UploadBatch
     *
     * @param localFiles The local files associated with the batch
     * @return true if all checkins succeeded, false otherwise
     */
    public boolean doCheckIn() {
        boolean result;
        Controller controller = Controller.getController();
        if (isChanged()) {
            String UPLOAD_BEFORE_CHECKIN = controller.getMsgString("UPLOAD_BEFORE_CHECKIN");
            String UPLOAD_THEN_CHECKIN = controller.getMsgString("UPLOAD_THEN_CHECKIN");
            String CHECKIN_NO_UPLOAD = controller.getMsgString("CHECKIN_NO_UPLOAD");
            String CANCEL_NO_CHECKIN = controller.getMsgString("CANCEL_NO_CHECKIN");
            String[] options = new String[] { UPLOAD_THEN_CHECKIN, CHECKIN_NO_UPLOAD, CANCEL_NO_CHECKIN };
            int response = RadioOptionPane.showRadioButtonDialog(controller.getManagerFrame(), "Upload Before Checkin?", UPLOAD_BEFORE_CHECKIN, options);

            switch (response) {
                case 0:
                    // Upload
                    result = uploadAllForBatch();
                    if (!result) {
                        return false;
                    }
                case 1:
                    break;
                default:
                    // They cancelled, so return false
                    return false;
            }            
            result = checkInAllForBatch();
            if (!result) {
                return false;
            }
        } else {
            result = checkInAllForBatch();
            if (!result) {
                return false;
            }
        }
        // Now remove the files from the files table
        FilesPanel filesPanel = controller.getManagerFrame().getFilesPanel();
        Iterator it = localFiles.iterator();
        while (it.hasNext()) {
            filesPanel.removeFileFromTable((FileMetadata) it.next());
        }
        checkedOut = false;
        //checkedOutBatches.remove(ub);
        return true;        
    }
    
    /**
     * Uploads all changed files associated with this batch
     *
     * @param ub The uploadBatch to upload
     * @param changedFiles The local files to upload
     * @return Whether the upload and checkin were successful or not
     */
    public boolean doUploadAndCheckIn() {
        Controller controller = Controller.getController();
        String UPLOAD_TO_SUBMIT = controller.getMsgString("UPLOAD_TO_SUBMIT");
        String UPLOAD_THEN_SUBMIT = controller.getMsgString("UPLOAD_THEN_SUBMIT");
        String SUBMIT_NO_UPLOAD = controller.getMsgString("SUBMIT_NO_UPLOAD");
        String CANCEL_NO_SUBMIT = controller.getMsgString("CANCEL_NO_SUBMIT");
        String[] options;
        if (batchId != -1) {
            options = new String[] { UPLOAD_THEN_SUBMIT, SUBMIT_NO_UPLOAD, CANCEL_NO_SUBMIT };
        } else {
            options = new String[] { UPLOAD_THEN_SUBMIT, CANCEL_NO_SUBMIT };
        }
        int response = RadioOptionPane.showRadioButtonDialog(controller.getManagerFrame(), "Upload Local Files?", UPLOAD_TO_SUBMIT, options);
        
        switch (response) {
            case 0:
                // Upload
                boolean result = uploadAllForBatch();
                if (!result) {
                    return false;
                }
                // Reset the batch id in case there was previously no batch
                // id associated with the batch
                batchId = Controller.getController().getBatchId();
                canPushPublic = Controller.getController().canPushPublic();
            case 1:
                // If the batch was previously not uploaded, then this index
                // was the cancel index, not the submit no upload
                if (batchId != -1) {
                    return doCheckIn();
                }
            default:
                return false;
        }
    }
    
    private boolean uploadAllForBatch() {
        Iterator it = changedLocalFiles.iterator();
        while (it.hasNext()) {
            FileMetadata file = (FileMetadata) it.next();
            boolean result = UploadTree.doUploadDialog(file.filePath);
            if (!result) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkInAllForBatch() {
        Controller controller = Controller.getController();
        Iterator it = localFiles.iterator();
        while (it.hasNext()) {
            FileMetadata metadata = (FileMetadata) it.next();
            // We only want to check in if the local file is part of the upload batck to check in
            if (downloadIDs.contains(new Integer(metadata.downloadId))) {
                String fileName = null;
                if (metadata.isLocal()) {
                    fileName = metadata.fileName;
                }
                boolean result = controller.getFileManager().checkIn(metadata.downloadId, fileName);
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean revertWorking() {
        boolean doRevert = true;
        boolean returnValue = true;
        if (checkedOut) {
            doRevert = doCheckIn();
        }
        if (doRevert) {
            Controller controller = Controller.getController();
            try {
                byte[] bytes = HttpRequestMaker.makeHttpRequest(controller.getCGIPath() + "revertWorking.pl?user_id=" + controller.getUserName() + "&password=" + controller.getPassword() + "&batch_id=" + batchId);
                if (bytes == null) {
                    String PROBLEM_REVERT_ALL = controller.getMsgString("PROBLEM_REVERT_ALL");
                    JOptionPane.showMessageDialog(Controller.getController().getManagerFrame(), PROBLEM_REVERT_ALL, "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException();
                }
                Element root = new SAXBuilder().build(new ByteArrayInputStream(bytes)).getRootElement();
                boolean result = UserValidationChecker.checkValidation(root);
                if (!result) {
                    returnValue = false;
                }
                if (root.getTextTrim().equals(XMLConstants.FAILURE)) {
                    returnValue = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                returnValue = false;
            }
        }
        return returnValue;
    }
    
    private Hashtable getBatchIdHashtable() {
        Hashtable hash = new Hashtable();
        hash.put(RequestParameters.BATCH_ID, "" + batchId);
        return hash;
    }
    
    public boolean unsubmitBatch() {
        return Controller.getController().getFileManager().unsubmitBatch(batchId);
    }
    
    /**
     * Either submits the batch for editorial approval or pushes it live,
     * depending on whether the user is an editor or not
     *
     * @param ub The batch to submit
     * @param batchPanel The panel to update
     * @return Whether the submit was successful or not
     */
    public boolean doSubmit() {
        Controller controller = Controller.getController();
        boolean returnVal = true;
        try {
            boolean doSubmit = true;
            if (checkedOut) {
                if (changedLocalFiles.size() > 0) {
                    doSubmit = doUploadAndCheckIn();
                } else {
                    doSubmit = doCheckIn();
                }
                System.out.println("doSubmit = " + doSubmit);
            }
            String SUBMIT_TAKE_AWHILE = controller.getMsgString("SUBMIT_TAKE_AWHILE");
            JOptionPane.showMessageDialog(controller.getManagerFrame(), SUBMIT_TAKE_AWHILE, "Please Be Patient", JOptionPane.INFORMATION_MESSAGE);
            controller.getManagerFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (doSubmit && !canPushPublic) {
                returnVal = submitToEditor();
            } else if (doSubmit && canPushPublic) {
                Hashtable args = getBatchIdHashtable();
                String pushPublicUrl = HttpRequestMaker.getExternalUrlString("PushPublic", args);
                byte[] bytes = HttpRequestMaker.makeHttpRequest(pushPublicUrl);
                if (bytes == null) {
                    String PROBLEM_PUSHING_PUBLIC = controller.getMsgString("PROBLEM_PUSHING_PUBLIC");
                    JOptionPane.showMessageDialog(controller.getManagerFrame(), PROBLEM_PUSHING_PUBLIC, "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                Element root = new SAXBuilder().build(new ByteArrayInputStream(bytes)).getRootElement();
                boolean result = UserValidationChecker.checkValidation(root);
                if (!result) {
                    returnVal = false;
                }
                if (returnVal && root.getTextTrim().equals(XMLConstants.FAILURE)) {
                    returnVal = false;
                }
            } else {
                // There was a problem on upload or check in, so don't update UI
                returnVal = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            returnVal = false;
        }
        return returnVal;
    }
    
    /**
     * Pushes the batch to the approval database and updates the panel
     *
     * @param ub The batch to push to approval
     * @param batchPanel The panel to update
     * @return Whether the batch was successfully pushed to approval
     */
    public boolean submitToEditor() {
        boolean returnVal = true;
        try {
            Controller controller = Controller.getController();
            String url = HttpRequestMaker.getExternalUrlString("BatchSubmission", getBatchIdHashtable());
            byte[] bytes = HttpRequestMaker.makeHttpRequest(url);
            if (bytes == null) {
                String PROBLEM_PUSHING_PUBLIC = controller.getMsgString("PROBLEM_PUSHING_PUBLIC");
                JOptionPane.showMessageDialog(controller.getManagerFrame(), PROBLEM_PUSHING_PUBLIC, "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException();
            }
            String resultString = new String(bytes);
            resultString.length();
            Element root = new SAXBuilder().build(new ByteArrayInputStream(bytes)).getRootElement();
            boolean result = UserValidationChecker.checkValidation(root);
            if (!result) {
                returnVal = false;
            }
            submitted = true;
        } catch (Exception e) {
            e.printStackTrace();
            returnVal = false;
        }
        return returnVal;
    }
    
    public boolean hasLastUploadToUndo() {
        return false;
    }
    
    /**
     * Undoes the last upload associated with the upload batch
     *
     * @return Whether the last upload was successfully undone
     */
    public boolean undoLastUpload() {
        boolean returnVal = true;
        final Controller controller = Controller.getController();
        //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));       
        doCheckIn();
        byte[] bytes = HttpRequestMaker.makeHttpRequest(controller.getCGIPath() + "revertLastUpload.pl?batch_id=" + batchId + "&user_id="+ controller.getUserName() + "&password=" + controller.getPassword());
        if (bytes == null) {
            String PROBLEM_REVERT = controller.getMsgString("PROBLEM_REVERT");
            JOptionPane.showMessageDialog(controller.getManagerFrame(), PROBLEM_REVERT, "Error", JOptionPane.ERROR_MESSAGE);                
            return false;
        } 
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Element root = doc.getRootElement();
        boolean validateSuccessful = UserValidationChecker.checkValidation(root);
        if (!validateSuccessful) {
            returnVal = false;
        }
        if (returnVal) {
            String result = root.getTextTrim();           
            if (!result.equals(XMLConstants.SUCCESS)) {
                returnVal = false;
            }
        }
        // Something bad happened, so don't worry about the local files
        return returnVal;
    }      
    
    public void addToLocalFiles(FileMetadata localFile) {
        localFiles.add(localFile);
    }
    
    public void addToChangedLocalFiles(FileMetadata localFile) {
        changedLocalFiles.add(localFile);
    }
    
    /**
     * Shows the lost connection error if the connection to the server is lost
     * during some important server operation
     *
     * @param the operation that was being performed
     */
    private void showLostConnectionMsg(String operation) {
        Vector vec = new Vector(1);
        vec.add(operation);
        String LOST_CONNECTION_GENERIC = Controller.getController().getMsgString("LOST_CONNECTION_GENERIC", vec);
        JOptionPane.showMessageDialog(Controller.getController().getManagerFrame(), LOST_CONNECTION_GENERIC, "Lost Connection", JOptionPane.ERROR_MESSAGE);    
    }    
    
    public static UploadBatch createUnuploadedBatchObject(int rootNodeId, String rootNodeName, int downloadId) {
        ArrayList downloadIds = new ArrayList();
        downloadIds.add(new Integer(downloadId));
        UploadBatch batch = new UploadBatch(-1, rootNodeId, null, rootNodeName, null, null, downloadIds, false, true, false, false, false);
        return batch;
    }
}
