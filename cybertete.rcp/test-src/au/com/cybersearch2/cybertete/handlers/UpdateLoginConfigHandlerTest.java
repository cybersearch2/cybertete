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

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.security.ProviderException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.internal.LoginConfig;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.LoginStatus;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * UpdateLoginConfigHandlerTest
 * @author Andrew Bowley
 * 11 Mar 2016
 */
public class UpdateLoginConfigHandlerTest
{
    private static final String SAVE_ERROR = "Changes not applied";
    static final String TEST_PASSWORD = "secret";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_PASSWORD2 = "secret2";
    static final String TEST_JID2 = "adeline@google.com";
    static final String GSSAPI_PRINCIPAL = "mickymouse";
    static final String TEST_HOST = "google.talk";
    static final String TEST_USERNAME = "donald";

    @Test
    public void test_applyChanges()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        LoginData loginData = mock(LoginData.class);
        updateLoginConfigHandler.loginData = loginData;
        LoginConfig loginConfig = mock(LoginConfig.class);
        //when(loginConfig.getGssapiPrincipal()).thenReturn(TEST_JID2);
        when(loginConfig.getHost()).thenReturn(TEST_HOST);
        when(loginConfig.getJid()).thenReturn(GSSAPI_PRINCIPAL + "@disney.com");
        when(loginConfig.getGssapiPrincipal()).thenReturn(GSSAPI_PRINCIPAL);
        when(loginConfig.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginConfig.getPort()).thenReturn(5222);
        when(loginConfig.getUsername()).thenReturn(TEST_USERNAME);
        when(loginConfig.isAutoLogin()).thenReturn(true);
        when(loginConfig.isPlainSasl()).thenReturn(true);

        assertThat(updateLoginConfigHandler.applyChanges(loginConfig, true)).isEqualTo(LoginStatus.noError);
        ArgumentCaptor<SessionDetails> sessionDetailsCaptor = ArgumentCaptor.forClass(SessionDetails.class);
        verify(loginData).setSessionDetails(sessionDetailsCaptor.capture());
        SessionDetails sessionDetails = sessionDetailsCaptor.getValue();
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails.getPort()).isEqualTo(5222);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
        verify(loginData).updateAutoLogin(true);
    }

    @Test
    public void test_applyChanges_empty_jid()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        updateLoginConfigHandler.errorDialog = errorDialog;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getJid()).thenReturn("");
        assertThat(updateLoginConfigHandler.applyChanges(loginConfig, true)).isEqualTo(LoginStatus.fail);
        verify(errorDialog).showError("Invalid JID", "JID field must not be blank.");
    }
    
    @Test
    public void test_applyChanges_invalid_jid()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        updateLoginConfigHandler.errorDialog = errorDialog;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getJid()).thenReturn("x?");
         assertThat(updateLoginConfigHandler.applyChanges(loginConfig,true)).isEqualTo(LoginStatus.fail);
        verify(errorDialog).showError("Invalid JID", "JID field format incorrect.");
    }
    
    @Test
    public void test_applyChanges_invalid_password()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        PersistentSecurityData sslData = mock(PersistentSecurityData.class);
        when(sslData.isClientCertAuth()).thenReturn(false);
        updateLoginConfigHandler.persistentSecurityData = sslData;
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        updateLoginConfigHandler.errorDialog = errorDialog;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getJid()).thenReturn(TEST_JID);
        when(loginConfig.getPassword()).thenReturn("12345");
        assertThat(updateLoginConfigHandler.applyChanges(loginConfig, true)).isEqualTo(LoginStatus.invalidPassword);
        verify(errorDialog).showError("Invalid Password", "Password must contain at least 6 characters");
    }
    
    @Test
    public void test_applyChanges_no_options()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        LoginData loginData = mock(LoginData.class);
        updateLoginConfigHandler.loginData = loginData;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getJid()).thenReturn(GSSAPI_PRINCIPAL + "@disney.com");
        when(loginConfig.getGssapiPrincipal()).thenReturn(GSSAPI_PRINCIPAL);
        when(loginConfig.getHost()).thenReturn("");
        when(loginConfig.getPassword()).thenReturn("");
        when(loginConfig.getUsername()).thenReturn("");
        assertThat(updateLoginConfigHandler.applyChanges(loginConfig, true)).isEqualTo(LoginStatus.noError);
        ArgumentCaptor<SessionDetails> sessionDetailsCaptor = ArgumentCaptor.forClass(SessionDetails.class);
        verify(loginData).setSessionDetails(sessionDetailsCaptor.capture());
        SessionDetails sessionDetails = sessionDetailsCaptor.getValue();
        assertThat(sessionDetails.getHost()).isNull();
        // Password guaranteed to be non-null
        assertThat(sessionDetails.getPassword()).isEqualTo("");
        assertThat(sessionDetails.getPort()).isEqualTo(0);
        assertThat(sessionDetails.getAuthcid()).isNull();
        assertThat(sessionDetails.getUsername()).isEqualTo(GSSAPI_PRINCIPAL);
    }
    
    @Test
    public void test_delete_1_session()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        Logger logger = mock(Logger.class);
        updateLoginConfigHandler.logger = logger;
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        Set<SessionDetails> deletedSessions = Collections.singleton(sessionDetails);
        LoginData loginData = mock(LoginData.class);
        updateLoginConfigHandler.loginData = loginData;
        SyncQuestionDialog syncQuestionDialog = mock(SyncQuestionDialog.class);
        when(syncQuestionDialog.ask("Cybertete", "Delete this account?\n" + TEST_JID + "\n")).thenReturn(true);
        updateLoginConfigHandler.syncQuestionDialog = syncQuestionDialog;
        assertThat(updateLoginConfigHandler.onSessionsDeleted(deletedSessions)).isTrue();
        verify(loginData).remove(deletedSessions);
        verify(logger).info(deletedSessions.size() + " account(s) deleted");
    }
    
    @Test
    public void test_delete_2_session()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        Logger logger = mock(Logger.class);
        updateLoginConfigHandler.logger = logger;
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        Set<SessionDetails> deletedSessions = new TreeSet<SessionDetails>();
        deletedSessions.add(sessionDetails1);
        deletedSessions.add(sessionDetails2);
        LoginData loginData = mock(LoginData.class);
        updateLoginConfigHandler.loginData = loginData;
        SyncQuestionDialog syncQuestionDialog = mock(SyncQuestionDialog.class);
        when(syncQuestionDialog.ask("Cybertete", "Delete these accounts?\n" + TEST_JID + "\n" + TEST_JID2 + "\n")).thenReturn(true);
        updateLoginConfigHandler.syncQuestionDialog = syncQuestionDialog;
        assertThat(updateLoginConfigHandler.onSessionsDeleted(deletedSessions)).isTrue();
        verify(loginData).remove(deletedSessions);
        verify(logger).info(deletedSessions.size() + " account(s) deleted");
    }
        
    @Test
    public void test_saveLoginConfig()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        LoginData loginData = mock(LoginData.class);
        Set<SessionDetails> deletedSessions = Collections.emptySet();
        when(loginData.getDeletedSessions()).thenReturn(deletedSessions);
        updateLoginConfigHandler.loginData = loginData;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getHost()).thenReturn(TEST_HOST);
        when(loginConfig.getJid()).thenReturn(GSSAPI_PRINCIPAL + "@disney.com");
        when(loginConfig.getGssapiPrincipal()).thenReturn(GSSAPI_PRINCIPAL);
        when(loginConfig.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginConfig.getPort()).thenReturn(5222);
        when(loginConfig.getUsername()).thenReturn(TEST_USERNAME);
        when(loginConfig.isAutoLogin()).thenReturn(true);
        when(loginConfig.isPlainSasl()).thenReturn(true);

        assertThat(updateLoginConfigHandler.saveLoginConfig(loginConfig, true)).isEqualTo(LoginStatus.noError);
        ArgumentCaptor<SessionDetails> sessionDetailsCaptor = ArgumentCaptor.forClass(SessionDetails.class);
        verify(loginData).setSessionDetails(sessionDetailsCaptor.capture());
        SessionDetails sessionDetails = sessionDetailsCaptor.getValue();
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails.getPort()).isEqualTo(5222);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
        verify(loginData).updateAutoLogin(true);
    }
    
    @Test
    public void test_saveLoginConfig_with_deleted_session()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        SyncQuestionDialog syncQuestionDialog = mock(SyncQuestionDialog.class);
        when(syncQuestionDialog.ask("Cybertete", "Delete this account?\n" + TEST_JID + "\n")).thenReturn(true);
        updateLoginConfigHandler.syncQuestionDialog = syncQuestionDialog;
        LoginData loginData = mock(LoginData.class);
        Logger logger = mock(Logger.class);
        updateLoginConfigHandler.logger = logger;
        SessionDetails todDeleteSessionDetails = mock(SessionDetails.class);
        when(todDeleteSessionDetails.getJid()).thenReturn(TEST_JID);
        Set<SessionDetails> deletedSessions = Collections.singleton(todDeleteSessionDetails);
        when(loginData.getDeletedSessions()).thenReturn(deletedSessions);
        updateLoginConfigHandler.loginData = loginData;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getHost()).thenReturn(TEST_HOST);
        when(loginConfig.getJid()).thenReturn(GSSAPI_PRINCIPAL + "@disney.com");
        when(loginConfig.getGssapiPrincipal()).thenReturn(GSSAPI_PRINCIPAL);
        when(loginConfig.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginConfig.getPort()).thenReturn(5222);
        when(loginConfig.getUsername()).thenReturn(TEST_USERNAME);
        when(loginConfig.isAutoLogin()).thenReturn(true);
        when(loginConfig.isPlainSasl()).thenReturn(true);

        assertThat(updateLoginConfigHandler.saveLoginConfig(loginConfig, true)).isEqualTo(LoginStatus.noError);
        ArgumentCaptor<SessionDetails> sessionDetailsCaptor = ArgumentCaptor.forClass(SessionDetails.class);
        verify(loginData).setSessionDetails(sessionDetailsCaptor.capture());
        SessionDetails sessionDetails = sessionDetailsCaptor.getValue();
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails.getPort()).isEqualTo(5222);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
        verify(loginData).updateAutoLogin(true);
        verify(loginData).applyChanges(deletedSessions, true);
    }
        
    @Test
    public void test_saveLoginConfig_exception()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        Logger logger = mock(Logger.class);
        updateLoginConfigHandler.logger = logger;
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        updateLoginConfigHandler.errorDialog = errorDialog;
        LoginData loginData = mock(LoginData.class);
        Set<SessionDetails> deletedSessions = Collections.emptySet();
        when(loginData.getDeletedSessions()).thenReturn(deletedSessions);
        updateLoginConfigHandler.loginData = loginData;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getHost()).thenReturn(TEST_HOST);
        when(loginConfig.getJid()).thenReturn(GSSAPI_PRINCIPAL + "@disney.com");
        when(loginConfig.getGssapiPrincipal()).thenReturn(GSSAPI_PRINCIPAL);
        when(loginConfig.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginConfig.getPort()).thenReturn(5222);
        when(loginConfig.getUsername()).thenReturn(TEST_USERNAME);
        when(loginConfig.isAutoLogin()).thenReturn(true);
        when(loginConfig.isPlainSasl()).thenReturn(true);
        ProviderException providerException = new ProviderException("Disk error", new Exception());
        doThrow(providerException)
        .when(loginData).setSessionDetails(isA(SessionDetails.class));
        assertThat(updateLoginConfigHandler.saveLoginConfig(loginConfig, true)).isEqualTo(LoginStatus.fail);
        verify(logger).error(providerException, SAVE_ERROR);
        verify(errorDialog).showError(SAVE_ERROR, "Disk error");
    }
    
    @Test
    public void test_onSaveLoginConfigHandler()
    {
        UpdateLoginConfigHandler updateLoginConfigHandler = new UpdateLoginConfigHandler();
        UISynchronize sync = mock(UISynchronize.class);
        updateLoginConfigHandler.sync = sync;
        SaveLoginSessionHandler saveLoginSessionHandler = mock(SaveLoginSessionHandler.class);
        updateLoginConfigHandler.saveLoginSessionHandler = saveLoginSessionHandler;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        updateLoginConfigHandler.jobScheduler = jobScheduler;
        LoginData loginData = mock(LoginData.class);
        Set<SessionDetails> deletedSessions = Collections.emptySet();
        when(loginData.getDeletedSessions()).thenReturn(deletedSessions);
        updateLoginConfigHandler.loginData = loginData;
        LoginConfig loginConfig = mock(LoginConfig.class);
        when(loginConfig.getHost()).thenReturn(TEST_HOST);
        String userJid = GSSAPI_PRINCIPAL + "@disney.com";
        when(loginConfig.getJid()).thenReturn(userJid);
        when(loginConfig.getGssapiPrincipal()).thenReturn(GSSAPI_PRINCIPAL);
        when(loginConfig.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginConfig.getPort()).thenReturn(5222);
        when(loginConfig.getUsername()).thenReturn(TEST_USERNAME);
        when(loginConfig.isAutoLogin()).thenReturn(true);
        when(loginConfig.isPlainSasl()).thenReturn(true);

        UpdateLoginConfigEvent updateLoginConfigEvent = mock(UpdateLoginConfigEvent.class);
        LoginConfigEnsemble loginConfigEnsemble = new LoginConfigEnsemble(loginConfig, updateLoginConfigEvent, true);
        updateLoginConfigHandler.onSaveLoginConfigHandler(loginConfigEnsemble);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save login configuration"), jobCaptor.capture());
        jobCaptor.getValue().run();
        ArgumentCaptor<SessionDetails> sessionDetailsCaptor = ArgumentCaptor.forClass(SessionDetails.class);
        verify(loginData).setSessionDetails(sessionDetailsCaptor.capture());
        SessionDetails sessionDetails = sessionDetailsCaptor.getValue();
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails.getPort()).isEqualTo(5222);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
        verify(loginData).updateAutoLogin(true);
        ArgumentCaptor<Runnable> syncCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).asyncExec(syncCaptor.capture());
        syncCaptor.getValue().run();
        verify(saveLoginSessionHandler).persistCurrentLoginSession(userJid);
    }
}
