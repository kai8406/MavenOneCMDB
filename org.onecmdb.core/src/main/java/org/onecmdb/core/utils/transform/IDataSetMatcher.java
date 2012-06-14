package org.onecmdb.core.utils.transform;

import java.io.IOException;

public interface IDataSetMatcher {
	public boolean match(IInstance instance) throws IOException;
	public DataSet getDataSet();
}
