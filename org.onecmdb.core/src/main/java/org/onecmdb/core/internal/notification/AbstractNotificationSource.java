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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class AbstractNotificationSource implements NotificationProvider {
	Set<Notification> notificationTypes;

	private Map<NotificationSink, Set<Notification>> notificationSinks = new HashMap<NotificationSink, Set<Notification>>();

	public void registerSink(NotificationSink sink, Set<Notification> eventTypes) {
		notificationSinks.put(sink, eventTypes);
	}

	public void sendNotification(Notification notification) {
		for (NotificationSink sink : notificationSinks.keySet()) {
			Set<Notification> eventTypes = notificationSinks.get(sink);
			if (eventTypes.contains(notification))
				sink.onNotification(notification);
		}
	}

	public Set<Notification> getNotificationTypes() {
		return this.notificationTypes;
	}

	public Set<NotificationTopic> getNotificationTopics() {
		return null;
	}

	public void sendNotification(NotificationTopic topic, Object payload) {
	}
}