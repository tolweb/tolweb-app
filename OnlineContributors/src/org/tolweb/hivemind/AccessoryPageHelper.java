package org.tolweb.hivemind;

import java.util.Hashtable;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.treegrow.main.Contributor;

public interface AccessoryPageHelper {
	public static final String USE_EDITOR = "use_editor";
	public MappedAccessoryPage initializeNewAccessoryPageInstance(Contributor contr, boolean isTreehouse, 
			AccessoryPageDAO dao);
	public MappedAccessoryPage initializeNewAccessoryPageInstance(Contributor contr, byte treehouseType, 
			boolean isTreehouse, AccessoryPageDAO dao, int teacherResourceType, boolean setInVisit);
	@SuppressWarnings("unchecked")
    public void submitTreehouse(IRequestCycle cycle, MappedAccessoryPage treehouse, String submitPageName,
            String treehousePropertyName, boolean isSubmitToTeacher, String submitComments, 
            Hashtable additionalPageArgs);
    public void establishEditorUseInRequestCycle();
    public boolean getShouldUseEditor();
}
