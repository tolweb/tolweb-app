package org.tolweb.tapestry.xml.taxaimport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.ObjectManifestRecord;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.xml.taxaimport.beans.OMAccessoryPage;
import org.tolweb.tapestry.xml.taxaimport.beans.OMContributor;
import org.tolweb.tapestry.xml.taxaimport.beans.OMMediaFile;
import org.tolweb.tapestry.xml.taxaimport.beans.OMNode;
import org.tolweb.tapestry.xml.taxaimport.beans.OMOtherName;
import org.tolweb.tapestry.xml.taxaimport.beans.OMRoot;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ViewObjectManifest extends BasePage implements IExternalPage, 
	MetaInjectable, BaseInjectable, NodeInjectable, PageInjectable, 
	ImageInjectable, UserInjectable, AccessoryInjectable, CookieInjectable {
	public SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
	
	public abstract String getXmlNamespace();
	public abstract void setXmlNamespace(String ns);	
	
	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);
	
	@Persist("session")
	public abstract String getKey();
	public abstract void setKey(String key);
	
	@Persist("session")
	public abstract String getDate();
	public abstract void setDate(String date);
	
	@Persist("session")
	public abstract List<OMNode> getNodes();
	public abstract void setNodes(List<OMNode> nodes);
	
	public abstract OMNode getCurrentNode();
	
	public List<OMContributor> getCurrentContributors() {
		return getCurrentNode().getContributors();
	} 
	
	public List<OMAccessoryPage> getCurrentAccessoryPages() {
		return getCurrentNode().getAccessorypages();
	}
	
	public List<OMOtherName> getCurrentOtherNames() {
		return getCurrentNode().getOthernames();
	}
	
	public List<OMMediaFile> getCurrentMediaFiles() {
		return getCurrentNode().getMediafiles();
	}
	
	public abstract OMContributor getCurrentContributor();
//	public abstract void setCurrentContributor(OMContributor contr);

	public String getCurrentContributorLink() {
		return (getCurrentContributor() != null) ? getPopupLinkText("/people", getCurrentContributor().getId()) : "[id-null]";
	}
	
	public abstract OMMediaFile getCurrentMediaFile();
//	public abstract void setCurrentMediaFile(OMMediaFile mediaFile);

	protected String getPopupLinkText(String relativeLink, Long id) {
		return "<a href=\"#\" onclick=\"javascript:window.open('"+relativeLink + "/" + id + 
			"', 'width=650,height=450,scrollbars=no,menubar=no,location=no,status=no,toolbar=no'); return false;\">" + 
			id + "</a>"; 
	}
	
	public String getCurrentMediaFileLink() { 
/*
<a href=\"#\" onclick=\"javascript:window.open('" + getUtils().getImageUrl(img) + "', '" + 
            	img.getId() + "', 'width=" + width + ",height=" + height + 
            	",scrollbars=no,menubar=no,location=no,status=no,toolbar=no'); return false;\">
span jwcid="@Insert" value=
									<span jwcid="@Insert" value="ognl:currentMediaFile.id" /></a>            	 
 */		
		return (getCurrentMediaFile() != null) ? getPopupLinkText("/media", getCurrentMediaFile().getId()) : "[id-null]";
	}
	
	public abstract OMOtherName getCurrentOtherName();
//	public abstract void setCurrentOtherName(OMOtherName othername);
	
	public abstract OMAccessoryPage getCurrentAccessoryPage();
//	public abstract void setCurrentAccessoryPage(OMAccessoryPage accPage);
	
	public String getCurrentAccessoryPageLink() {
		return (getCurrentAccessoryPage() != null) ? "notes/?note_id="+getCurrentAccessoryPage().getId() : "#";
	}

	public abstract String getCurrentManifestXml();
	public abstract void setCurrentManifestXml(String xml);
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		String key = cycle.getParameter("key");
		if (StringUtils.isEmpty(key) && args.length == 1) {
			key = (String)args[0];
		}
		if (args.length == 2) {
			Long basalNodeId = (Long)args[0];
			ObjectManifestRecord record = getObjectManifestLogDAO().getLatestObjectManifestRecordWithNodeId(basalNodeId);
			initializeRecord(record);
		}
		
		System.out.println("\tkey: " + key);
		if (StringUtils.notEmpty(key)) {
			initializeRecord(key);
		}
		
	}

	private void initializeRecord(String key) {
		ObjectManifestRecord record = getObjectManifestLogDAO().getObjectManifestRecord(key);
		initializeRecord(record);
	}
	
	private void initializeRecord(ObjectManifestRecord record) {
		extractNodesFromManifest(record);
		setKey(record.getKeyValue());
		//setCurrentManifestXml(record.getManifest());
		initializeManifestXml(record.getManifest());
		setDate(dateFormat.format(record.getTimestamp()));
	}
	
	protected void initializeManifestXml(String manifest) {

		
        try {
    		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();            
        	Builder parser = new Builder();
            Document doc = parser.build(new StringReader(manifest));
            Serializer serializer = new Serializer(fileOut, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.setPreserveBaseURI(false);
            serializer.write(doc);
            serializer.flush();
            setCurrentManifestXml(fileOut.toString());
          } catch (ParsingException ex) {
            System.out.println(manifest + " is not well-formed.");
            System.out.println(ex.getMessage());
          } catch (IOException ioe) {
        	  
          }
		
	}
	
	protected void extractNodesFromManifest(ObjectManifestRecord record) {
		try {
			
			StringReader reader = new StringReader(record.getManifest());
			BeanReader beanReader = new BeanReader();
			beanReader.registerBeanClass(OMRoot.class);
			OMRoot root = (OMRoot)beanReader.parse(reader);
			if (root != null) {
				setXmlNamespace(root.getXmlNamespace());
				setBasalNodeId(root.getBasalNodeId());				
				List<OMNode> nodes = root.getNodes();
				removeNodesWithEmptyNames(nodes);
				Collections.sort(nodes);
				setNodes(nodes);
				System.out.println("be happy... nodes parsed and set");
			} else {
				System.out.println("bummer... the root was null, something happened with betwixt parsing...");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("lame... exception");
		}
	}

	private void removeNodesWithEmptyNames(List<OMNode> nodes) {
		if (nodes != null) {
			List<Integer> removeIndexes = new ArrayList<Integer>();
			for (int i = 0; i < nodes.size(); i++) {
				OMNode curr = nodes.get(i);
				if (StringUtils.isEmpty(curr.getName())) {
					removeIndexes.add(i);
				}
			}
			for (int i = removeIndexes.size()-1; i >= 0; i--) {
				int rem = removeIndexes.get(i).intValue();
				nodes.remove(rem);
			}
		}
	}
	
    public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 350;
    	return getRendererFactory().getLinkRenderer("Object Manifest for " + getKey(), width, height);
    }	
}
