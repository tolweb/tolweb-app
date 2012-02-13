package org.tolweb.tapestry.xml.taxaimport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.TaxaImportRecord;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ViewTaxaImportRecord extends BasePage implements IExternalPage, MetaInjectable, NodeInjectable {

	public SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
	
	public abstract TaxaImportRecord getCurrentRecord();
	public abstract void setCurrentRecord(TaxaImportRecord record);
	
	public abstract String getDisplayXml();
	public abstract void setDisplayXml(String xml);
	
	public abstract String getDate();
	public abstract void setDate(String value);
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentRecord().getBasalNodeId());
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}	
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		String idParam = cycle.getParameter("id");
		Long id = null;
		if (StringUtils.isEmpty(idParam) && args.length == 1) {
			id = (Long)args[0];
		}
		if (args.length == 2) {
			Long basalNodeId = (Long)args[0];
			TaxaImportRecord record = getTaxaImportLogDAO().getLatestTaxaImportRecordWithNodeId(basalNodeId);
			initializeRecord(record);
		}
		if (id != null) {
			initializeRecord(id);
		}
	}	
	
	private void initializeRecord(Long id) {
		TaxaImportRecord record = getTaxaImportLogDAO().getTaxaImportRecordWithId(id);
		initializeRecord(record);
	}
	
	private void initializeRecord(TaxaImportRecord record) {
		setCurrentRecord(record);
		initializeXml(record.getIngest());
		setDate(dateFormat.format(record.getTimestamp()));
	}
	
	protected void initializeXml(String ingest) {
        try {
    		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();            
        	Builder parser = new Builder();
            Document doc = parser.build(new StringReader(ingest));
            Serializer serializer = new Serializer(fileOut, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.setPreserveBaseURI(false);
            serializer.write(doc);
            serializer.flush();
            setDisplayXml(fileOut.toString());
          } catch (ParsingException ex) {
            System.out.println(ingest + " is not well-formed.");
            System.out.println(ex.getMessage());
          } catch (IOException ioe) {
        	  
          }
	}	
}
