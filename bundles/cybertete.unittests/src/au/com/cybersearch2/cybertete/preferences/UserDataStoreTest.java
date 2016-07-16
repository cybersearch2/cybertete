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
package au.com.cybersearch2.cybertete.preferences;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.SecurityConfig;
import au.com.cybersearch2.e4.SecureStorage;

/**
 * UserDataStoreTest
 * @author Andrew Bowley
 * 23 May 2016
 */
public class UserDataStoreTest
{
    class TestUserDataStore extends UserDataStore
    {
        public TestUserDataStore(IEclipsePreferences preferences, SecureStorage secureStorage)
        {
            this.preferences = preferences;
            this.secureStorage = secureStorage;
        }
    }
    
    static final String APPLICATION_PATH = "/instance/au.com.cybersearch2.cybertete";
    static final String PREFERENCES_ROOT = APPLICATION_PATH + "/" + PreferenceConstants.SAVED;
    static final String TEST_JID = "mickymouse@disney.com";
    static final String KEYSTORE_FILE = "keystore.jks";
    static final String KEYSTORE_PASS = "changeme";
    static final String JKS = "JKS";

    @Test
    public void test_postConstruct() throws BackingStoreException
    {
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        IEclipsePreferences appPreferences = mock(IEclipsePreferences.class);
        when(appPreferences.absolutePath()).thenReturn(APPLICATION_PATH);
        Preferences savedNode = mock(Preferences.class);
        when(appPreferences.node(PreferenceConstants.SAVED)).thenReturn(savedNode);
        when(savedNode.absolutePath()).thenReturn(PREFERENCES_ROOT);
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(node.childrenNames()).thenReturn(new String[]{TEST_JID});
        when(preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        when(secureStorage.secureGet(TEST_JID, "password")).thenReturn("");
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(new String[]{"plain_sasl", "prefs_single_signon"});
        String PATH1 = PREFERENCES_ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        when(node1.getBoolean("plain_sasl", false)).thenReturn(false);
        when(node1.getBoolean("prefs_single_signon", false)).thenReturn(false);
        underTest.postConstruct(loggerProvider, appPreferences);
        assertThat(underTest.getSessionDetails(TEST_JID)).isNotNull();
        verify(savedNode).sync();
    }
    
    @Test
    public void test_postConstruct_exception() throws BackingStoreException
    {
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UserDataStore.class)).thenReturn(logger);
        IEclipsePreferences appPreferences = mock(IEclipsePreferences.class);
        when(appPreferences.absolutePath()).thenReturn(APPLICATION_PATH);
        Preferences savedNode = mock(Preferences.class);
        when(appPreferences.node(PreferenceConstants.SAVED)).thenReturn(savedNode);
        when(savedNode.absolutePath()).thenReturn(PREFERENCES_ROOT);
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        Throwable exception = new BackingStoreException("Eclipse cannot perform operation");
        when(node.childrenNames()).thenThrow(exception);
        when(preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        try
        {
            underTest.postConstruct(loggerProvider, appPreferences);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch(CyberteteException e)
        {
            verify(logger).error(exception, "Configuration retrieval from disk failed");
            assertThat(e.getMessage()).isEqualTo("Configuration retrieval from disk failed");
            assertThat(e.getCause()).isEqualTo(exception);
        }
        verify(savedNode).sync();
    }

    @Test
    public void test_getKeystore()
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(node.get("prefs_keystore_file", "/users/micky.pfx")).thenReturn(KEYSTORE_FILE);
        when(node.get("prefs_keystore_type", "PKCS12")).thenReturn(JKS);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        when(secureStorage.secureGet("global_preferences", "prefs_keystore_password")).thenReturn(KEYSTORE_PASS);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        underTest.APPLICATION_PATH = APPLICATION_PATH;
        SecurityConfig defaultConfig = 
                new SecurityConfig("/users/micky.pfx", "PKCS12", "changeit");
        KeystoreConfig keystoreConfig = underTest.getKeystore(defaultConfig);
        assertThat(keystoreConfig.getKeystoreFile()).isEqualTo(KEYSTORE_FILE);
        assertThat(keystoreConfig.getKeystoreType()).isEqualTo(JKS);
        assertThat(keystoreConfig.getKeystorePassword()).isEqualTo(KEYSTORE_PASS);
    }
    
    @Test
    public void test_setKeystore() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        underTest.APPLICATION_PATH = APPLICATION_PATH;
        SecurityConfig keystoreConfig = 
                new SecurityConfig(KEYSTORE_FILE, JKS, KEYSTORE_PASS);
        underTest.setKeystore(keystoreConfig);
        verify(node).put("prefs_keystore_file", KEYSTORE_FILE);
        verify(node).put("prefs_keystore_type", JKS);
        verify(secureStorage).secureSave("global_preferences", "prefs_keystore_password", KEYSTORE_PASS);
        verify(preferences).flush();
        verify(secureStorage).flush();
    }
    
    @Test
    public void test_saveSessionDetails() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        when(node.childrenNames()).thenReturn(new String[]{TEST_JID});
        when(preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        when(secureStorage.secureGet(TEST_JID, "password")).thenReturn("");
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(new String[]{"plain_sasl", "prefs_single_signon"});
        String PATH1 = PREFERENCES_ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        when(node1.getBoolean("plain_sasl", false)).thenReturn(false);
        when(node1.getBoolean("prefs_single_signon", false)).thenReturn(false);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        IEclipsePreferences appPreferences = mock(IEclipsePreferences.class);
        when(appPreferences.absolutePath()).thenReturn(APPLICATION_PATH);
        Preferences savedNode = mock(Preferences.class);
        when(appPreferences.node(PreferenceConstants.SAVED)).thenReturn(savedNode);
        when(savedNode.absolutePath()).thenReturn(PREFERENCES_ROOT);
        underTest.postConstruct(loggerProvider, appPreferences);
        underTest.saveSessionDetails(new SessionDetails(TEST_JID, ""));
        verify(node).put("prefs_last_connection", TEST_JID);
        verify(preferences).flush();
        verify(secureStorage).flush();
   }

    @Test
    public void test_removeSessionDetails() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        when(node.childrenNames()).thenReturn(new String[]{TEST_JID});
        when(preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        when(secureStorage.secureGet(TEST_JID, "password")).thenReturn("");
        IEclipsePreferences node1 = mock(IEclipsePreferences.class);
        when(node1.keys()).thenReturn(new String[]{"plain_sasl", "prefs_single_signon"});
        String PATH1 = PREFERENCES_ROOT + "/" + TEST_JID;
        when(preferences.node(PATH1)).thenReturn(node1);
        when(node1.getBoolean("plain_sasl", false)).thenReturn(false);
        when(node1.getBoolean("prefs_single_signon", false)).thenReturn(false);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        IEclipsePreferences appPreferences = mock(IEclipsePreferences.class);
        when(appPreferences.absolutePath()).thenReturn(APPLICATION_PATH);
        Preferences savedNode = mock(Preferences.class);
        when(appPreferences.node(PreferenceConstants.SAVED)).thenReturn(savedNode);
        when(savedNode.absolutePath()).thenReturn(PREFERENCES_ROOT);
        underTest.postConstruct(loggerProvider, appPreferences);
        underTest.removeSessionDetails(Collections.singletonList(new SessionDetails(TEST_JID, "")));
        assertThat(underTest.getSessionDetails(TEST_JID)).isNull();
        verify(secureStorage).secureRemove(TEST_JID, "password");
        verify(node1).removeNode();
        verify(preferences).flush();
        verify(secureStorage).flush();
   }
    
    @Test
    public void test_saveLastUser() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        underTest.APPLICATION_PATH = APPLICATION_PATH;
        underTest.saveLastUser(TEST_JID);
        verify(node).put("prefs_last_connection", TEST_JID);
        verify(preferences).flush();
   }

    @Test
    public void test_setters() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        underTest.APPLICATION_PATH = APPLICATION_PATH;
        underTest.setClientCertAuth(true);
        verify(node).putBoolean("prefs_client_Cert_auth", true);
        verify(preferences).flush();
        underTest.setAutoLogin(true);
        verify(node).putBoolean("prefs_auto_login", true);
        verify(preferences, times(2)).flush();
   }
    
    @Test
    public void test_getters() throws BackingStoreException, IOException
    {
        IEclipsePreferences preferences = mock(IEclipsePreferences.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        IEclipsePreferences node = mock(IEclipsePreferences.class);
        when(preferences.node(APPLICATION_PATH)).thenReturn(node);
        UserDataStore underTest = new TestUserDataStore(preferences, secureStorage);
        underTest.APPLICATION_PATH = APPLICATION_PATH;
        when(node.getBoolean("prefs_client_Cert_auth", false)).thenReturn(true);
        assertThat(underTest.isClientCertAuth()).isTrue();
        when(node.getBoolean("prefs_auto_login", false)).thenReturn(true);
        assertThat(underTest.isAutoLogin()).isTrue();
    }
}
