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
package au.com.cybersearch2.cybertete.service;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.service.ChatAccount;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;
import au.com.cybersearch2.cybertete.security.KerberosData;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.cybertete.service.SessionDetailsSet;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;

/**
 * LoginDataTest
 * @author Andrew Bowley
 * 16 Mar 2016
 */
public class LoginDataTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_PASSWORD = "secret";
    static final String TEST_PASSWORD2 = "secret2";
    static final String TEST_JID2 = "adeline@google.com";
    static final String GSSAPI_PRINCIPAL = "mickymouse";
    private static final String TEST_HOST = "google.talk";
    private static final String TEST_USERNAME = "donald";

    @Test
    public void test_persist() throws BackingStoreException, IOException
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetailsSet.applySessionDetails(TEST_JID)).thenReturn(sessionDetails );
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        underTest.persist(TEST_JID);
        verify(userDataStore).saveSessionDetails(sessionDetails);
    }

    @Test
    public void test_postConstruct()
    {
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(userDataStore.getSessionDetails(TEST_JID)).thenReturn(sessionDetails );
        when(userDataStore.getLastUser()).thenReturn(TEST_JID);
        List<String> users = Collections.emptyList();
        when(userDataStore.getUserCollection()).thenReturn(users);
        LoginData underTest = new LoginData();
        underTest.userDataStore = userDataStore;
        underTest.postConstruct();
        assertThat(underTest.getSessionDetails()).isEqualTo(sessionDetails);
    }

    @Test
    public void test_getSessionDetails()
    {
        LoginData underTest = new LoginData();
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetailsSet.getSessionDetails()).thenReturn(sessionDetails);
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        assertThat(underTest.getSessionDetails()).isEqualTo(sessionDetails);
    }

    @Test
    public void test_getSessionDetailsSet()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        
        List<SessionDetails> collection = new ArrayList<SessionDetails>();
        when(sessionDetailsSet.getCollection()).thenReturn(collection);
        underTest.userDataStore = userDataStore;
         assertThat(underTest.getAllSessionDetails()).isEqualTo(collection);
    }
    
    @Test
    public void test_isAutoLogin()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isAutoLogin()).thenReturn(true);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        assertThat(underTest.isAutoLogin()).isTrue();
    }
    
    @Test
    public void test_isSingleSignonEnabled()
    {
        LoginData underTest = new LoginData();
        KerberosData kerberosData = mock(KerberosData.class);
        when(kerberosData.isSingleSignonEnabled()).thenReturn(true);
        underTest.kerberosData = kerberosData;
        UserDataStore userDataStore = mock(UserDataStore.class);
         SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
         underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        assertThat(underTest.isSingleSignonEnabled()).isTrue();
    }

    @Test
    public void test_deleteSession()
    {
        LoginData underTest = new LoginData();
        SessionDetails sessionDetails = mock(SessionDetails.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        when(sessionDetailsSet.removeUser(TEST_JID)).thenReturn(sessionDetails);
        UserDataStore userDataStore = mock(UserDataStore.class);
        underTest.sessionDetailsSet = sessionDetailsSet;
        when(sessionDetailsSet.getSessionDetailsByJid(TEST_JID)).thenReturn(sessionDetails);
        underTest.userDataStore = userDataStore;
        underTest.deleteSession(TEST_JID);
        assertThat(underTest.deletedSessionDetailsSet.size()).isEqualTo(1);
        assertThat(underTest.deletedSessionDetailsSet.contains(sessionDetails));
    }

    @Test
    public void test_getDeletedSessions()
    {
        LoginData underTest = new LoginData();
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        Set<SessionDetails> deletedSessionDetailsSet = new HashSet<SessionDetails>();
        underTest.deletedSessionDetailsSet = deletedSessionDetailsSet;
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        deletedSessionDetailsSet.add(sessionDetails);
        underTest.sessionDetailsSet = sessionDetailsSet;
        when(sessionDetailsSet.getSessionDetails()).thenReturn(sessionDetails);
        underTest.userDataStore = userDataStore;
        Set<SessionDetails> deleted = underTest.getDeletedSessions();
        assertThat(deleted).isEqualTo(deletedSessionDetailsSet);
        assertThat(deleted.contains(sessionDetails));
    }

    @Test
    public void test_getDeletedSessions_null()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        Set<SessionDetails> deleted = underTest.getDeletedSessions();
        assertThat(deleted.size()).isEqualTo(0);
    }

    @Test
    public void test_getUserList()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        when(sessionDetailsSet.getNewUser()).thenReturn("");
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        List<SessionDetails> collection = new ArrayList<SessionDetails>();
        when(sessionDetailsSet.getCollection()).thenReturn(collection);
        collection.add(new SessionDetails(TEST_JID + "0"));
        collection.add(new SessionDetails(TEST_JID + "1"));
        List<String> userList = underTest.getUserList();
       assertThat(userList.size()).isEqualTo(2);
       assertThat(userList.get(0)).isEqualTo(TEST_JID + "0");
       assertThat(userList.get(1)).isEqualTo(TEST_JID + "1");
    }
    
    @Test
    public void test_getUserList_empty_deleted()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        when(sessionDetailsSet.getNewUser()).thenReturn("");
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        List<SessionDetails> collection = new ArrayList<SessionDetails>();
        when(sessionDetailsSet.getCollection()).thenReturn(collection);
        collection.add(new SessionDetails(TEST_JID + "0"));
        collection.add(new SessionDetails(TEST_JID + "1"));
        underTest.deletedSessionDetailsSet = new TreeSet<SessionDetails>();
        List<String> userList = underTest.getUserList();
       assertThat(userList.size()).isEqualTo(2);
       assertThat(userList.get(0)).isEqualTo(TEST_JID + "0");
       assertThat(userList.get(1)).isEqualTo(TEST_JID + "1");
    }
      
    @Test
    public void test_getUserList_all_deleted()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        when(sessionDetailsSet.getNewUser()).thenReturn("");
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        List<SessionDetails> collection = new ArrayList<SessionDetails>();
        Set<SessionDetails> deleted = new TreeSet<SessionDetails>();
        when(sessionDetailsSet.getCollection()).thenReturn(collection);
        collection.add(new SessionDetails(TEST_JID + "0"));
        collection.add(new SessionDetails(TEST_JID + "1"));
        deleted.add(new SessionDetails(TEST_JID + "0"));
        deleted.add(new SessionDetails(TEST_JID + "1"));
        underTest.deletedSessionDetailsSet = deleted;
        List<String> userList = underTest.getUserList();
       assertThat(userList.size()).isEqualTo(0);
    }

    @Test
    public void test_updateAutoLogin_false() throws BackingStoreException, IOException
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isAutoLogin()).thenReturn(true);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        underTest.updateAutoLogin(false);
        verify(userDataStore).setAutoLogin(false);
    }

    @Test
    public void test_updateAutoLogin_true() throws BackingStoreException, IOException
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isAutoLogin()).thenReturn(false);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        underTest.updateAutoLogin(true);
        verify(userDataStore).setAutoLogin(true);
    }

    @Test
    public void test_saveLastUser() throws BackingStoreException, IOException
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isAutoLogin()).thenReturn(true);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        underTest.saveLastUser(TEST_JID);
        verify(userDataStore).saveLastUser(TEST_JID);
    }

    @Test
    public void test_getLoginDetails()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SessionDetails configDetails = getConfigDetails();
        SessionDetails sessionDetails = underTest.getLoginDetails(configDetails, ConnectionError.noError);
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails.getPort()).isEqualTo(5223);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
    }

    @Test
    public void test_getLoginDetails_clear_notAuthorized()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SessionDetails configDetails = getConfigDetails();
        SessionDetails sessionDetails = underTest.getLoginDetails(configDetails, ConnectionError.notAuthorized);
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEmpty();
        assertThat(sessionDetails.getPort()).isEqualTo(5223);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
    }

    @Test
    public void test_getLoginDetails_new_account()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SessionDetails configDetails = new SessionDetails("");
        SessionDetails sessionDetails = underTest.getLoginDetails(configDetails, ConnectionError.noError);
        assertThat(sessionDetails.getJid()).isEmpty();
        assertThat(sessionDetails.getHost()).isEmpty();
        assertThat(sessionDetails.getPassword()).isEmpty();
        assertThat(sessionDetails.getPort()).isEqualTo(0);
        assertThat(sessionDetails.getAuthcid()).isEmpty();
        assertThat(sessionDetails.getUsername()).isEqualTo("");
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(false);
    }

    @Test
    public void test_getLoginDetails_default_port()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SessionDetails configDetails = new SessionDetails(TEST_JID);
        configDetails.setHost(TEST_HOST);
        SessionDetails sessionDetails = underTest.getLoginDetails(configDetails, ConnectionError.noError);
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEmpty();
        assertThat(sessionDetails.getPort()).isEqualTo(5222);
        assertThat(sessionDetails.getAuthcid()).isEmpty();
        assertThat(sessionDetails.getUsername()).isEqualTo("");
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(false);
    }

    @Test
    public void test_deselectCurrentAccount()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetails configDetails = getConfigDetails();
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        when(sessionDetailsSet.getSessionDetails()).thenReturn(configDetails);
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        underTest.questionDialog = questionDialog;
        when(questionDialog .ask("Configuration for user " + TEST_JID, "Apply changes to this configuration")).thenReturn(true);
        assertThat(underTest.deselectCurrentAccount()).isTrue();
    }
    
    @Test
    public void test_getaccount()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SessionDetails configDetails = getConfigDetails();
        when(sessionDetailsSet.setCurrentUser(TEST_JID)).thenReturn(configDetails);
        ChatAccount sessionDetails = underTest.selectAccount(TEST_JID, ConnectionError.noError);
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEqualTo(TEST_HOST);
        assertThat(sessionDetails.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails.getPort()).isEqualTo(5223);
        assertThat(sessionDetails.getAuthcid()).isEqualTo(TEST_USERNAME);
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(true);
    }

    @Test
    public void test_getaccount_jid_not_found()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        when(sessionDetailsSet.setCurrentUser(TEST_JID)).thenReturn(null);
        when(sessionDetailsSet.createNewUser(TEST_JID)).thenReturn(new SessionDetails(TEST_JID));
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
         ChatAccount sessionDetails = underTest.selectAccount(TEST_JID, ConnectionError.noError);
        assertThat(sessionDetails.getJid()).isEqualTo(TEST_JID);
        assertThat(sessionDetails.getHost()).isEmpty();
        assertThat(sessionDetails.getPassword()).isEmpty();
        assertThat(sessionDetails.getPort()).isEqualTo(0);
        assertThat(sessionDetails.getAuthcid()).isEmpty();
        assertThat(sessionDetails.isPlainSasl()).isEqualTo(false);
    }


    SessionDetails getConfigDetails()
    {
        SessionDetails sessionDetails = new SessionDetails(TEST_JID, TEST_PASSWORD);
        sessionDetails.setHost(TEST_HOST);
        sessionDetails.setPort(5223);
        sessionDetails.setAuthcid(TEST_USERNAME);
        sessionDetails.setPlainSasl(true);
        return sessionDetails;
    }
    
    @Test
    public void test_setSessionDetails()
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        SessionDetails newSessionDetails = getConfigDetails();
        underTest.setSessionDetails(newSessionDetails);
        verify(sessionDetailsSet).setSessionDetails(newSessionDetails);

    }

    @Test
    public void test_applyChanges()
    {
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        Set<SessionDetails> deletedSessions = new HashSet<SessionDetails>();
        deletedSessions.add(sessionDetails1);
        deletedSessions.add(sessionDetails2);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        Set<SessionDetails> deletedSessionDetailsSet = Collections.emptySet();
        underTest.deletedSessionDetailsSet = deletedSessionDetailsSet;
        underTest.applyChanges(deletedSessions, true);
        assertThat(deletedSessionDetailsSet).isEmpty();
        deletedSessions.add(sessionDetails1);
        deletedSessions.add(sessionDetails2);
        underTest.applyChanges(deletedSessions, false);
        verify(sessionDetailsSet).undoChanges(deletedSessions);
        assertThat(deletedSessionDetailsSet).isEmpty();
    }

    @Test
    public void test_remove() throws BackingStoreException, IOException
    {
        LoginData underTest = new LoginData();
        UserDataStore userDataStore = mock(UserDataStore.class);
        SessionDetailsSet sessionDetailsSet = mock(SessionDetailsSet.class); 
        underTest.sessionDetailsSet = sessionDetailsSet;
        underTest.userDataStore = userDataStore;
        Set<SessionDetails> removeSessionDetailsSet = Collections.emptySet();
        underTest.remove(removeSessionDetailsSet);
        verify(userDataStore).removeSessionDetails(removeSessionDetailsSet);
    }
}
