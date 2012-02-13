package org.tolweb.btol.dao;

import java.util.Arrays;
import java.util.List;

import org.tolweb.btol.Chromatogram;
import org.tolweb.btol.ChromatogramBatch;
import org.tolweb.treegrow.main.StringUtils;

public class ChromatogramBatchDAOImpl extends ProjectAssociatedDAOImpl implements ChromatogramBatchDAO {
	public void saveChromatogram(Chromatogram chromatogram) {
		getHibernateTemplate().saveOrUpdate(chromatogram);
	}
	public void saveChromatogramBatch(ChromatogramBatch batch, Long projectId) {
		doProjectAssociatedSave(batch, projectId, getJoinTableName());
	}
	public ChromatogramBatch getChromatogramBatchWithId(Long value) {
		return (ChromatogramBatch) getHibernateTemplate().load(ChromatogramBatch.class, value);
	}
	public String getForeignKeyColumnName() {
		return "chromatogramBatchId";
	}
	public String getJoinTableName() {
		return "ChromatogramBatchesToProjects";
	}
	public ChromatogramBatch getChromatogramBatchWithName(String batchName) {
		String queryString = "from org.tolweb.btol.ChromatogramBatch where name=?";
		return (ChromatogramBatch) getFirstObjectFromQuery(queryString, batchName);
	}
	public List<String> getChromatogramFilenamesInBatchWithName(String batchName) {
		List strings = Arrays.asList(batchName.split(", "));
		String queryString = "select c.filename from org.tolweb.btol.ChromatogramBatch b join b.chromatograms as c where b.name " 
			+ StringUtils.returnSqlCollectionString(strings, true);
		return getHibernateTemplate().find(queryString);
	}
	public List<Chromatogram> getChromatogramsWithFilenames(List filenames, Long projectId) {
		String queryString = "select c from org.tolweb.btol.Project p join p.chromatogramBatchesSet as b join b.chromatograms as c where p.id=" + projectId + 
			" and c.filename " + StringUtils.returnSqlCollectionString(filenames, true);
		return getHibernateTemplate().find(queryString);
	}
}
