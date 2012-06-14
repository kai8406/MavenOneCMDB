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
package org.onecmdb.ui.gwt.toolkit.client.view.ci;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class CIIconDisplayNameWidget extends CIWidget {
	
	private ClickListener clickListener;


	public CIIconDisplayNameWidget(String type) {
		super(type);
		update();
	}
	
	public CIIconDisplayNameWidget(GWT_CiBean bean) {
		super(bean);
		update();
	}
	
	public CIIconDisplayNameWidget(GWT_CiBean bean, ClickListener click) {
		super(bean);
		this.clickListener = click;
		update();
	}

	
	public void load(GWT_CiBean bean) {
		vPanel.clear();
		HorizontalPanel cipanel =  new HorizontalPanel();
		cipanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
		CIDisplayNameWidget display = new CIDisplayNameWidget(bean, clickListener);
		cipanel.add(display);
		cipanel.setCellWidth(display, "100%");
		cipanel.setCellHorizontalAlignment(display, HorizontalPanel.ALIGN_LEFT);
		vPanel.add(cipanel);
	}
	

}
