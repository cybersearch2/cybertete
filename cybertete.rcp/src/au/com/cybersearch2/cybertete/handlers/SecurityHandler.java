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

import java.security.ProviderException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.security.KeystoreHelper;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncInfoDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * SecurityHandler
 * Saves keystore and client certificate authentication configurations. 
 * Validates keystore configuration too as this potentially involves performing disk operations. 
 * @author Andrew Bowley
 * 10 Mar 2016
 */
public class SecurityHandler
{
    public static final String INVALID_KEYSTORE = "Invalid Keystore";
    public static final String SAVE_ERROR = "Changes not saved";
    
    /** Logger */
    Logger logger;

    /** SSL configuration saved as preferences */
    @Inject
    PersistentSecurityData persistentSecurityData;
    /** Keystore load and get SSL context helper */
    @Inject
    KeystoreHelper keystoreHelper;
    /** Creates and schedules a job given a job name and task to perform */
    @Inject 
    JobScheduler jobScheduler;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** Information dialog */
    @Inject
    SyncInfoDialog infoDialog;
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;
    
    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(SecurityHandler.class);
    }

    /**
     * Handles validation 
     * @param keystoreConfig Configuration to validate
     */
    @Inject @Optional
    void onValidateKeystoreConfigHandler(@UIEventTopic(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG) KeystoreConfig keystoreConfig)
    {
        validateKeystoreConfig(keystoreConfig);
    }

    /**
     * Handles saving keystore configuration
     * @param keystoreConfig Configuration to save
     */
    @Inject @Optional
    void onSaveKeystoreConfigHandler(@UIEventTopic(CyberteteEvents.SAVE_KEYSTORE_CONFIG) KeystoreConfig keystoreConfig)
    {
        saveKeystoreConfig(keystoreConfig);
    }

    /**
     * Handles saving client cert. auth. configuration
     * @param isClientCertAuth Configuration flag
     */
    @Inject @Optional
    void onSaveClientCertConfigHandler(@UIEventTopic(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG) Boolean isClientCertAuth)
    {
        saveClientCertConfig(isClientCertAuth);
    }

    /**
     * Validate keystore configuration. 
     * Displays keystore certificate chain in confirmation dialog to allow user to intervene.
     * @param keystoreConfig The configuration to validate
     */
    private void validateKeystoreConfig(final KeystoreConfig keystoreConfig)
    {
        // Schedule job to load the keystore and show a confirmation dialog
        jobScheduler.schedule("Validate keystore config", new Runnable(){

            @Override
            public void run()
            {
                X509Certificate[] certificateChain = null;
                try
                {
                    certificateChain = keystoreHelper.getKeystoreData(keystoreConfig).getCertificateChain();
                    StringBuilder builder = new StringBuilder("Certificate chain:");
                    for (X509Certificate x509Cert: certificateChain)
                        builder.append('\n').append(x509Cert.getSubjectDN().getName());
                    infoDialog.showInfo("Key store validation",  builder.toString());
                }
                catch (CyberteteException e)
                {
                    logger.error(e, INVALID_KEYSTORE);
                    errorDialog.showError(INVALID_KEYSTORE, e.getMessage());
                }
                eventBroker.post(CyberteteEvents.KEYSTORE_CONFIG_DONE, certificateChain != null);
            }});
    }

    /**
     * Save keystore configuration
     * @param keystoreConfig Configuration to save
     */
    private void saveKeystoreConfig(final KeystoreConfig keystoreConfig)
    {
        // Schedule job to write configuration to Eclipse secure preferences
        jobScheduler.schedule("Save keystore config", new Runnable(){

            @Override
            public void run()
            {
                try
                {
                    persistentSecurityData.saveConfig(keystoreConfig);
                }
                catch (ProviderException e)
                {
                    handleSaveError(e);
                }
            }});
    }

    /**
     * Save client certificate authentication configuration
     * @param isClientCertAuth Flag for configuration
     */
    private void saveClientCertConfig(final boolean isClientCertAuth)
    {
        // Schedule job to write configuration to Eclipse secure preferences
        jobScheduler.schedule("Save client cert. auth. config", new Runnable(){

            @Override
            public void run()
            {
                try
                {
                    persistentSecurityData.updateClientCertAuth(isClientCertAuth);
                }
                catch (ProviderException e)
                {
                    handleSaveError(e);
                }
            }});
    }
 
    /**
     * Log and display exception thrown while saving configuration
     * @param providerException
     */
    private void handleSaveError(ProviderException providerException)
    {
        logger.error(providerException, SAVE_ERROR);
        errorDialog.showError(SAVE_ERROR, providerException.getMessage());
    }
    
}
