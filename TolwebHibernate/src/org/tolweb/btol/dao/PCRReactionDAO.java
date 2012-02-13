package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.PCRReaction;
import org.tolweb.dao.BaseDAO;

public interface PCRReactionDAO extends BaseDAO {
	public PCRReaction getReactionWithCode(String code);
	public void saveReactions(List<PCRReaction> reactions);
	public void saveReaction(PCRReaction reaction);
}
