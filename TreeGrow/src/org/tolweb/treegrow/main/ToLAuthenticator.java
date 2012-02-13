/*
 * ToLAuthenticator.java
 *
 * Created on April 13, 2004, 11:07 AM
 */

package org.tolweb.treegrow.main;

import java.net.*;

/**
 *
 * @author  dmandel
 */
public class ToLAuthenticator extends Authenticator {
    private String password;
    private String username;
    
    /** Creates a new instance of ToLAuthenticator */
    public ToLAuthenticator(String user, String pw) {
        username = user;
        password = pw;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        char[] bytes;
        bytes = new char[password.length()];
        password.getChars(0, password.length(), bytes, 0);
        return new PasswordAuthentication(username, bytes);
    }    
}
