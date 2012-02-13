package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.Sequence;
import org.tolweb.dao.BaseDAO;

public interface SequenceDAO extends BaseDAO {
	public void saveSequence(Sequence seq);
	public List<Sequence> getSequencesForReaction(PCRReaction reaction);
}
