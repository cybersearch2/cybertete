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

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.security.KerberosData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * LoadKerberosConfigHandlerTest
 * @author Andrew Bowley
 * 22 Apr 2016
 */
public class LoadKerberosConfigHandlerTest
{
    static final String PRINCIPAL = "MickyMouse";

    @Test
    public void test_onLoadKerberosConfigHandler() throws InvocationTargetException, InterruptedException, IOException
    {
        UISynchronize sync = mock(UISynchronize.class);
        ProgressMonitorDialog progress = mock(ProgressMonitorDialog.class);
        LoadKerberosConfigHandler underTest = new LoadKerberosConfigHandler();
        underTest.sync = sync;
        KerberosData kerberosData = mock(KerberosData.class);
        when(kerberosData.getGssapiPrincipal()).thenReturn(PRINCIPAL);
        underTest.kerberosData = kerberosData;
        LoadKerberosConfigEvent configCallback = mock(LoadKerberosConfigEvent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progress);
        underTest.onLoadKerberosConfigHandler(configCallback, controlFactory);
        verify(progress).setCancelable(false);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progress).run(eq(true), eq(false), taskCaptor.capture());
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        when(kerberosData.isSingleSignonEnabled()).thenReturn(false);
        taskCaptor.getValue().run(monitor );
        verify(monitor).beginTask("Getting your credentials...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Checking configuration file");
        verify(kerberosData).createLoginConfigFile();
        verify(monitor).subTask("Getting Kerberos ticket");
        ArgumentCaptor<Runnable> syncCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).asyncExec(syncCaptor.capture());
        syncCaptor.getValue().run();
        verify(configCallback).onLoadKerberosConfig(PRINCIPAL);
    }

    @Test
    public void test_onLoadKerberosConfigHandler_exception() throws InvocationTargetException, InterruptedException, IOException
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        ProgressMonitorDialog progress = mock(ProgressMonitorDialog.class);
        LoadKerberosConfigHandler underTest = new LoadKerberosConfigHandler();
        underTest.errorDialog = errorDialog;
        KerberosData kerberosData = mock(KerberosData.class);
        when(kerberosData.getGssapiPrincipal()).thenReturn(PRINCIPAL);
        underTest.kerberosData = kerberosData;
        LoadKerberosConfigEvent configCallback = mock(LoadKerberosConfigEvent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progress);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(LoadKerberosConfigHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.onLoadKerberosConfigHandler(configCallback, controlFactory);
        verify(progress).setCancelable(false);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progress).run(eq(true), eq(false), taskCaptor.capture());
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        when(kerberosData.isSingleSignonEnabled()).thenReturn(false);
        IOException exception = new IOException("File not found");
        doThrow(exception)
        .when(kerberosData).createLoginConfigFile();
        try
        {
            // Catch InvocationTargetException and throw it from progress dialog run()
            taskCaptor.getValue().run(monitor );
        }
        catch(InvocationTargetException e)
        {
            verify(monitor).beginTask("Getting your credentials...", IProgressMonitor.UNKNOWN);
            verify(monitor).subTask("Checking configuration file");
            doThrow(e)
            .when(progress).run(eq(true), eq(false), isA(IRunnableWithProgress.class));
            underTest.onLoadKerberosConfigHandler(configCallback, controlFactory);
            verify(errorDialog).showError("Error", "File not found");
            verify(logger).error("Error creating login configuration file", exception);
            verify(configCallback, times(0)).onLoadKerberosConfig(PRINCIPAL);
       }
    }
}
