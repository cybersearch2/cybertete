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
import java.io.IOException;
import java.security.ProviderException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.osgi.service.prefs.BackingStoreException;

import au.com.cybersearch2.cybertete.GlobalProperties;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;

/**
 * PersistentSecurityData
 * Uses Preferences to persist client certificate authentication flag and SSL values
 * @author Andrew Bowley
 * 16 Mar 2016
 */
@Creatable
public class PersistentSecurityData
{
    private static final String SAVE_ERROR = "Error updating configuration";

    /** Flag set true if using client certificate authentication */
    boolean isClientCertAuth;

    /** Persists Login configurations, last user to login and auto login flag using Eclipse preferences */
    @Inject
    UserDataStore userDataStore;
    /** Properties such as System properties which are universally accessible */
    @Inject
    GlobalProperties globalProperties;

    /**
     * postConstruct()
     */
    @PostConstruct
    public void postConstruct()
    {
        isClientCertAuth = userDataStore.isClientCertAuth();
    }

    /**
     * Returns security configuration values from preferences
     * @return SecurityConfig object
     */
    public KeystoreConfig keystoreConfigInstance()
    {
        File keystorePath = new File(globalProperties.getUserHome(), globalProperties.getUserName() + ".pfx");
        SecurityConfig defaultConfig = 
            new SecurityConfig(keystorePath.getAbsolutePath(), "PKCS12", "changeit");
        return userDataStore.getKeystore(defaultConfig);
    }

    /**
     * Returns keystore configuration for creation of SSL Context
     * @return KeystoreConfig object
     */
    public KeystoreConfig getKeystoreConfig()
    {
        if (isClientCertAuth)
            // Extract Client certificate from specified keystore.
            return keystoreConfigInstance();
         else
            // Extract Client certificate from default keystore.
            return new SecurityConfig();
    }

    /**
     * Save security configuration
     * @param keystoreConfig Keystore configuration to save
     * @throws ProviderException  
     */
    public void saveConfig(KeystoreConfig keystoreConfig)
    {
        try
        {
            userDataStore.setKeystore(keystoreConfig);
        }
        catch (BackingStoreException | IOException e)
        {   // Throw runtime exception
            throw new ProviderException(SAVE_ERROR, e);
        }
    }

    /**
     * Returns flag set true if using client certificate authentication
     * @return boolean
     */
    public boolean isClientCertAuth()
    {
        return isClientCertAuth;
    }

    /**
     * Set client certificate authentication flag
     * @param isClientCertAuth boolean
     * @throws ProviderException  
     */
    public void setClientCertAuth(boolean isClientCertAuth)
    {
        try
        {
            userDataStore.setClientCertAuth(isClientCertAuth);
            this.isClientCertAuth = isClientCertAuth; 
        }
        catch (BackingStoreException | IOException e)
        {   // Throw runtime exception
            throw new ProviderException(SAVE_ERROR, e);
        }
    }

    /**
     * Toggle client certificate authentication flag
     * @param isClientCertAuth The value to set
     */
    public void updateClientCertAuth(boolean isClientCertAuth)
    {
        if (this.isClientCertAuth != isClientCertAuth)
        { 
            setClientCertAuth(isClientCertAuth);
        }
    }
}
