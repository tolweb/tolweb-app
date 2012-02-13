package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.WebServicesKey;

public class WebServicesKeyDAOTest extends ApplicationContextTestAbstract {
	private WebServicesKeyDAO dao;
	
	public WebServicesKeyDAOTest(String name) {
		super(name);
		dao = (WebServicesKeyDAO)context.getBean("webServicesKeyDAO");
	}
	
	public void testWebServicesKeyCreate() {
		WebServicesKey wsKey = WebServicesKey.createWebServicesKey();
		wsKey.setUserName("Andrew Lenards");
		wsKey.setUserEmail("andrew..lenards@gmail.com");
		wsKey.setUserUrl("http://gigism.com/");
		wsKey.setIntendedUse("dominate the world");
		wsKey.setUseCategory((byte)0);
		dao.createWebServicesKey(wsKey);
	}
	
	@SuppressWarnings("unchecked")
	public void testFindSomeWebServicesKeys() {
		List keys = dao.getAllWebServicesKeys();
		assertTrue(keys != null);
		assertTrue(keys.size() > 0);
	}
	
	public void testRetrieveWebServicesKeyWithId() {
		WebServicesKey wsKey = dao.getWebServicesKeyWithId(new Long(1));
		assertTrue(wsKey != null);
		assertTrue(wsKey.getId() == 1L);
	}
	
	public void testRetrieveWebServicesKeyWithEmail() {
		String email = "andrew.lenards@gmail.com";
		WebServicesKey wsKey = dao.getWebServicesKeyWithEmail(email);
		assertTrue(wsKey != null);
		assertTrue(wsKey.getUserEmail().equals(email));		
	}
	
	public void testExistsWorks() {
		String email = "andrew.lenards@gmail.com";
		boolean exists = dao.getWebServicesKeyExistsWithEmail(email);
		assertTrue(exists);
	}
}
