/*
 * MD5ServiceImpl.java
 *
 * Created on April 30, 2004, 10:14 AM
 */

package org.tolweb.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author  dmandel
 */
public class MD5ServiceImpl implements MD5Service {
    private MessageDigest md5Digest;
    
    public MD5ServiceImpl() {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 Algorithm not available.  Passwords will not work.");
        }
    }
    
    public String getMD5Hash(String originalString) {
        try {
            return encodeToHex(md5Digest.digest(originalString.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
    * The byte[] returned by MessageDigest does not have a nice
    * textual representation, so some form of encoding is usually performed.
    *
    * This implementation follows the example of David Flanagan's book
    * "Java In A Nutshell", and converts a byte array into a String
    * of hex characters.
    *
    * Another popular alternative is to use a "Base64" encoding.
    */
    private String encodeToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for ( int idx = 0; idx < bytes.length; ++idx) {
            byte b = bytes[idx];
            result.append( digits[ (b&0xf0) >> 4 ] );
            result.append( digits[ b&0x0f] );
        }
        return result.toString();
    }
}
