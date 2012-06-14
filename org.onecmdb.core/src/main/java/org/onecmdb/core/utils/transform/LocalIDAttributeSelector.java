package org.onecmdb.core.utils.transform;

import java.io.IOException;

public class LocalIDAttributeSelector extends AAttributeSelector {

	public LocalIDAttributeSelector() {
		super();
	}
	
	public LocalIDAttributeSelector(String name, boolean naturalKey) {
		setName(name);
		setNaturalKey(naturalKey);
	}
	
	public IAttributeValue getAttribute(IInstance row) throws IOException {
		return(new TextAttributeValue(this, row.getLocalID()));
	}

}
