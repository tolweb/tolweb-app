package org.tolweb.tapestry.xml.taxaimport;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.ReattachmentProgressRecord;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.xml.taxaimport.beans.OMNode;
import org.tolweb.tapestry.xml.taxaimport.beans.OMRoot;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ViewReattachmentProgress extends ViewObjectManifest implements IExternalPage, MetaInjectable, 
	BaseInjectable, NodeInjectable, PageInjectable, ImageInjectable, UserInjectable, AccessoryInjectable,
	CookieInjectable, PageBeginRenderListener {

	@Persist("session")
	public abstract String getKey();
	public abstract void setKey(String key);
	
	@Persist("session")
	public abstract String getDate();
	public abstract void setDate(String date);
	
	@Persist("session")
	public abstract List<OMNode> getNodes();
	public abstract void setNodes(List<OMNode> nodes);
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		String key = cycle.getParameter("key");
		if (StringUtils.isEmpty(key) && args.length >= 1) {
			key = (String)args[0];
		} else {
			key = getKey();
		}
		System.out.println("\tkey: " + key);
		loadReattachmentData(key);
	}
	
	private void loadReattachmentData(String key) {
		ReattachmentProgressRecord record = getReattachmentProgressDAO().getReattachmentProgress(key);
		extractNodesFromManifest(record);
		setKey(record.getKeyValue());
		initializeManifestXml(record.getManifest());
		setDate(dateFormat.format(record.getTimestamp()));
	}

	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			System.out.println("\tkey: " + getKey() + " (null? " + StringUtils.isEmpty(getKey()) + ")");
			loadReattachmentData(getKey());
		}
	}
	
	private void extractNodesFromManifest(ReattachmentProgressRecord record) {
		try {
			
			StringReader reader = new StringReader(record.getManifest());
			BeanReader beanReader = new BeanReader();
			beanReader.registerBeanClass(OMRoot.class);
			OMRoot root = (OMRoot)beanReader.parse(reader);
			if (root != null) {
				setXmlNamespace(root.getXmlNamespace());
				setBasalNodeId(root.getBasalNodeId());				
				List<OMNode> nodes = root.getNodes(); 
				Collections.sort(nodes);
				setNodes(nodes);
				System.out.println("be happy... ");
			} else {
				System.out.println("bummer...");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			System.out.println("lame... exception");
		}
	}
}
