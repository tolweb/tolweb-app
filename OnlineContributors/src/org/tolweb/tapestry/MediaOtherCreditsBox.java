package org.tolweb.tapestry;

import java.util.Iterator;

import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.selectionmodels.PersistentObjectSelectionModelWithDefault;
import org.tolweb.treegrow.main.StringUtils;

public abstract class MediaOtherCreditsBox extends EditMediaComponent implements NodeInjectable {
	public abstract String getCollectionName();
	public abstract String getCollectionHomepage();
	
	public void addOtherMediaCollection() {
		// if not rewinding just ignore
		if (getPage().getRequestCycle().isRewinding()) {
			if (StringUtils.notEmpty(getCollectionName())) {
				// do the necessary adding of source collections
				ForeignDatabase newDb = new ForeignDatabase();
				newDb.setName(getCollectionName());
				newDb.setUrl(getCollectionHomepage());
				getMiscNodeDAO().saveForeignDatabase(newDb);
				getMedia().setSourceDbId(newDb.getId());
			}
		}
	}
	public PersistentObjectSelectionModelWithDefault getSelectionModel() {
		return new PersistentObjectSelectionModelWithDefault(getMiscNodeDAO().getAllForeignDatabases(), "No source collection");
	}
	public void setForeignDatabase(ForeignDatabase db) {
		if (db != null) {
			getMedia().setSourceDbId(db.getId());
		} else {
			getMedia().setSourceDbId(null);
		}
	}
	@SuppressWarnings("unchecked")
	public ForeignDatabase getForeignDatabase() {
		Long foreignDbId = getMedia().getSourceDbId();
		if (foreignDbId != null) {
			for (Iterator iter = getMiscNodeDAO().getAllForeignDatabases().iterator(); iter.hasNext();) {
				ForeignDatabase database = (ForeignDatabase) iter.next();
				if (database.getId().equals(foreignDbId)) {
					return database;
				}
			}
		}
		return null;
	}
}
