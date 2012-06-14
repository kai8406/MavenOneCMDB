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
package org.onecmdb.core.tests.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.internal.notification.Notification;
import org.onecmdb.core.internal.notification.NotificationProvider;
import org.onecmdb.core.internal.notification.NotificationService;
import org.onecmdb.core.internal.notification.NotificationSink;
import org.onecmdb.core.internal.notification.NotificationTopic;

public class TestNotification extends TestCase {

	public void testEqualsEtc() {

		NotificationTopic a = new NotificationTopic("update");
		NotificationTopic b = new NotificationTopic("update");

		Assert.assertNotSame(a, b);

		Assert.assertEquals(a, b);
		Assert.assertEquals(a.hashCode(), b.hashCode());

		NotificationTopic c = new NotificationTopic("remove");
		NotificationTopic d = new NotificationTopic("remove");

		Assert.assertEquals(c, d);
		Assert.assertEquals(c.hashCode(), d.hashCode());

		Assert.assertFalse(a.equals(c));
		Assert.assertFalse(c.equals(a));
		Assert.assertFalse(b.equals(d));
		Assert.assertFalse(d.equals(b));

		Assert.assertTrue(a.equals(a) && a.equals(b) && b.equals(a)
				&& b.equals(b));

	}

	public void testNotifiaction() {

		// set up a the notifcation service
		final NotificationService notificationService = new NotificationService();

		// set up a notification provider, that is, someone actually sending
		// notifiactions

		final NotificationProvider providerA = new NotificationProvider() {

			private HashSet<NotificationTopic> topics = new HashSet<NotificationTopic>();

			{
				// register and set up some topics
				topics.add(new NotificationTopic("update"));
				topics.add(new NotificationTopic("remove"));
				notificationService.registerProvider(this, topics);
			}

			public Set<NotificationTopic> getNotificationTopics() {
				return topics;
			}

			public void sendNotification(NotificationTopic topic, Object data) {

				Notification notification = topic
						.createNotification(this, data);
				notificationService.sendNotification(notification);

			}
		};

		final NotificationProvider providerB = new NotificationProvider() {

			private HashSet<NotificationTopic> topics = new HashSet<NotificationTopic>();

			{
				// register and set up some topics
				topics.add(new NotificationTopic("update"));
				notificationService.registerProvider(this, topics);
			}

			public Set<NotificationTopic> getNotificationTopics() {
				return topics;
			}

			public void sendNotification(NotificationTopic topic, Object data) {

				Notification notification = topic
						.createNotification(this, data);
				notificationService.sendNotification(notification);

			}
		};

		final HashMap<NotificationProvider, Integer> counter = new HashMap<NotificationProvider, Integer>();

		// set up two clients

		new NotificationSink() {
			// register and set up some topics
			private HashMap<NotificationTopic, Set<Object>> topics = new HashMap<NotificationTopic, Set<Object>>();

			{
				Set<Object> eqs = new HashSet<Object>();
				eqs.add(providerA);
				topics.put(new NotificationTopic("update"), eqs);
				notificationService.registerSink(this, topics);
			}

			public void onNotification(Notification notification) {

				Assert.assertEquals(new NotificationTopic("update"),
						notification.getTopic());

				Integer count = counter.get(notification.getSource());
				if (count == null)
					count = 1;
				else
					count++;
				counter.put(notification.getSource(), count);

			}
		};

		new NotificationSink() {
			// register and set up some topics
			private HashMap<NotificationTopic, Set<Object>> topics = new HashMap<NotificationTopic, Set<Object>>();

			{
				Set<Object> eqs = new HashSet<Object>(); // all is blocked
				eqs.add(providerA);
				eqs.add(providerB);
				topics.put(new NotificationTopic("update"), eqs);
				notificationService.registerSink(this, topics);
			}

			public void onNotification(Notification notification) {

				Assert.assertEquals(new NotificationTopic("update"),
						notification.getTopic());

				Integer count = counter.get(notification.getSource());
				if (count == null)
					count = 1;
				else
					count++;
				counter.put(notification.getSource(), count);

			}
		};

		{
			NotificationTopic topic = new NotificationTopic("update");
			providerA.sendNotification(topic, "KALLE");
			providerB.sendNotification(topic, "KALLE");
		}
		{
			Exception exception = null;
			NotificationTopic topic = new NotificationTopic("remove");
			try {
				providerA.sendNotification(topic, "KALLE");
			} catch (IllegalStateException e) {
				exception = e;

			}
			Assert.assertNull(exception);

			exception = null;
			try {
				providerB.sendNotification(topic, "KALLE");
			} catch (IllegalStateException e) {
				exception = e;

			}
			Assert.assertNotNull(exception);

		}

		Assert.assertEquals((int) 2, (int) counter.get(providerA));
		Assert.assertEquals((int) 1, (int) counter.get(providerB));

	}

}
