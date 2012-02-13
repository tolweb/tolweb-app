package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.misc.MetaNode;

public class MetaNodePreparer extends XmlPreparerImpl implements XmlPreparer {
	private MetaNode mnode;
	
	public MetaNodePreparer(MetaNode mnode) {
		this(mnode, NS);
	}

	public MetaNodePreparer(MetaNode mnode, String namespace) {
		super(namespace);
		this.mnode = mnode;
	}
	
	@Override
	public Element toElement() {
		Element nodeContainer = createElement("node");
		addAttributes(nodeContainer);
		addAttachedObjects(nodeContainer);
		return nodeContainer;
	}

	private void addAttachedObjects(Element node) {
		addAttachedPage(node);
		addAttachedAccessoryPages(node);
		addAttachedMedia(node);
		addAttachedOtherNames(node);
		addAttachedContributors(node);
	}

	@SuppressWarnings("unchecked")
	private boolean shouldInclude(List lst) {
		return lst != null && !lst.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private boolean shouldInclude(Set set) {
		return set != null && !set.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private boolean shouldInclude(Map map) {
		return map != null && !map.isEmpty();
	}
	private void addAttachedContributors(Element node) {
		ContributorsPreparer contrPrep = new ContributorsPreparer(mnode.getContributors());
		if (shouldInclude(mnode.getContributors())) {
			node.appendChild(contrPrep.toElement());
		}
	}
	
	private void addAttachedOtherNames(Element node) {
		OtherNamesPreparer otherNamePrep = new OtherNamesPreparer(mnode.getOtherNameIds());
		if (shouldInclude(mnode.getOtherNameIds())) {
			node.appendChild(otherNamePrep.toElement());
		}
	}
	
	private void addAttachedMedia(Element node) {
		MediaFilePreparer mediaPrep = new MediaFilePreparer(mnode.getMedia());
		if (shouldInclude(mnode.getMedia())) {
			node.appendChild(mediaPrep.toElement());
		}
	}
	
	private void addAttachedAccessoryPages(Element node) {
		MappedAccessoryPagesPreparer accPagePrep = new MappedAccessoryPagesPreparer(mnode.getAccessoryPages());
		if (shouldInclude(mnode.getAccessoryPages())) {
			node.appendChild(accPagePrep.toElement());
		}
	}
	
	private void addAttachedPage(Element node) {
		MappedPagePreparer pagePrep = new MappedPagePreparer(mnode.getPage());
		if (mnode.getPage() != null) {
			node.appendChild(pagePrep.toElement());
		}
	}
	
	private void addAttributes(Element node) {
		node.addAttribute(new Attribute("id", ""+getMetaNode().getNode().getId()));
		node.addAttribute(new Attribute("name", ""+getMetaNode().getNode().getName()));
	}
	
	/**
	 * @return the mnode
	 */
	public MetaNode getMetaNode() {
		return mnode;
	}

	/**
	 * @param mnode the mnode to set
	 */
	public void setMetaNode(MetaNode mnode) {
		this.mnode = mnode;
	}
}
