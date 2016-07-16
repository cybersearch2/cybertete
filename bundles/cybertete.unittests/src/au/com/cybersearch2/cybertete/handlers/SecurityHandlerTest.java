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

import java.security.Principal;
import java.security.ProviderException;
import java.security.cert.X509Certificate;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.security.KeystoreData;
import au.com.cybersearch2.cybertete.security.KeystoreHelper;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncInfoDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * SecurityHandlerTest
 * @author Andrew Bowley
 * 21 Apr 2016
 */
public class SecurityHandlerTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String SUBJECT_DN = "CN=" + TEST_JID;

    @Test
    public void test_onValidateKeystoreConfigHandler()
    {
        X509Certificate x509Cert = mock(X509Certificate.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(SUBJECT_DN);
        when(x509Cert.getSubjectDN()).thenReturn(principal);
        final KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        final KeystoreData keystoreData= mock(KeystoreData.class);
        X509Certificate[] certificateChain = new X509Certificate[]{x509Cert};
        when(keystoreData.getCertificateChain()).thenReturn(certificateChain );
        JobScheduler jobScheduler = mock(JobScheduler.class);
        SyncInfoDialog infoDialog = mock(SyncInfoDialog.class);
        IEventBroker eventBroker = mock(IEventBroker.class);
        KeystoreHelper keystoreHelper = mock(KeystoreHelper.class);
        when(keystoreHelper.getKeystoreData(keystoreConfig)).thenReturn(keystoreData);
        SecurityHandler underTest = new SecurityHandler();
        underTest.jobScheduler = jobScheduler;
        underTest.infoDialog = infoDialog;
        underTest.eventBroker = eventBroker;
        underTest.onValidateKeystoreConfigHandler(keystoreConfig );
        underTest.keystoreHelper = keystoreHelper;
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Validate keystore config"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(infoDialog).showInfo("Key store validation", "Certificate chain:\n" + SUBJECT_DN);
        verify(eventBroker).post(CyberteteEvents.KEYSTORE_CONFIG_DONE, true);
    }

    @Test
    public void test_onValidateKeystoreConfigHandler_exception()
    {
        final CyberteteException exception = new CyberteteException("Keystore file not found");
        final KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        JobScheduler jobScheduler = mock(JobScheduler.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        IEventBroker eventBroker = mock(IEventBroker.class);
        Logger logger = mock(Logger.class);
        KeystoreHelper keystoreHelper = mock(KeystoreHelper.class);
        when(keystoreHelper.getKeystoreData(keystoreConfig)).thenThrow(exception);
        SecurityHandler underTest = new SecurityHandler();
        underTest.jobScheduler = jobScheduler;
        underTest.errorDialog = errorDialog;
        underTest.eventBroker = eventBroker;
        underTest.logger = logger;
        underTest.onValidateKeystoreConfigHandler(keystoreConfig );
        underTest.keystoreHelper = keystoreHelper;
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Validate keystore config"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(errorDialog).showError("Invalid Keystore", "Keystore file not found");
        verify(eventBroker).post(CyberteteEvents.KEYSTORE_CONFIG_DONE, false);
        verify(logger).error(exception, "Invalid Keystore");
    }


    @Test
    public void test_onSaveKeystoreConfigHandler()
    {
        final KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        JobScheduler jobScheduler = mock(JobScheduler.class);
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        SecurityHandler underTest = new SecurityHandler();
        underTest.persistentSecurityData = persistentSecurityData;
        underTest.jobScheduler = jobScheduler;
        underTest.onSaveKeystoreConfigHandler(keystoreConfig);
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save keystore config"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(persistentSecurityData).saveConfig(keystoreConfig);
    }

    @Test
    public void test_onSaveKeystoreConfigHandler_exception()
    {
        ProviderException exception = new ProviderException("File error");
        final KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        JobScheduler jobScheduler = mock(JobScheduler.class);
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        Logger logger = mock(Logger.class);
        SecurityHandler underTest = new SecurityHandler();
        underTest.persistentSecurityData = persistentSecurityData;
        underTest.jobScheduler = jobScheduler;
        underTest.errorDialog = errorDialog;
        underTest.logger = logger;
        underTest.onSaveKeystoreConfigHandler(keystoreConfig);
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save keystore config"), taskCaptor.capture());
        doThrow(exception)
        .when(persistentSecurityData).saveConfig(keystoreConfig);
        taskCaptor.getValue().run();
        verify(errorDialog).showError("Changes not saved", "File error");
        verify(logger).error(exception, "Changes not saved");
    }

    @Test
    public void test_onSaveClientCertConfigHandler()
    {
        Boolean isClientCertAuth = Boolean.TRUE;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        SecurityHandler underTest = new SecurityHandler();
        underTest.persistentSecurityData = persistentSecurityData;
        underTest.jobScheduler = jobScheduler;
        underTest.onSaveClientCertConfigHandler(isClientCertAuth);
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save client cert. auth. config"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(persistentSecurityData).updateClientCertAuth(isClientCertAuth);
    }

    @Test
    public void test_onSaveClientCertConfigHandler_exception()
    {
        ProviderException exception = new ProviderException("File error");
        Boolean isClientCertAuth = Boolean.TRUE;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        Logger logger = mock(Logger.class);
        SecurityHandler underTest = new SecurityHandler();
        underTest.persistentSecurityData = persistentSecurityData;
        underTest.jobScheduler = jobScheduler;
        underTest.errorDialog = errorDialog;
        underTest.logger = logger;
        underTest.onSaveClientCertConfigHandler(isClientCertAuth);
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save client cert. auth. config"), taskCaptor.capture());
        doThrow(exception)
        .when(persistentSecurityData).updateClientCertAuth(isClientCertAuth);
        taskCaptor.getValue().run();
        verify(errorDialog).showError("Changes not saved", "File error");
        verify(logger).error(exception, "Changes not saved");
    }


}
