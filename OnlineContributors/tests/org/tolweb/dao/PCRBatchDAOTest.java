/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.dao.GeneDAO;
import org.tolweb.btol.dao.PCRBatchDAO;
import org.tolweb.btol.dao.SpecimenExtractionDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PCRBatchDAOTest extends ApplicationContextTestAbstract {
    protected PCRBatchDAO dao;
    private GeneDAO geneDao;
    private SpecimenExtractionDAO extractionDao;
    //private ContributorDAO contributorDAO;
    
    public PCRBatchDAOTest(String name) {
        super(name);      
        dao = (PCRBatchDAO) context.getBean("pcrBatchDAO");
        geneDao = (GeneDAO) context.getBean("geneDAO");
        extractionDao = (SpecimenExtractionDAO) context.getBean("specimenExtractionDAO");
        //contributorDAO = (ContributorDAO) context.getBean("contributorDAO");
    }

    /*public void testGetBatchesWithoutImageForContributor() {
    	Contributor contr = contributorDAO.getContributorWithId(664);
    	List<PCRBatch> batchesNoImage = dao.getBatchesWithoutImageForContributor(contr, 1L);
    	System.out.println("batches w/o image length is: " + batchesNoImage.size());
    	System.out.println("first object is: " + batchesNoImage.get(0));
    }*/
    
    @SuppressWarnings("unchecked")
    public void testGetChromatogramsForExtractionAndGene() {
    	List abiFiles = doTestChroFilenameQuery();
    	System.out.println("num abis: " + abiFiles.size());
    	for (Iterator iter = abiFiles.iterator(); iter.hasNext();) {
			String nextFilename = (String) iter.next();
	    	System.out.println("next abi is: " + nextFilename);
		}
    }

    @SuppressWarnings("unchecked")
	protected List doTestChroFilenameQuery() {
		SpecimenExtraction extraction = extractionDao.getExtractionWithCollectionAndCode("DNA1780", 1L);
    	assertNotNull(extraction);
    	Gene gene = geneDao.getGeneWithName("Wingless", 1L);
    	assertNotNull(gene);
    	List extractionIds = new ArrayList<Long>();
    	extractionIds.add(extraction.getId());
    	List geneIds = new ArrayList<Long>();
    	geneIds.add(gene.getId());
    	List abiFiles = dao.getChromatogramFilenamesForExtractionGeneAndTaxon(null, geneIds, null, 1L);
		return abiFiles;
	}
}
