package org.tolweb.treegrow.main;

/**
 * Class used to store search results about Nodes
 */
public class NodeDetails {
	protected String m_NodeId = null;
	protected String m_NodeName = null;
	protected String m_Depth = null;
	protected String m_Root = null;
	protected String m_TimeStamp = null;
	protected String m_User = null;
	protected String m_Type = null; //DOWNLOAD, APPROVAL
	protected String m_Status = null; //given so that we have an idea what is the status of this node
    private String parentPageName;
    private String parentPageNodeId;
    private boolean canDownload;
        /**
         * Stores the batch id associated with this node if it has been submitted
         * and the user has privileges to play with this submitted node
         */
        private String batchId = null;
        protected int downloadId;

	// Status values --  1) NOT_CHKD_OUT  2) CHKD_BY_ELSE  3) CHKD_BY_U_INTERNAL
	//                   4) CHKD_BY_U_AT_ROOT_FILE_EXISTS  5) CHKD_BY_U_AT_ROOT_FILE_DOESNT_EXIST

	public NodeDetails()
	{
	}

	public String getNodeId()
	{
		return m_NodeId;
	}
        
	public int getNodeIdInt() {
		return new Integer(m_NodeId).intValue();
	}
	public void setNodeId(String id)
	{
		m_NodeId = id;
	}

	public String getNodeName()
	{
		return m_NodeName;
	}

	public void setNodeName(String name)
	{
		m_NodeName = name;
	}
	
	public String getDepth()
	{
		return m_Depth;
	}

	public void setDepth(String depth)
	{
		m_Depth = depth;
	}
	
	public String getRoot()
	{
		return m_Root;
	}

	public void setRoot(String root)
	{
		m_Root = root;
	}
	
	public String getType()
	{
		return m_Type;
	}

	public void setType(String type)
	{
		m_Type = type;
	}

	
	public String getTimeStamp()
	{
		return m_TimeStamp;
	}

	public void setTimeStamp(String time)
	{
		String timeStamp = formatTime(time);
		m_TimeStamp = timeStamp;
	}
	
	public String getDate()
	{
		if(m_TimeStamp == null)
			return null;

		return m_TimeStamp.substring(0,11);
	}
	
	public String getUser()
	{
		return m_User;
	}

	public void setUser(String user)
	{
		m_User = user;
	}
	
	public String getStatus()
	{
		return m_Status;
	}

	public void setStatus(String status)
	{
		m_Status = status;
	}
        
    public void setDownloadId(int value) {
        downloadId = value;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setBatchId(String value) {
        batchId = value;
    }

    public String getBatchId() {
        return batchId;
    }
	
    public void setParentPageName(String value) {
        parentPageName = value;
    }
    
    public String getParentPageName() {
        return parentPageName;
    }
    
    public void setParentPageNodeId(String value) {
        parentPageNodeId = value;
    }
    
    public String getParentPageNodeId() {
        return parentPageNodeId;
    }
    
    public int getParentPageNodeIdInt() {
        return new Integer(parentPageNodeId).intValue();
    }
    
    public boolean canDownload() {
        return canDownload;
    }
    
    public void setCanDownload(boolean value) {
        canDownload = value;
    }

    private String formatTime(String time)
	{
		try
		{
			//20030211120823
                        //
			if(time == null)
				return null;

			String timeStamp = new String("");
			timeStamp = timeStamp + time.substring(5,7) + "-";
			timeStamp = timeStamp + time.substring(8,10) + "-";
			timeStamp = timeStamp + time.substring(0,4) + " : ";
			timeStamp = timeStamp + time.substring(11,13) + ":";
			timeStamp = timeStamp + time.substring(14,16) + ":";
			timeStamp = timeStamp + time.substring(17,19);

			return timeStamp;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
