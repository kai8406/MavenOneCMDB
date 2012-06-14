package org.onecmdb.core.utils.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSetSelector extends DataSet {
	
	private List<IDataSetMatcher> matchers = new ArrayList<IDataSetMatcher>();
	private DataSet defaultDataSet;

	public void addDataSetMatcher(IDataSetMatcher matcher) {
		matchers.add(matcher);
	}
	
	public void setDataSetMatcher(List<IDataSetMatcher> list) {
		this.matchers.clear();
		this.matchers.addAll(list);
	}
	
	public List<IInstance> getInstances() throws IOException {
		List<IInstance> instances = getInstanceSelector().getInstances(this);
		List<IInstance> resultInstances = new ArrayList<IInstance>();
		
		if (matchers.size() == 0) {
			resultInstances = instances;
		}
		for (IInstance instance : instances)  {			
			boolean found = false;
			for (IDataSetMatcher m : matchers) {
				if (m.match(instance)) {
					instance.setDataSet(m.getDataSet());
					instance.setTemplate(m.getDataSet().getInstanceSelector().getTemplate());
					resultInstances.add(instance);
					found = true;
					break;
				}
			}
			if (!found) {
				if (defaultDataSet != null) {
					instance.setDataSet(defaultDataSet);
					resultInstances.add(instance);
					getReport().addWarn(getName() + ": No DataSet matched instance row", instance);
					continue;
				}
				// Handle not found here....
				getReport().addError(getName() + ": No DataSet matched instance row", instance);
			}

		}
		getReport().addDebug(getName() + ": returned " + resultInstances + " rows");
		return(resultInstances);
	}

	public void setDefaultDataSet(DataSet dataSet) {
		this.defaultDataSet = dataSet;
		
	}
}
