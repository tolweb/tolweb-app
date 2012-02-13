package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.valid.ValidationConstraint;
import org.tolweb.btol.PCRProtocol;
import org.tolweb.btol.injections.PCRProtocolInjectable;
import org.tolweb.btol.tapestry.selection.BoundedIntPropertySelectionModel;
import org.tolweb.btol.tapestry.selection.MinutesSecondsSelectionModel;
import org.tolweb.btol.tapestry.selection.ProtocolTypeSelectionModel;
import org.tolweb.dao.BaseDAO;



public abstract class EditPCRProtocol extends AbstractEditPage implements PCRProtocolInjectable {
	private BoundedIntPropertySelectionModel cyclesModel;
	@Persist("session")
	public abstract PCRProtocol getProtocol();
	public abstract void setProtocol(PCRProtocol protocol);
	@Bean
	public abstract ProtocolTypeSelectionModel getProtocolTypeSelectionModel();
	@Bean
	public abstract MinutesSecondsSelectionModel getMinutesSecondsSelectionModel();
	
	public BoundedIntPropertySelectionModel getCyclesModel() {
		if (cyclesModel == null) {
			cyclesModel = new BoundedIntPropertySelectionModel(2, 1, false);
		}
		return cyclesModel;
	}
	
	public AbstractEditPage editNewObject(IPage prevPage) {
		// set up some defaults
		PCRProtocol protocol = new PCRProtocol();
		initializeAndSetProtocol(protocol);		
		if (prevPage != null) {
			setPreviousPageName(prevPage.getPageName());
		}
		return this;
	}
	private void initializeAndSetProtocol(PCRProtocol protocol) {
		protocol.setDdH20Prop(0.7F);
		//
		protocol.setTemplateProp(0.04F);
		protocol.setForwardPrimerConc(10);
		//
		protocol.setForwardPrimerProp(0.04F);
		protocol.setReversePrimerConc(10);
		//
		protocol.setReversePrimerProp(0.04F);
		protocol.setTenXBufferProp(0.1F);
		protocol.setDntpConc(10);
		protocol.setDntpProp(0.08F);
		protocol.setMgCl2Added(0);
		protocol.setTaqName("hotmaster");
		protocol.setTaqProp(0.0F);
		protocol.setIniDTime(120);
		protocol.setIniDTemp(94.0F);
		protocol.setStages(1);
		protocol.setC1Cycles(34);
		protocol.setD1Time(20);
		protocol.setD1Temp(94.0F);
		protocol.setA1Time(20);
		protocol.setA1Temp(51);
		protocol.setE1Time(40);
		protocol.setE1Temp(65.0F);
		protocol.setFinalExtentionTime(180);
		setProtocol(protocol);
	}
	public String getPropertiesString() {
		return getRequiredProperties() + getStage2Properties() + getNotesProperties() + ",defunct";
	}
	protected String getRequiredProperties() {
		return "name{required},protocolType,recipe,ddH20Prop{required,real},templateProp{required,real},forwardPrimerConc{required,real},forwardPrimerProp{required,real},reversePrimerConc{required,real},reversePrimerProp{required,real},tenXBufferProp{required,real}," + 
		"dntpConc{required,real},dntpProp{required,real},mgCl2Added,taqName{required},taqProp{required,real},cycle,iniDTime,iniDTemp{required,real},stages," + 
		"finalExtentionTime{required,real},stage1,c1Cycles{real},d1Time,d1Temp{required,real},a1Time,a1Temp{required,real},e1Time,e1Temp{required,real}";
	}
	protected String getStage2Properties() {
		return "stage2,c2Cycles{real},d2Time,d2Temp{real}," +
			"a2Time,a2Temp{real},e2Time,e2Temp{real},";
	}
	protected String getNotesProperties() {
		return "notes";
	}
	@SuppressWarnings("unchecked")
	public IPage saveProtocol(IRequestCycle cycle) {
		if (getValidationDelegate().getHasErrors()) {
			return null;
		}
		// check to make sure all of the proportions add up to 1.0
		PCRProtocol prot = getProtocol();
		float totalProportions;
		totalProportions = prot.getDdH20Prop() + prot.getTemplateProp() + prot.getForwardPrimerProp() +
			prot.getReversePrimerProp() + prot.getTenXBufferProp() + prot.getDntpProp() + prot.getTaqProp();
		if (totalProportions < 0.99 || totalProportions > 1.01) {
			getValidationDelegate().setFormComponent(null);
			getValidationDelegate().record("All proportions must add up to 1.0, current proportions add up to " + totalProportions, ValidationConstraint.CONSISTENCY);
			return null;
		}
		List existingProtocols = getProtocolDAO().getProtocolsWithNameAndNotId(prot.getName(), prot.getId(), getProject().getId());
		if (existingProtocols != null && existingProtocols.size() > 0 ) {
			getValidationDelegate().record("There is an existing PCR Protocol named " + prot.getName() + ".  Protocol names must be unique.", ValidationConstraint.CONSISTENCY);
			return null;
		}
		getProtocolDAO().saveProtocol(getProtocol(), getProject().getId());
		// if there is no previous page we were called from a popup window so 
		// display a gene saved message instead of going to another page
		return conditionallyGotoPreviousPage();
	}	
	public BaseDAO getDAO() {
		return getProtocolDAO();
	}
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return PCRProtocol.class;
	}
	protected void setObjectToEdit(Object value) {
		setProtocol((PCRProtocol) value);
	}
	public EditPCRProtocol editNewProtocolWithName(String missingProtocolName) {
		PCRProtocol newProtocol = new PCRProtocol();
		newProtocol.setName(missingProtocolName);
		initializeAndSetProtocol(newProtocol);
		return this;
	}
}
