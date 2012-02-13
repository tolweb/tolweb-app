package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.PCRBatchDAO;
import org.tolweb.btol.dao.PCRProtocolDAO;
import org.tolweb.btol.dao.PCRReactionDAO;
import org.tolweb.btol.dao.SpecimenExtractionDAO;
import org.tolweb.btol.util.GelImageUtils;

public interface PCRInjectable {
	@InjectObject("spring:pcrProtocolDAO")
	public PCRProtocolDAO getProtocolDAO();
	@InjectObject("spring:specimenExtractionDAO")
	public SpecimenExtractionDAO getSpecimenExtractionDAO();
	@InjectObject("spring:pcrReactionDAO")
	public PCRReactionDAO getPCRReactionDAO();
	@InjectObject("spring:pcrBatchDAO")
	public PCRBatchDAO getPCRBatchDAO();
	@InjectObject("spring:gelImageUtils")
	public GelImageUtils getGelImageUtils();
}
