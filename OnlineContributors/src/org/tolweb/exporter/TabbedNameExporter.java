package org.tolweb.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

public class TabbedNameExporter {
	private NodeDAO workingNodeDAO;
	private NodeDAO publicNodeDAO;
	private NodeDAO miscNodeDAO;
	private static final char SEPARATOR = '\t';
	private static final char NAME_SEPARATOR = ' ';
	private static final char DATE_SEPARATOR = ' ';
	private static MappedNodeInfoExtractor nodeExtractor = new MappedNodeInfoExtractor();
	private static ObjectArrayInfoExtractor arrayExtractor = new ObjectArrayInfoExtractor();
	
	public String getTabSeparatedTreeStringForBtol(Long rootNodeId, Hashtable<Long, List<MappedNode>> nodes, MappedNode rootNode) {
		TaxonNameDecorator preNameDecorator = new TaxonNameDecorator() {
			public String getTaxonNameDecorationString(MappedNode sourceNode) {
				if (sourceNode.getAdditionalFields() != null) {
					int tier = sourceNode.getAdditionalFields().getTier();
					if (tier == 1) {
						return "<span class=\"boldtext\">";
					}
				}
				return null;				
			}			
		};
		TaxonNameDecorator postNameDecorator = new TaxonNameDecorator() {
			public String getTaxonNameDecorationString(MappedNode sourceNode) {
				if (sourceNode.getAdditionalFields() != null) {
					int tier = sourceNode.getAdditionalFields().getTier();
					if (tier == 1) {
						return "***</span>";
					} else if (tier == 2) {
						return "*";
					}
				}
				return null;				
			}
		};
		return buildTabSeparatedStringBuffer(rootNodeId, nodes, nodeExtractor, nodes, rootNode, preNameDecorator, postNameDecorator).toString();
	}
	
	@SuppressWarnings("unchecked")
	public String getTabSeparatedTreeString(Long rootNodeId, boolean isWorking) {
		NodeDAO nodeDAO;

		Hashtable<Long, Object[]> nodeIdToInfo = new Hashtable<Long, Object[]>();
		if (isWorking) {
			nodeDAO = getWorkingNodeDAO(); 
		} else {
			nodeDAO = getPublicNodeDAO();
		}
		Collection descendantIds = getMiscNodeDAO().getDescendantIdsForNode(rootNodeId, true);
		List results = nodeDAO.getDescendantsForTabbedExport(rootNodeId, descendantIds);
		Hashtable<Long, List<Object[]>> parentNodeIdsToChildren = buildParentNodeIdsToChildrenHash(results, nodeIdToInfo); 

		// got the hash built up, so iterate over the children and write out
		// the stringbuffer
		StringBuffer exportBuffer = buildTabSeparatedStringBuffer(rootNodeId, parentNodeIdsToChildren, arrayExtractor, nodeIdToInfo, null, null, null);
		return exportBuffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	private Hashtable<Long, List<Object[]>> buildParentNodeIdsToChildrenHash(List<Object[]> results, Hashtable<Long, Object[]> nodeIdToInfo) {
		Hashtable<Long, List<Object[]>> parentNodeIdsToChildren = new Hashtable<Long, List<Object[]>>();
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] nextResult = (Object[]) iter.next();
			Long parentNodeId = (Long) nextResult[5];
			Long nodeId = (Long) nextResult[0];
			nodeIdToInfo.put(nodeId, nextResult);
			List children = parentNodeIdsToChildren.get(parentNodeId);
			if (children == null) {
				children = new ArrayList<Object[]>();
				parentNodeIdsToChildren.put(parentNodeId, children);
			}
			children.add(nextResult);
		}
		return parentNodeIdsToChildren;
	}
	
	@SuppressWarnings("unchecked")
	private StringBuffer buildTabSeparatedStringBuffer(Long rootNodeId, Hashtable nodeIdsToChildrenObjects, 
			NodeInfoExtractor extractor, Object sourceObject, Object rootNode, TaxonNameDecorator preNameDecorator, TaxonNameDecorator postNameDecorator) {
		// got the hash built up, so iterate over the children and write out
		// the stringbuffer
		StringBuffer exportBuffer = new StringBuffer();
		Stack<ParentIdNumTabs> parentIdStack = new Stack<ParentIdNumTabs>();
		ParentIdNumTabs rootObj = new ParentIdNumTabs(rootNodeId, 0);
		parentIdStack.push(rootObj);
		while (!parentIdStack.isEmpty()) {
			ParentIdNumTabs currentParentId = parentIdStack.pop();
			List children = (List) nodeIdsToChildrenObjects.get(currentParentId.getParentId());
			Object currentNode = extractor.getObjectToActOn(sourceObject, currentParentId.getParentId());
			if (currentNode == null) {
				currentNode = rootNode;
			}
			if (children != null) {
				String nodeName = extractor.getNodeName(currentNode);
				int currentNumTabs = currentParentId.getNumTabs();
				if (StringUtils.notEmpty(nodeName)) {
					currentNumTabs = currentParentId.getNumTabs() + 1;
				}
				Stack<ParentIdNumTabs> tempStack = new Stack<ParentIdNumTabs>();
				for (Object node : children) {
					Long nextId = extractor.getNodeId(node);
					ParentIdNumTabs nextObj = new ParentIdNumTabs(nextId, currentNumTabs);
					// use a temp stack so things don't get backwarsd
					tempStack.push(nextObj);
				}
				// unpop the temp stakc to get them in the right order
				while (!tempStack.empty()) {
					parentIdStack.push(tempStack.pop());
				}
			}
			appendCurrentNodeInfoToBuffer(exportBuffer, currentParentId.getNumTabs(), currentNode, extractor, preNameDecorator, postNameDecorator);			
		}	
		return exportBuffer;
	}
	
	private void appendCurrentNodeInfoToBuffer(StringBuffer buffer, int numTabs, Object node, NodeInfoExtractor extractor, TaxonNameDecorator preNameDecorator, TaxonNameDecorator postNameDecorator) {
		String nodeName = extractor.getNodeName(node);
		if (StringUtils.notEmpty(nodeName)) {
			for (int i = 0; i < numTabs; i++) {
				buffer.append(SEPARATOR);
			}
			addDecorationToBuffer(buffer, node, preNameDecorator);
			buffer.append(nodeName);
			if (extractor.getIsExtinct(node)) {
				buffer.append('+');
			}
			String authority = extractor.getAuthority(node);
			if (StringUtils.notEmpty(authority)) {
				buffer.append(NAME_SEPARATOR);
				buffer.append(authority);
				Integer authDate = extractor.getAuthorityDate(node);
				if (authDate != null && authDate > 0) {
					buffer.append(DATE_SEPARATOR);
					buffer.append(authDate);
				}
			}
			addDecorationToBuffer(buffer, node, postNameDecorator);
			buffer.append('\n');
		}
	}

	private void addDecorationToBuffer(StringBuffer buffer, Object node, TaxonNameDecorator decorator) {
		if (decorator != null && MappedNode.class.isInstance(node)) {
			String decorationString = decorator.getTaxonNameDecorationString((MappedNode) node);
			if (StringUtils.notEmpty(decorationString)) {
				buffer.append(decorationString);
			}
		}
	}
	public NodeDAO getPublicNodeDAO() {
		return publicNodeDAO;
	}
	public void setPublicNodeDAO(NodeDAO publicNodeDAO) {
		this.publicNodeDAO = publicNodeDAO;
	}
	public NodeDAO getWorkingNodeDAO() {
		return workingNodeDAO;
	}
	public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
		this.workingNodeDAO = workingNodeDAO;
	}
	public NodeDAO getMiscNodeDAO() {
		return miscNodeDAO;
	}
	public void setMiscNodeDAO(NodeDAO miscNodeDAO) {
		this.miscNodeDAO = miscNodeDAO;
	}	
	private static class ParentIdNumTabs {
		private Long parentId;
		private int numTabs;
		public ParentIdNumTabs(Long parentId, int numTabs) {
			this.parentId = parentId;
			this.numTabs = numTabs;
		}
		public Long getParentId() {
			return parentId;
		}
		public void setParentId(Long parentId) {
			this.parentId = parentId;
		}
		public int getNumTabs() {
			return numTabs;
		}
		public void setNumTabs(int numTabs) {
			this.numTabs = numTabs;
		}
	}
	/**
	 * Interface for extracting node info from different sorts of objects 
	 * (MappedNodes & Object arrays are two)
	 */
	private static interface NodeInfoExtractor {
		public String getNodeName(Object node);
		public boolean getIsExtinct(Object node);
		public String getAuthority(Object node);
		public Integer getAuthorityDate(Object node);
		public Object getObjectToActOn(Object sources, Long nodeId);
		public Long getNodeId(Object node);
	}
	private static class MappedNodeInfoExtractor implements NodeInfoExtractor {
		public String getNodeName(Object node) {
			return ((MappedNode) node).getName();
		}
		public boolean getIsExtinct(Object node) {
			return ((MappedNode) node).getExtinct() == Node.EXTINCT;
		}
		public String getAuthority(Object node) {
			// in this case we don't want the authority
			//return ((MappedNode) node).getNameAuthority();
			return "";
		}
		public Integer getAuthorityDate(Object node) {
			//return ((MappedNode) node).getAuthorityDate();
			return null;
		}
		@SuppressWarnings("unchecked")
		public Object getObjectToActOn(Object sources, Long nodeId) {
			Hashtable<Long, List<MappedNode>> nodes = (Hashtable<Long, List<MappedNode>>) sources;
			return TaxaIndex.getNodeFromHashtable(nodeId, nodes);
		}
		public Long getNodeId(Object node) {
			return ((MappedNode) node).getNodeId();
		}
	}
	private static class ObjectArrayInfoExtractor implements NodeInfoExtractor {
		public String getNodeName(Object node) {
			return (String) ((Object[]) node)[1];
		}
		public boolean getIsExtinct(Object node) {
			return (Integer) ((Object[]) node)[4] == Node.EXTINCT;
		}
		public String getAuthority(Object node) {
			return (String) ((Object[]) node)[2];
		}
		public Integer getAuthorityDate(Object node) {
			return (Integer) ((Object[]) node)[3];
		}
		@SuppressWarnings("unchecked")
		public Object getObjectToActOn(Object sources, Long nodeId) {
			return ((Hashtable) sources).get(nodeId);
		}
		public Long getNodeId(Object node) {
			return (Long) ((Object[]) node)[0];
		}
	}
}
