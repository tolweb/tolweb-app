package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.EditedPageDAO;
import org.tolweb.treegrowserver.BatchPusher;
import org.tolweb.treegrowserver.BatchResultsBuilder;
import org.tolweb.treegrowserver.BatchSubmitter;
import org.tolweb.treegrowserver.DownloadBuilder;
import org.tolweb.treegrowserver.DownloadCheckin;
import org.tolweb.treegrowserver.NodeSearchResultsBuilder;
import org.tolweb.treegrowserver.ServerXMLReader;
import org.tolweb.treegrowserver.ServerXMLWriter;
import org.tolweb.treegrowserver.UploadBuilder;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.PublicationBatchDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;
import org.tolweb.treegrowserver.dao.UploadDAO;

public interface TreeGrowServerInjectable {
	@InjectObject("spring:downloadDAO")
	public abstract DownloadDAO getDownloadDAO();
	@InjectObject("spring:batchPusher")
	public abstract BatchPusher getBatchPusher();
	@InjectObject("spring:editedPageDAO")
	public abstract EditedPageDAO getEditedPageDAO();	
	@InjectObject("spring:uploadBuilder")
	public abstract UploadBuilder getUploadBuilder();
	@InjectObject("spring:uploadBatchDAO")
	public abstract UploadBatchDAO getUploadBatchDAO();
	@InjectObject("spring:publicationBatchDAO")
	public abstract PublicationBatchDAO getPublicationBatchDAO();
	@InjectObject("spring:batchSubmitter")
	public abstract BatchSubmitter getBatchSubmitter();
	@InjectObject("spring:downloadCheckin")
	public abstract DownloadCheckin getDownloadCheckin();
	@InjectObject("spring:batchResultsBuilder")
	public abstract BatchResultsBuilder getBatchResultsBuilder();
	@InjectObject("spring:nodeSearchResultsBuilder")
	public abstract NodeSearchResultsBuilder getNodeSearchResultsBuilder();
	@InjectObject("spring:serverXMLWriter")
	public abstract ServerXMLWriter getServerXMLWriter();
	@InjectObject("spring:publicServerXMLWriter")
	public abstract ServerXMLWriter getPublicServerXMLWriter();	
	@InjectObject("spring:serverXMLReader")
	public abstract ServerXMLReader getServerXMLReader();
	@InjectObject("spring:downloadBuilder")
	public abstract DownloadBuilder getDownloadBuilder();
	@InjectObject("spring:uploadDAO")
	public abstract UploadDAO getUploadDAO();
}
