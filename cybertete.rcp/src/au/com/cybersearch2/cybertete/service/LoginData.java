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
package au.com.cybersearch2.cybertete.service;

import java.io.IOException;
import java.security.ProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.osgi.service.prefs.BackingStoreException;

import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.service.ChatAccount;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.preferences.StorageKey;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;
import au.com.cybersearch2.cybertete.security.KerberosData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;

/**
 * LoginData
 * Container holding information required to log in
 * @author Andrew Bowley
 * 29 Nov 2015
 */
public class LoginData 
{
    /** Defaul XMPP port */
    public static final int DEFAULT_XMPP_PORT = 5222;
    /** Collection of session details */ 
    SessionDetailsSet sessionDetailsSet; 
    /** Collection of session details deleted by the user */
    Set<SessionDetails> deletedSessionDetailsSet; 
    // Cache for current single signon user
    volatile String singleSignonUser;

    /** Single signon configuration */
    @Inject
    KerberosData kerberosData;
    /** Login configuration saved as preferences */
    @Inject
    UserDataStore userDataStore;
    /** Displays error dialog */
    @Inject
    SyncErrorDialog errorDialog;
    @Inject 
    SyncQuestionDialog questionDialog;
    
    /**
     * postConstruct()
     */
    @PostConstruct
    public void postConstruct()
    {
        sessionDetailsSet = new SessionDetailsSet(userDataStore, userDataStore.getLastUser());
    }
    
    /**
     * @return the sessionDetails or null if not available
     */
    public SessionDetails getSessionDetails()
    {
        return sessionDetailsSet.getSessionDetails();
    }

    /**
     * @return the sessionDetailsSet (read-only)
     */
    public List<SessionDetails> getAllSessionDetails()
    {
        return sessionDetailsSet.getCollection();
    }

    /**
     * @return the autoLogin
     */
    public boolean isAutoLogin()
    {
        return userDataStore.isAutoLogin();
    }

    /**
     * @return the isSingleSignonEnabled
     */
    public boolean isSingleSignonEnabled()
    {
        return kerberosData.isSingleSignonEnabled();
    }
    
    /**
     * Delete login session identified by user. Adds session to deleted list.
     * Permanent removal occurs after Apply buttion pressed and user confirms.
     * Also reset current session configuration. 
     * @param jid User JID
     */
    public void deleteSession(String jid)
    {
        SessionDetails sessionDetails = sessionDetailsSet.removeUser(jid);
        if (sessionDetails != null)
        {
            // Lazy initialize collection
            if (deletedSessionDetailsSet == null)
                deletedSessionDetailsSet = new HashSet<SessionDetails>();
            deletedSessionDetailsSet.add(sessionDetails);
        }
    }

    /**
     * Returns read-only deleted sessions collection
     * @return SessionDetails set 
     */
    public Set<SessionDetails> getDeletedSessions()
    {
        if (deletedSessionDetailsSet == null)
            return Collections.emptySet();
        else
            return Collections.unmodifiableSet(deletedSessionDetailsSet);
    }

    /**
     * Returns list of user identities
     * @return List of JIDs
     */
    public List<String> getUserList()
    {
        List<String> userList = new ArrayList<String>();
        String newUser = sessionDetailsSet.getNewUser();
        if (!newUser.isEmpty())
            userList.add(newUser);
        boolean hasDeleted = (deletedSessionDetailsSet != null) && !deletedSessionDetailsSet.isEmpty();
        for (SessionDetails sessionDetails: getAllSessionDetails())
            if (!hasDeleted || !deletedSessionDetailsSet.contains(sessionDetails))
                userList.add(sessionDetails.getJid());
        return userList;
    }
 
    /**
     * Toggle auto login flag if opposite to specified value
     * @param autoLogin boolean
     */
    public void updateAutoLogin(boolean autoLogin)
    {
        if (userDataStore.isAutoLogin() != autoLogin)
            setValue(StorageKey.auto_login, autoLogin);
    }

    public boolean deselectCurrentAccount()
    {
        String user = sessionDetailsSet.getSessionDetails().getJid();
        if (user.isEmpty())
            return false;
        return (questionDialog.ask("Configuration for user " + user, "Apply changes to this configuration"));
    }
    
    /**
     * Returns session details to display for specified user
     * @param jid User JID
     * @param connectionError Error status from last login - "notAuthorized" results in password being cleared
     * @return ChatAccount object
     */
    public ChatAccount selectAccount(String jid, ConnectionError connectionError)
    {
        SessionDetails accountDetails = null;
        SessionDetails currentDetails = sessionDetailsSet.setCurrentUser(jid);
        if (currentDetails != null)
        {
            accountDetails = getLoginDetails(currentDetails, connectionError);
            // If password was invalid clear it to force reentry
            if (accountDetails.getPassword().isEmpty() && !currentDetails.getPassword().isEmpty())
                currentDetails.clearPassword();
            return accountDetails;
        }
        return sessionDetailsSet.createNewUser(jid);
    }

    /**
     * Returns details to display in Login controls 
     * @param configDetails Configuration details selected by JID
     * @param connectionError Current connection error may indicate a value is invalid and needs to be cleared
     * @return ChatAccount object
     */
    public SessionDetails getLoginDetails(ChatAccount configDetails, ConnectionError connectionError)
    {
        SessionDetails account = new SessionDetails(configDetails);
        String host = configDetails.getHost();
        if ((host != null) && !host.isEmpty())
        {
            int port = configDetails.getPort();
            if (port == 0)
                account.setPort(DEFAULT_XMPP_PORT);
        }
        else
        {
            account.setHost("");
            account.setPort(0);
        }
        if (configDetails.getAuthcid() == null)
            account.setAuthcid("");
        String password = configDetails.getPassword();
        if ((password == null) || password.isEmpty() || (connectionError == ConnectionError.notAuthorized))
            account.clearPassword();
        return account;
    }

    /**
     * Set current session details
     * @param newSessionDetails SessionDetails object containing values to set
     */
    public void setSessionDetails(SessionDetails newSessionDetails)
    {
        sessionDetailsSet.setSessionDetails(newSessionDetails);
    }

    /**
     * Apply pending changes
     */
    public void applyChanges(Set<SessionDetails> deletedSessions, boolean isConfirmed)
    {
        if (!isConfirmed)
            sessionDetailsSet.undoChanges(deletedSessions);
        deletedSessionDetailsSet.clear();
    }

    /**
     * Persist last user to successfully login
     * @param user User JID
     */
    public void saveLastUser(String user)
    {
        setValue(StorageKey.last_user, user);
    }
    
    /**
     * Persist single signon user
     * @param user User JID
     */
    public void saveSingleSignonUser(String user)
    {
    	singleSignonUser = user;
        setValue(StorageKey.sso_user, user);
    }

    public String getSingleSignonUser() 
    {
    	if (singleSignonUser == null)
    		singleSignonUser = userDataStore.getSingleSignonUser();
    	return singleSignonUser; 
    }
    /**
     * Persist session details of given user
     * @param user User JID
     */
    public void persist(String user)
    {
        try
        {
            userDataStore.saveSessionDetails(sessionDetailsSet.applySessionDetails(user));
        }
        catch (BackingStoreException | IOException e)
        {   // Throw runtime exception
            throw new ProviderException(e);
        }
    }

    /**
     * Remove specified session details from storage
     * @param sessionDetails  SessionDetails object containing values to remove 
     */
    public synchronized void remove(Set<SessionDetails> sessionDetails)
    {
        try
        {
            userDataStore.removeSessionDetails(sessionDetails);
        }
        catch (BackingStoreException | IOException e)
        {   // Throw runtime exception
            throw new ProviderException(e);
        }
    }

    /**
     * Persist boolean value
     * @param key Storage key used to identify value
     * @param value The boolean to store
     */
    void setValue(StorageKey key, boolean value)
    {
        try
        {
            switch(key)
            {
            case auto_login: 
                userDataStore.setAutoLogin(value); break;
            default: break;
            }
        }
        catch (BackingStoreException | IOException e)
        {   // Throw runtime exception
            throw new ProviderException(e);
        }
    }

    /**
     * Persist text value
     * @param key Storage key used to identify value
     * @param value The text to store
     */
    void setValue(StorageKey key, String value)
    {
        try
        {
            switch(key)
            {
            case last_user: 
                userDataStore.saveLastUser(value); break;
            case sso_user:
            	userDataStore.saveSingleSignonUser(value); break;
            default: break;
            }
        }
        catch (BackingStoreException | IOException e)
        {   // Throw runtime exception
            throw new ProviderException(e);
        }
    }

}
