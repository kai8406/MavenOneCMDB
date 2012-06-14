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
package org.onecmdb.web.acegi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.el.ELException;

import net.sf.jasperreports.engine.JasperExportManager;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AcegiSecurityException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.acegisecurity.InsufficientAuthenticationException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.AccessDeniedHandler;
import org.acegisecurity.ui.AccessDeniedHandlerImpl;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.acegisecurity.ui.ExceptionTranslationFilter;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.acegisecurity.util.PortResolver;
import org.acegisecurity.util.PortResolverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;



public class AccessDeniedFilter implements Filter, InitializingBean {
    
        //~ Static fields/initializers =====================================================================================

        private static final Log logger = LogFactory.getLog(AccessDeniedFilter.class);

        //~ Instance fields ================================================================================================

        private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

        //~ Methods ========================================================================================================

        public void afterPropertiesSet() throws Exception {
            Assert.notNull(accessDeniedHandler, "accessDeniedHandler must be specified");
        }

        public void destroy() {}

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            if (!(request instanceof HttpServletRequest)) {
                throw new ServletException("HttpServletRequest required");
            }

            if (!(response instanceof HttpServletResponse)) {
                throw new ServletException("HttpServletResponse required");
            }

            try {
                request.setAttribute("ACCESS_DENIED", false);
                
                
                chain.doFilter(request, response);

                if (logger.isDebugEnabled()) {
                    logger.debug("Chain processed normally");
                }
            } catch (AccessDeniedException ex) {
                handleException(request, response, chain, ex);
            } catch (ServletException ex) {
                
                Throwable rx = getRootCause(ex); 
                if (rx instanceof AccessDeniedException) {
                    handleException(request, response, chain, (AcegiSecurityException) rx);
                } else {
                    throw ex;
                }
            } catch (IOException ex) {
                throw ex;
            }
        }

        private Throwable getRootCause(Throwable ex) {
            final Throwable rx;
            if (ex instanceof ServletException) {
                rx = ((ServletException) ex).getRootCause();
            } else if (ex instanceof ELException) {
                rx = ((ELException) ex).getRootCause();
            } else if (ex != null) {
                rx = ex.getCause();
            } else {
                rx = null;
            }
            return rx == null ? ex : getRootCause(rx);
        }
        private void handleException(ServletRequest request, ServletResponse response, 
                FilterChain chain, AcegiSecurityException exception) 
        throws IOException, ServletException 
        {
            if (exception instanceof AccessDeniedException) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Access is denied; delegating to AccessDeniedHandler",
                            exception);
                }
                
                request.setAttribute("ACCESS_DENIED", true);
                
                accessDeniedHandler.handle(request, response, (AccessDeniedException) exception);
            }
        }

        public void init(FilterConfig filterConfig) throws ServletException {}


        public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
            Assert.notNull(accessDeniedHandler, "AccessDeniedHandler required");
            this.accessDeniedHandler = accessDeniedHandler;
        }




    

}



