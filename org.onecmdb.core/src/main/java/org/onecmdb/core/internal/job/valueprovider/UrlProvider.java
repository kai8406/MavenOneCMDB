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
package org.onecmdb.core.internal.job.valueprovider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueProvider;
import org.onecmdb.core.internal.model.ConfigurationItem;

public class UrlProvider extends ConfigurationItem implements IValueProvider {

	private String value;

	private URL url;

	private long ttl = 0;

	private long lastFetched;

	private Set<IAttributeModifiable> attached = new HashSet<IAttributeModifiable>(
			0);

	public UrlProvider(String urlString) {
		setUrlString(urlString);

		Runnable r = new Runnable() {

			public void run() {

				while (true) {
					while (isValid()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					fetchValueContent();
				}
			}
		};
		new Thread(r, "UrlProvider").start();

	}

	private void setUrlString(String urlString) {
		try {
			setUrl(new URL(urlString));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void setUrl(URL url) {
		this.url = url;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onecmdb.core.IValueProvider#fetchValueContent()
	 */
	public IValue fetchValueContent() {
		try {
			URLConnection c = url.openConnection();
			Map<String, List<String>> headerFields = c.getHeaderFields();
			c.connect();

			List<String> values = headerFields.get("Keep-Alive");
			if (values.size() == 1) {
				String tmout = values.get(0);
				tmout = tmout.split(",")[0];
				tmout = tmout.split("=")[1];

				ttl = Long.parseLong(tmout) * 1000;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = c.getInputStream();
			for (int b; (b = in.read()) != -1;) {
				out.write(b);
			}
			in.close();
			out.close();

			value = out.toString();
			lastFetched = System.currentTimeMillis();

			// create a rfc to get the value updated
			for (IAttributeModifiable attr : this.attached) {
				// TODO: need to convert the value to IValue.
				// attr.setValue(value);
			}

			return null;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onecmdb.core.IValueProvider#isOld()
	 */
	public boolean isValid() {
		return System.currentTimeMillis() - lastFetched < ttl;

	}

	/** add a specific attribute to this provider's list of */
	public void attach(IAttributeModifiable productsheet) {
		this.attached.add(productsheet);
	}

}
