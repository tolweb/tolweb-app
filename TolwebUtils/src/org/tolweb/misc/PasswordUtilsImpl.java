/*
 * PasswordSenderImpl.java
 *
 * Created on April 28, 2004, 4:14 PM
 */

package org.tolweb.misc;

import java.util.Random;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;

/**
 *
 * @author  dmandel
 */
public class PasswordUtilsImpl implements PasswordUtils {   
    private MD5Service md5;
    private ContributorDAO dao;
    
    /**
     * Resets the password for the specified contributor and
     * saves the changed password to the database
     * 
     * @param contr The Contributor to set the password for
     * @return The newly-generated password
     */
    public String resetPassword(Contributor contr) {
        String password = generatePassword();
        setAndSaveContributorPassword(password, contr);
        return password;
    }
    
    public void setContributorPassword(Contributor contr, String password) {
        setAndSaveContributorPassword(password, contr);
    }
    
    private String setAndSaveContributorPassword(String originalPassword, Contributor contr) {
        String hashedPassword = md5.getMD5Hash(originalPassword); 
        contr.setPassword(hashedPassword);
        dao.setContributorPassword(contr.getId(), hashedPassword);
        return hashedPassword;
    }
    
    /**
     * Generates a random 8 character password
     */
    public String generatePassword() {
        int beginNumChar = 48;
        int beginLowercaseChar = 97;
        StringBuilder password = new StringBuilder();
        Random random1 = new Random(System.currentTimeMillis());
        Random random2 = new Random(System.currentTimeMillis() + 1);
        for (int i = 0; i < 8; i++) {
            if (random1.nextBoolean()) {
                // generate a number
                char next = (char) (Math.abs((random2.nextInt() % 10)) + beginNumChar);
                password.append(next);
            } else {
                // generate a lowercase char
                char next = (char) (Math.abs((random2.nextInt() % 26)) + beginLowercaseChar);
                password.append(next);
            }
        }
        return password.toString();
    }
    
    public String getInitialPasswordForStudent(Student stud) {
        return md5.getMD5Hash(stud.getAlias());
    }
    
    public MD5Service getMD5Service() {
        return md5;
    }
    
    public void setMD5Service(MD5Service service) {
        md5 = service;
    }
    
    public ContributorDAO getContributorDAO() {
        return dao;
    }
    
    public void setContributorDAO(ContributorDAO value) {
        dao = value;
    }
    
    public boolean checkPassword(String email, String password) {
    	Contributor contr = dao.getContributorWithEmail(email);
    	if (contr != null) {
    		return checkPassword(contr, password);
    	} else {
    		return false;
    	}
    }
    
    public boolean checkPassword(Contributor contr, String password) {
        String md5Password = md5.getMD5Hash(password);
        return contr.getPassword().equals(md5Password) || contr.getPassword().equals(password);
    }
    public String getMD5Hash(String originalString) {
    	return md5.getMD5Hash(originalString);
    }
}
