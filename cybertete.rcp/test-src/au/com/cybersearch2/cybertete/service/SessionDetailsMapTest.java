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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.preferences.PreferenceConstants;
import au.com.cybersearch2.e4.SecureStorage;

/**
 * SessionDetailsMapTest
 * 
 * @author Andrew Bowley 20 May 2016
 */
public class SessionDetailsMapTest
{
    /**
     * TestSessionDetailsMap
     * Extend class under test to be able to set super fields
     */
    class TestSessionDetailsMap extends SessionDetailsMap
    {
        TestSessionDetailsMap(IEclipsePreferences preferences, SecureStorage secureStorage)
        {
            this.preferences = preferences;
            this.secureStorage = secureStorage;
        }
    }
    
    static final String ROOT = "au.com.cybersearch2.cybertete/saved_connections";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_JID2 = "aliz@google.com";
    static final String TEST_PASSWORD = "secret";
    static final String TEST_PASSWORD2 = "secret2";
    static final String TEST_HOST1 = "chat.disney.com";
    static final String TEST_HOST2 = "google.talk";
    static final String TEST_USERNAME1 = "donald";
    static final String TEST_USERNAME2 = "hilliary";
    
    static final String[] STORAGE_KEYS =
    	{
			PreferenceConstants.HOST,
			PreferenceConstants.PORT,
			PreferenceConstants.AUTH_CID,
			PreferenceConstants.PLAIN_SASL,
			PreferenceConstants.PASSWORD,
			PreferenceConstants.LAST_USER,
			PreferenceConstants.AUTO_LOGIN,
			PreferenceConstants.CLIENT_CERT_AUTH,
			PreferenceConstants.KEYSTORE_TYPE,
			PreferenceConstants.KEYSTORE_FILE,
			PreferenceConstants.KEYSTORE_PASSWORD,
			PreferenceConstants.SSO_USER
    	};

    @Test
    public void test_saveSessionDetails() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.isDirty()).thenReturn(true);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        when(sessionDetails1.getHost()).thenReturn(TEST_HOST1);
        when(sessionDetails1.getPort()).thenReturn(5222);
        when(sessionDetails1.getAuthcid()).thenReturn(TEST_USERNAME1);
        when(sessionDetails1.isPlainSasl()).thenReturn(false);
         when(sessionDetails1.getPassword()).thenReturn(TEST_PASSWORD);
        underTest.sessionDetailsMap.put(TEST_JID, sessionDetails1);
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        String PATH1 = ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.isDirty()).thenReturn(true);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        when(sessionDetails2.getHost()).thenReturn(TEST_HOST2);
        when(sessionDetails2.getPort()).thenReturn(5223);
        when(sessionDetails2.getAuthcid()).thenReturn(TEST_USERNAME2);
        when(sessionDetails2.isPlainSasl()).thenReturn(true);
        when(sessionDetails2.getPassword()).thenReturn(TEST_PASSWORD2);
        underTest.sessionDetailsMap.put(TEST_JID2, sessionDetails2);
        IEclipsePreferences node2 = mock(IEclipsePreferences.class);
        String PATH2 = ROOT + "/" + TEST_JID2;
        when(preferences.node(PATH2)).thenReturn(node2);
        underTest.saveSessionDetails(ROOT);
        verify(node1).put("host", TEST_HOST1);
        verify(node1).put("port", "5222");
        verify(node1).put("authcid", TEST_USERNAME1);
        verify(node1).putBoolean("plain_sasl", false);
        verify(secureStorage).secureSave(TEST_JID, "password", TEST_PASSWORD);
        verify(node2).put("host", TEST_HOST2);
        verify(node2).put("port", "5223");
        verify(node2).put("authcid", TEST_USERNAME2);
        verify(node2).putBoolean("plain_sasl", true);
        verify(secureStorage).secureSave(TEST_JID2, "password", TEST_PASSWORD2);
        verify(preferences).flush();
        verify(secureStorage).flush();
        verify(sessionDetails1).clearDirtyFlag();
        verify(sessionDetails2).clearDirtyFlag();
    }
    
    @Test
    public void test_saveSessionDetails_new() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.isDirty()).thenReturn(true);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        when(sessionDetails1.getHost()).thenReturn(null);
        when(sessionDetails1.getPort()).thenReturn(0);
        when(sessionDetails1.getAuthcid()).thenReturn(null);
        when(sessionDetails1.isPlainSasl()).thenReturn(false);
        when(sessionDetails1.getPassword()).thenReturn(null);
        underTest.sessionDetailsMap.put(TEST_JID, sessionDetails1);
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(new String[]{"host", "port", "authcid", "plain_sasl", "prefs_single_signon"});
        String PATH1 = ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        underTest.saveSessionDetails(ROOT);
        verify(node1).remove("host");
        verify(node1).remove("port");
        verify(node1).remove("authcid");
        verify(node1).putBoolean("plain_sasl", false);
        verify(secureStorage).secureSave(TEST_JID, "password", "");
        verify(preferences).flush();
        verify(secureStorage).flush();
        verify(sessionDetails1).clearDirtyFlag();
    }
    
    @Test
    public void test_loadSessionDetails() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(node.childrenNames()).thenReturn(new String[]{TEST_JID, TEST_JID2});
        when(preferences.node(ROOT)).thenReturn(node);
        when(secureStorage.secureGet(TEST_JID, "password")).thenReturn(TEST_PASSWORD);
        when(secureStorage.secureGet(TEST_JID2, "password")).thenReturn(TEST_PASSWORD2);
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(new String[]{"host", "port", "authcid", "plain_sasl"});
        String PATH1 = ROOT + "/" + TEST_JID;
        IEclipsePreferences node2 = mock(IEclipsePreferences.class);
        when(node2.keys()).thenReturn(new String[]{"host", "port", "authcid", "plain_sasl"});
        String PATH2 = ROOT + "/" + TEST_JID2;
        when(preferences.node(PATH2)).thenReturn(node2);
        when(preferences.node(PATH1)).thenReturn(node1);
        when(node1.get("host", "")).thenReturn(TEST_HOST1);
        when(node1.get("port", "")).thenReturn("5222");
        when(node1.get("authcid", "")).thenReturn(TEST_USERNAME1);
        when(node1.getBoolean("plain_sasl", false)).thenReturn(false);
        when(node1.getBoolean("prefs_single_signon", false)).thenReturn(false);
        when(node2.get("host", "")).thenReturn(TEST_HOST2);
        when(node2.get("port", "")).thenReturn("5223");
        when(node2.get("authcid", "")).thenReturn(TEST_USERNAME2);
        when(node2.getBoolean("plain_sasl", false)).thenReturn(true);
        when(node2.getBoolean("prefs_single_signon", false)).thenReturn(true);
        underTest.loadSessionDetails(ROOT);
        SessionDetails sessionDetails1 = underTest.sessionDetailsMap.get(TEST_JID);
        assertThat(sessionDetails1.getHost()).isEqualTo(TEST_HOST1);
        assertThat(sessionDetails1.getPort()).isEqualTo(5222);
        assertThat(sessionDetails1.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(sessionDetails1.getAuthcid()).isEqualTo(TEST_USERNAME1);
        assertThat(sessionDetails1.isPlainSasl()).isFalse();
        assertThat(sessionDetails1.isDirty()).isTrue();
        SessionDetails sessionDetails2 = underTest.sessionDetailsMap.get(TEST_JID2);
        assertThat(sessionDetails2.getHost()).isEqualTo(TEST_HOST2);
        assertThat(sessionDetails2.getPort()).isEqualTo(5223);
        assertThat(sessionDetails2.getPassword()).isEqualTo(TEST_PASSWORD2);
        assertThat(sessionDetails2.getAuthcid()).isEqualTo(TEST_USERNAME2);
        assertThat(sessionDetails2.isPlainSasl()).isTrue();
        assertThat(sessionDetails2.isDirty()).isTrue();
    }
    
    @Test
    public void test_loadSessionDetails_new() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(node.childrenNames()).thenReturn(new String[]{TEST_JID});
        when(preferences.node(ROOT)).thenReturn(node);
        when(secureStorage.secureGet(TEST_JID, "password")).thenReturn("");
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(STORAGE_KEYS);
        String PATH1 = ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        when(node1.get(isA(String.class), isA(String.class))).thenReturn("");
        when(node1.getBoolean("plain_sasl", false)).thenReturn(false);
        when(node1.getBoolean("prefs_single_signon", false)).thenReturn(false);
        underTest.loadSessionDetails(ROOT);
        SessionDetails sessionDetails1 = underTest.sessionDetailsMap.get(TEST_JID);
        assertThat(sessionDetails1.getHost()).isNull();
        assertThat(sessionDetails1.getPort()).isEqualTo(0);
        assertThat(sessionDetails1.getPassword()).isEmpty();
        assertThat(sessionDetails1.getAuthcid()).isNull();
        assertThat(sessionDetails1.isPlainSasl()).isFalse();
        assertThat(sessionDetails1.isDirty()).isTrue();
    }

    @Test
    public void test_loadSessionDetails_empty_port() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(node.childrenNames()).thenReturn(new String[]{TEST_JID});
        when(preferences.node(ROOT)).thenReturn(node);
        when(secureStorage.secureGet(TEST_JID, "password")).thenReturn("");
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(new String[]{"host", "plain_sasl"});
        String PATH1 = ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        when(node1.get("host", "")).thenReturn(TEST_HOST1);
        when(node1.get("port", "")).thenReturn("");
        when(node1.getBoolean("plain_sasl", false)).thenReturn(false);
        underTest.loadSessionDetails(ROOT);
        SessionDetails sessionDetails1 = underTest.sessionDetailsMap.get(TEST_JID);
        assertThat(sessionDetails1.getHost()).isEqualTo(TEST_HOST1);
        assertThat(sessionDetails1.getPort()).isEqualTo(0);
        assertThat(sessionDetails1.getPassword()).isEmpty();
        assertThat(sessionDetails1.getAuthcid()).isNull();
        assertThat(sessionDetails1.isPlainSasl()).isFalse();
        assertThat(sessionDetails1.isDirty()).isTrue();
    }

    @Test
    public void test_put() throws BackingStoreException, IOException
    {
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        underTest.putSessionDetails(sessionDetails);
        assertThat(underTest.getSessionDetails(TEST_JID)).isEqualTo(sessionDetails);
    }

    @Test
    public void test_removeSessionDetails() throws BackingStoreException, IOException
    {
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        SessionDetailsMap underTest = new TestSessionDetailsMap(preferences, secureStorage);
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        String PATH1 = ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        IEclipsePreferences node2 = mock(IEclipsePreferences.class);
        String PATH2 = ROOT + "/" + TEST_JID2;
        when(preferences.node(PATH2)).thenReturn(node2);
        underTest.putSessionDetails(sessionDetails1);
        underTest.putSessionDetails(sessionDetails2);
        ArrayList<SessionDetails> deletedSessionDetails = new ArrayList<SessionDetails>(2);
        deletedSessionDetails.add(sessionDetails1);
        deletedSessionDetails.add(sessionDetails2);
        underTest.removeSessionDetails(ROOT, deletedSessionDetails);
        assertThat(underTest.sessionDetailsMap).isEmpty();
        verify(secureStorage).secureRemove(TEST_JID, "password");
        verify(secureStorage).secureRemove(TEST_JID2, "password");
        verify(node1).removeNode();
        verify(node2).removeNode();
        verify(preferences).flush();
        verify(secureStorage).flush();
   }
}
