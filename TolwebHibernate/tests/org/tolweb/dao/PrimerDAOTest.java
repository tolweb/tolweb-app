package org.tolweb.dao;

import java.util.List;

import org.tolweb.btol.dao.PrimerDAO;

public class PrimerDAOTest extends ApplicationContextTestAbstract {
	private PrimerDAO primerDAO;
	
    public PrimerDAOTest(String name) {
        super(name);
        primerDAO = (PrimerDAO) context.getBean("primerDAO");
    }  
    
    public void testPrimerWithNameAndNotId() {
    	String primerName  = "SSU4F";
    	Long id = 24L;
    	List primers = primerDAO.getPrimersWithNameOrSynonymAndNotId(primerName, id, 1L);
    	System.out.println("primers are " + primers);
    	assertTrue(primers != null && primers.size() == 0);
    }
}
