package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.treegrow.main.Contributor;

public class AdditionalFieldsDAOImpl extends BaseDAOImpl implements
		AdditionalFieldsDAO {
	private ContributorDAO contributorDAO;
	public void updateInMemoryFieldsForAll(Collection additionalFields) {
		for (Iterator iter = additionalFields.iterator(); iter.hasNext();) {
			AdditionalFields nextFields = (AdditionalFields) iter.next();
			if (nextFields != null) {
				updateInMemoryFieldsForFields(nextFields);
			}
		}
	}
	public void updateInMemoryFieldsForFields(AdditionalFields nextFields) {
		ContributorDAO dao = getContributorDAO();
		nextFields.setLarvalDestinationPerson(dao.getContributorWithId(nextFields.getLarvalDestinationPersonId()));
		nextFields.setLarvalPossessionPerson(dao.getContributorWithId(nextFields.getLarvalPossessionPersonId()));
		nextFields.setAdultDestinationPerson(dao.getContributorWithId(nextFields.getAdultDestinationPersonId()));
		nextFields.setAdultPossessionPerson(dao.getContributorWithId(nextFields.getAdultPossessionPersonId()));
		nextFields.setDnaDestinationPerson(dao.getContributorWithId(nextFields.getDnaDestinationPersonId()));
		nextFields.setDnaPossessionPerson(dao.getContributorWithId(nextFields.getDnaPossessionPersonId()));	
		nextFields.setMicroCTPossessionPerson(dao.getContributorWithId(nextFields.getMicroCTPossessionPersonId()));
	}
	public void saveAdditionalFields(AdditionalFields fields) {
		getHibernateTemplate().saveOrUpdate(fields);
	}
	public void saveAdditionalFields(Collection additionalFields) {
		getHibernateTemplate().saveOrUpdateAll(additionalFields);
	}
	public void updateTierForAdditionalFields(int newTier, Long fieldsId) {
		executeUpdateQuery("update org.tolweb.btol.AdditionalFields set tier=" + newTier + " where id=" + fieldsId);
	}
	public void updateLarvalPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "larvalPossessionPersonId");
	}
	public void updateLarvalDestinationPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "larvalDestinationPersonId");
	}
	public void updateAdultPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "adultPossessionPersonId");
	}
	public void updateAdultDestinationPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "adultDestinationPersonId");
	}
	public void updateDnaPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "dnaPossessionPersonId");
	}
	public void updateDnaDestinationPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "dnaDestinationPersonId");
	}
    public void updateLarvalSpecimensStateForAdditionalFields(Integer newSpecimensState, Long id) {
    	doStateUpdate(newSpecimensState, id, "larvalSpecimensState");
    }
    public void updateAdultSpecimensStateForAdditionalFields(Integer newSpecimensState, Long id) {
    	doStateUpdate(newSpecimensState, id, "adultSpecimensState");
    }
    public void updateDnaSpecimensStateForAdditionalFields(Integer newSpecimensState, Long id) {
    	doStateUpdate(newSpecimensState, id, "dnaSpecimensState");
    }    
    public void updateMtGenomeStateForAdditionalFields(Integer newGenomeState, Long id) {
    	doStateUpdate(newGenomeState, id, "mtGenomeState");
    }
    public void updateMicroCTStateForAdditionalFields(Integer newMicroCTState, Long id) {
    	doStateUpdate(newMicroCTState, id, "microCTState");
    }    
	public void updateMicroCTPossessionPersonForAdditionalFields(Contributor contr, Long fieldsId) {
		doPersonUpdate(contr, fieldsId, "microCTPossessionPersonId");
	}
	public void updateMicroCTNotesForAdditionalFields(String notes, Long id) {
    	doNotesUpdate("microCTNotes", notes, id);				
	}	
    private void doStateUpdate(Integer newSpecimensState, Long id, String field) {
        executeUpdateQuery("update org.tolweb.btol.AdditionalFields set " + field + "=" + newSpecimensState + " where id=" + id);    	
    }
	private void doPersonUpdate(Contributor contr, Long fieldsId, String column) {
		Object contributorId = contr != null ? Integer.valueOf(contr.getId()) : "NULL";
		executeRawSQLUpdate("update AdditionalFields set " + column + "=" + contributorId + " where id=" + fieldsId);		
	}
    public void updateTierNotesForAdditionalFields(String value, Long id) {
    	doNotesUpdate("tierNotes", value, id);
    }
	public void updateLarvalNotesForAdditionalFields(String notes, Long fieldsId) {
    	doNotesUpdate("larvalNotes", notes, fieldsId);		
	}
	public void updateAdultNotesForAdditionalFields(String notes, Long id) {
    	doNotesUpdate("adultNotes", notes, id);		
	}
	public void updateDnaNotesForAdditionalFields(String notes, Long id) {
    	doNotesUpdate("dnaNotes", notes, id);				
	}
	public void updateGeographicDistributionForAdditionalFields(String geo, Long id) {
		doNotesUpdate("geographicDistribution", geo, id);
	}
	private void doNotesUpdate(String columnName, String newValue, Long fieldsId) {
    	Hashtable<String, Object> args = new Hashtable<String, Object>();
    	args.put("notes", newValue);
    	executeUpdateQuery("update org.tolweb.btol.AdditionalFields set " + columnName + "=:notes where id=" + fieldsId, args);		
	}
    public void moveAdditionalFieldsToOther(AdditionalFields sourceFields, AdditionalFields destFields, 
    		boolean moveTier, boolean moveMtGenome, boolean moveDNA, boolean moveAdults, boolean moveLarvae) {
    	moveAdditionalFieldsToOther(sourceFields, destFields, moveTier, moveMtGenome, moveDNA, moveAdults, moveLarvae, false);
    }
    public void moveAdditionalFieldsToOther(AdditionalFields sourceFields, AdditionalFields destFields, 
    		boolean moveTier, boolean moveMtGenome, boolean moveDNA, boolean moveAdults, boolean moveLarvae,
    		boolean moveMicroCT) {
    	// they're the same thing, so don't bother
    	if (sourceFields == destFields) {
    		return;
    	}
    	if (moveTier) {
    		destFields.setTier(sourceFields.getTier());
    		destFields.setTierNotes(sourceFields.getTierNotes());
    		sourceFields.setTier(AdditionalFields.NO_TIER_SET);
    		sourceFields.setTierNotes(null);
    	}
    	if (moveMtGenome) {
    		destFields.setMtGenomeState(sourceFields.getMtGenomeState());
    		// if this is Tier1 = set to BYU Needs (aka NO_MT_GENOME)
    		if (sourceFields.getTier() == 1)
    			sourceFields.setMtGenomeState(AdditionalFields.NO_MT_GENOME);
    		else // otherwise, we default to not needing it
    			sourceFields.setMtGenomeState(AdditionalFields.MT_GENOME_NOT_NEEDED);
    	}
    	if (moveDNA) {
    		destFields.setDnaDestinationPerson(sourceFields.getDnaDestinationPerson());
    		destFields.setDnaNotes(sourceFields.getDnaNotes());
    		destFields.setDnaPossessionPerson(sourceFields.getDnaPossessionPerson());
    		destFields.setDnaSpecimensState(sourceFields.getDnaSpecimensState());
    		sourceFields.setDnaDestinationPerson(null);
    		sourceFields.setDnaNotes(null);
    		sourceFields.setDnaPossessionPerson(null);
    		sourceFields.setDnaSpecimensState(AdditionalFields.DONT_HAVE_ANY);
    	}
    	if (moveAdults) {
    		destFields.setAdultDestinationPerson(sourceFields.getAdultDestinationPerson());
    		destFields.setAdultNotes(sourceFields.getAdultNotes());
    		destFields.setAdultPossessionPerson(sourceFields.getAdultPossessionPerson());
    		destFields.setAdultSpecimensState(sourceFields.getAdultSpecimensState());
    		sourceFields.setAdultDestinationPerson(null);
    		sourceFields.setAdultNotes(null);
    		sourceFields.setAdultPossessionPerson(null);
    		sourceFields.setAdultSpecimensState(AdditionalFields.DONT_HAVE_ANY);    		
    	}
    	if (moveLarvae) {
    		destFields.setLarvalDestinationPerson(sourceFields.getLarvalDestinationPerson());
    		destFields.setLarvalNotes(sourceFields.getLarvalNotes());
    		destFields.setLarvalPossessionPerson(sourceFields.getLarvalPossessionPerson());
    		destFields.setLarvalSpecimensState(sourceFields.getLarvalSpecimensState());
    		sourceFields.setLarvalDestinationPerson(null);
    		sourceFields.setLarvalNotes(null);
    		sourceFields.setLarvalPossessionPerson(null);
    		sourceFields.setLarvalSpecimensState(AdditionalFields.DONT_HAVE_ANY);    		
    	}
    	if (moveMicroCT) {
    		// set the pertinent fields for moving MicroCT data (state, notes, possession person)
    		destFields.setMicroCTPossessionPerson(sourceFields.getMicroCTPossessionPerson());
    		destFields.setMicroCTNotes(sourceFields.getMicroCTNotes());
    		destFields.setMicroCTState(sourceFields.getMicroCTState());
    		// clear out the old values left in memory
    		sourceFields.setMicroCTPossessionPerson(null);
    		sourceFields.setMicroCTNotes(null);
    		sourceFields.setMicroCTState(AdditionalFields.MICRO_CT_NOT_NEEDED);
    	}
    	getHibernateTemplate().saveOrUpdate(sourceFields);
    	getHibernateTemplate().saveOrUpdate(destFields);
    }
	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}
	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}
}
