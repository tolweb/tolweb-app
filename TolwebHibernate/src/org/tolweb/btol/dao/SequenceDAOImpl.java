package org.tolweb.btol.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.btol.Chromatogram;
import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.Sequence;
import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.treegrow.main.StringUtils;

public class SequenceDAOImpl extends BaseDAOImpl implements SequenceDAO {
	public void saveSequence(Sequence seq) {
		getHibernateTemplate().saveOrUpdate(seq);
	}
	public List<Sequence> getSequencesForReaction(PCRReaction reaction) {
		Set<Long> chromatogramIds = new HashSet<Long>();
		for (Iterator iter = reaction.getChromatograms().iterator(); iter.hasNext();) {
			Chromatogram nextChro = (Chromatogram) iter.next();
			chromatogramIds.add(nextChro.getId());
		}
		if (chromatogramIds.size() > 0) {
			String queryString = "select distinct s from org.tolweb.btol.Sequence s join s.chromatograms as c where c.id " + 
				StringUtils.returnSqlCollectionString(chromatogramIds);
			return getHibernateTemplate().find(queryString);
		} else {
			return new ArrayList<Sequence>();
		}
	}
}
