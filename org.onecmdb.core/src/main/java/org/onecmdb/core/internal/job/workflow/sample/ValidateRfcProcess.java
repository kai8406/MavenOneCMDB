/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.core.internal.job.workflow.sample;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.BeanScope;

public class ValidateRfcProcess extends WorkflowProcess  {

	public void run() throws Throwable {
		
		BeanScope scope = (BeanScope) in.get("scope");
		if (scope == null) {
			throw new IllegalArgumentException("No 'scope' input parameter!");
		}
		final List<IRFC> rfcs = scope.getRFCs(); 

		JTextPane pane = new JTextPane();
		pane.setContentType("text/html");
		Set<String> unresolved = scope.getUnresolvedAliases();
		StringBuffer buf = new StringBuffer();
		List<CiBean> beans = scope.getProcessedBeans();

		buf.append("<h1 color=\"red\">");
		buf.append(unresolved.size() + " unresolved ci's "
				+ (unresolved.size() == 0 ? "OK" : "ERROR"));
		buf.append("</h1>");
		for (String alias : unresolved) {
			buf.append("<li>");
			buf.append(alias);
			buf.append("</li>");
		}
		HashMap<String, List<CiBean>> duplicatedMap = scope
				.getDuplicatedBeans();
		buf.append("<h1 color=\"red\">");
		buf.append(duplicatedMap.size() + " duplicated ci's ");
		buf.append("</h1>");
		for (String alias : duplicatedMap.keySet()) {
			buf.append("<li>");
			buf.append(alias);
			for (CiBean bean : duplicatedMap.get(alias)) {
				buf.append("<li>");
				if (bean.isTemplate()) {
					buf.append("T: ");
				} else {
					buf.append("I: ");
				}
				buf.append(bean.getAlias() + "::" + bean.getDerivedFrom());
				buf.append("</li>");
			}
			buf.append("</li>");
		}
		Set<String> simpleTypes = scope.getSimpleTypesUsed();
		buf.append("<h1>");
		buf.append(simpleTypes.size() + " simple types used");
		buf.append("</h1>");
		for (String type : simpleTypes) {
			buf.append("<li>");
			buf.append(type);
			buf.append("</li>");
		}

		HashMap<String, CiBean> repBeanMap = scope.getReposiotryBeanUsed();
		buf.append("<h1>");
		buf.append(repBeanMap.size() + " repository ci's used");
		buf.append("</h1>");
		for (CiBean bean : repBeanMap.values()) {
			buf.append("<li>");
			if (bean.isTemplate()) {
				buf.append("T: ");
			} else {
				buf.append("I: ");
			}
			buf.append(bean.getAlias() + "::" + bean.getDerivedFrom());
			buf.append("</li>");
		}

		buf.append("<h1>");
		buf.append(beans.size() + " Processed Beans");
		buf.append("</h1>");
		for (CiBean bean : beans) {
			buf.append("<li>");
			if (bean.isTemplate()) {
				buf.append("T: ");
			} else {
				buf.append("I: ");
			}
			buf.append(bean.getAlias() + "::" + bean.getDerivedFrom() + " ");
			buf.append("</li>");
		}

		buf.append("<h1>");
		buf.append(rfcs.size() + " RFCs");
		buf.append("</h1>");
		for (IRFC rfc : rfcs) {
			buf.append("<li>");
			buf.append(rfc.getSummary());
			buf.append("</li>");
		}

		pane.setText(buf.toString());
		
		final Dialog dialog = new Dialog(new JFrame());
		dialog.setModal(true);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton cancel = new JButton("Cancel");

		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				out.put("ok", "false");
				out.put("cause", "Canceled..");
				dialog.setVisible(false);
			}
		});

		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				out.put("ok", "true");
				out.put("rfcs", rfcs);
				dialog.setVisible(false);
			}
		});

		buttonPanel.add(submit);
		buttonPanel.add(cancel);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(pane), BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.NORTH);
		dialog.setLayout(new BorderLayout());
		dialog.add(panel, BorderLayout.CENTER);
		dialog.setSize(500, 600);
		
		// Should be modal.
		dialog.setVisible(true);
		
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}
}
