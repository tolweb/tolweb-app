package org.tolweb.hibernate;

/**
 * experimental code when doing tree gif testing
 * @hibernate.class table="Specimens"
 * @author dmandel
 *
 */
public class Specimen extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3578342701675505132L;
	public static final int DNA_TYPE = 0;
	public static final int ADULT_TYPE = 1;
	public static final int LARVAE_TYPE = 2;
    private MappedNode node;
    private String molecularData;
    private String description;
    private String imagePath;
    private EditHistory editHistory;
    private int type;

    /**
     * @hibernate.property
     * @return Returns the molecularData.
     */
    public String getMolecularData() {
        return molecularData;
    }
    /**
     * @param molecularData The molecularData to set.
     */
    public void setMolecularData(String molecularData) {
        this.molecularData = molecularData;
    }
    /**
     * @hibernate.property
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return Returns the imagePath.
     */
    public String getImagePath() {
        return imagePath;
    }
    /**
     * @param imagePath The imagePath to set.
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    /**
     * 
     * @hibernate.many-to-one column="nodeId" class="org.tolweb.hibernate.MappedNode" cascade="none"
     * @return Returns the node.
     */
    public MappedNode getNode() {
        return node;
    }
    /**
     * @param node The node to set.
     */
    public void setNode(MappedNode node) {
        this.node = node;
    }
    /**
     * @hibernate.many-to-one column="editHistoryId" class="org.tolweb.hibernate.EditHistory" cascade="all"
     * @return Returns the editHistory.
     */
    public EditHistory getEditHistory() {
        return editHistory;
    }
    /**
     * @param editHistory The editHistory to set.
     */
    public void setEditHistory(EditHistory editHistory) {
        this.editHistory = editHistory;
    }
    /**
     * @hibernate.property
     * @return
     */
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
