/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.toolkit.client.control.input;

import java.util.List;

public class TextAttributeControl extends AttributeControl {
	public static final String TEXT_SUGGEST_TYPE = "suggest";
	public static final String TEXT_BOX_TYPE = "box";
	public static final String TEXT_AREA_TYPE = "area";
	public static final String TEXT_PASSWORD_TYPE = "password";
	public static final String TEXT_ENUM_TYPE = "enum";
	
	
	private Integer lines;
	private String textType;
	private List availableValues;
	
	public TextAttributeControl() {
		super();
	}
	
	public TextAttributeControl(String alias, boolean readonly, boolean requiered, String textType, Integer lines, List enumValues) {
		super(alias, readonly, requiered);
		this.lines = lines;
		this.textType = textType;
		this.availableValues = enumValues;
	}
	
	public List getAvailableValues() {
		return availableValues;
	}
	
	public void setAvailableValues(List availableValues) {
		this.availableValues = availableValues;
	}
	
	public Integer getLines() {
		return lines;
	}
	
	public void setLines(Integer lines) {
		this.lines = lines;
	}
	
	public String getTextType() {
		return textType;
	}
	
	public void setTextType(String textType) {
		this.textType = textType;
	}

	
}
