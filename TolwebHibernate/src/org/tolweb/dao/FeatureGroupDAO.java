package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.FeatureGroup;
import org.tolweb.misc.FeatureGroupCategory;

public interface FeatureGroupDAO extends BaseDAO {
	public void create(FeatureGroup fgrp);
	public void save(FeatureGroup fgrp);
	public FeatureGroup getFeatureGroupWithId(Long id);
	public List<FeatureGroup> getFeatureGroupsWithCategory(FeatureGroupCategory category);
	public List<FeatureGroup> getFeatureGroupsWithString(String category);
	public List<FeatureGroup> getAllFeatureGroups();
	public FeatureGroup getRandomFeatureGroup();
}
