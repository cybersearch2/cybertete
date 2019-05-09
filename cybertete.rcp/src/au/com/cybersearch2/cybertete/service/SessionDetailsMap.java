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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.prefs.BackingStoreException;

import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.preferences.StorageKey;
import au.com.cybersearch2.e4.StorageSupport;

/**
 * SessionDetailsMap
 * SessionDetails collection mapped by user JID and backed to file system for persistence
 * @author Andrew Bowley
 * 13 May 2016
 */
public class SessionDetailsMap extends StorageSupport
{
    /** Session details container */
    Map<String, SessionDetails> sessionDetailsMap;

    /**
     * Create SessionDetailsMap object
     */
    public SessionDetailsMap()
    {
        sessionDetailsMap = new HashMap<String, SessionDetails>();
    }

    /**
     * Returns session details for specified user JID
     * @param jid User JID
     * @return SessionDetails object or null if jid not found
     */
    public SessionDetails getSessionDetails(String jid)
    {
        return sessionDetailsMap.get(jid);
    }

    /**
     * Store new session details in memory. Use saveSessionDetails() for persistence.
     * @param sessionDetails Session details
     */
    public void putSessionDetails(SessionDetails sessionDetails)
    {
        sessionDetailsMap.put(sessionDetails.getJid(), sessionDetails);
    }

    /**
     * Returns unmodifiable collection of user JIDs held in memory
     * @return user JIDs
     */
    public Collection<String> getUserCollection()
    {
        return Collections.unmodifiableSet(sessionDetailsMap.keySet());
    }

    /**
     * Persist session details
     * @param root Path to storage location. Each session details configuration is located in a user JID sub-path.
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file system error occurs
     */
    protected void saveSessionDetails(String root) throws BackingStoreException, IOException 
    {
        // Save all sessions
        for (SessionDetails sessionDetails: sessionDetailsMap.values()) 
        {
            if (sessionDetails.isDirty())
                saveSessionDetails(root, sessionDetails.getJid(), sessionDetails);
        }
        flush();
        for (SessionDetails sessionDetails: sessionDetailsMap.values()) 
        {
            if (sessionDetails.isDirty())
                sessionDetails.clearDirtyFlag();
        }
    }

    /**
     * Load all session details configurations to container
     * @param root Path to storage location. Each session details configuration is located in a user JID sub-path.
     * @throws BackingStoreException if error occurs in Eclipse preferences
     */
    protected void loadSessionDetails(String root) throws BackingStoreException 
    {
        String[] users = getChildrenNames(root);
        for (int i = 0; i < users.length; i++) 
        {
            String user = users[i];
            sessionDetailsMap.put(user, loadSessionDetails(root, user));
        }
    }

    /**
     * Returns a single session details configuration
     * @param root Path to storage location. Each session details configuration is located in a user JID sub-path.
     * @param user User JID
     * @return SessionDetails object
     * @throws BackingStoreException if error occurs in Eclipse preferences
     */
    private SessionDetails loadSessionDetails(String root, String user) throws BackingStoreException
    {
        SessionDetails sessionDetails = 
            new SessionDetails(user, getPassword(user));
        String path = root + "/" + user;
        String[] keys = getKeys(path); 
        for (String attribute: keys)
        {
            switch (StorageKey.toStorageKey(attribute))
            {
            case host: getHost(path, sessionDetails); break;
            case auth_cid: getAuthCid(path, sessionDetails); break;
            case plain_sasl: getPlainSasl(path, sessionDetails); break;
            default: break;
            }
        }
        return sessionDetails;
    }

    /**
     * Returns password for specified user JID
     * @param user User JID
     * @return password
     */
    String getPassword(String user)
    {
        return getSecureValue(user, StorageKey.password);
    }

    /**
     * Set plain SASL config
     * @param path Absolute path for storage location
     * @param sessionDetails Configuration object to set
     */
    void getPlainSasl(String path, SessionDetails sessionDetails)
    {
        sessionDetails.setPlainSasl(getBoolean(path, StorageKey.plain_sasl, false));
    }

    /**
     * Set authCID (username) config
     * @param path Absolute path for storage location
     * @param sessionDetails Configuration object to set
     */
    void getAuthCid(String path, SessionDetails sessionDetails)
    {
        String username = getValue(path, StorageKey.auth_cid, "");
        if (!username.isEmpty())
            sessionDetails.setAuthcid(username);
    }

    /**
     * Set host and port config
     * @param path Absolute path for storage location
     * @param sessionDetails Configuration object to set
     */
    void getHost(String path, SessionDetails sessionDetails)
    {
        String host = getValue(path, StorageKey.host, "");
        if (!host.isEmpty())
        {
            sessionDetails.setHost(host);
            int port = 0;
            String text = getValue(path, StorageKey.port, "");
            if (!text.isEmpty())
                port = Integer.parseInt(text);
            sessionDetails.setPort(port);
        }
    }


    /**
     * Stores a single session details configuration
     * @param root Path to storage location. Each session details configuration is located in a user JID sub-path.
     * @param user User JID
     * @param sessionDetails Session details configuration to save
     * @throws BackingStoreException if error occurs in Eclipse preferences
     */
    private void saveSessionDetails(String root, String user, SessionDetails sessionDetails) throws BackingStoreException
    {
        String path = root + "/" + user;
        setHost(sessionDetails, path);
        setAuthCid(sessionDetails, path);
        setPlainSasl(sessionDetails, path);
        setPassword(user, sessionDetails);
    }

    /**
     * Sets password
     * @param user User JID
     * @param sessionDetails Session details configuration to set
     */
    void setPassword(String jid, SessionDetails sessionDetails)
    {
        String password = sessionDetails.getPassword();
        if (password == null)
            password = "";
        setSecureValue(jid, StorageKey.password, password);
    }
    
    /**
     * Sets plain SASL
     * @param sessionDetails Session details configuration to set
     * @param path Absolute path for storage location
     */
    void setPlainSasl(SessionDetails sessionDetails, String path)
    {
        setBoolean(path, StorageKey.plain_sasl, sessionDetails.isPlainSasl());
    }

    /**
     * Sets authCID (username)
     * @param sessionDetails Session details configuration to set
     * @param path Absolute path for storage location
     */
    void setAuthCid(SessionDetails sessionDetails, String path) throws BackingStoreException
    {
        String authCid = sessionDetails.getAuthcid();
        // If auth CID (username substitute for JID) exists, save it, otherwise clear preference value
        if ((authCid != null) && !authCid.isEmpty())
            setValue(path, StorageKey.auth_cid, authCid);
        else
            removeAttributes(path, StorageKey.auth_cid);
    }

    /**
     * Sets host and port
     * @param sessionDetails Session details configuration to set
     * @param path Absolute path for storage location
     */
    void setHost(SessionDetails sessionDetails, String path) throws BackingStoreException
    {
        String host = sessionDetails.getHost();
        String port = Integer.toString(sessionDetails.getPort());
        // If host set, save host and port, otherwise clear preference values
        if ((host != null) && !host.isEmpty())
        {
            setValue(path, StorageKey.host, host);
            setValue(path, StorageKey.port, port);
        }
        else
            removeAttributes(path, StorageKey.host, StorageKey.port);
    }

    /**
     * Remove given session details configurations
     * @param root Path to storage location. Each session details configuration is located in a user JID sub-path.
     * @param deletedSessionDetails Session details configurations to delete
     * @throws BackingStoreException if error occurs in Eclipse preferences
     * @throws IOException if file system error occurs
     */
    protected void removeSessionDetails(String root, Collection<SessionDetails> deletedSessionDetails) throws BackingStoreException, IOException
    {
        for (SessionDetails sessionDetails: deletedSessionDetails) 
        {
            String user = sessionDetails.getJid();
            sessionDetailsMap.remove(user);
            removeSecureValue(user, StorageKey.password);
            removeNode(root + "/" + user);
        }
        flush();
    }
}
