package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NewsItem;

public interface NewsItemDAO extends BaseDAO {
	public void save(NewsItem item);
	public NewsItem getNewsItemWithId(Long id);
	public List getAllNewsItems();
}
