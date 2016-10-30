/**
    Copyright (C) 2016  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.cybertete.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.security.KerberosData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * LoadKerberosConfigHandler
 * Creates Kerboros configuration file, if one does not exist, then acquires a ticket to perform signon
 * @author Andrew Bowley
 * 3 Mar 2016
 */
public class LoadKerberosConfigHandler
{
    /** The authentication name */
    private String gssapiPrincipal;
    /** Logger */
    Logger logger;

    /** Keberos login information */
    @Inject
    KerberosData kerberosData;
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;
    @Inject
    UISynchronize sync;

    /**
     * postConstruct
     * @param loggerProvider
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(LoadKerberosConfigHandler.class);
    }
 
    /**
     * Handle request for single signon principal
     * @param configCallback Object to callback result
     * @param controlFactory SWT widget factory
     */
    @Inject @Optional
    void onLoadKerberosConfigHandler(
            @UIEventTopic(CyberteteEvents.LOAD_KERBEROS_CONFIG) LoadKerberosConfigEvent configCallback,
            ControlFactory controlFactory)
    {
        getSingleSignonPrincipal(controlFactory.progressMonitorDialogInstance(), configCallback);
    }
 
    /**
     * Returns single signon principal
     * @param progress Progress monitor dialog
     * @param origin Origin of request
     */
    private void getSingleSignonPrincipal(final ProgressMonitorDialog progress, final LoadKerberosConfigEvent configCallback)
    {
        gssapiPrincipal = null;
        progress.setCancelable(false);
        try
        {
            progress.run(true, false, new IRunnableWithProgress(){

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException
                {
                    gssapiPrincipal = runTask(monitor);
                    sync.asyncExec(new Runnable(){

                        @Override
                        public void run()
                        {
                            configCallback.onLoadKerberosConfig(gssapiPrincipal);
                        }
                    });
                }
            });
        }
        catch (InvocationTargetException e)
        {
            logger.error("Error creating login configuration file", e.getCause());
            errorDialog.showError("Error", e.getCause().getMessage());
        }
        catch (InterruptedException e)
        {
        }
    }

    /**
     * Create Kerboros configuration file, if one does not exist, then acquires a ticket to perform signon
     * @param monitor Progress monitor
     * @return GSSAPI Principal
     * @throws InvocationTargetException containing IOException if error occurs with file system
     */
    private String runTask(IProgressMonitor monitor)
            throws InvocationTargetException
    {
        try
        {
            monitor.beginTask("Getting your credentials...", IProgressMonitor.UNKNOWN);
            monitor.subTask("Checking configuration file");
            if (!kerberosData.isSingleSignonEnabled())
                // No login configuration entry exists for GSSAPI, so create one.
                kerberosData.createLoginConfigFile();
            
            monitor.subTask("Getting Kerberos ticket");
            return kerberosData.getGssapiPrincipal();
        }
        catch (IOException e)
        {
            throw new InvocationTargetException(e);
        }
        finally 
        {
            monitor.done();
        }
    }
}
