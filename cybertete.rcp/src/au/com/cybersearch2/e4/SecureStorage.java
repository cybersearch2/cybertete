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
package au.com.cybersearch2.e4;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import au.com.cybersearch2.cybertete.preferences.PreferenceConstants;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * SecureStorage
 * Persists sensitive information such as passwords in an encrypted form.
 * @author Andrew Bowley
 * 29 Feb 2016
 */
public class SecureStorage
{
    private static final String SECURITY_STORAGE = "Secure storage error";
    private static final String EMPTY_VALUE = "";

    /** Secure preferences persist sensitive information in an encrypted form */
    ISecurePreferences rootNode;
    
    /** Logger */
    Logger logger;
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;

    /**
     * postConstruct()
     * @param loggerProvider Logger factory
     * @param preferencesRoot Secure preferences root node 
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider, ISecurePreferences preferencesRoot)
    {
        logger = loggerProvider.getClassLogger(SecureStorage.class);
        rootNode = preferencesRoot.node(PreferenceConstants.SAVED);
    }
 
    /**
     * Persist value to node with given path and key
     * @param path Absolute or relative path to the node
     * @param key Key with which the value is going to be associated
     * @param value Value to store
     */
    public void secureSave(String path, String key, String value)
    {
        ISecurePreferences node = rootNode.node(path);
        try
        {
            node.put(key, value, true);
        }
        catch (StorageException e)
        {
            String message = "Error setting \"" + key + "\"";
            logger.error(e, message);
            errorDialog.showError(SECURITY_STORAGE, message);
        }
    }

    /**
     * Returns value from node with given path and key
     * @param path Absolute or relative path to the node
     * @param key Key with which the value is associated
     * @return value or empty String if value not found
     */
    public String secureGet(String path, String key)
    {
        ISecurePreferences node = rootNode.node(path);
        String value = null;
        try
        {
            value = node.get(key, EMPTY_VALUE);
        }
        catch (StorageException e)
        {
            String message =  "Error getting \"" + key + "\" from \"" + path + "\"";
            logger.error(e, message);
            errorDialog.showError(SECURITY_STORAGE, message);
            value = EMPTY_VALUE;
        }
        return value;
    }

    /**
     * Removes value associated with the key
     * @param path Absolute or relative path to the node
     * @param key Key with which the value is associated
     */
    public void secureRemove(String path, String key)
    {
        ISecurePreferences node = rootNode.node(path);
        node.remove(key);
    }
 
    /**
     * Saves the tree of secure preferences to the persistent storage. This method can be called
     * on any node in the secure preference tree.
     * @throws IOException if error occurred while saving secure preferences
     */
    public void flush() throws IOException
    {
        rootNode.flush();
    }

}
