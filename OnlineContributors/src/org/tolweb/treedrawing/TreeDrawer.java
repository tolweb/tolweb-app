package org.tolweb.treedrawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TreeGif;
import org.tolweb.misc.URLBuilder;

public class TreeDrawer {
	private NodeDAO miscNodeDAO;
    private NodeDAO nodeDAO;
    private PageDAO pageDAO;
    private TreeImagePanel treePanel;
    private URLBuilder urlBuilder;
    private String treeGifDirPath;
    private String urlPrefix;
    private String serverPrefix;
    private String arialFontPath;
    private String arialItalicFontPath;
    private String extinctImagePath;
    private boolean drawDescriptions;
    private boolean drawAuthorities;
    private boolean includeSpecimenInfo;
    
    // we want to chop off extra whitespace that the
    // tree panel adds in due to TreeGrow
    
    public TreeDrawer() {

    }
    
    /**
     * @return Returns the nodeDAO.
     */
    public NodeDAO getNodeDAO() {
        return nodeDAO;
    }
    
    /**
     * @param nodeDAO The nodeDAO to set.
     */
    public void setNodeDAO(NodeDAO nodeDAO) {
        this.nodeDAO = nodeDAO;
    }
    
    /**
     * @return Returns the pageDAO.
     */
    public PageDAO getPageDAO() {
        return pageDAO;
    }
    
    /**
     * @param pageDAO The pageDAO to set.
     */
    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }
    
    /**
     * @return Returns the treePanel.
     */
    public TreeImagePanel getTreePanel() {
        if (treePanel == null) {
            treePanel = new TreeImagePanel(getArialFontPath(), getArialItalicFontPath(), getExtinctImagePath());
        }
    	return treePanel;
    }
    
    /**
     * @param treePanel The treePanel to set.
     */
    public void setTreePanel(TreeImagePanel treePanel) {
        this.treePanel = treePanel;
    }
    
    /**
     * Enables or disables the trunk coloring feature when performing 
     * tree image drawing.  This feature is on by default.
     * @param toggle - either turns the trunk coloring feature on 
     * or off based this param value.
     */
    public void setTrunkColoringEnabled(boolean toggle) {
    	getTreePanel().setTrunkColoringEnabled(toggle);
    }
    
    @SuppressWarnings("unchecked")
    public TreeGif drawTreeForPage(MappedPage page) {
        // get the nodes on the page
        List nodes = getPageDAO().getOrderedByParentNodesOnPage(page, true);
        MappedNode rootNode = page.getMappedNode();
        nodes.add(rootNode);
        // build a hashtable to set up the tree structure
        Hashtable<Long, MappedNode> idsToNodes = new Hashtable();
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            idsToNodes.put(nextNode.getNodeId(), nextNode);
        }
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            // get our parent and add ourselves to the children of the parent
            MappedNode parent = idsToNodes.get(nextNode.getParentNodeId());
            if (parent != null) {
                parent.addToChildren(nextNode);
            }
            /*if (getIncludeSpecimenInfo()) {
				AdditionalFields fields = getMiscNodeDAO()
						.getAdditionalFieldsForNode(nextNode);
				if (fields == null) {
					System.err.println("node missing fields is: " + nextNode);
				} else {
					nextNode.setAdditionalFields(fields);
					fields.setHasPage(getSpecimenDAO()
							.getNodeHasSpecimens(nextNode, Specimen.ADULT_TYPE,
									true));
					/*fields.setHasDnaSpecimens(getSpecimenDAO()
							.getNodeHasSpecimens(nextNode, Specimen.DNA_TYPE,
									true));
					fields.setHasLarvaeSpecimens(getSpecimenDAO()
							.getNodeHasSpecimens(nextNode,
									Specimen.LARVAE_TYPE, true));/
				}
			}*/
        }
        int h, w;
        BufferedImage image = null;
        String pageStatus = page.getStatus();
        boolean isBlack = !pageStatus.equalsIgnoreCase(MappedPage.SKELETAL);        
        // only allow one tree to be drawn at a time so we can reuse the same
        // panel.  only talks about 20ms (on my home PC -- server should be faster, too) 
        // so it's not a performance drag
        String mapString;
        synchronized(this) {
	        TreeImagePanel treePanel = getTreePanel();
	        treePanel.setDrawDescriptions(getDrawDescriptions());
	        treePanel.setDrawAuthorities(getDrawAuthorities());
	        //treePanel.setIncludeSpecimenInfo(getIncludeSpecimenInfo());
	        treePanel.setIncludeSpecimenInfo(false);
	        treePanel.setPageDAO(getPageDAO());
	        treePanel.setUrlBuilder(getUrlBuilder());
	        treePanel.setIsBlackLines(isBlack);        
	        treePanel.setRootNode(rootNode);
	        treePanel.rebuildTree();
	        treePanel.setBackground(Color.white);
	        w = treePanel.getWidth(); 
	        h = treePanel.getHeight();
	        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2 = image.createGraphics();
	        g2.fillRect(0, 0, w, h);
	        treePanel.paint(g2);
	        g2.dispose();
	        mapString = treePanel.getLinkCoords(page);
        }
        //image = image.getSubimage(w, h - AbstractTreePanel.TOP_PADDING, image.getWidth(), image.getHeight() - AbstractTreePanel.TOP_PADDING);        
        String filename = page.getGroupName().replaceAll("\\s", "_") + ".png"; 
        String destinationFilePath = getTreeGifDirPath() + filename;
        try {
            ImageIO.write(image, "png", new File(destinationFilePath));
        } catch (IOException e) {
        	e.printStackTrace();
        }
        TreeGif treeGif = new TreeGif();
        treeGif.setHeight(h);
        treeGif.setWidth(w);
        treeGif.setPageId(page.getPageId());
        treeGif.setMapString(mapString);
        String nameString = getServerPrefix();
        nameString += getUrlPrefix();
        nameString += filename;
        treeGif.setName(nameString);
        return treeGif;
    }

    /**
     * @return Returns the treeGifDirPath.
     */
    public String getTreeGifDirPath() {
        return treeGifDirPath;
    }

    /**
     * @param treeGifDirPath The treeGifDirPath to set.
     */
    public void setTreeGifDirPath(String treeGifDirPath) {
        this.treeGifDirPath = treeGifDirPath;
    }

    /**
     * @return Returns the serverPrefix.
     */
    public String getServerPrefix() {
        return serverPrefix;
    }

    /**
     * @param serverPrefix The serverPrefix to set.
     */
    public void setServerPrefix(String serverPrefix) {
        this.serverPrefix = serverPrefix;
    }

    /**
     * @return Returns the urlPrefix.
     */
    public String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * @param urlPrefix The urlPrefix to set.
     */
    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

	public boolean getDrawDescriptions() {
		return drawDescriptions;
	}

	public void setDrawDescriptions(boolean drawDescriptions) {
		this.drawDescriptions = drawDescriptions;
	}

	public boolean getDrawAuthorities() {
		return drawAuthorities;
	}

	public void setDrawAuthorities(boolean drawAuthorities) {
		this.drawAuthorities = drawAuthorities;
	}

	public boolean getIncludeSpecimenInfo() {
		return includeSpecimenInfo;
	}

	public void setIncludeSpecimenInfo(boolean includeSpecimenInfo) {
		this.includeSpecimenInfo = includeSpecimenInfo;
	}

	public NodeDAO getMiscNodeDAO() {
		return miscNodeDAO;
	}

	public void setMiscNodeDAO(NodeDAO miscNodeDAO) {
		this.miscNodeDAO = miscNodeDAO;
	}

	public URLBuilder getUrlBuilder() {
		return urlBuilder;
	}

	public void setUrlBuilder(URLBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
	}

	public String getArialFontPath() {
		return arialFontPath;
	}

	public void setArialFontPath(String arialFontPath) {
		this.arialFontPath = arialFontPath;
	}

	public String getArialItalicFontPath() {
		return arialItalicFontPath;
	}

	public void setArialItalicFontPath(String arialItalicFontPath) {
		this.arialItalicFontPath = arialItalicFontPath;
	}

	public String getExtinctImagePath() {
		return extinctImagePath;
	}

	public void setExtinctImagePath(String extinctImagePath) {
		this.extinctImagePath = extinctImagePath;
	}
}
