package org.tolweb.misc;

import java.io.Serializable;
import java.util.Comparator;

import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class ContributorNameComparator implements Serializable, Comparator {
    /** */
	private static final long serialVersionUID = 4619157718864187668L;

	public int compare(Object o1, Object o2) {
        Contributor c1 = (Contributor) o1;
        Contributor c2 = (Contributor) o2;
        int result = 0;
        boolean bothStudents = Student.class.isInstance(c1) && Student.class.isInstance(c2);
        boolean bothNotStudents = !Student.class.isInstance(c1) && !Student.class.isInstance(c2);
        String lastName1 = getContributorComparisonString(c1);
        String lastName2 = getContributorComparisonString(c2);
        boolean sameFirstLetter = false;
        if (StringUtils.notEmpty(lastName1) && StringUtils.notEmpty(lastName2)
                && (lastName1.toUpperCase().charAt(0) == lastName2.toUpperCase().charAt(0))) {
            sameFirstLetter = true;
        }
        if (bothStudents || bothNotStudents || !sameFirstLetter) {        
            result = compareStrings(lastName1, lastName2);
        } else {
            // we want the students to show up last in a page if their alias
            // begins with the same letter as a contributor's last name
            if (Student.class.isInstance(c1) && !Student.class.isInstance(c2)) {
                result = 1;
            } else if (!Student.class.isInstance(c1) && Student.class.isInstance(c2)) {
                result = -1;
            }
        }
        return result;
    }
    
    private String getContributorComparisonString(Contributor contr) {
        if (Student.class.isInstance(contr)) {
            return ((Student) contr).getAlias();
        } else {
            String returnString = contr.getLastName();
            if (returnString == null) {
                returnString = contr.getInstitution();
            }
            return returnString;
        }
    }
    
    private int compareStrings(String s1, String s2) {
        if (StringUtils.notEmpty(s1) && StringUtils.isEmpty(s2)) {
            return -1;
        } else if (StringUtils.isEmpty(s1) && StringUtils.notEmpty(s2)) {
            return 1;
        } else if (StringUtils.notEmpty(s1) && StringUtils.notEmpty(s2)) {
            s1 = s1.toUpperCase();
            s2 = s2.toUpperCase();            
            return s1.compareTo(s2);        
        } else {
            return 0;
        }
    }    
}
