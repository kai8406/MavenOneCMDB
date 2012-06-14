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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.onecmdb.core.internal.model.ConfigurationItem;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ResourceProvider extends ConfigurationItem implements
		ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	private Resource resource;

	public ResourceProvider(String string) {
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public ResourceProvider() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onecmdb.core.IValueProvider#fetchValueContent()
	 */
	public Object fetchValueContent() {
		try {
			InputStream is = resource.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(is);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			for (int c; (c = buf.read()) != -1;) {
				os.write(c);
			}
			os.close();
			buf.close();
			return os.toString();

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
		return false;
	}

	public Object getAdapter(Class type) {
		// TODO Auto-generated method stub
		return null;
	}

}
