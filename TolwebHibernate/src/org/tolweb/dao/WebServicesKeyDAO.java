package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.WebServicesKey;

public interface WebServicesKeyDAO extends BaseDAO {
	public void createWebServicesKey(WebServicesKey wsKey);
	public WebServicesKey getWebServicesKeyWithId(Long id);
	public WebServicesKey getWebServicesKeyWithEmail(String email);
	public boolean getWebServicesKeyExistsWithEmail(String email);
	public List getAllWebServicesKeys();
}
