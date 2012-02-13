package org.tolweb.dao;

import java.util.ArrayList;
import java.util.List;

import org.tolweb.hibernate.FeatureGroup;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.FeatureGroupCategory;

public class FeatureGroupDAOTest extends ApplicationContextTestAbstract {
	private FeatureGroupDAO dao; 
	private NodeDAO nodeDAO; 
	
	public FeatureGroupDAOTest(String name) {
		super(name);
		dao = (FeatureGroupDAO)context.getBean("featureGroupDAO");
		nodeDAO = (NodeDAO)context.getBean("nodeDAO");
	}

	public void test_spring_configuration() {
		assertNotNull(dao);
		List<FeatureGroup> result = dao.getAllFeatureGroups();
		assertNotNull(result);
	}
	
	public void test_feature_group_save() {
		MappedNode mnode = nodeDAO.getNodeWithId(15994L);
		FeatureGroup grp = new FeatureGroup(mnode);
		grp.setCategory(FeatureGroupCategory.POPULAR);
		grp.setGroupDescription("Marsupials are awesome...");
		grp.setFeatureText("CHECK IS OUT");
		grp.setImageId(650);
		dao.create(grp);
		System.out.println(grp.getId());
		
		FeatureGroup fetch = dao.getFeatureGroupWithId(grp.getId());
		assertNotNull(fetch);
		assertEquals(grp.getId(), fetch.getId());
		assertEquals(grp.getCategory(), fetch.getCategory());
		assertEquals("("+grp.getGroupDescription()+")", fetch.getGroupDescription());
		assertEquals(grp.getFeatureText(), fetch.getFeatureText());
		assertEquals(grp.getImageId(), fetch.getImageId());
		
		fetch.setActive(false);
		fetch.setCategory(FeatureGroupCategory.OBSCURE);
		dao.save(fetch);
		
		FeatureGroup test = dao.getFeatureGroupWithId(fetch.getId());
		assertFalse(test.isActive());
		assertEquals(FeatureGroupCategory.OBSCURE, test.getCategory());
	}
	
	public void test_get_random_feature_group() {
		FeatureGroup grp1 = dao.getRandomFeatureGroup();
		FeatureGroup grp2 = dao.getRandomFeatureGroup();
		FeatureGroup grp3 = dao.getRandomFeatureGroup();
		FeatureGroup grp4 = dao.getRandomFeatureGroup();
		ArrayList<FeatureGroup> arrayList = new ArrayList<FeatureGroup>();
		arrayList.add(grp1);
		arrayList.add(grp2);
		arrayList.add(grp3);
		arrayList.add(grp4);
		System.out.println(arrayList);
	}
	
	public void test_get_feature_group_by_category() {
		List<FeatureGroup> obscure = dao.getFeatureGroupsWithCategory(FeatureGroupCategory.OBSCURE);
		List<FeatureGroup> obscureGrp = dao.getFeatureGroupsWithString("Obscure");
		assertNotNull(obscure);
		assertFalse(obscure.isEmpty());
		assertNotNull(obscureGrp);
		assertFalse(obscureGrp.isEmpty());		
		
		List<FeatureGroup> popular = dao.getFeatureGroupsWithCategory(FeatureGroupCategory.POPULAR);
		List<FeatureGroup> popularGrp = dao.getFeatureGroupsWithString("Popular");
		assertNotNull(popular);
		assertFalse(popular.isEmpty());
		assertNotNull(popularGrp);
		assertFalse(popularGrp.isEmpty());		
	}
}
