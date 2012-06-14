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
package org.onecmdb.core;


public interface IJobService extends IService {

	/**
	 * Retrieve the root Job.
	 * 
	 * @return
	 */
	public ICi getRootJob();

	/**
	 * Return the template for all cron triggers.
	 * @return
	 */
	public ICi getRootCronTrigger();
	
	/**
	 * Return the template for all RFC triggers.
	 * @return
	 */
	public ICi getRootIntervallTrigger();

	/**
	 * Return the template for all RFc triggers.
	 * @return
	 */
	public ICi getRootManualTrigger();

	/**
	 * Return the template for all processes.
	 * 
	 * @return
	 */
	public ICi getRootProcess();

	/**
	 * Validate if a specified CI is an offspring of a Job, implying it can be
	 * started.
	 * 
	 * @param ci
	 * @return
	 */
	public boolean isJob(ICi ci);

	/**
	 * Start a manual <em>triggable<em> job. If job is currently running it will be 
     * canceled before started again.
	 * 
	 * @param ci
	 */
	public IJobStartResult startJob(ISession session, ICi ci);

	/**
	 * Cancel an ongoing job.
	 * 
	 * @param ci
	 */
	public IJobStartResult cancelJob(ISession session, ICi ci);

	
	/**
	 * Reschedule a specific trigger. If trigger is a template all offsprings 
     * will be rescheduled. Triggered jobs that are running will be canceled 
     * before rescheduling.
	 * 
	 * @param trigger
	 */
	public void reschedualeTrigger(ISession session, ICi trigger);
	
	/**
	 * Cancel a specific trigger. If trigger is a template all offsprings will be
	 * canceled. 
	 * 
	 * @param trigger
	 */
	public void cancelTrigger(ISession session, ICi trigger);
	

}
