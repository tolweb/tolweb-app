package org.tolweb.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.MetaNodeDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;

public class MetaNodeLoadTest extends ApplicationContextTestAbstract {
	private NodeDAO ndao; 
	private MetaNodeDAO mdao;
	
	public MetaNodeLoadTest(String name) {
		super(name);
		ndao = (NodeDAO)context.getBean("workingNodeDAO");
		mdao = (MetaNodeDAO)context.getBean("workingMetaNodeDAO");
	}

	public void test_meta_node_isnt_duplicating_other_names() {
		MappedNode mnode = createNewNode();
		List<Long> ids = new ArrayList<Long>();
		ids.add(mnode.getNodeId());
		List<MetaNode> metanodes = mdao.getMetaNodes(ids, true);
		MetaNode meta = metanodes.get(0);
		System.out.println(meta.toString());
	}
	
	@SuppressWarnings("unchecked")
	public MappedNode createNewNode() {
		MappedNode parent = ndao.getNodeWithId(122963L);
		MappedNode mnode = new MappedNode();
		mnode.setSynonyms(new TreeSet());
		mnode.setName("Metalkin " + System.currentTimeMillis());
		mnode.setParent(parent);
		mnode.setParentNodeId(parent.getNodeId());
		mnode.setOrderOnParent(parent.getChildren().size() + 1);
		mnode.setOrderOnPage(parent.getChildren().size() + 1);
		mnode.setPageId(parent.getPageId());

		MappedOtherName moname = new MappedOtherName();
		moname.setName("Oxel Metal");
		moname.setAuthority("Lenards");
		moname.setAuthorityYear(new Integer(2008));
		mnode.addSynonym(moname);
		ndao.saveNode(mnode);
		return mnode;
	}
}
