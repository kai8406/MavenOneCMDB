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
package org.onecmdb.core.internal.ccb.rfc;

/**
 * Implementation of how to copy a ICI to another ICI. The implementation
 * dependce hevily on Policyes to control the actuall copy mechanism.
 * 
 */
public class RFCCopyCi extends RFC {

	/*
	 * protected ConfigurationItem copy() {
	 * 
	 * ConfigurationItem copy = new ConfigurationItem(); for (IAttribute a :
	 * getAttributes()) {
	 *  // strategy 1) // find the policy.... from (?)
	 * 
	 * 
	 * 
	 *  // //{{{ strategy 2) // send a message, and provide a callback, where
	 * the the new // offspring can be feteched
	 * 
	 * 
	 * final Map<String key , IAttribute offspring > data = new HashMap<String,IAttribute>(1);
	 * 
	 * NotificationCallback callback = new NotificationCallback() { void
	 * callback(Object callbackData) { data.put("offspring", (IAttribute)
	 * callbackData);
	 *  // let others now (should be handled more generically though) beencalled =
	 * true; synchronized(this) { notify(); } } };
	 * 
	 * CreateOffspringNotification notification = new
	 * CreateOffspringNotification(ConfigurationItem.this, callback);
	 * 
	 * sendNotification(notification);
	 * 
	 * callback.waitForRProceessed(); IAttribute newOffspring =
	 * data.get("offspring");
	 * 
	 * 
	 *  // wait for callback (at most 2 seconds)
	 * 
	 * 
	 * 
	 * 
	 *  // }}}
	 * 
	 * 
	 * 
	 *  // clone the attribute, by accessing IExtensibleAttribute ea =
	 * (IExtensibleAttribute)
	 * a.getDerivedFrom().getAdapter(IExtensibleAttribute.class);
	 * ea.createOffspring(a.getName(), a.getType(), null);
	 * 
	 * copy.addAttribute(a); }
	 * 
	 * 
	 * return copy; }
	 */
}
