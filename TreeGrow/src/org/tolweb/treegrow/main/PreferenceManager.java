/*
 * PreferenceManager.java
 *
 * Created on March 11, 2004, 2:15 PM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.tolweb.base.xml.BaseXMLWriter;

/**
 * Class used for storing all of the information about the user and their
 * associated preferences
 */
public class PreferenceManager {
    private Hashtable preferences = new Hashtable();
    
    /** Creates a new instance of PreferenceManager */
    public PreferenceManager() {
        setUseCustCursors(true);
    }
    
    /**
     * Sets the username of the user editing the tree
     *
     * @param user The username
     */
    public void setUserName(String user) {
        System.out.println("username getting set to: " + user);
        preferences.put(XMLConstants.USERNAME, user);
    }

    /**
     * Returns the username of the user editing the tree
     *
     * @return username
     */
    public String getUserName() {
        return (String) preferences.get(XMLConstants.USERNAME);
    }    
    
    /**
     * Sets the MD5 hash of the user's password.  This is stored locally
     *
     * @param pw TheMD5 hash of the password
     */
    public void setPassword(String pw) {
        preferences.put(XMLConstants.PASSWORD, pw);
    }
    
    /**
     * Returns the local stored password, which is an MD5 of the pw on server
     *
     * @return the local stored password
     */
    public String getPassword() {
        return (String) preferences.get(XMLConstants.PASSWORD);
    }

    /**
     * Sets as to whether the user uses custom cursors
     *
     * @param useCust Whether the user wants to use custom cursor
     */
    public void setUseCustCursors(boolean useCust) {
        preferences.put(XMLConstants.CUSTOM_CURSORS, Boolean.valueOf(useCust));
    }

    /**
     * Returns whether the user is using custom cursors
     *
     * @return Whether the user is using custom cursors
     */
    public boolean getUseCustCursors() {
        Boolean value = (Boolean) preferences.get(XMLConstants.CUSTOM_CURSORS);
        return value != null && value.booleanValue();
    }      

    /**
     * Sets the last edited date on the ToLEditor.jar file on the
     * editor.tolweb.org site.  This is used to determine if a download of a
     * new file is necessary
     *
     * @param ts The last modified date of the ToLEditor.jar file on the server
     */
    public void setEditorJarTimestamp(long ts) {
        preferences.put(XMLConstants.EDITORJAR_TIMESTAMP, Long.valueOf(ts));
    }

    /**
     * Returns the last edited date on the ToLEditor.jar file on the server
     *
     * @return The last edited date of the ToLEditor.jar file on the server
     */
    public long getEditorJarTimestamp() {
        Long value = (Long) preferences.get(XMLConstants.EDITORJAR_TIMESTAMP);
        if (value != null) {
            return value.longValue();
        } else {
            return 0;
        }
    }      

    /**
     * Sets the last edited date on the ToLSupportFiles.jar file on the
     * editor.tolweb.org site.  This is used to determine if a download of a
     * new file is necessary
     *
     * @param ts The last modified date of the ToLSupportFiles.jar file
     */
    public void setSupportJarTimestamp(long ts) {
        preferences.put(XMLConstants.SUPPORTJAR_TIMESTAMP, Long.valueOf(ts));
    }

    /**
     * Returns the last edited date of the ToLSupportFile.jar file 
     *
     * @return The last edited date of the ToLSupportFile.jar file 
     */
    public long getSupportJarTimestamp() {
        return ((Long) preferences.get(XMLConstants.SUPPORTJAR_TIMESTAMP)).longValue();
    }
    
    public boolean hasOpenedFile() {
        Boolean value = (Boolean) preferences.get(XMLConstants.LAST_USER);
        if (value != null) {
            return value.booleanValue();
        } else {
            return false;
        }
    }
    
    public void setHasOpenedFile(boolean value) {
        preferences.put(XMLConstants.LAST_USER, Boolean.valueOf(value));
    }
    
    public int getCopyHolderId() {
        Integer value = (Integer) preferences.get(XMLConstants.ID);
        if (value != null) {
            return value.intValue();
        } else {
            return -1;
        }
    }
    
    public void setCopyHolderId(int value) {
        preferences.put(XMLConstants.ID, Integer.valueOf(value));
    }
    
    public String getCopyHolder() {
        return (String) preferences.get(XMLConstants.COPYRIGHTOWNER);
    }
    
    public void setCopyHolder(String value) {
        preferences.put(XMLConstants.COPYRIGHTOWNER, value);
    }
    
    public String getCopyUrl() {
        return (String) preferences.get(XMLConstants.COPYRIGHTURL);
    }
    
    public void setCopyUrl(String value) {
        preferences.put(XMLConstants.COPYRIGHTURL, value);
    }    
    
    public String getCopyEmail() {
        return (String) preferences.get(XMLConstants.EMAIL);
    }
    
    public void setCopyEmail(String value) {
        preferences.put(XMLConstants.EMAIL, value);
    }
    
    public String getCopyDate() {
        return (String) preferences.get(XMLConstants.COPYRIGHTDATE);
    }
    
    public void setCopyDate(String value) {
        preferences.put(XMLConstants.COPYRIGHTDATE, value);
    }
    
    public byte getUsePermission() {
        Byte value = (Byte) preferences.get(XMLConstants.PERMISSION);
        if (value != null) {
            return value.byteValue();
        } else {
            return -1;
        }
    }
    
    public void setUsePermission(byte value) {
        preferences.put(XMLConstants.PERMISSION, new Byte(value));
    }
    
    public boolean getInPublicDomain() {
        Boolean value = (Boolean) preferences.get(XMLConstants.PUBLICDOMAIN);
        if (value != null) {
            return value.booleanValue();
        } else {
            return false;
        }
    }
    
    public void setInPublicDomain(boolean value) {
        preferences.put(XMLConstants.PUBLICDOMAIN, Boolean.valueOf(value));
    }
    
    public boolean dontShowCloseDialog() {
        Boolean value = (Boolean) preferences.get(XMLConstants.DONTSHOWCLOSE);
        if (value != null) {
            return value.booleanValue();
        } else {
            return false;
        }
    }  
    
    public void setDontShowCloseDialog(boolean value) {
        preferences.put(XMLConstants.DONTSHOWCLOSE, Boolean.valueOf(value));
    }    
    
    public void writePreferencesToDisk() {
        Enumeration en = preferences.keys();
        Element mainElement = new Element(XMLConstants.PREFERENCES);
        while (en.hasMoreElements()) {
            String nextKey = (String) en.nextElement();
            System.out.println("next key is: " + nextKey);
            Object value = preferences.get(nextKey);
            System.out.println("next value is: " + value);
            Element settingElement = new Element(nextKey);
            settingElement.addContent(value.toString());
            mainElement.addContent(settingElement);
        }
        Document recentDocument = new Document(mainElement);
        try {
            FileOutputStream out = new FileOutputStream(Controller.getController().getFileManager().getConfigFile());
            XMLOutputter serializer = BaseXMLWriter.getXMLOutputter();
            serializer.output(recentDocument, out);
            out.flush();
            out.close();
        } catch (IOException e) {
        }        
    }
    
    public void fetchConfigInfo() {
        try {
            File configFile = Controller.getController().getFileManager().getConfigFile();
            if(!configFile.exists()) {
                setUserName(null);
                setUseCustCursors(true);
                return;
            }

            URL url = configFile.toURL();

            Element rootElmt = null;
            SAXBuilder builder = null;
            Document doc = null;

            builder = new SAXBuilder();
            doc = builder.build(url);
            rootElmt = doc.getRootElement();
            
            boolean isOld = rootElmt.getName().equals(XMLConstants.SETTINGS);
            if (isOld) {
                fetchConfigInfoOldStyle(rootElmt);
                return;
            }
            
            String lastUserText = rootElmt.getChildText(XMLConstants.LAST_USER);
            setHasOpenedFile(lastUserText != null && lastUserText.equalsIgnoreCase(XMLConstants.TRUE));
            String dontShowClose = rootElmt.getChildText(XMLConstants.DONTSHOWCLOSE);
            System.out.println("setting dontshowclose to " + dontShowClose);
            setDontShowCloseDialog(dontShowClose != null && dontShowClose.equalsIgnoreCase(XMLConstants.TRUE));
            String userName = rootElmt.getChildText(XMLConstants.USERNAME);
            setUserName(userName);
            String passwordText = rootElmt.getChildText(XMLConstants.PASSWORD);
            setPassword(passwordText);
            String useCustomCursors = rootElmt.getChildText(XMLConstants.CUSTOM_CURSORS);
            setUseCustCursors(useCustomCursors != null && useCustomCursors.equalsIgnoreCase(XMLConstants.TRUE));
            String timestamp = rootElmt.getChildText(XMLConstants.EDITORJAR_TIMESTAMP);
            if (timestamp != null) {
                setEditorJarTimestamp(Long.parseLong(timestamp));
            } else {
                setEditorJarTimestamp(0);
            }
            timestamp = rootElmt.getChildText(XMLConstants.SUPPORTJAR_TIMESTAMP);
            if (timestamp != null) {
                setSupportJarTimestamp(Long.parseLong(timestamp));
            } else {
                setSupportJarTimestamp(0);
            }
            String id = rootElmt.getChildText(XMLConstants.ID);
            setCopyHolderId(Integer.parseInt(id));
            String use = rootElmt.getChildText(XMLConstants.PERMISSION);
            if (use != null && !use.equals("")) {
                setUsePermission(new Byte(use).byteValue());
            } else {
                setUsePermission(NodeImage.EVERYWHERE_USE);
            }

            String date = rootElmt.getChildText(XMLConstants.COPYRIGHTDATE);
            setCopyDate(date);
            String holder = rootElmt.getChildText(XMLConstants.COPYRIGHTOWNER);
            setCopyHolder(holder);
            String urlString = rootElmt.getChildText(XMLConstants.COPYRIGHTURL);
            setCopyUrl(urlString);
            String email = rootElmt.getChildText(XMLConstants.COPYRIGHTEMAIL);
            setCopyEmail(email);
            String pub = rootElmt.getAttributeValue(XMLConstants.PUBLICDOMAIN);
            setInPublicDomain(pub != null && pub.equalsIgnoreCase(XMLConstants.TRUE));
        } catch (Exception e) {
            
        }
    }
   
    /**
     * Here so users won't have to type in their information again when they 
     * get the new version
     */
    public void fetchConfigInfoOldStyle(Element rootElmt) {
        boolean useCustomCursors = true;
        try  {
            String attrValue = rootElmt.getAttributeValue(XMLConstants.LAST_USER);
            setHasOpenedFile(attrValue != null && attrValue.equals(XMLConstants.ONE));
            
            attrValue = rootElmt.getAttributeValue(XMLConstants.DONTSHOWCLOSE);
            setDontShowCloseDialog(attrValue != null && attrValue.equals(XMLConstants.ONE));
            
            Element userElem = rootElmt.getChild(XMLConstants.USERNAME);
            String userName = userElem.getTextTrim();
            
            Element passwordElem = rootElmt.getChild(XMLConstants.PASSWORD);
            String password = null;
            if (passwordElem != null) {
                 password = passwordElem.getTextTrim();
            }

            Element cursorElem = rootElmt.getChild(XMLConstants.CUSTOM_CURSORS);
            String result = cursorElem.getTextTrim();
            if(result == null || result.equals("")) {
                setUseCustCursors(true);
            } else {
                setUseCustCursors(result.equals(XMLConstants.TRUE));
            }

            long editorJarTimestamp = 0;
            Element editorJarTimeElem = rootElmt.getChild(XMLConstants.EDITORJAR_TIMESTAMP);
            if (editorJarTimeElem != null) {
                result = editorJarTimeElem.getTextTrim();
                if(result != null && !result.equals("")) {
                    editorJarTimestamp = Long.valueOf(result).longValue();
                }
            }
            
            long supportJarTimestamp = 0;
            Element supportJarTimeElem = rootElmt.getChild(XMLConstants.SUPPORTJAR_TIMESTAMP);
            if (supportJarTimeElem != null) {
                result = supportJarTimeElem.getTextTrim();
                if(result != null && !result.equals("")) {
                    supportJarTimestamp = Long.valueOf(result).longValue();
                }            
            }
            
            Element imagePrefsElt = rootElmt.getChild(XMLConstants.IMAGES);
            if (imagePrefsElt != null) {
                String id = imagePrefsElt.getAttributeValue(XMLConstants.ID);
                setCopyHolderId(new Integer(id).intValue());
                String use = imagePrefsElt.getAttributeValue(XMLConstants.PERMISSION);
                if (use != null && !use.equals("")) {
                    setUsePermission(new Byte(use).byteValue());
                } else {
                    setUsePermission(NodeImage.EVERYWHERE_USE);
                }
                
                String date = imagePrefsElt.getAttributeValue(XMLConstants.COPYRIGHTDATE);
                setCopyDate(date);
                String holder = imagePrefsElt.getChildText(XMLConstants.COPYRIGHTOWNER);
                setCopyHolder(holder);
                String urlString = imagePrefsElt.getChildText(XMLConstants.COPYRIGHTURL);
                setCopyUrl(urlString);
                String email = imagePrefsElt.getChildText(XMLConstants.COPYRIGHTEMAIL);
                setCopyEmail(email);
                String pub = imagePrefsElt.getAttributeValue(XMLConstants.PUBLICDOMAIN);
                setInPublicDomain(pub != null && pub.equals(XMLConstants.ONE));
            } else {
                setUsePermission(NodeImage.EVERYWHERE_USE);
            }
            setUseCustCursors(useCustomCursors);
            setUserName(userName);
            setPassword(password);
            setEditorJarTimestamp(editorJarTimestamp);
            setSupportJarTimestamp(supportJarTimestamp);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }    
}
