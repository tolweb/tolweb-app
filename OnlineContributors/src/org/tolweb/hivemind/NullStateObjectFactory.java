package org.tolweb.hivemind;

import org.apache.tapestry.engine.state.StateObjectFactory;

public class NullStateObjectFactory implements StateObjectFactory {
	public Object createStateObject() {
		return null;
	}
}
