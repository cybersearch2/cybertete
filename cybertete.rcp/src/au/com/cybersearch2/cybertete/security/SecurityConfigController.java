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

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import au.com.cybersearch2.cybertete.handlers.SecurityHandler;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.views.LoginView;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * SecurityConfigController
 * @author Andrew Bowley
 * 9 May 2016
 */
@Creatable
public class SecurityConfigController
{
    // Dirty flags
    boolean keystoreDirty;
    boolean clientCertDirty;

    /** Keystore configuration */
    SecurityConfig securityConfig;

    /** SSL configuration saved as preferences */
    @Inject
    PersistentSecurityData persistentSecurityData;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** E4 application model part service */
    @Inject
    EPartService partService;
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;

    /**
     * postConstruct
     */
    @PostConstruct
    public void postConstruct() 
    {
        KeystoreConfig config = persistentSecurityData.keystoreConfigInstance();
        // Initialize keystore configuration from storage
        securityConfig = 
            new SecurityConfig(config.getKeystoreFile(), 
                                config.getKeystoreType(), 
                                config.getKeystorePassword());
    }

    /*
     * User interface event handlers:
     * onApply, onOk. onCancel. onKeystoreConfigChange, onClientCertSelect
     */
    /**
     * Handle Apply button clicked
     * @param isClientCertAuth Client certificate authentication value
     * @param keystoreConfig Keystore configuration
     * @return flag set true if changes applied successfully
     */
    public boolean onApply(boolean isClientCertAuth,  KeystoreConfig keystoreConfig)
    {
        // Save configurate when Apply button pressed
        return applyChanges(isClientCertAuth, keystoreConfig);
    }

    /**
     * Handle OK button clicked
     * @param isClientCertAuth Client certificate authentication value
     * @param keystoreConfig Keystore configuration
     */
    public void onOk(boolean isClientCertAuth,  KeystoreConfig keystoreConfig)
    {
        // Save configuration and exit
        if (applyChanges(isClientCertAuth, keystoreConfig))
            onCancel();
    }

    /**
     * Handle Cancel button clicked
     */
    public void onCancel()
    {
        // Exit if Cancel pressed
        activateLoginView();
    }

    /**
     * Handle key pressed to change a keystore field 
     */
    public void onKeystoreConfigChange()
    {
        keystoreDirty = true;
    }

    /** 
     * Handle client cert. auth. checkbox click
     */
    public void onClientCertSelect()
    {
        clientCertDirty = true;
    }

    /**
     * Activate login view, which happens on security view cancel or OK
     */
    public void activateLoginView()
    {
        MPart loginViewPart = partService.findPart(LoginView.LOGIN_VIEW_ID);
        partService.showPart(loginViewPart, PartState.ACTIVATE);
    }

    /**
     * Post event to validate security configuration.
     */
    public void validateKeystoreConfig(KeystoreConfig keystoreConfig)
    {
        eventBroker.post(CyberteteEvents.VALIDATE_KEYSTORE_CONFIG, keystoreConfig);
    }

    /**
     * Post event to persist client certificate authentication setting
     * @param isClientCertAuth
     */
    public void updateClientCertAuth(boolean isClientCertAuth)
    {
        eventBroker.post(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG, isClientCertAuth);
    }

    /**
     * Save security configuration. 
     * @param keystoreConfig Keystore configuration
     */
    public void saveKeystoreConfig(KeystoreConfig keystoreConfig)
    {
        securityConfig.setConfig(keystoreConfig);
        eventBroker.post(CyberteteEvents.SAVE_KEYSTORE_CONFIG, keystoreConfig);
    }

    /**
     * Return keystore configuration
     * @return KeystoreConfig object
     */
    public KeystoreConfig getKeystoreConfig()
    {
        return securityConfig;
    }

    /**
     * Returns flag set true if client certificate authentication is used
     * @return boolean
     */
    public boolean isClientCertAuth()
    {
        return persistentSecurityData.isClientCertAuth();
    }

    /**
     * Save security configuration. Runs in job.
     * isClientCertAuth Flag set true if client certificate authenitication selected
     * securityConfig Configuration to save
     * @return Flag set true if configuration is valid
     */
    boolean applyChanges(boolean isClientCertAuth, KeystoreConfig securityConfig)
    {
        boolean success = true;
        if (clientCertDirty)
        {
            clientCertDirty = false;
            updateClientCertAuth(isClientCertAuth);
        }
        if (keystoreDirty)
        {
            keystoreDirty = false;
            // Only save SSL config if controls are enabled
            if (securityConfig.getKeystoreFile() != null)
                success = saveSslConfig(securityConfig);
        }
        return success;
    }

    /**
     * Save SSL configuration 
     * keystoreConfig Configuration to save
     * @return Flag set false if error detected with keystore file field
     */
    boolean saveSslConfig(KeystoreConfig securityConfig)
    {
        String keystorePath = securityConfig.getKeystoreFile();
        if (keystorePath.isEmpty()) 
        {
            errorDialog.showError(SecurityHandler.INVALID_KEYSTORE,
                    "Keystore field must not be blank.");
            return false;
        }
        File keystoreFile = getKeystoreFile(keystorePath);
        if (!keystoreFile.exists())
        {
            errorDialog.showError(SecurityHandler.INVALID_KEYSTORE,
                    "Keystore file not found.");
            return false;
        }
        else if (!keystoreFile.isFile())
        {
            errorDialog.showError(SecurityHandler.INVALID_KEYSTORE,
                    "Keystore is not a file.");
            return false;
        }
        // Complete validation as job and save configuration in KEYSTORE_CONFIG_DONE event
        validateKeystoreConfig(securityConfig);
        return true;
     }

    /**
     * Returns keystore file as a File object
     * @param keystorePath Keystore file path
     * @return File object
     */
    protected File getKeystoreFile(String keystorePath)
    {
        return new File(keystorePath);
    }
}

