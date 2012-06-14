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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestOracle;


public class MultiWordStartSuggestOracle  extends SuggestOracle {

	private List suggestions = new ArrayList();
	private int matchIndex;
	private String[] words;
	
	public MultiWordStartSuggestOracle() {
	}
	
	public void requestSuggestions(Request request, Callback callback) {
		String query = request.getQuery();
		Response response = getResponse(query);
		callback.onSuggestionsReady(request, response);
	}

	public void addSuggestion(final String value) {		
		this.suggestions.add(new MySuggestion(value));
	}
	
	private Response getResponse(String query) {
		Response r = new Response();
		String words[] = query.split(" ");
		r.setSuggestions(new ArrayList());
		for (int i = 0; i < words.length; i++) {
			if (words[i].startsWith("$") && words[i].length() == 1) {
				this.words = words;
				this.matchIndex = i;
				r.setSuggestions(suggestions);
				break;
			}
		}
		return(r);
	}
	
	class MySuggestion implements Suggestion {
		private String display;

		public MySuggestion(String display) {
			this.display = display;
		}
		public String getDisplayString() {
			return(this.display);
		}

		public String getReplacementString() {
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < words.length; i++) {
				if (i == matchIndex) {
					b.append(this.display);
				} else {
					b.append(words[i]);
				}
				if (i < (words.length-1)) {
					b.append(" ");
				}
			}
			return(b.toString());
		}
	}

}
