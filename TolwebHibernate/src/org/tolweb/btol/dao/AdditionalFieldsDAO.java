package org.tolweb.btol.dao;

import java.util.Collection;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.dao.BaseDAO;
import org.tolweb.treegrow.main.Contributor;

public interface AdditionalFieldsDAO extends BaseDAO {
	public void saveAdditionalFields(AdditionalFields fields);
	public void saveAdditionalFields(Collection additionalFields);
	public void updateTierForAdditionalFields(int newTier, Long fieldsId);
    public void updateTierNotesForAdditionalFields(String notes, Long fieldsId);	
	public void updateLarvalPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId);
	public void updateLarvalDestinationPersonForAdditionalFields(Contributor contr, Long fieldsId);
    public void updateLarvalSpecimensStateForAdditionalFields(Integer newSpecimensState, Long id);
    public void updateLarvalNotesForAdditionalFields(String notes, Long fieldsId);
    public void updateAdultPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId);
    public void updateAdultDestinationPersonForAdditionalFields(Contributor contr, Long fieldsId);
    public void updateAdultSpecimensStateForAdditionalFields(Integer newSpecimensState, Long id);
    public void updateAdultNotesForAdditionalFields(String notes, Long fieldsId);
    public void updateDnaPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId);
    public void updateDnaDestinationPersonForAdditionalFields(Contributor contr, Long fieldsId);
    public void updateDnaSpecimensStateForAdditionalFields(Integer newSpecimensState, Long id);
    public void updateDnaNotesForAdditionalFields(String notes, Long fieldsId);    
    public void updateInMemoryFieldsForAll(Collection additionalFields);
    public void updateInMemoryFieldsForFields(AdditionalFields nextFields);
    public void updateMtGenomeStateForAdditionalFields(Integer newMtGenomeState, Long id);
    public void updateMicroCTStateForAdditionalFields(Integer newMicroCTState, Long id);
    public void updateMicroCTPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId);
    public void updateMicroCTNotesForAdditionalFields(String notes, Long fieldsId);    
    public void moveAdditionalFieldsToOther(AdditionalFields sourceFields, AdditionalFields destFields, 
    		boolean moveTier, boolean moveMtGenome, boolean moveDNA, boolean moveAdults, boolean moveLarvae);
    public void moveAdditionalFieldsToOther(AdditionalFields sourceFields, AdditionalFields destFields, 
    		boolean moveTier, boolean moveMtGenome, boolean moveDNA, boolean moveAdults, boolean moveLarvae, 
    		boolean moveMicroCT);
    public void updateGeographicDistributionForAdditionalFields(String newValue, Long id);
}
