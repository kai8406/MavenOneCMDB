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

import org.onecmdb.core.ICi;
import org.onecmdb.core.IService;
import org.onecmdb.core.internal.model.ConfigurationItem;

/**
 * The notification service is supposed to act as a broker between notification
 * providers and notification sinks.
 * <p>
 * Registration of <em>providers</em> and <em>topics</em> must occur prior
 * of invoking services.
 * 
 * @author nogun
 * 
 */
public class NotificationService extends ConfigurationItem implements IService {

	public ICi getRoot() {
		return null;
	}

	private Map<NotificationProvider, Set<NotificationTopic>> providers = new HashMap<NotificationProvider, Set<NotificationTopic>>();

	private Map<NotificationSink, Map<NotificationTopic, Set<Object>>> sinks = new HashMap<NotificationSink, Map<NotificationTopic, Set<Object>>>();

	/**
	 * Sends a notification to all registred notification subscribers.
	 * 
	 * @throws IllegalStateException
	 *             in case the notification is not allowed to be sent;
	 */
	public void sendNotification(Notification notification) {

		/*
		 * is the source of the notification allowed to actually send
		 */
		Set<NotificationTopic> topics = this.providers.get(notification
				.getSource());
		if (topics == null) {
			throw new IllegalStateException("Publisher '"
					+ notification.getSource() + "' not registred as publisher");
		}
		if (!topics.contains(notification.getTopic())) {

			throw new IllegalStateException("Publisher '"
					+ notification.getSource()
					+ "' not allowed to send topic '" + notification.getTopic()
					+ "'");

		}

		for (NotificationSink sink : this.sinks.keySet()) {
			Map<NotificationTopic, Set<Object>> sinkrules = this.sinks
					.get(sink);

			if (sinkrules.containsKey(notification.getTopic())) {

				Set<Object> sources = sinkrules.get(notification.getTopic());
				if (sources == null
						|| sources.contains(notification.getSource())) {

					/*
					 * simple ``synchronized'' notification event
					 */
					sink.onNotification(notification);
				}
			}
		}
	}

	/**
	 * Register a notification provider
	 * 
	 * @param The
	 *            provider to register
	 * @param topics
	 *            The topics the provider is supposed to send
	 */
	public void registerProvider(NotificationProvider provider,
			Set<NotificationTopic> topics) {
		this.providers.put(provider, topics);

	}

	/**
	 * Register a sink--as en entity to recieve notifications.
	 * 
	 * @param sink
	 *            The sink to register
	 * @param topics
	 *            The types of topics the sink is interested in, together with a
	 *            list of sources (as a set) from which the sink should be
	 *            notified from.
	 */
	public void registerSink(NotificationSink sink,
			HashMap<NotificationTopic, Set<Object>> topics) {
		this.sinks.put(sink, topics);
	}

	public Set<Notification> getNotificationTypes() {
		return null;
	}

	/** a factory used to create notifications to be sent */
	protected Notification createNotification(Notification type, Object data) {
		return null;
	}

	public void close() {
		this.providers.clear();
		this.sinks.clear();
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	// }}}

}
