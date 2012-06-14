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
package org.onecmdb.core.internal.notification;

public class NotificationTopic {

	private final String name;

	public NotificationTopic(String name) {
		this.name = name;
	}

	public Notification createNotification(final NotificationProvider s,
			final Object data) {
		Notification notification = new Notification() {
			private static final long serialVersionUID = 1L;

			private NotificationTopic topic = NotificationTopic.this;

			private NotificationProvider source = s;

			private Object payload = data;

			public NotificationTopic getTopic() {
				return this.topic;
			}

			public Object getPayload() {
				return this.payload;
			}

			public NotificationProvider getSource() {
				return this.source;
			}

			@Override
			public String toString() {

				return getSource() + "{" + getTopic() + "}:" + getPayload();
			}

		};

		return notification;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !getClass().equals(obj.getClass()))
			return false;

		NotificationTopic other = (NotificationTopic) obj;
		return this.name.equals(other.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return this.name;
	}

}
