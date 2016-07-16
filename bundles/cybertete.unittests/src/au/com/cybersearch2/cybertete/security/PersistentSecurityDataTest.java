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

import java.io.File;
import java.io.IOException;
import java.security.ProviderException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.service.prefs.BackingStoreException;

import au.com.cybersearch2.cybertete.GlobalProperties;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;

/**
 * PersistentSecurityDataTest
 * @author Andrew Bowley
 * 30 May 2016
 */
public class PersistentSecurityDataTest
{
    private static final String KEYSTORE_FILE = "keystore.jks";
    private static final String KEYSTORE_PASS = "changeme";
    protected static final String JKS = "JKS";
    protected static final String PKCS = "PKCS12";

    @Test
    public void test_postConstruct()
    {
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.postConstruct();
        assertThat(underTest.isClientCertAuth).isTrue();
        assertThat(underTest.isClientCertAuth()).isTrue();
    }

    @Test
    public void test_keystoreConfigInstance()
    {
        String userHomePath = "/users/micky";
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        when(globalProperties.getUserHome()).thenReturn(userHomePath);
        String userName = "micky";
        when(globalProperties.getUserName()).thenReturn(userName);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        KeystoreConfig keystoreConfig = new SecurityConfig(KEYSTORE_FILE, JKS, KEYSTORE_PASS);
        when(userDataStore.getKeystore(isA(SecurityConfig.class))).thenReturn(keystoreConfig);
        assertThat(underTest.keystoreConfigInstance()).isEqualTo(keystoreConfig);
        ArgumentCaptor<SecurityConfig> configCaptor = ArgumentCaptor.forClass(SecurityConfig.class);
        verify(userDataStore).getKeystore(configCaptor.capture());
        KeystoreConfig defaultConfig = configCaptor.getValue();
        assertThat(defaultConfig.getKeystoreFile()).isEqualTo(new File(userHomePath, userName + ".pfx").getAbsolutePath());
        assertThat(defaultConfig.getKeystoreType()).isEqualTo(PKCS);
        assertThat(defaultConfig.getKeystorePassword()).isEqualTo("changeit");
    }


    @Test
    public void test_getKeystoreConfig()
    {
        String userHomePath = "/users/micky";
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        when(globalProperties.getUserHome()).thenReturn(userHomePath);
        String userName = "micky";
        when(globalProperties.getUserName()).thenReturn(userName);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        KeystoreConfig keystoreConfig = new SecurityConfig(KEYSTORE_FILE, JKS, KEYSTORE_PASS);
        when(userDataStore.getKeystore(isA(SecurityConfig.class))).thenReturn(keystoreConfig);
        assertThat(underTest.getKeystoreConfig()).isEqualTo(keystoreConfig);
        ArgumentCaptor<SecurityConfig> configCaptor = ArgumentCaptor.forClass(SecurityConfig.class);
        verify(userDataStore).getKeystore(configCaptor.capture());
        KeystoreConfig defaultConfig = configCaptor.getValue();
        assertThat(defaultConfig.getKeystoreFile()).isEqualTo(new File(userHomePath, userName + ".pfx").getAbsolutePath());
        assertThat(defaultConfig.getKeystoreType()).isEqualTo(PKCS);
        assertThat(defaultConfig.getKeystorePassword()).isEqualTo("changeit");
    }

    @Test
    public void test_getKeystoreConfig_default()
    {
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(false);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        KeystoreConfig result = underTest.getKeystoreConfig();
        assertThat(result.getKeystoreFile()).isNull();
    }


    @Test
    public void test_saveConfig() throws BackingStoreException, IOException
    {
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(false);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        KeystoreConfig keystoreConfig = new SecurityConfig(KEYSTORE_FILE, JKS, KEYSTORE_PASS);
        underTest.saveConfig(keystoreConfig);
        verify(userDataStore).setKeystore(keystoreConfig);
    }

    @Test
    public void test_saveConfig_BackingStoreException() throws BackingStoreException, IOException
    {
        BackingStoreException exception = new BackingStoreException("Eclipse error");
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(false);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        KeystoreConfig keystoreConfig = new SecurityConfig(KEYSTORE_FILE, JKS, KEYSTORE_PASS);
        doThrow(exception)
        .when(userDataStore).setKeystore(keystoreConfig);
        try
        {
            underTest.saveConfig(keystoreConfig);
            failBecauseExceptionWasNotThrown(ProviderException.class);
        }
        catch(ProviderException e)
        {
            assertThat(e.getMessage()).isEqualTo("Error updating configuration");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }

    @Test
    public void test_saveConfig_IOException() throws BackingStoreException, IOException
    {
        IOException exception = new IOException("File error");
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(false);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        KeystoreConfig keystoreConfig = new SecurityConfig(KEYSTORE_FILE, JKS, KEYSTORE_PASS);
        doThrow(exception)
        .when(userDataStore).setKeystore(keystoreConfig);
        try
        {
            underTest.saveConfig(keystoreConfig);
            failBecauseExceptionWasNotThrown(ProviderException.class);
        }
        catch(ProviderException e)
        {
            assertThat(e.getMessage()).isEqualTo("Error updating configuration");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
    
    @Test
    public void test_setClientCertAuth() throws BackingStoreException, IOException
    {
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.postConstruct();
        underTest.setClientCertAuth(false);
        assertThat(underTest.isClientCertAuth()).isFalse();
        verify(userDataStore).setClientCertAuth(false);
    }

    @Test
    public void test_setClientCertAuth_BackingStoreException() throws BackingStoreException, IOException
    {
        BackingStoreException exception = new BackingStoreException("Eclipse error");
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(false);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        doThrow(exception)
        .when(userDataStore).setClientCertAuth(true);
        try
        {
            underTest.setClientCertAuth(true);
            failBecauseExceptionWasNotThrown(ProviderException.class);
        }
        catch(ProviderException e)
        {
            assertThat(e.getMessage()).isEqualTo("Error updating configuration");
            assertThat(e.getCause()).isEqualTo(exception);
            assertThat(underTest.isClientCertAuth()).isFalse();
        }
    }

    @Test
    public void test_setClientCertAuth_IOException() throws BackingStoreException, IOException
    {
        IOException exception = new IOException("File error");
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.globalProperties = globalProperties;
        underTest.postConstruct();
        doThrow(exception)
        .when(userDataStore).setClientCertAuth(false);
        try
        {
            underTest.setClientCertAuth(false);
            failBecauseExceptionWasNotThrown(ProviderException.class);
        }
        catch(ProviderException e)
        {
            assertThat(e.getMessage()).isEqualTo("Error updating configuration");
            assertThat(e.getCause()).isEqualTo(exception);
            assertThat(underTest.isClientCertAuth()).isTrue();
        }
    }

    @Test
    public void test_updateClientCertAuth() throws BackingStoreException, IOException
    {
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.postConstruct();
        underTest.updateClientCertAuth(false);
        assertThat(underTest.isClientCertAuth()).isFalse();
        verify(userDataStore).setClientCertAuth(false);
    }

    @Test
    public void test_updateClientCertAuth_nochange() throws BackingStoreException, IOException
    {
        UserDataStore userDataStore = mock(UserDataStore.class);
        when(userDataStore.isClientCertAuth()).thenReturn(true);
        PersistentSecurityData underTest = new PersistentSecurityData();
        underTest.userDataStore = userDataStore;
        underTest.postConstruct();
        underTest.updateClientCertAuth(true);
        assertThat(underTest.isClientCertAuth()).isTrue();
        verify(userDataStore, times(0)).setClientCertAuth(true);
    }

}
