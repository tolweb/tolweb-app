package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.BtolMetaNodeDAO;
import org.tolweb.dao.AutoReconciliationLogDAO;
import org.tolweb.dao.ManualReconciliationLogDAO;
import org.tolweb.dao.MetaNodeDAO;
import org.tolweb.dao.NodeObjectManagementLogDAO;
import org.tolweb.dao.NodeRetirementLogDAO;
import org.tolweb.dao.ObjectManifestLogDAO;
import org.tolweb.dao.OtherNameDAO;
import org.tolweb.dao.ReattachmentProgressDAO;
import org.tolweb.dao.TaxaImportLogDAO;

public interface MetaInjectable {
	@InjectObject("spring:publicMetaNodeDAO")	
	public MetaNodeDAO getPublicMetaNodeDAO();
	@InjectObject("spring:workingMetaNodeDAO")	
	public MetaNodeDAO getWorkingMetaNodeDAO();
	@InjectObject("spring:workingMetaNodeDAO")	
	public MetaNodeDAO getMetaNodeDAO();
	@InjectObject("spring:btolMetaNodeDAO")	
	public BtolMetaNodeDAO getBtolMetaNodeDAO();
	@InjectObject("spring:workingOtherNameDAO")
	public OtherNameDAO getWorkingOtherNameDAO();
	@InjectObject("spring:miscOtherNameDAO")
	public OtherNameDAO getMiscOtherNameDAO();
	@InjectObject("spring:objectManifestLogDAO")	
	public ObjectManifestLogDAO getObjectManifestLogDAO();
	@InjectObject("spring:reattachmentProgressDAO")	
	public ReattachmentProgressDAO getReattachmentProgressDAO();
	@InjectObject("spring:taxaImportLogDAO")	
	public TaxaImportLogDAO getTaxaImportLogDAO();
	@InjectObject("spring:autoRecLogDAO")
	public AutoReconciliationLogDAO getAutoReconciliationLogDAO();
	@InjectObject("spring:manualRecLogDAO")
	public ManualReconciliationLogDAO getManualReconciliationLogDAO();
	@InjectObject("spring:nodeRetirementLogDAO")
	public NodeRetirementLogDAO getNodeRetirementLogDAO();
	@InjectObject("spring:nodeObjectManagementLogDAO")
	public NodeObjectManagementLogDAO getNodeObjectManagementLogDAO();
}
