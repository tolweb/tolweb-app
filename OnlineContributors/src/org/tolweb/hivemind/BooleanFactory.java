package org.tolweb.hivemind;

import org.apache.tapestry.engine.state.StateObjectFactory;

public class BooleanFactory implements StateObjectFactory {
	private boolean booleanState;
	
	public Object createStateObject() {
		return Boolean.valueOf(booleanState);
	}

	public boolean getBooleanState() {
		return booleanState;
	}

	public void setBooleanState(boolean booleanState) {
		this.booleanState = booleanState;
	}
}
