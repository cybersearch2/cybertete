/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.SecurityConfig;
import au.com.cybersearch2.cybertete.service.SessionDetailsMap;

/**
 * UserDataStore
 * Persists Login configurations, last user to login and auto login flag using Eclipse preferences
 * @author Andrew Bowley
 * 23 Nov 2015
 */
public class UserDataStore extends SessionDetailsMap
{
    private static final String DATA_RETEIVAL_MESSAGE = "Configuration retrieval from disk failed";
    private static final String GLOBAL_PREFS = "global_preferences";
 
    /** Application absolute path */
    String APPLICATION_PATH;
    /** User preferences under APPLICATION_PATH */
    String PREFERENCES_ROOT;
 
    /**
     * postConstruct   
     * @param loggerProvider Logger factory
     * @param preferences Preferences root node
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider,
                               IEclipsePreferences preferences)
    {
        APPLICATION_PATH = preferences.absolutePath();
        try
        {
            // Create root node or ensure no changes outstanding if it exists
            Preferences savedNode = preferences.node(PreferenceConstants.SAVED);
            savedNode.sync();
            PREFERENCES_ROOT = savedNode.absolutePath();
            loadSessionDetails(PREFERENCES_ROOT);
        }
        catch (BackingStoreException e)
        {   // This error will be fatal in post construct context
            loggerProvider.getClassLogger(UserDataStore.class).error(e, DATA_RETEIVAL_MESSAGE);
            throw new CyberteteException(DATA_RETEIVAL_MESSAGE, e);
        }
    }

    /**
     * Returns JID of last user to successfully login
     * @return JID or empty string if no successful login has occurred
     */
    public String getLastUser()
    {
        return getValue(APPLICATION_PATH, StorageKey.last_user, "");
    }
 
    /**
     * Returns JID of single signon user
     * @return JID or empty string if no single signon user configured
     */
    public String getSingleSignonUser()
    {
        return getValue(APPLICATION_PATH, StorageKey.sso_user, "");
    }
 
    /**
     * Returns flag set true if auto login configured
     * @return boolean
     */
    public boolean isAutoLogin()
    {
        return getBoolean(APPLICATION_PATH, StorageKey.auto_login, false);
    }

    /**
     * Set auto login flag
     * @param autoLogin The boolean value to set
     * @throws BackingStoreException 
     * @throws IOException 
     */
    public void setAutoLogin(boolean autoLogin) throws BackingStoreException, IOException
    {
        setBoolean(APPLICATION_PATH, StorageKey.auto_login, autoLogin);
        flush();
    }

    /** 
     * Returns flag set true if client certificate authenthentication configured
     * @return boolean
     */
    public boolean isClientCertAuth()
    {
        return getBoolean(APPLICATION_PATH, StorageKey.client_cert_auth, false);
    }

    /**
     * Set client certificate authentication flag
     * @param isClientCertAuth
     * @throws BackingStoreException 
     * @throws IOException 
     */
    public void setClientCertAuth(boolean isClientCertAuth) throws BackingStoreException, IOException
    {
        setBoolean(APPLICATION_PATH, StorageKey.client_cert_auth, isClientCertAuth);
        flush();
    }

    /**
     * Returns keystore configuration, falling back on given defaults
     * @param defaultConfig Default keystore configuration
     */
    public KeystoreConfig getKeystore(KeystoreConfig defaultConfig)
    {
        String keystoreFile = getValue(APPLICATION_PATH, StorageKey.keystore_file, defaultConfig.getKeystoreFile());
        String keystoreType = getValue(APPLICATION_PATH, StorageKey.keystore_type, defaultConfig.getKeystoreType());
        String password = getSecureValue(GLOBAL_PREFS, StorageKey.keystore_password);
        if ((password == null) || password.isEmpty())
            password = defaultConfig.getKeystorePassword();
        return new SecurityConfig(keystoreFile, keystoreType, password);
    }

    /**
     * Set keystore configuration
     * @param keystoreConfig Keystore configuration
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file error occurs on flush
     */
    public void setKeystore(KeystoreConfig keystoreConfig) throws BackingStoreException, IOException
    {
        setValue(APPLICATION_PATH, StorageKey.keystore_file, keystoreConfig.getKeystoreFile());
        setValue(APPLICATION_PATH, StorageKey.keystore_type, keystoreConfig.getKeystoreType());
        setSecureValue(GLOBAL_PREFS, StorageKey.keystore_password, keystoreConfig.getKeystorePassword());
        flush();
    }

    /**
     * Persist session details
     * @param sessionDetails Current session details
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file error occurs on flush
     */
    public void saveSessionDetails(SessionDetails sessionDetails) throws BackingStoreException, IOException 
    {
        // Set last user to JID
        setLastUser(sessionDetails.getJid());
        // This will call flush()
        saveSessionDetails(PREFERENCES_ROOT);
    }

    /**
     * Remove session details
     * @param deletedSessionDetails Collection of Session details to delete
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file error occurs on flush
     */
    public void removeSessionDetails(Collection<SessionDetails> deletedSessionDetails) throws BackingStoreException, IOException
    {
        // This will call flush()
        removeSessionDetails(PREFERENCES_ROOT, deletedSessionDetails);
    }

    /**
     * Persists JID of last user to successfully login
     * @param user User JID
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file error occurs on flush
     */
    public void saveLastUser(String user) throws BackingStoreException, IOException
    {
        setLastUser(user);
        flush();
    }

    /**
     * Saves JID of last user to successfully login
     * @param user User JID
     * @throws BackingStoreException
     */
    void setLastUser(String jid) throws BackingStoreException
    {
        setValue(APPLICATION_PATH, StorageKey.last_user, jid);
    }

    /**
     * Persists JID of single signon user to successfully login
     * @param user User JID
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file error occurs on flush
     */
    public void saveSingleSignonUser(String user) throws BackingStoreException, IOException
    {
        setSingleSignonUser(user);
        flush();
    }

    /**
     * Saves JID of single signon user
     * @param user User JID
     * @throws BackingStoreException
     */
    void setSingleSignonUser(String jid) throws BackingStoreException
    {
        setValue(APPLICATION_PATH, StorageKey.sso_user, jid);
    }
}
