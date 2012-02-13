package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.TaxaImportRecord;

public interface TaxaImportLogDAO extends BaseDAO {
	public TaxaImportRecord getTaxaImportRecordWithId(Long id);
	public TaxaImportRecord getLatestTaxaImportRecordWithNodeId(Long basalNodeId);
	public void createTaxaImportRecord(TaxaImportRecord record);
	public void saveTaxaImportRecord(TaxaImportRecord record);
	public List<TaxaImportRecord> getAllTaxaImportRecords();
	public List<TaxaImportRecord> getAllTaxaImportRecords(boolean descOrderBy);
	public List<Object[]> getLatestTaxaImportRecords();
}
