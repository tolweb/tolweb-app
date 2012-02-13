package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NewsItem;

public class NewsItemDAOTest extends ApplicationContextTestAbstract {
	private NewsItemDAO nidao; 
	
	public NewsItemDAOTest(String name) {
		super(name);
		nidao = (NewsItemDAO)context.getBean("newsItemDAO");
	}
	
	public void testNewsItemCreate() {
		NewsItem item = new NewsItem();
		item.setNewsItemText("We have revised our tools for image contributors. There's now a new Edit Image Data form ...");
		item.setCreatedBy("lenards@tolweb.org");
		nidao.save(item);
	}
	
	public void testFetchAllNewsItems() {
		List items = nidao.getAllNewsItems();
		System.out.println(items);
		assertNotNull(items);
		assertTrue(!items.isEmpty());
	}
}
