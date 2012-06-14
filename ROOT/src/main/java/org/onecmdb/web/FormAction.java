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
package org.onecmdb.web;


import org.onecmdb.web.SiteAction.IValueMap;
import org.springframework.validation.BindException;

/**
 * An action which is supposed to expose and and handle form data-
 * @author nogun
 *
 */
public interface FormAction {

    /**
     * Called in case of a form submission, having no validation errors.
     * Errors produced by the apply must be reported back in errors objects.
     * @param errors
     */
    void apply(BindException errors);

    /**
     * Called in case the form is cancelled, and must undo all changes
     * performed (so far) in this action
     * @param errors TODO
     */
    void cancel(BindException errors);

    /** 
     * Validates the data in about to apply. Errors are reported back in the
     * passed errors object.
     * @param errors
     */
    void validate(BindException errors);

    
    /**
     * Property used to indicate where a successful apply should travel.
     * @param navigate A string indicating the action shoud return to
     * @see SiteCommand#setNavigate(String)
     */
    void setReturnTo(String navigate);

    /** 
     * Return this actions <em>form parameters</em>, which should be seen as
     * <em>temporary</em>. These paramters are rebound on every submission
     * until the form is applied. 
     * @return The map of form paramteres. The value can alwas safely be cast 
     * into a <code>IValue</code>
     */
    public IValueMap /*IValue*/ getFormParams();
    
}
