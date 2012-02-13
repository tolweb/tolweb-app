/*
 * FileManager.java
 *
 * Created on July 11, 2003, 2:13 PM
 */
package org.tolweb.treegrow.main;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import org.jdom.*;
import org.jdom.input.*;
import org.apache.commons.httpclient.*;
import org.tolweb.base.http.BaseHttpRequestMaker;
import org.tolweb.treegrow.tree.*;

/**
 * Class that is responsible for managing the various data files the application
 * uses. Also responsible for file interaction with the server, i.e. checking
 * in, checking out, determining if files are locked, etc. Responsible for
 * preferences as well.
 */
public class FileManager {
    private Controller controller;

    private PreferenceManager preferenceManager;

    private Hashtable userMsgMapping;

    private String currentDirString;

    private String dataDirString, imageDirString, recycleDirString,
            systemDirString;

    private File configFile;

    private Vector localDataFiles;

    private static final String MESG_FILE_NAME = "UserMsgNew2.xml";

    private static final String CONFIG_FILE_NAME = "settings.xml";

    private static final String OPEN_CDATA = "<![CDATA[";

    private static final String CLOSE_CDATA = "]]>";

    public static final String BEGIN_IMG_COMMENT = "<!-- Do not alter this content -->";

    public static final String END_IMG_COMMENT = "<!-- Ok to alter after this -->";

    /** Creates a new instance of FileManager */
    public FileManager(Controller con) {
        String fileSep = System.getProperty("file.separator");
        currentDirString = System.getProperty("user.dir");
        controller = con;
        preferenceManager = con.getPreferenceManager();

        System.out.println("preferencemanager is: " + preferenceManager);

        int subdir = currentDirString.indexOf(fileSep + "Resources" + fileSep
                + "SysFiles");
        if (subdir != -1) {
            currentDirString = currentDirString.substring(0, subdir);
        }

        currentDirString = currentDirString + fileSep + "Resources" + fileSep;
        systemDirString = currentDirString + "SysFiles" + fileSep;
        createDirIfNecessary(systemDirString);
        dataDirString = currentDirString + "DataFiles" + fileSep;
        createDirIfNecessary(dataDirString);
        imageDirString = currentDirString + "Images" + fileSep;
        createDirIfNecessary(imageDirString);
        recycleDirString = currentDirString + "Recycled" + fileSep;
        createDirIfNecessary(recycleDirString);
        userMsgMapping = new Hashtable();
        fetchUserMessages();
        configFile = new File(systemDirString + CONFIG_FILE_NAME);
    }

    /**
     * Fetches the message Strings the application uses into memory
     */
    public void fetchUserMessages() {
        String fileName = systemDirString + MESG_FILE_NAME;
        userMsgMapping = UserMessageController.buildMessageXMLFile(fileName);
    }

    /**
     * Returns whether the configuration file exists on the local machine
     * 
     * @return Whether the configuration file exists on the local machine
     */
    public boolean configFileExists() {
        return configFile.exists();
    }

    public File getConfigFile() {
        return configFile;
    }

    /**
     * Creates the configuration file on the local disk. Initialize values to
     * defaults.
     */
    public void createConfigFile() {
        try {
            configFile.createNewFile();
        } catch (Exception ex) {
        }
        preferenceManager.setUseCustCursors(true);

        long serverSupportJarTime = 0, serverEditorJarTime = 0;
        try {
            String baseURL = "http://editor.tolweb.org/";
            String editorJar = "ToLEditor.jar";
            String supportJar = "ToLSupportFiles.jar";

            URL editorJarURL = new URL(baseURL + editorJar);
            URLConnection editorJarConn = editorJarURL.openConnection();
            serverEditorJarTime = editorJarConn.getLastModified();

            URL supportJarURL = new URL(baseURL + supportJar);
            URLConnection supportJarConn = supportJarURL.openConnection();
            serverSupportJarTime = supportJarConn.getLastModified();
        } catch (Exception e) {
            preferenceManager.writePreferencesToDisk();
        }

        preferenceManager.setEditorJarTimestamp(serverEditorJarTime);
        preferenceManager.setSupportJarTimestamp(serverSupportJarTime);
        preferenceManager.writePreferencesToDisk();
    }

    /**
     * Returns the user message associated with the passed-in message name
     * 
     * @param messageName
     *            The name of the message to look for
     * @return The user message associated with the message name
     */
    public String getMessageForName(String messageName) {
        return (String) userMsgMapping.get(messageName);
    }

    /**
     * Returns the path to the data directory
     * 
     * @return the path to the data directory
     */
    public String getDataPath() {
        return dataDirString;
    }

    /**
     * Returns the path to the system directory
     * 
     * @return the path to the system directory
     */
    public String getSystemDir() {
        return systemDirString;
    }

    /**
     * Returns the path to the images directory
     * 
     * @return the path to the images directory
     */
    public String getImagePath() {
        return imageDirString;
    }

    /**
     * Reads in the necessary metadata from a local file in order to determine
     * if a redownload is necessary
     * 
     * @param file
     *            The filename of the XML file to open
     * @return true if the necessary values were present, false otherwise
     */
    public boolean getMetadataFromFile(String file) {
        TreeGrowXMLReader reader = constructXMLReaderForFileNotRoot(file);
        return reader.fetchTreeMetadata();
    }

    public int getDownloadIdFromFile(String file) {
        TreeGrowXMLReader reader = constructXMLReaderForFileNotRoot(file);
        return reader.fetchDownloadId();
    }

    private TreeGrowXMLReader constructXMLReaderForFileNotRoot(String file) {
        String fileName;
        if (file.indexOf(dataDirString) == -1) {
            String filenameNoExtension = file.substring(0, file
                    .lastIndexOf('.'));

            fileName = dataDirString + filenameNoExtension + "/" + file;
        } else {
            fileName = file;
        }
        TreeGrowXMLReader reader = new TreeGrowXMLReader(fileName, false);
        return reader;
    }

    /**
     * Checks whether the open file has a current download id
     * 
     * @param downloadId
     *            TODO
     * 
     * @return The response from the server TRUE if it is, FALSE otherwise
     */
    public String isVersionCurrent(int downloadId) {
        CheckNetConnection check = new CheckNetConnection();
        if (check.isConnected() > 0) {
            String tempString = null;
            try {
                Hashtable additionalArgs = new Hashtable();
                additionalArgs.put(RequestParameters.DOWNLOAD_ID, ""
                        + downloadId);
                URL url = new URL(HttpRequestMaker.getServerUtilsUrlString(
                        RequestParameters.CURRENT_DOWNLOAD, additionalArgs));
                //System.out.println(url);
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(url);
                Element rootElmt = doc.getRootElement();
                tempString = rootElmt.getTextTrim();
            } catch (Exception e) {
                return null;
            }
            return tempString;
        } else {
            return null;
        }
    }

    /**
     * Checks in the subtree rooted at rootNodeId using the downloadId
     * associated with the current download
     * 
     * @param rootNodeId
     */
    public boolean checkInSubtree(int rootNodeId) {
        if (rootNodeId == TreePanel.getTreePanel().getTree().getRoot().getId()) {
            return checkIn();
        }
        String result;
        Hashtable args = new Hashtable();
        args
                .put(RequestParameters.DOWNLOAD_ID, controller.getDownloadId()
                        + "");
        args.put(RequestParameters.ROOTNODE_ID, "" + rootNodeId);
        String urlString = HttpRequestMaker.getExternalUrlString("Checkin",
                args);
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new URL(urlString));
            Element root = doc.getRootElement();
            result = root.getTextTrim();
        } catch (Exception e) {
            return false;
        }
        if (result.equals(XMLConstants.SUCCESS)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks in the currently open file
     * 
     * @return true if checkin was successful, false otherwise
     */
    public boolean checkIn() {
        return checkIn(controller.getDownloadId());
    }

    /**
     * Checks in a file with the passed-in downloadId
     * 
     * @param downloadId
     *            The downloadId of the file to check in
     * @return true if checkin was successful, false otherwise
     */
    public boolean checkIn(int downloadId) {
        System.out.println("calling checkin with downloadId: " + downloadId);
        return checkIn(downloadId, controller.getFileName());
    }

    /**
     * Checks in the file with passed-in downloadId and fileName.
     * 
     * @param downloadId
     *            The download id to check in
     * @param fileName
     *            The name of the file on the local machine
     * @return true if no error occurred, false otherwise
     */
    public boolean checkIn(int downloadId, String fileName) {
        if (downloadId <= 0) {
            // At this point, there isn't a real download, there was an error
            // during download, so just return (hitting the server at this point
            // will do no good).
            return true;
        }
        System.out.println("calling checkin with downloadId: " + downloadId
                + " and filename: " + controller.getFileName());
        try {
            String file = null;
            if (fileName != null) {
                file = fileName + Controller.EXTENSION;
            }

            CheckNetConnection check = new CheckNetConnection();
            if (check.isConnected() > 0) {
                Hashtable args = new Hashtable();
                args.put(RequestParameters.DOWNLOAD_ID, "" + downloadId);
                String checkStr = HttpRequestMaker.getExternalUrlString(
                        "Checkin", args);
                String tempString = null;
                try {
                    byte[] bytes = HttpRequestMaker.makeHttpRequest(checkStr);
                    if (bytes == null) {
                        String CANT_CHECK_IN = controller
                                .getMsgString("CANT_CHECK_IN");
                        JOptionPane.showMessageDialog(controller
                                .getManagerFrame(), CANT_CHECK_IN, "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    System.out.println("response is: " + new String(bytes));
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder
                            .build(new ByteArrayInputStream(bytes));
                    Element Root = doc.getRootElement();
                    tempString = Root.getTextTrim();
                } catch (JDOMException e) {
                    e.printStackTrace();
                    return false;
                }

                if (tempString == null
                        || tempString.equals(XMLConstants.FAILURE)) {
                    System.out.println("failure on checkin!!");
                    return false;
                }

                if (controller.getBatchId() != -1) {
                    boolean result = unsubmitBatch(controller.getBatchId());
                    if (result) {
                        controller.setBatchId(-1);
                    } else {
                        return false;
                    }
                }

                if (file == null) {
                    return true;
                }

                boolean success = deleteFile(file);

                if (!success) {
                    System.out.println("problems deleting file: " + file);
                    return false;
                }

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unsubmitBatch(int batchId) {
        boolean returnVal = true;
        Controller controller = Controller.getController();
        try {
            Hashtable args = new Hashtable();
            args.put(RequestParameters.BATCH_ID, "" + batchId);
            args.put(RequestParameters.UNSUBMIT, XMLConstants.ONE);
            String url = HttpRequestMaker.getExternalUrlString(
                    "BatchSubmission", args);
            byte[] bytes = HttpRequestMaker.makeHttpRequest(url);
            if (bytes == null) {
                String PROBLEM_REVERT_APPROVAL = controller
                        .getMsgString("PROBLEM_REVERT_APPROVAL");
                JOptionPane.showMessageDialog(Controller.getController()
                        .getManagerFrame(), PROBLEM_REVERT_APPROVAL, "Error",
                        JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException();
            }
            Element root = new SAXBuilder().build(
                    new ByteArrayInputStream(bytes)).getRootElement();
            boolean result = UserValidationChecker.checkValidation(root);
            if (!result) {
                returnVal = false;
            }
            if (root.getTextTrim().equals(XMLConstants.FAILURE)) {
                returnVal = false;
            } else {
                System.out.println("successfully reverted");
            }
        } catch (Exception ex) {
            returnVal = false;
        }
        return returnVal;
    }

    /**
     * Moves the file located at the path parameter to the recycle bin directory
     * 
     * @param file
     *            The path to the file to move
     */
    public void moveToRecycleBin(String file) {
        try {
            String tempFile = file;
            String fileName = null;
            String filenameNoExtension = file.substring(0, file
                    .lastIndexOf('.'));
            if (file.indexOf(dataDirString) == -1) {
                fileName = dataDirString + filenameNoExtension + "/" + file;
            } else {
                fileName = file;
            }

            File tempfile = new File(fileName);
            if (!tempfile.exists()) {
                return;
            }
            String filenameNoPath = file.substring(file.lastIndexOf("/") + 1);
            File newFile = new File(recycleDirString + filenameNoPath);
            boolean success = tempfile.renameTo(newFile);
            if (success) {
                File directory = new File(dataDirString
                        + filenameNoPath.substring(0, filenameNoPath
                                .lastIndexOf('.')));
                File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
                directory.delete();
            }
            return;
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Deletes the currently open file in the tree editor and its associated
     * images
     */
    public boolean deleteFile() {
        return deleteFile(controller.getFileName() + Controller.EXTENSION);
    }

    /**
     * Deletes the local xml file and images associated with the file with the
     * passed-in name
     * 
     * @param file
     *            The name of the file to delete
     */
    public boolean deleteFile(String file) {
        try {
            if (file == null) {
                return false;
            }

            String filenameNoExtension = file.substring(0, file
                    .lastIndexOf('.'));
            String fileName = null;

            if (file.indexOf(dataDirString) == -1) {
                fileName = dataDirString + filenameNoExtension + "/" + file;
            } else {
                fileName = file;
            }

            File tempFile = new File(fileName);
            if (!tempFile.exists()) {
                return false;
            }

            boolean success = tempFile.delete();

            success = deleteImageFiles(dataDirString + filenameNoExtension);
            return success;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes all images for the open file
     */
    public boolean deleteImageFiles() {
        return deleteImageFiles(controller.getFileName());
    }

    /**
     * Deletes all images associated with the file located at the fileName path
     * 
     * @param fileName
     *            The path to the file to delete images from
     */
    public boolean deleteImageFiles(String fileName) {
        try {
            boolean success = true;
            File fileimgName = new File(fileName);
            File files[] = fileimgName.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return !name.endsWith(".xml");
                }
            });
            if (files == null) {
                return false;
            }

            int tempSize = 0;
            int size = 0;
            size = files.length;

            while (tempSize < size) {
                success = files[tempSize].delete();
                if (success == false) {
                    success = files[tempSize].delete();
                }
                tempSize++;
            }
            success = fileimgName.delete();

            return success;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Looks for the file named fileName in the images directory
     * 
     * @param fileName
     *            The fileName of the image
     * @return A File Object representing that image
     */
    public File getImageNamed(String fileName) {
        return new File(getImageDirName(), fileName);
    }

    /**
     * Translates between local images and urls and tries to return the path to
     * the local path for a file
     * 
     * @param url
     *            The url string for the image
     * @return The local path for the image
     */
    public String getLocalImageWithUrl(String url) {
        if (url != null) {
            if (url.startsWith("http://")) {
                String prefixString = controller.getWebPath();
                String name = url.substring(prefixString.length() - 1);
                String newName = changeSlash(name);
                return getImageDirName() + newName;
            } else if (url.startsWith("file://")) {
                return url.substring("file://".length());
            } else {
                return url;
            }
        } else {
            return "";
        }
    }

    public String getLocalImageFromNodeImage(NodeImage img) {
        if (img.getId() > 0) {
            return getLocalImagePathForId(img.getId() + "");
        } else {
            return img.getLocation();
        }
    }

    public String getLocalImagePathForId(String id) {
        return getImageDirName() + id + ".jpg";
    }

    /**
     * Returns the name of the image directory
     * 
     * @return The name of the image directory
     */
    public String getImageDirName() {
        return getDataPath() + controller.getFileName() + "/";
    }

    /**
     * Takes some xml tag and turns the content inside of it to a cdata
     */
    private String makeSectionCDATA(String xmlString, String tagName) {
        String openTagString = "<" + tagName + ">";
        String endTagString = "</" + tagName + ">";
        int startDataIndex = xmlString.indexOf(openTagString)
                + openTagString.length();
        int endDataIndex = xmlString.indexOf(endTagString);
        String firstPart = xmlString.substring(0, startDataIndex);
        String lastPart = xmlString.substring(endDataIndex);
        String futureCDATA = xmlString.substring(startDataIndex, endDataIndex);
        futureCDATA = OPEN_CDATA + futureCDATA + CLOSE_CDATA;

        return firstPart + futureCDATA + lastPart;
    }

    /**
     * Copies a file from source path to destination path
     * 
     * @param source
     *            The source path containing the file
     * @param dest
     *            The destination path to write the file to
     */
    private void copyFile(String source, String dest) {
        try {
            File inputFile = new File(source);
            File outputFile = new File(dest);

            FileInputStream in = new FileInputStream(inputFile);
            FileOutputStream out = new FileOutputStream(outputFile);
            int c;

            while ((c = in.read()) != -1) {
                out.write(c);
            }

            in.close();
            out.close();
        } catch (Exception error) {
            return;
        }
    }

    public String changeSlash(String str) {
        try {
            if (str == null) {
                return str;
            }

            if (str.indexOf("/") == -1) {
                return str;
            }

            String newStr = "";
            String tempStr = str;

            while (tempStr.indexOf("/") != -1) {
                int loc = tempStr.indexOf("/");
                newStr += tempStr.substring(0, loc);
                newStr += "_";
                tempStr = tempStr.substring(loc + 1);
            }
            if (tempStr != null) {
                newStr += tempStr;
            }

            return newStr;
        } catch (Exception error) {
            return null;
        }
    }

    /**
     * Reads in all XML tree files stored locally and returns them
     * 
     * @return A vector of the filenames of local data files
     */
    public Vector getLocalDataFiles() {
        localDataFiles = null;
        File dataDir = new File(dataDirString);
        File files[] = dataDir.listFiles();
        if (files == null) { // error
            return new Vector();
        }

        int counter = 0;
        int fileSize = 0;
        int size = 0;
        size = files.length;

        localDataFiles = new Vector();
        if (size > 0) {
            while (counter < size) {
                File currentFile = files[counter++];
                if (!currentFile.isDirectory()) {
                    continue;
                }
                String[] subdirXMLFiles = currentFile
                        .list(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".xml");
                            }
                        });
                if (subdirXMLFiles.length > 0) {
                    localDataFiles.add(currentFile + File.separator + subdirXMLFiles[0]);
                }
            }
        }
        return localDataFiles;
    }
    
    public String getPathForOpenFile() {
        String fileName = controller.getFileName();
        String fullPath = dataDirString + fileName + "/" + fileName + ".xml";
        return fullPath;
    }

    public void fetchNewUserMessages() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String url = "http://treegrow.tolweb.org/" + MESG_FILE_NAME;
                try {
                    String xmlString = "";
                    File file = new File(systemDirString + MESG_FILE_NAME);
                    FileOutputStream fos = new FileOutputStream(file);
                    Credentials credentials = new UsernamePasswordCredentials(
                            NewVersionDownloader.USERNAME,
                            NewVersionDownloader.PASSWORD);
                    byte[] bytes = BaseHttpRequestMaker.makeHttpRequest(url,
                            credentials, NewVersionDownloader.REALM);
                    try {
                        // Try and construct a document from the response. If it
                        // is
                        // valid XML then go ahead and write out to disk,
                        // otherwise skip the writing
                        new SAXBuilder().build(new ByteArrayInputStream(bytes));
                        fos.write(bytes);
                    } catch (Exception e) {
                        fos.close();
                    }
                    UserMessageController.buildMessageXMLFile(systemDirString
                            + MESG_FILE_NAME);
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * Utility method that creates a directory if one doesn't already exist
     * 
     * @param dirString
     *            The path to the new directory to create
     */
    private void createDirIfNecessary(String dirString) {
        File dir = new File(dirString);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}