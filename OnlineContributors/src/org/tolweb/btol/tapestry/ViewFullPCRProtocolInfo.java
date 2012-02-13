package org.tolweb.btol.tapestry;

import org.tolweb.treegrow.main.StringUtils;

public abstract class ViewFullPCRProtocolInfo extends EditPCRProtocol {
	public String getPropertiesString() {
		String propertiesString = getRequiredProperties();
		int numStages = getProtocol().getStages();
		if (numStages > 1) {
			propertiesString += getStage2Properties();
		}
		String notes = getProtocol().getNotes();
		if (StringUtils.notEmpty(notes)) {
			propertiesString += getNotesProperties();
		}
		return propertiesString;
	}	
	
	protected String getRequiredProperties() {
		return "name,protocolTypeString,recipe,ddH20Prop,templateProp,forwardPrimerConc,forwardPrimerProp,reversePrimerConc,reversePrimerProp,tenXBufferProp," + 
		"dntpConc,dntpProp,mgCl2Added,taqName,taqProp,cycle,iniDTimeString,iniDTemp,stages," + 
		"finalExtentionTimeString,stage1,c1Cycles,d1TimeString,d1Temp,a1TimeString,a1Temp,e1TimeString,e1Temp,";
	}
	protected String getStage2Properties() {
		return "stage2,c2Cycles,d2TimeString,d2Temp," +
			"a2TimeString,a2Temp,e2TimeString,e2Temp,";
	}
	protected String getNotesProperties() {
		return "notes";
	}	
}
