package org.tolweb.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;

public class NodeOtherNameRelationTest extends ApplicationContextTestAbstract {
	private NodeDAO nodeDAO;
	private OtherNameDAO otherNameDAO;
	private PageDAO pageDAO;
	private SessionFactory sessionFactory; 
	private Session session;
	
	public NodeOtherNameRelationTest(String name) {
		super(name);
		nodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		otherNameDAO = (OtherNameDAO)context.getBean("workingOtherNameDAO");
		pageDAO = (PageDAO)context.getBean("workingPageDAO");
		assertNotNull(nodeDAO);
		assertNotNull(otherNameDAO);
		assertNotNull(pageDAO);
	}
	
	@SuppressWarnings("unchecked")
	public void testOtherNamesFetch() {
		
		sessionFactory = (SessionFactory)context.getBean("workingSessionFactory");

	    session = SessionFactoryUtils.getSession(sessionFactory, true);
	    TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));		
		
		boolean exists = nodeDAO.getNodeExistsWithId(new Long(2383));
		assertTrue(exists);
		MappedNode nd = nodeDAO.getNodeWithId(new Long(2383));
		assertNotNull(nd);
		Set syns = nd.getSynonyms();
		assertNotNull(syns);
		for(Iterator itr = syns.iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName)itr.next();
			String otherNameId = (moname.getOtherNameId() != null) ? moname.getOtherNameId().toString() : "<null-id>";
			System.out.println(moname + " | " + otherNameId);
		}
		
	    TransactionSynchronizationManager.unbindResource(sessionFactory);
	    SessionFactoryUtils.releaseSession(session, sessionFactory);		
	}
	
	@SuppressWarnings("unchecked")
	public void testLifeOnEarth() {
		MappedNode life = nodeDAO.getNodeWithId(new Long(1));
		MappedPage page = pageDAO.getPage(life);
		
		List lst = pageDAO.getNodesOnPage(page, true);
		
		assertNotNull(lst);
		System.out.println(".... so we've made it this far huh? ");
		for(Iterator itr = lst.iterator(); itr.hasNext(); ) {
			MappedNode nd = (MappedNode)itr.next();
			if (nd == null) {
				System.out.println("WWWWOOOOWWW!!  You're NULL!");
			} else {
				System.out.println(nd.toString());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testFindNames() {
		List lst = nodeDAO.findNodesExactlyNamed("Hellbenders");
		assertNotNull(lst);
		System.out.println("list: " + lst.size());
		for(Iterator itr = lst.iterator(); itr.hasNext(); ) {
			MappedNode node = (MappedNode)itr.next();
			System.out.println("node-name: " + node.getName());
		}
	}
	
	
}
