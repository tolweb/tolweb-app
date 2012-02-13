/*
 * PasswordSenderTest.java
 *
 * Created on April 30, 2004, 8:33 AM
 */

package org.tolweb.misc;

import junit.framework.TestCase;

import org.tolweb.treegrow.main.Contributor;

/**
 *
 * @author  dmandel
 */
public class PasswordUtilsTest extends TestCase {
    
    private PasswordUtils utils;
    
    public PasswordUtilsTest() {
        utils = new PasswordUtilsImpl();
        utils.setMD5Service(new MD5ServiceImpl());
    }
    
    public void testGeneratePassword() {
        // Generate 1000 passwords and make sure they all meet the requirements
        for (int i = 0; i < 1000; i++) {
            String nextPw = utils.generatePassword();
            System.out.println("next pw is: " + nextPw);
            assertNotNull(nextPw);
            int length = nextPw.length();
            System.out.println("length is: " + length);
            assertTrue(length == 8);
            for (int j = 0; j < length; j++) {
                char nextChar = nextPw.charAt(j);
                assertTrue(nextChar >= 48 && nextChar <= 57 || nextChar >= 97 && nextChar <= 122);
            }
        }
    }
    
    public void testCheckPassword() {
        Contributor contr = new Contributor();
        String totalitarianism = "totalitarianism";
        String md5totalitarianism = "0a1862044f104b1532d0dd6f0d756347";        
        contr.setPassword(md5totalitarianism);
        assertTrue(utils.checkPassword(contr, totalitarianism));
        assertFalse(utils.checkPassword(contr, "wrong"));
    }
}
