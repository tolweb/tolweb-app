/*
 * UserValidationChecker.java
 *
 * Created on September 24, 2003, 3:37 PM
 */

package org.tolweb.treegrow.main;

import java.net.*;
import java.util.*;
import javax.swing.*;
import org.jdom.*;
import org.jdom.input.*;


/**
 * Utility class to check if validation succeeded during some server operation
 * @author  dmandel
 */
public class UserValidationChecker {
    
    /** Creates a new instance of UserValidationChecker */
    public UserValidationChecker() {
    }
    
    /**
     * Checks to see that the root response isn't named FAILURE.  Displays 
     * a message box if validation wasn't successful
     *
     * @param root The element to check
     * @return true if The element isn't named FAILURE, false otherwise
     */
    public static boolean checkValidation(Element root) {
        Controller controller = Controller.getController();
        if (root.getName().equals(XMLConstants.FAILURE)) {
            String BAD_PASSWORD = controller.getMsgString("BAD_PASSWORD");
            String password = "";
            controller.setPassword("");
            controller.showPreferencesWindow();
            return false;
        }
        return true;
    }
    
    /**
     * Returns the root element from trying to validate the user and password
     *
     * @param userName The username to validate
     * @param password The password to validate
     * @param newPassword If this is present and the original password is 
     *        valid, then this overwrites the old password
     * @return The root element returned by the server
     */
    public static Element sendValidationRequest(String userName, String password, String newPassword) {
        Controller controller = Controller.getController();
        Hashtable args = new Hashtable();
        args.put(RequestParameters.PASSWORD, password);
        args.put(RequestParameters.USER_ID, userName);
        if (StringUtils.notEmpty(newPassword)) {
            args.put(RequestParameters.NEW_PASSWORD, URLEncoder.encode(newPassword));
        }
        String urlString = HttpRequestMaker.getServerUtilsUrlString(RequestParameters.USER_VALIDATION, args);
        // This should get overwritten just set it to a non-null value
        Element root = null;
        try {
            URL url = new URL(urlString);
            root = new SAXBuilder().build(url).getRootElement();
        } catch (Exception ex) {
            System.out.println(urlString);
            ex.printStackTrace();
            String SERVER_PROBLEMS = controller.getMsgString("SERVER_PROBLEMS");
            JOptionPane.showMessageDialog(controller.getManagerFrame(), SERVER_PROBLEMS, "Server Problems", JOptionPane.ERROR_MESSAGE);            
        }
        return root;
    }
    
    public static boolean checkCheckedOut(Element root, JFrame frame) {
        String checkedOut = root.getAttributeValue(XMLConstants.CHECKED_OUT_FILE);
        if (checkedOut != null && checkedOut.equals(XMLConstants.ONE)) {
            Vector args = new Vector();
            args.add(root.getChildText(XMLConstants.FULLNAME));
            args.add(root.getAttributeValue(XMLConstants.EMAIL));
            args.add(root.getAttributeValue(XMLConstants.DATE_TIME));
            String IMAGE_CHECKED_OUT = Controller.getController().getMsgString("IMAGE_CHECKED_OUT", args);
            JOptionPane.showMessageDialog(frame, IMAGE_CHECKED_OUT, "Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }
    
}
