/*
 * MD5ServiceTest.java
 *
 * Created on April 30, 2004, 10:22 AM
 */

package org.tolweb.misc;

import junit.framework.TestCase;

/**
 *
 * @author  dmandel
 */
public class MD5ServiceTest extends TestCase {
    public void testGetMD5Hash() {
        // Ran some values through MYSQL to get these
        String md5anarchy = "ce09b59f734f7f5641f2962a5cf94bd1";
        String md5socialism = "95b8194270d365f28b903479c33bf453";
        String md5totalitarianism = "0a1862044f104b1532d0dd6f0d756347";
        
        MD5ServiceImpl md5 = new MD5ServiceImpl();
        assertEquals(md5anarchy, md5.getMD5Hash("anarchy"));
        assertEquals(md5socialism, md5.getMD5Hash("socialism"));
        assertEquals(md5totalitarianism, md5.getMD5Hash("totalitarianism"));
    }
}
