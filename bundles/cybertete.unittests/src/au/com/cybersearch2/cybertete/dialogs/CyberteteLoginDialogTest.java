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
package au.com.cybersearch2.cybertete.dialogs;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.dialogs.LoginDialog;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.ServiceThreadManager;
import au.com.cybersearch2.cybertete.service.ChatLoginProgressTask;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.ProgressDialog;


/**
 * CyberteteLoginDialogTest
 * @author Andrew Bowley
 * 18 Mar 2016
 */
public class CyberteteLoginDialogTest
{
    static final String GSSAPI_PRINCIPAL = "test_user";
    static final String CONNECTION_TIMEOUT = "Connection timeout";
    static final String TEST_JID = "mickymouse@disney.com";

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_setConnectionError()
    {
        LoginDialog loginDialog = new LoginDialog();
        CustomDialog dialog = mock(CustomDialog.class);
        loginDialog.dialog = dialog;
        LoginCustomControls loginControls = mock(LoginCustomControls.class);
        when(dialog.getCustomControls()).thenReturn(loginControls);
        loginDialog.setConnectionError(ConnectionError.noError);
        verify(loginControls).setConnectionError(ConnectionError.noError);
    }

    @Test
    public void test_setConnectionError_null_dialog()
    {
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.setConnectionError(ConnectionError.noError);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_open()
    {
        LoginDialog loginDialog = new LoginDialog();
        DialogLoginControls dialogLoginControls = mock(DialogLoginControls.class);
        loginDialog.loginControls = dialogLoginControls;
        UISynchronize sync = mock(UISynchronize.class);
        loginDialog.sync = sync;
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        loginDialog.configNotifier = configNotifier;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        loginDialog.dialogFactory = dialogFactory;
        LoginData loginData = mock(LoginData.class);
        CustomDialog dialog = mock(CustomDialog.class);
        when(dialog.syncOpen(sync)).thenReturn(2); // Window.CANCEL + 1
        when(dialogFactory.loginDialogInstance("Cybertete Login", dialogLoginControls)).thenReturn(dialog);
        loginDialog.open(loginData);
        assertThat(loginDialog.advancedOptions).isTrue();
     }  
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_save()
    {
        LoginDialog loginDialog = new LoginDialog();
        CustomDialog dialog = mock(CustomDialog.class);
        loginDialog.dialog = dialog;
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        loginDialog.configNotifier = configNotifier;
        UISynchronize sync = mock(UISynchronize.class);
        loginDialog.sync = sync;
        loginDialog.save(TEST_JID);
        verify(configNotifier).saveLoginConfig(TEST_JID);
        verify(dialog).syncClose(sync);
        assertThat(loginDialog.dialog).isNull();
    }
    
    @Test
    public void test_displayProgressDialog() throws InvocationTargetException, InterruptedException
    {
        LoginDialog loginDialog = new LoginDialog();
        UISynchronize sync = mock(UISynchronize.class);
        loginDialog.sync = sync;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        loginDialog.dialogFactory = dialogFactory;
        ProgressDialog progress = mock(ProgressDialog.class);
        when(dialogFactory.progressDialogInstance()).thenReturn(progress);
        ServiceThreadManager serviceThreadManager = mock(ServiceThreadManager.class);
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        loginDialog.displayProgressDialog(serviceThreadManager, serviceLoginTask);
        ArgumentCaptor<IRunnableWithProgress> runnableCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progress).open(runnableCaptor.capture(), eq(serviceThreadManager));
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        runnableCaptor.getValue().run(monitor);
        verify(serviceThreadManager).connectLogin(serviceLoginTask);
        verify(serviceThreadManager).addNetworkListener(isA(ChatLoginProgressTask.class));        
        verify(monitor).beginTask("Connecting...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Contacting host...");
        verify(monitor).done();
        verify(serviceThreadManager).removeNetworkListener(isA(ChatLoginProgressTask.class));        
    }
       
    @Test
    public void test_displayProgressDialog_invocation_exception() throws InvocationTargetException, InterruptedException
    {
        LoginDialog loginDialog = new LoginDialog();
        UISynchronize sync = mock(UISynchronize.class);
        loginDialog.sync = sync;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        loginDialog.dialogFactory = dialogFactory;
        ArrayIndexOutOfBoundsException unexpectedException = 
                new ArrayIndexOutOfBoundsException("Out of bounds");
        InvocationTargetException invocationTargetException = new InvocationTargetException(unexpectedException);
        ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        ProgressDialog progress = new ProgressDialog(progressMonitorDialog, sync);
        when(dialogFactory.progressDialogInstance()).thenReturn(progress);
        doThrow(invocationTargetException)
        .when(progressMonitorDialog).run(eq(true), eq(true), isA(IRunnableWithProgress.class)); 
        ServiceThreadManager serviceThreadManager = mock(ServiceThreadManager.class);
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        loginDialog.displayProgressDialog(serviceThreadManager, serviceLoginTask);
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).syncExec(taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(serviceThreadManager).uncaughtException(isA(Thread.class), eq(unexpectedException));
    }
}
