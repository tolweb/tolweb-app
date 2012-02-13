package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.PCRReaction;
import org.tolweb.dao.BaseDAOImpl;

public class PCRReactionDAOImpl extends BaseDAOImpl implements PCRReactionDAO  {
	public PCRReaction getReactionWithCode(String code) {
		if (code == null) {
			return null;
		} else {
			return (PCRReaction) getFirstObjectFromQuery("from org.tolweb.btol.PCRReaction where btolCode=?", code);
		}
	}

	public void saveReactions(List<PCRReaction> reactions) {
		getHibernateTemplate().saveOrUpdateAll(reactions);
	}
	public void saveReaction(PCRReaction reaction) {
		getHibernateTemplate().saveOrUpdate(reaction);
	}
}
