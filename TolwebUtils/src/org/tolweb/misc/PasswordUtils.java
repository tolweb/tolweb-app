/*
 * PasswordSender.java
 *
 * Created on April 28, 2004, 4:13 PM
 */

package org.tolweb.misc;

import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;

/**
 *
 * @author  dmandel
 */
public interface PasswordUtils {
    public static final String INACTIVE_STUDENT_PASSWORD = "inactive";
    
    public void setMD5Service(MD5Service service);
    public void setContributorDAO(ContributorDAO value);
    public boolean checkPassword(Contributor contr, String password);
    public boolean checkPassword(String email, String password);
    public String generatePassword();
    public String resetPassword(Contributor contr);
    public void setContributorPassword(Contributor contr, String password);
    public String getInitialPasswordForStudent(Student stud);
    public String getMD5Hash(String originalString);
}
