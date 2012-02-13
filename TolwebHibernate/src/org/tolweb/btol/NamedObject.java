package org.tolweb.btol;

import org.tolweb.hibernate.PersistentObject;

public abstract class NamedObject extends PersistentObject {
	private String name;

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return getName();
	}
}