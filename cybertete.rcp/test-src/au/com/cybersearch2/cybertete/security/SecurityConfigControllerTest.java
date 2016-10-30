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
import static org.mockito.Mockito.*;

import java.io.File;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.junit.Test;

import au.com.cybersearch2.cybertete.handlers.SecurityHandler;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.views.LoginView;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * SecurityConfigControllerTest
 * @author Andrew Bowley
 * 4 May 2016
 */
public class SecurityConfigControllerTest
{
    static final String KEYSTORE = "/dir/keystore.jks";
    static final String KEYSTORE_TYPE = SecurityConfig.KEYSTORE_TYPES[1];
    static final String PASSWORD = "changeit";

    @Test
    public void test_postConstruct()
    {
        SecurityConfigController underTest = new SecurityConfigController();
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        when(persistentSecurityData.keystoreConfigInstance()).thenReturn(new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD));
        underTest.persistentSecurityData = persistentSecurityData;
        underTest.postConstruct();
        assertThat(underTest.securityConfig.getKeystoreFile()).isEqualTo(KEYSTORE);
        assertThat(underTest.securityConfig.getKeystoreType()).isEqualTo(KEYSTORE_TYPE);
        assertThat(underTest.securityConfig.getKeystorePassword()).isEqualTo(PASSWORD);
    }

    @Test
    public void test_onApply()
    {
        SecurityConfig keystoreConfig = new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD);
        final File keystoreFile = mock(File.class);
        when(keystoreFile.exists()).thenReturn(true);
        when(keystoreFile.isFile()).thenReturn(true);
        SecurityConfigController underTest = new SecurityConfigController()
        {
            @Override
            protected File getKeystoreFile(String keystorePath)
            {
                return keystoreFile;
            }
        };
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        underTest.keystoreDirty = true;
        underTest.clientCertDirty = true;
        assertThat(underTest.onApply(true, keystoreConfig)).isTrue();
        assertThat(underTest.keystoreDirty).isFalse();
        assertThat(underTest.clientCertDirty).isFalse();
        verify(eventBroker).post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, true);
        verify(eventBroker).post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
    }
    
    @Test
    public void test_onApply_new_config()
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        SecurityConfig keystoreConfig = new SecurityConfig("","","");
        SecurityConfigController underTest = new SecurityConfigController();
        underTest.errorDialog = errorDialog;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        underTest.keystoreDirty = true;
        underTest.clientCertDirty = true;
        assertThat(underTest.onApply(true, keystoreConfig)).isFalse();
        assertThat(underTest.keystoreDirty).isFalse();
        assertThat(underTest.clientCertDirty).isFalse();
        verify(eventBroker).post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, true);
        verify(errorDialog).showError(SecurityHandler.INVALID_KEYSTORE,
                    "Keystore field must not be blank.");
        verify(eventBroker, times(0)).post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
    }
    
    @Test
    public void test_onApply_config_file_not_exists()
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        SecurityConfig keystoreConfig = new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD);
        final File keystoreFile = mock(File.class);
        when(keystoreFile.exists()).thenReturn(false);
        SecurityConfigController underTest = new SecurityConfigController()
        {
            @Override
            protected File getKeystoreFile(String keystorePath)
            {
                return keystoreFile;
            }
        };
        underTest.errorDialog = errorDialog;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        underTest.keystoreDirty = true;
        underTest.clientCertDirty = true;
        assertThat(underTest.onApply(true, keystoreConfig)).isFalse();
        assertThat(underTest.keystoreDirty).isFalse();
        assertThat(underTest.clientCertDirty).isFalse();
        verify(eventBroker).post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, true);
        verify(errorDialog).showError(SecurityHandler.INVALID_KEYSTORE,
                "Keystore file not found.");
        verify(eventBroker, times(0)).post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
    }
    
    @Test
    public void test_onApply_config_file_not_file()
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        SecurityConfig keystoreConfig = new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD);
        final File keystoreFile = mock(File.class);
        when(keystoreFile.exists()).thenReturn(true);
        when(keystoreFile.isFile()).thenReturn(false);
        SecurityConfigController underTest = new SecurityConfigController()
        {
            @Override
            protected File getKeystoreFile(String keystorePath)
            {
                return keystoreFile;
            }
        };
        underTest.errorDialog = errorDialog;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        underTest.keystoreDirty = true;
        underTest.clientCertDirty = true;
        assertThat(underTest.onApply(true, keystoreConfig)).isFalse();
        assertThat(underTest.keystoreDirty).isFalse();
        assertThat(underTest.clientCertDirty).isFalse();
        verify(eventBroker).post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, true);
        verify(errorDialog).showError(SecurityHandler.INVALID_KEYSTORE,
                "Keystore is not a file.");
        verify(eventBroker, times(0)).post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
    }

    @Test
    public void test_onApply_not_dirty()
    {
        SecurityConfig keystoreConfig = new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD);
        SecurityConfigController underTest = new SecurityConfigController();
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        assertThat(underTest.onApply(true, keystoreConfig)).isTrue();
        verify(eventBroker, times(0)).post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, true);
        verify(eventBroker, times(0)).post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
    }
    

    @Test
    public void test_short_methods()
    {
        SecurityConfigController underTest = new SecurityConfigController();
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        EPartService partService = mock(EPartService.class);
        MPart loginViewPart = mock(MPart.class);
        when(partService.findPart(LoginView.LOGIN_VIEW_ID)).thenReturn(loginViewPart);
        underTest.partService = partService;
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        SecurityConfig securityConfig = mock(SecurityConfig.class);
        when(persistentSecurityData.keystoreConfigInstance()).thenReturn(new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD));
        when(persistentSecurityData.isClientCertAuth()).thenReturn(true);
        underTest.persistentSecurityData = persistentSecurityData;
        underTest.postConstruct();
        underTest.activateLoginView();
        verify(partService).showPart(loginViewPart, PartState.ACTIVATE);
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        underTest.validateKeystoreConfig(keystoreConfig);
        verify(eventBroker).post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
        underTest.updateClientCertAuth(true);
        verify(eventBroker).post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, true);
        underTest.securityConfig = securityConfig;
        underTest.saveKeystoreConfig(keystoreConfig);
        verify(securityConfig).setConfig(keystoreConfig);
        verify(eventBroker).post(CyberteteEvents.SAVE_KEYSTORE_CONFIG, keystoreConfig);
        assertThat(underTest.getKeystoreConfig()).isEqualTo(securityConfig);
        assertThat(underTest.isClientCertAuth()).isTrue();
    }
}
