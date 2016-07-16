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
package au.com.cybersearch2.cybertete.security;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.security.Security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.window.Window;
import org.junit.Test;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.PasswordControls;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.LoginData;


/**
 * KerberosCallbackHandlerTest
 * @author Andrew Bowley
 * 4 May 2016
 */
public class KerberosCallbackHandlerTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_PASSWORD = "secret";
    private static final String PROMPT = "Password: ";
   
    @Test
    public void test_postConstruct()
    {
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosCallbackHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        assertThat(underTest.logger).isEqualTo(logger);
        assertThat(KerberosCallbackHandler.singleton).isEqualTo(underTest);
        assertThat(Security.getProperty("auth.login.defaultCallbackHandler")).isEqualTo(KerberosCallbackHandler.class.getName());
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_handle() throws IOException, UnsupportedCallbackException
    {
        final PasswordControls passwordControls = mock(PasswordControls.class);
        when(passwordControls.getPassword()).thenReturn(TEST_PASSWORD.toCharArray());
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        UISynchronize sync = mock(UISynchronize.class);
        underTest.sync = sync;
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        underTest.loginData = loginData;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        CustomDialog passwordDialog = mock(CustomDialog.class);
        when(passwordDialog.getCustomControls()).thenReturn(passwordControls);
        when(passwordDialog.syncOpen(sync)).thenReturn(Window.OK);
        when(dialogFactory.passwordDialog("Signon mickymouse", "mickymouse")).thenReturn(passwordDialog);
        underTest.dialogFactory = dialogFactory;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosCallbackHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        TextOutputCallback textCallback = mock(TextOutputCallback.class);
        NameCallback nameCallback = mock(NameCallback.class);
        PasswordCallback passwordCallback = mock(PasswordCallback.class);
        ConfirmationCallback confirmationCallback = mock(ConfirmationCallback.class);
        Callback[] callbacks = new Callback[]{textCallback,nameCallback,passwordCallback,confirmationCallback};
        underTest.handle(callbacks);
        verify(nameCallback).setName("mickymouse");
        verify(passwordCallback).setPassword(TEST_PASSWORD.toCharArray());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_handle_user_cancel() throws IOException, UnsupportedCallbackException
    {
        final PasswordControls passwordControls = mock(PasswordControls.class);
        when(passwordControls.getPassword()).thenReturn(TEST_PASSWORD.toCharArray());
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        UISynchronize sync = mock(UISynchronize.class);
        underTest.sync = sync;
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        underTest.loginData = loginData;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        CustomDialog passwordDialog = mock(CustomDialog.class);
        when(passwordDialog.getCustomControls()).thenReturn(passwordControls);
        when(passwordDialog.syncOpen(sync)).thenReturn(Window.CANCEL);
        when(dialogFactory.passwordDialog("Signon mickymouse", "mickymouse")).thenReturn(passwordDialog);
        underTest.dialogFactory = dialogFactory;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosCallbackHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        TextOutputCallback textCallback = mock(TextOutputCallback.class);
        NameCallback nameCallback = mock(NameCallback.class);
        PasswordCallback passwordCallback = mock(PasswordCallback.class);
        when(passwordCallback.getPrompt()).thenReturn(PROMPT);
        ConfirmationCallback confirmationCallback = mock(ConfirmationCallback.class);
        Callback[] callbacks = new Callback[]{textCallback,nameCallback,passwordCallback,confirmationCallback};
        underTest.handle(callbacks);
        verify(nameCallback).setName("mickymouse");
        verify(passwordCallback, times(0)).setPassword(TEST_PASSWORD.toCharArray());
        verify(eventBroker).send(CyberteteEvents.USER_CANCEL, PROMPT);
    }

    @Test
    public void test_handle_null_singleton() throws IOException, UnsupportedCallbackException
    {
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        TextOutputCallback textCallback = mock(TextOutputCallback.class);
        NameCallback nameCallback = mock(NameCallback.class);
        PasswordCallback passwordCallback = mock(PasswordCallback.class);
        ConfirmationCallback confirmationCallback = mock(ConfirmationCallback.class);
        Callback[] callbacks = new Callback[]{textCallback,nameCallback,passwordCallback,confirmationCallback};
        try
        {
            underTest.handle(callbacks);
            failBecauseExceptionWasNotThrown(UnsupportedCallbackException.class);
        }
        catch(UnsupportedCallbackException e)
        {
            assertThat(e.getMessage()).isEqualTo("Signon support not available");
        }
    }
    
    @Test
    public void test_handle_unknown_callback() throws IOException, UnsupportedCallbackException
    {
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosCallbackHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        Callback unknownCallback = mock(Callback.class);
        Callback[] callbacks = new Callback[]{unknownCallback};
        try
        {
            underTest.handle(callbacks);
            failBecauseExceptionWasNotThrown(UnsupportedCallbackException.class);
        }
        catch(UnsupportedCallbackException e)
        {
            assertThat(e.getMessage()).isEqualTo("Unrecognized Callback");
            assertThat(e.getCallback()).isEqualTo(unknownCallback);
        }
    }

    @Test
    public void test_loadClass_not_found() throws IOException, UnsupportedCallbackException
    {
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosCallbackHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        try
        {
            underTest.loadClass("com.disney.MickyMouse");
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch(CyberteteException e)
        {
            verify(logger).error(eq("com.disney.MickyMouse not found by classloader"), isA(ClassNotFoundException.class));
            assertThat(e.getMessage()).isEqualTo("com.disney.MickyMouse not found by classloader");
        }
    }

    @Test
    public void test_handle_jid_empty_name() throws IOException, UnsupportedCallbackException
    {
        KerberosCallbackHandler underTest = new KerberosCallbackHandler();
        KerberosCallbackHandler.singleton = null;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosCallbackHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn("@disney.com");
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        underTest.loginData = loginData;
        NameCallback nameCallback = mock(NameCallback.class);
        when(nameCallback.getPrompt()).thenReturn(PROMPT);
        Callback[] callbacks = new Callback[]{nameCallback};
        try
        {
            underTest.handle(callbacks);
            failBecauseExceptionWasNotThrown(UnsupportedCallbackException.class);
        }
        catch(UnsupportedCallbackException e)
        {
            assertThat(e.getMessage()).isEqualTo("\"" + PROMPT + "\" is required for for signon");
            assertThat(e.getCallback()).isEqualTo(nameCallback);
        }
    }

}
