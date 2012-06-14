package org.onecmdb.core.utils.transform.matcher;

import java.io.IOException;
import java.util.regex.Pattern;

import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IAttributeSelector;
import org.onecmdb.core.utils.transform.IAttributeValue;
import org.onecmdb.core.utils.transform.IDataSetMatcher;
import org.onecmdb.core.utils.transform.IInstance;

/**
 * Class <code>RegExprMatcher</code> matches onr attribute value with a </br>
 * regular expression.<br/>
 * The match function always convert the attribute value to lower cases</br> 
 * <br/>
 * If it matches the provided dataset will be used.
 *
 */
public class RegExprMatcher implements IDataSetMatcher {
	
	private String regExpr;
	private IAttributeSelector attributeSelector;
	private DataSet dataSet;
	private Pattern regExprPattern;
	private boolean lowerCase = true;
	private boolean matchEmpty = false;

	public RegExprMatcher() {
	}
	
	public RegExprMatcher(String regExpr, IAttributeSelector aSelector, DataSet ds, boolean lowerCase) {
		setRegExpr(regExpr);
		setAttributeSelector(aSelector);
		setDataSet(ds);
		setLowerCase(lowerCase);
	}

	
	public boolean isMatchEmpty() {
		return matchEmpty;
	}

	public void setMatchEmpty(boolean matchEmpty) {
		this.matchEmpty = matchEmpty;
	}

	public boolean isLowerCase() {
		return lowerCase;
	}

	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}

	public String getRegExpr() {
		return regExpr;
	}

	public void setRegExpr(String regExpr) {
		this.regExpr = regExpr;
		this.regExprPattern = Pattern.compile(regExpr);
	}

	public IAttributeSelector getAttributeSelector() {
		return attributeSelector;
	}

	public void setAttributeSelector(IAttributeSelector attributeSelector) {
		this.attributeSelector = attributeSelector;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	
	public DataSet getDataSet() {
		return(this.dataSet);
	}

	
	public boolean match(IInstance instance) throws IOException {
		IAttributeValue attribute = attributeSelector.getAttribute(instance);
		String attributeValue = attribute.getText();
		if (attributeValue == null) {
			attributeValue = "";
		}
		if (lowerCase) {
			attributeValue = attributeValue.toLowerCase();
		}
		boolean match = regExprPattern.matcher(attributeValue).find();
		if (matchEmpty) {
			if (attributeValue.length() == 0) {
				match = true;
			}
		}
	
		if (match) {
			System.out.println("regExp[" + this.regExpr + "] match [" + attributeValue + "] == " + match);
		}
		
		return(match);
	}

}
