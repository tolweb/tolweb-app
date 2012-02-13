package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Chromatogram;
import org.tolweb.btol.ChromatogramBatch;
import org.tolweb.dao.BaseDAO;

public interface ChromatogramBatchDAO extends BaseDAO {
	public void saveChromatogram(Chromatogram chromatogram);
	public void saveChromatogramBatch(ChromatogramBatch batch, Long projectId);
	public ChromatogramBatch getChromatogramBatchWithId(Long value);
	public List<String> getChromatogramFilenamesInBatchWithName(String batchName);
	public List<Chromatogram> getChromatogramsWithFilenames(List filenames, Long projectId);
}
